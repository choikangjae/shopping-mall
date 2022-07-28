package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.CartManipulationRequest;
import com.jay.shoppingmall.dto.response.cart.CartPriceResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceTotalResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/cart")
@Api(tags = "cart")
public class CartApiController {

    private final CartService cartService;

    @ApiOperation(value = "장바구니 전체 상품 개수 조회", notes = "유저가 로그인 상태라면 장바구니 상품 개수를 리턴하고 비로그인 상태라면 0을 반환합니다.")
    @PostMapping("/total")
    public ResponseEntity<?> showCartTotal(@CurrentUser User user) {
        Integer totalQuantity = 0;
        if (user == null) {
            return ResponseEntity.ok(totalQuantity);
        }
        totalQuantity = cartService.getTotalQuantity(user);

        return ResponseEntity.ok(totalQuantity);
    }

    //Session으로 구현하여 GET 요청으로 사용.
    @ApiOperation(value = "선택된 상품들 장바구니에 추가", notes = "해당 상품에 옵션이 존재한다면 옵션을 반드시 선택하게 하고 이미 해당 옵션이 존재한다면 예외 처리")
    @GetMapping("/add")
    public ResponseEntity<?> addToCart(
            HttpSession session,
            @CurrentUser User user) {

        @SuppressWarnings("unchecked")
        final List<ItemOptionResponse> itemOptions = (List<ItemOptionResponse>) session.getAttribute("itemOptions");
        if (itemOptions == null) {
            throw new ItemNotFoundException("옵션을 선택해주세요");
        }
        cartService.addOptionItemsToCart(itemOptions, user);

        return ResponseEntity.ok().body(null);
    }
    @ApiOperation(value = "장바구니 상품 제거")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteCart(@Valid @RequestBody CartManipulationRequest request, @CurrentUser User user) {

        CartPriceResponse cartPriceResponse = cartService.deleteCart(request, user);

        return ResponseEntity.ok().body(cartPriceResponse);
    }
    @ApiOperation(value = "장바구니 상품 상태 변경", notes = "상품의 선택 여부와 개수를 받고 해당 상품 가격의 합, 해당 셀러 상품 가격의 합, 전체 상품 가격의 합을 반환합니다.")
    @PostMapping("/item/price")
    public ResponseEntity<?> cartManipulation(@Valid @RequestBody CartManipulationRequest request, @CurrentUser User user) {
        final CartPriceResponse cartPriceResponse = cartService.changeQuantity(request, user);

        return ResponseEntity.ok(cartPriceResponse);
    }
    @ApiOperation(value = "장바구니 가격 총합 반환", notes = "")
    @GetMapping("/item/total")
    public ResponseEntity<?> getCartTotals(@CurrentUser User user) {
        final CartPriceTotalResponse cartPriceTotalResponse = cartService.cartPriceTotal(user);

        return ResponseEntity.ok(cartPriceTotalResponse);
    }
    @ApiOperation(value = "장바구니 전체 상품 선택 토글", notes = "true를 받으면 모든 상품을 선택하고, false를 받으면 모든 상품 선택을 해제합니다. 현재 선택된 상품들과는 상관없습니다.")
    @GetMapping("/select/{boolean}")
    public ResponseEntity<?> getCartSelects(@PathVariable("boolean") String check, @CurrentUser User user) {
        final CartPriceResponse cartPriceResponse = cartService.cartSelectAll(check, user);

        return ResponseEntity.ok(cartPriceResponse);
    }
}
