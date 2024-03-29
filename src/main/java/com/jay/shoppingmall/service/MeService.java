package com.jay.shoppingmall.service;

import com.jay.shoppingmall.config.ExcludeFromJacocoGeneratedReport;
import com.jay.shoppingmall.domain.browse_history.BrowseHistoryRepository;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.request.AgreeRequest;
import com.jay.shoppingmall.dto.request.DeleteMeRequest;
import com.jay.shoppingmall.dto.response.MeDetailResponse;
import com.jay.shoppingmall.dto.request.UserUpdateRequest;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MeService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ZzimRepository zzimRepository;
    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final BrowseHistoryRepository browseHistoryRepository;

    private final FileHandler fileHandler;

    @ExcludeFromJacocoGeneratedReport
    public Boolean passwordCheck(String password, User user) {

        return passwordEncoder.matches(password, user.getPassword());
    }

    public MeDetailResponse findById(final Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return MeDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    public Boolean agreeCheck(final AgreeRequest agreeRequest, final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("잘못된 요청입니다"));

        if (!agreeRequest.getIsMandatoryAgree()) {
            throw new AgreeException("필수 항목을 반드시 동의하셔야 합니다");
        }
        Agree agree = Agree.builder()
                .isMandatoryAgree(agreeRequest.getIsMandatoryAgree())
                .isMarketingAgree(agreeRequest.getIsMarketingAgree())
                .build();

        user.setAgree(agree);

        return true;
    }

    public void updateInfo(final UserUpdateRequest request, final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("잘못된 요청입니다"));

        Address address = Address.builder()
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .extraAddress(request.getExtraAddress())
                .zipcode(request.getZipcode())
                .build();
        Name name = Name.builder()
                .last(request.getLastName())
                .first(request.getFirstName())
                .build();

        Agree agree = Agree.builder()
                .isMandatoryAgree(true)
                .isMarketingAgree(request.getIsMarketingAgree())
                .build();

        user.userUpdate(address, name, agree, splitPhoneNumber(request.getPhoneNumber()));

        userRepository.save(user);
    }

    private PhoneNumber splitPhoneNumber(String phoneNumber) {
        if (phoneNumber.contains("-")) {
            phoneNumber = phoneNumber.replace("-", "");
        }
        return PhoneNumber.builder()
                .first(phoneNumber.substring(0, 3))
                .middle(phoneNumber.length() == 11 ?
                        phoneNumber.substring(3, 7) :
                        phoneNumber.substring(3, 6))
                .last(phoneNumber.substring(phoneNumber.length() - 4))
                .build();
    }

    public void deleteMe(final User user) {
        log.info("User deleted. email = '{}'", user.getEmail());

        userRepository.delete(user);
    }

//    public List<ItemResponse> getAllMeZzim(User user) {
//        List<Zzim> zzims = zzimRepository.findByUser(user).orElseThrow(() -> new ItemNotFoundException("아이디에 상품이 없습니다"));
//        List<ItemResponse> itemResponses = new ArrayList<>();
//
//        for (Zzim zzim : zzims) {
//            if (!zzim.getIsZzimed()) {
//                continue;
//            }
//            Item item = itemRepository.findById(zzim.getItem().getId())
//                    .orElseThrow(() -> new ItemNotFoundException("상품이 없습니다"));
//
//            itemResponses.add(ItemResponse.builder()
//                    .itemId(item.getId())
//                    .name(item.getName())
//                    .zzim(item.getZzim())
//                    .mainImage(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN,item.getId())))
//                    .priceNow(item.getPrice())
//                    .originalPrice(item.getSalePrice())
//                            .isZzimed(zzim.getIsZzimed())
//                    .build());
//        }
//        return itemResponses;
//    }

    public void duplicationCheck(final String phoneNumber) {
        final PhoneNumber splitPhoneNumber = splitPhoneNumber(phoneNumber);

        if (userRepository.findByPhoneNumber(
                splitPhoneNumber.getFirst(), splitPhoneNumber.getMiddle(), splitPhoneNumber.getLast()
        ).isPresent()) {
            throw new AlreadyExistsException("이미 해당 번호가 존재합니다");
        }
    }
}
