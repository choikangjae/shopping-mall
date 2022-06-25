package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
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
import com.jay.shoppingmall.dto.response.cart.CartPricePerSellerResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceTotalResponse;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public Map<SellerResponse, List<ItemAndQuantityResponse>> showCartItemsList(User user) {

        //유저를 기준으로 장바구니 물품 가져오기
        final List<Cart> carts = cartRepository.findByUser(user);

        Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = new ConcurrentHashMap<>();
        final List<Seller> sellers = carts.stream().map(Cart::getItem).map(Item::getSeller).distinct().collect(Collectors.toList());

        //판매자를 기준으로 상품 정렬
        for (Seller seller : sellers) {
            this.cartPricePerSeller(user, seller);
            final List<Cart> cartList = carts.stream().filter(cart -> cart.getItem().getSeller().equals(seller)).collect(Collectors.toList());

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

            long itemTotalPricePerSeller = 0;
            int itemTotalQuantityPerSeller = 0;

            //Value List<ItemAndQuantityResponse> 생성
            for (Cart cart : cartList) {
                if (cart.getIsSelected()) {
                    itemTotalPricePerSeller += cart.getPriceNow() * cart.getQuantity();
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
//                        .image(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, cart.getItem().getId())))
                        .zzim(cart.getItem().getZzim())
                        .quantity(cart.getQuantity())
                        .isSelected(cart.getIsSelected())
                        .build());
            }
            setImage(cartList, itemAndQuantityResponses);
            //배송비 무료 여부 및 총 가격
            sellerResponse.setShippingFee(shippingFeeCheck(seller, itemTotalPricePerSeller));
            sellerResponse.updateTotal(itemTotalPricePerSeller, itemTotalQuantityPerSeller);

            sellerResponseListMap.put(sellerResponse, itemAndQuantityResponses);
        }
        return sellerResponseListMap;
    }

    private void setImage(final List<Cart> cartList, final List<ItemAndQuantityResponse> itemAndQuantityResponses) {
        final List<Long> itemIds = cartList.stream().map(Cart::getItem).map(Item::getId).collect(Collectors.toList());
        final List<Image> images = imageRepository.findByImageRelationAndForeignIdIn(ImageRelation.ITEM_MAIN, itemIds);

        for (ItemAndQuantityResponse itemAndQuantityResponse : itemAndQuantityResponses) {
            for (Image image : images) {
                final String stringImage = fileHandler.getStringImage(image);
                if (itemAndQuantityResponse.getItemId().equals(image.getForeignId())) {
                    itemAndQuantityResponse.setImage(stringImage);
                    break;
                }
            }
        }
    }

    public void addOptionItemsToCart(final List<ItemOptionResponse> itemOptions, final User user) {
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

    public CartPriceTotalResponse cartPriceTotal(User user) {

        final List<CartPricePerSellerResponse> cartPricePerSellerResponses = cartPricePerSeller(user);

        long cartTotalPrice = cartPricePerSellerResponses.stream().mapToLong(CartPricePerSellerResponse::getItemTotalPricePerSeller).sum();
        int cartTotalQuantity = cartPricePerSellerResponses.stream().mapToInt(CartPricePerSellerResponse::getItemTotalQuantityPerSeller).sum();
        int cartTotalShippingFee = cartPricePerSellerResponses.stream().mapToInt(CartPricePerSellerResponse::getItemShippingFeePerSeller).sum();

        return CartPriceTotalResponse.builder()
                .cartTotalPrice(cartTotalPrice)
                .cartTotalQuantity(cartTotalQuantity)
                .cartTotalShippingFee(cartTotalShippingFee)
                .build();
    }
    public CartPriceTotalResponse cartPriceTotal(List<CartPricePerSellerResponse> cartPricePerSellerResponses) {

        long cartTotalPrice = cartPricePerSellerResponses.stream().mapToLong(CartPricePerSellerResponse::getItemTotalPricePerSeller).sum();
        int cartTotalQuantity = cartPricePerSellerResponses.stream().mapToInt(CartPricePerSellerResponse::getItemTotalQuantityPerSeller).sum();
        int cartTotalShippingFee = cartPricePerSellerResponses.stream().mapToInt(CartPricePerSellerResponse::getItemShippingFeePerSeller).sum();

        return CartPriceTotalResponse.builder()
                .cartTotalPrice(cartTotalPrice)
                .cartTotalQuantity(cartTotalQuantity)
                .cartTotalShippingFee(cartTotalShippingFee)
                .build();
    }

    //판매자별 가격, 개수, 배송비
    public CartPricePerSellerResponse cartPricePerSeller(User user, Seller seller) {
        final List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user);

        final List<Cart> cartList = carts.stream().filter(cart -> cart.getItemSeller().equals(seller)).collect(Collectors.toList());
        long cartTotalPricePerSeller = 0;
        int cartTotalQuantityPerSeller = 0;

        for (Cart cart : cartList) {
            if (cart.getIsSelected()) {
                cartTotalPricePerSeller += cart.getPriceNow() * cart.getQuantity();
                cartTotalQuantityPerSeller += cart.getQuantity();
            }
        }
        return CartPricePerSellerResponse.builder()
                .sellerId(seller.getId())
                .itemTotalPricePerSeller(cartTotalPricePerSeller)
                .itemTotalQuantityPerSeller(cartTotalQuantityPerSeller)
                .itemShippingFeePerSeller(shippingFeeCheck(seller, cartTotalPricePerSeller))
                .build();
    }

    public List<CartPricePerSellerResponse> cartPricePerSeller(User user) {
        List<CartPricePerSellerResponse> cartPricePerSellerResponses = new ArrayList<>();
        final List<Cart> carts = cartRepository.findByUser(user);

        final List<Seller> sellers = carts.stream().map(Cart::getItem).map(Item::getSeller).distinct().collect(Collectors.toList());
        for (Seller seller : sellers) {
            long cartTotalPricePerSeller = 0;
            int cartTotalQuantityPerSeller = 0;

            final List<Cart> cartsPerSeller = carts.stream().filter(cart -> cart.getItemSeller().equals(seller)).collect(Collectors.toList());

            for (Cart cart : cartsPerSeller) {
                if (!cart.getIsSelected())
                    continue;

                cartTotalPricePerSeller += cart.getPriceNow() * cart.getQuantity();
                cartTotalQuantityPerSeller += cart.getQuantity();
            }

            final Integer shippingFee = shippingFeeCheck(seller, cartTotalPricePerSeller);

            final CartPricePerSellerResponse cartPricePerSellerResponse = CartPricePerSellerResponse.builder()
                    .sellerId(seller.getId())
                    .itemTotalPricePerSeller(cartTotalPricePerSeller)
                    .itemTotalQuantityPerSeller(cartTotalQuantityPerSeller)
                    .itemShippingFeePerSeller(shippingFee)
                    .build();

            cartPricePerSellerResponses.add(cartPricePerSellerResponse);
        }
        return cartPricePerSellerResponses;
    }

    public CartPriceResponse changeQuantity(final CartManipulationRequest request, final User user) {
        final Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new CartEmptyException("장바구니가 비어있습니다"));
        cart.setIsSelected(request.getIsSelected());
        cart.manipulateQuantity(request.getCartQuantity());

        final long cartManipulatedPrice = cart.manipulateQuantity(request.getCartQuantity()) * cart.getPriceNow();

        final CartPricePerSellerResponse cartPricePerSellerResponse = cartPricePerSeller(user, cart.getItemSeller());
        final CartPriceTotalResponse cartPriceTotalResponse = cartPriceTotal(user);

        return CartPriceResponse.builder()
                .cartManipulatedPrice(cartManipulatedPrice)
                .cartPricePerSellerResponse(cartPricePerSellerResponse)
                .cartPriceTotalResponse(cartPriceTotalResponse)
                .build();
    }
    public CartPriceResponse cartSelectAll(final String check, final User user) {
        if (!check.equals("false") && !check.equals("true")) {
            throw new NotValidException("잘못된 요청입니다");
        }
        List<Cart> carts = cartRepository.findByUser(user);

        if (check.equals("false")) {
            for (Cart cart : carts) {
                cart.setIsSelected(false);
            }
        } else {
            for (Cart cart : carts) {
                cart.setIsSelected(true);
            }
        }
        final List<CartPricePerSellerResponse> cartPricePerSellerResponses = cartPricePerSeller(user);
        final CartPriceTotalResponse cartPriceTotalResponse = cartPriceTotal(cartPricePerSellerResponses);

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
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException("잘못된 접근입니다");
        }
        cartRepository.delete(cart);

        final CartPricePerSellerResponse cartPricePerSellerResponse = this.cartPricePerSeller(user, cart.getItem().getSeller());
        final CartPriceTotalResponse cartPriceTotalResponse = this.cartPriceTotal(user);

        return CartPriceResponse.builder()
                .cartPricePerSellerResponse(cartPricePerSellerResponse)
                .cartPriceTotalResponse(cartPriceTotalResponse)
                .build();
    }

    public Integer getTotalQuantity(final User user) {
        List<Cart> carts = cartRepository.findByUser(user);

        Integer totalQuantity = 0;
        for (Cart cart : carts) {
            totalQuantity += cart.getQuantity();
        }
        return totalQuantity;
    }

    public Integer shippingFeeCheck(Seller seller, Long itemTotalPricePerSeller) {
        //무료 배송 정책이 있으며 구매 가격이 그 이상일 때 무료
        if (seller.getShippingFeeFreePolicy() != null && itemTotalPricePerSeller >= seller.getShippingFeeFreePolicy()) {
            return 0;
        } else {
            return seller.getShippingFeeDefault();
        }
    }
}
