package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.CartManipulationRequest;
import com.jay.shoppingmall.dto.request.CartRequest;
import com.jay.shoppingmall.dto.response.cart.CartPricePerSellerResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceTotalResponse;
import com.jay.shoppingmall.dto.response.cart.CartResponse;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import com.jay.shoppingmall.exception.exceptions.CartEmptyException;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.SellerNotFoundException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@EqualsAndHashCode
public class CartService {

    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;
    private final FileHandler fileHandler;
    private final ItemOptionRepository itemOptionRepository;
    private final SellerRepository sellerRepository;

    //TODO 밑의 코드들로 리팩토링 하기
    public Map<SellerResponse, List<ItemAndQuantityResponse>> showCartItemsList(User user) {
        //유저를 기준으로 장바구니 물품 가져오기
        final List<Cart> carts = cartRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다"));

        Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = new ConcurrentHashMap<>();
        final List<Seller> sellers = carts.stream().map(Cart::getItem).map(Item::getSeller).distinct().collect(Collectors.toList());
        //판매자를 기준으로 상품 정렬
        for (Seller seller : sellers) {
            final List<Cart> cartList = carts.stream().filter(cart -> cart.getItem().getSeller().equals(seller)).collect(Collectors.toList());
            long itemTotalPricePerSeller = 0;
            int itemTotalQuantityPerSeller = 0;

            //Key 판매자 생성
            final SellerResponse sellerResponse = SellerResponse.builder()
                    .sellerId(seller.getId())
                    .companyName(seller.getCompanyName())
                    .shippingFeeFreePolicy(seller.getShippingFeeFreePolicy())
                    .build();

            List<ItemAndQuantityResponse> itemAndQuantityResponses;
            if (sellerResponseListMap.containsKey(sellerResponse)) {
                itemAndQuantityResponses = sellerResponseListMap.get(sellerResponse);
            } else {
                itemAndQuantityResponses = new ArrayList<>();
            }

            //Value List<ItemAndQuantityResponse> 생성
            for (Cart cart : cartList) {
                if (cart.getIsSelected()) {
                    itemTotalPricePerSeller += cart.getItemOption().getItemPrice().getPriceNow() * cart.getQuantity();
                    itemTotalQuantityPerSeller += cart.getQuantity();
                }
                itemAndQuantityResponses.add(ItemAndQuantityResponse.builder()
                        .cartId(cart.getId())
                        .itemId(cart.getItem().getId())
                        .option1(cart.getItemOption().getOption1())
                        .option2(cart.getItemOption().getOption2())
                        .optionId(cart.getItemOption().getId())
                        .name(cart.getItem().getName())
                        .priceNow(cart.getItemOption().getItemPrice().getPriceNow())
                        .image(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, cart.getItem().getId())))
                        .zzim(cart.getItem().getZzim())
                        .quantity(cart.getQuantity())
                        .isSelected(cart.getIsSelected())
                        .build());
            }
            //배송비 무료 여부 및 총 가격
            sellerResponse.setShippingFee(shippingFeeCheck(seller.getId(), itemTotalPricePerSeller));
            sellerResponse.updateTotal(itemTotalPricePerSeller, itemTotalQuantityPerSeller);

//            if (itemTotalPricePerSeller == 0) {
//                sellerResponse.setShippingFee(0);
//            }
            sellerResponseListMap.put(sellerResponse, itemAndQuantityResponses);
        }
        return sellerResponseListMap;
    }

    public void addItemOptionsToCart(final List<ItemOptionResponse> itemOptions, final User user) {
        for (ItemOptionResponse itemOptionResponse : itemOptions) {
            Item item = itemRepository.findById(itemOptionResponse.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
            ItemOption itemOption = itemOptionRepository.findById(itemOptionResponse.getItemOptionId())
                    .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지않습니다"));

            if (cartRepository.findByUserAndItemAndItemOption(user, item, itemOption).isPresent()) {
                throw new AlreadyExistsException("해당 상품이 장바구니에 존재합니다");
            }
            Cart cart = Cart.builder()
                    .isSelected(true)
                    .item(item)
                    .itemOption(itemOption)
                    .quantity(itemOptionResponse.getItemQuantity())
                    .user(user)
                    .build();

            cartRepository.save(cart);
        }
    }

//    public Integer addItemToCart(CartRequest request, User user) {
//        Item item = itemRepository.findById(request.getItemId())
//                .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
//        ItemOption itemOption = itemOptionRepository.findByOption1AndOption2AndItemId(request.getOption1(), request.getOption2(), request.getItemId())
//                .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지않습니다"));
//
//        Cart cart = cartRepository.findByUserAndItemAndItemOption(user, item, itemOption)
//                .orElseThrow(() -> new AlreadyExistsException("해당 상품이 장바구니에 존재합니다"));
//
//        if (item.getStock() == 0) {
//            throw new ItemNotFoundException("죄송합니다. 해당 상품은 품절되었습니다");
//        }
//        Integer addedQuantity = request.getQuantity();
//        if (cart != null) {
////            if (Objects.equals(cart.getItemOption().getId(), itemOption.getId())) {
//            throw new AlreadyExistsException("이미 상품이 장바구니에 존재합니다");
//            //            addedQuantity = quantity + cart.getQuantity();
//            //            cart.manipulateQuantity(addedQuantity);
//            //            cartRepository.save(cart);
////            }
//        } else {
//            Cart newCart = Cart.builder()
//                    .user(user)
//                    .item(item)
//                    .itemOption(itemOption)
//                    .quantity(addedQuantity)
//                    .build();
//            cartRepository.save(newCart);
//        }
//        return addedQuantity;
//    }

    //장바구니 전체 가격, 총 배송비, 개수
    public CartPriceTotalResponse cartPriceTotal(User user) {
        final List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user)
                .orElseThrow(() -> new CartEmptyException("장바구니가 비어있습니다"));

        long cartTotalPrice = 0;
        int cartTotalQuantity = 0;
        int cartTotalShippingFee = 0;

        //장바구니 판매자 기준 분류
        final List<Seller> sellers = carts.stream().map(Cart::getItem).map(Item::getSeller).distinct().collect(Collectors.toList());
        for (Seller seller : sellers) {
            final CartPricePerSellerResponse cartPricePerSellerResponse = this.cartPricePerSeller(user, seller);

            cartTotalPrice += cartPricePerSellerResponse.getItemTotalPricePerSeller();
            cartTotalQuantity += cartPricePerSellerResponse.getItemTotalQuantityPerSeller();
            cartTotalShippingFee += cartPricePerSellerResponse.getItemShippingFeePerSeller();
        }
        return CartPriceTotalResponse.builder()
                .cartTotalPrice(cartTotalPrice)
                .cartTotalQuantity(cartTotalQuantity)
                .cartTotalShippingFee(cartTotalShippingFee)
                .build();
    }

    //판매자별 가격, 개수, 배송비
    public CartPricePerSellerResponse cartPricePerSeller(User user, Seller seller) {
        final List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user)
                .orElseThrow(() -> new CartEmptyException("장바구니가 비어있습니다"));

        final List<Cart> cartList = carts.stream().filter(cart -> cart.getItem().getSeller().equals(seller)).collect(Collectors.toList());
        long itemTotalPricePerSeller = 0;
        int itemTotalQuantityPerSeller = 0;

        for (Cart cart : cartList) {
            itemTotalPricePerSeller += cart.getItemOption().getItemPrice().getPriceNow() * cart.getQuantity();
            itemTotalQuantityPerSeller += cart.getQuantity();
        }
        return CartPricePerSellerResponse.builder()
                .sellerId(seller.getId())
                .itemTotalPricePerSeller(itemTotalPricePerSeller)
                .itemTotalQuantityPerSeller(itemTotalQuantityPerSeller)
                .itemShippingFeePerSeller(shippingFeeCheck(seller.getId(), itemTotalPricePerSeller))
                .build();
    }

    public CartPriceResponse changeQuantity(final CartManipulationRequest request, final User user) {
        final Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new CartEmptyException("장바구니가 비어있습니다"));

        cart.setIsSelected(request.getIsSelected());

        final long cartManipulatedPrice = cart.manipulateQuantity(request.getCartQuantity()) * cart.getItemOption().getItemPrice().getPriceNow();
        final CartPricePerSellerResponse cartPricePerSellerResponse = cartPricePerSeller(user, cart.getItem().getSeller());
        final CartPriceTotalResponse cartPriceTotalResponse = cartPriceTotal(user);

        return CartPriceResponse.builder()
                .cartManipulatedPrice(cartManipulatedPrice)
                .cartPricePerSellerResponse(cartPricePerSellerResponse)
                .cartPriceTotalResponse(cartPriceTotalResponse)
                .build();
    }
    public CartPriceResponse cartSelect(final String check, final User user) {
        List<Cart> carts = cartRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다"));
        List<CartPricePerSellerResponse> cartPricePerSellerResponses = new ArrayList<>();

        for (Cart cart : carts) {
            cart.setIsSelected(check.equals("true"));
            cartPricePerSellerResponses.add(cartPricePerSeller(user, cart.getItem().getSeller()));
        }
        final CartPriceTotalResponse cartPriceTotalResponse = cartPriceTotal(user);

        return CartPriceResponse.builder()
                .cartPricePerSellerResponses(cartPricePerSellerResponses)
                .cartPriceTotalResponse(cartPriceTotalResponse)
                .build();
    }

//    public CartResponse updateCart(final CartRequest request, final User user) {
//
//        Cart cart = cartRepository.findByUserIdAndItemIdAndItemOptionId(user.getId(), request.getItemId(), request.getOptionId())
//                .orElseThrow(() -> new AlreadyExistsException("해당 상품이 존재하지 않습니다"));
//
//        if (Objects.equals(request.getQuantity(), cart.getQuantity())) {
//            throw new AlreadyExistsException("상품 개수가 변동되지 않았습니다");
//        }
//
//        final long oldTotalPrice = request.getTotalPrice() - cart.getQuantity() * cart.getItemOption().getItemPrice().getPriceNow();
//        final long multipliedPrice = cart.manipulateQuantity(request.getQuantity()) * cart.getItemOption().getItemPrice().getPriceNow();
//        final Long newTotalPrice = oldTotalPrice + multipliedPrice;
//
//        cartRepository.save(cart);
//
//        return CartResponse.builder()
//                .id(cart.getId())
//                .quantity(cart.getQuantity())
//                .multipliedPrice(multipliedPrice)
//                .totalPrice(newTotalPrice)
//                .totalQuantity(this.getTotalQuantity(user))
//                .build();
//    }

    //TODO 삭제된 시간과 롤백 기능.
    public CartPriceResponse deleteCart(final CartManipulationRequest request, final User user) {
        final Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new CartEmptyException("장바구니가 비어있습니다"));
        cartRepository.delete(cart);

        final CartPricePerSellerResponse cartPricePerSellerResponse = cartPricePerSeller(user, cart.getItem().getSeller());
        final CartPriceTotalResponse cartPriceTotalResponse = cartPriceTotal(user);

        return CartPriceResponse.builder()
                .cartPricePerSellerResponse(cartPricePerSellerResponse)
                .cartPriceTotalResponse(cartPriceTotalResponse)
                .build();
    }

    public Integer getTotalQuantity(final User user) {
        List<Cart> carts = cartRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다"));
        Integer totalQuantity = 0;
        for (Cart cart : carts) {
            totalQuantity += cart.getQuantity();
        }
        return totalQuantity;
    }

    public Integer shippingFeeCheck(Long sellerId, Long itemTotalPricePerSeller) {
        final Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("판매자가 존재하지 않습니다"));

        //무료 배송 정책이 있으며 구매 가격이 그 이상일 때 무료
        if (seller.getShippingFeeFreePolicy() != null && itemTotalPricePerSeller >= seller.getShippingFeeFreePolicy()) {
            return 0;
        } else {
            return seller.getShippingFeeDefault();
        }
    }
}
