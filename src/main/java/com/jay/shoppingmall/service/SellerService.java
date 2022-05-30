package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.QnaAnswerRequest;
import com.jay.shoppingmall.dto.request.SellerAgreeRequest;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final FileHandler fileHandler;
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final ZzimService zzimService;
    private final QnaRepository qnaRepository;

    private final QnaService qnaService;

    public List<ItemResponse> showItemsBySeller(User user, Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        Page<Item> items = itemRepository.findBySellerId(seller.getId(), pageable);

        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            Image mainImage = imageRepository.findByItemIdAndIsMainImageTrue(item.getId());
            String stringMainImage = fileHandler.getStringImage(mainImage);

            ItemResponse itemResponse = ItemResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .price(item.getPrice())
//                    .salePrice(item.getSalePrice())
                    .zzim(item.getZzim())
                    .mainImage(stringMainImage)
                    .isZzimed(zzimService.isZzimed(user.getId(), item.getId()))
                    .build();
            itemResponses.add(itemResponse);
        }
        return itemResponses;
    }

    public Long writeItem(WriteItemRequest writeItemRequest, final MultipartFile file, final List<MultipartFile> files, final User user) {
        User userForId = userRepository.findById(user.getId())
                        .orElseThrow(() -> new UserNotFoundException("유저가 없습니다"));
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(userForId.getId())
                        .orElseThrow(() -> new SellerNotFoundException("판매자 자격이 없습니다"));

        List<Image> imageList = new ArrayList<>();

        Image mainImage = fileHandler.parseFilesInfo(file);
        mainImage.setIsMainImage(true);
        imageList.add(mainImage);

        if (!files.get(0).isEmpty()) {
            for (MultipartFile multipartFile : files) {
                imageList.add(fileHandler.parseFilesInfo(multipartFile));
            }
        }
        Item item = Item.builder()
                .name(writeItemRequest.getName())
                .description(writeItemRequest.getDescription())
                .price(writeItemRequest.getPrice())
                .stock(writeItemRequest.getStock())
                .seller(seller)
                .build();

        for (Image image : imageList) {
            item.addImage(imageRepository.save(image));
        }
        return itemRepository.save(item).getId();
    }

    public Boolean sellerAgreeCheck(final SellerAgreeRequest sellerAgreeRequest, final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("잘못된 요청입니다"));

        if (sellerRepository.findByUserIdAndIsActivatedTrue(id).isPresent()) {
            throw new AlreadyExistsException("이미 판매자로 가입이 되어있습니다");
        }

        user.updateUserRole(Role.ROLE_SELLER);

        Seller seller = Seller.builder()
                .userId(user.getId())
                .isLawAgree(sellerAgreeRequest.getIsLawAgree())
                .isSellerAgree(sellerAgreeRequest.getIsSellerAgree())
                .isActivated(true)
                .build();

        sellerRepository.save(seller);
        return true;
    }

    public void qnaAnswer(final QnaAnswerRequest qnaAnswerRequest, final User user) {
        Long qnaId = qnaAnswerRequest.getQnaId();
        Boolean isAnswered = qnaRepository.findById(qnaId)
                .map(Qna::getIsAnswered)
                .orElse(false);
        if (isAnswered) {
            throw new AlreadyExistsException("답변이 이미 존재합니다");
        }
        Long itemId = qnaRepository.findById(qnaId)
                .map(Qna::getItem)
                .map(Item::getId)
                .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지 않습니다"));

        if (qnaService.sellerCheck(itemId, user)) {
            Qna qna = qnaRepository.findById(qnaId)
                    .orElseThrow(() -> new QnaException("QnA가 존재하지 않습니다"));
            qna.answerUpdate(qnaAnswerRequest.getAnswer());
        }

    }
}
