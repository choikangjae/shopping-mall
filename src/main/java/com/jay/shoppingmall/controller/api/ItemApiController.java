package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ItemOptionRequest;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.exception.exceptions.OptionDuplicatedException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/item")
@PreAuthorize("hasRole('USER')")
@Api(tags = "item")
public class ItemApiController {

    private final ItemService itemService;

    @ApiOperation(value = "관심 상품 목록에 추가", notes = "")
    @PostMapping("/zzim")
    public ResponseEntity<?> itemZzim(@Valid @RequestBody ItemZzimRequest request, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("로그인이 필요한 서비스입니다");
        }

        ZzimResponse zzimResponse = itemService.itemZzim(request, user);
        return ResponseEntity.ok(zzimResponse);
    }
    @ApiOperation(value = "옵션 선택 상품 세션에 추가", notes = "상품에서 옵션을 고르면 Session에 해당 옵션들이 List 구조로 저장됩니다.")
    @PostMapping("/option/add")
    public ResponseEntity<?> itemOptionCheckAndAdd(@RequestBody ItemOptionRequest request, @CurrentUser User user, HttpSession session) {
        if (user == null) {
            throw new UserNotFoundException("로그인이 필요한 서비스입니다");
        }
        final ItemOptionResponse itemOptionResponse = itemService.itemOptionAddToList(request);

        if (session.getAttribute("itemOptions") == null) {
            List<ItemOptionResponse> itemOptionResponses = new ArrayList<>();
            itemOptionResponses.add(itemOptionResponse);
            session.setAttribute("itemOptions", itemOptionResponses);
        } else {
            @SuppressWarnings("unchecked")
            final List<ItemOptionResponse> itemOptions = (List<ItemOptionResponse>) session.getAttribute("itemOptions");
            for (ItemOptionResponse optionResponse : itemOptions) {
                if (optionResponse.getItemId().equals(itemOptionResponse.getItemId()) && optionResponse.getOption2().equals(itemOptionResponse.getOption2()) && optionResponse.getOption1().equals(itemOptionResponse.getOption1())) {
                    throw new OptionDuplicatedException("같은 옵션이 선택되어 있습니다");
                }
            }
            itemOptions.add(itemOptionResponse);
        }
        setItemTotals(session);

        return ResponseEntity.ok(itemOptionResponse);
    }

    @ApiOperation(value = "옵션 선택 상품 세션에서 제거", notes = "")
    @PostMapping("/option/delete")
    public ResponseEntity<?> deleteOption(@Valid @RequestBody ItemOptionRequest request, HttpSession session) {
        final Long itemId = request.getItemId();
        final String option1 = request.getOption1();
        final String option2 = request.getOption2();

        @SuppressWarnings("unchecked")
        final List<ItemOptionResponse> itemOptions = (List<ItemOptionResponse>) session.getAttribute("itemOptions");
        if (!itemOptions.isEmpty()) {
            itemOptions.removeIf(itemOptionResponse -> itemId.equals(itemOptionResponse.getItemId()) && option1.equals(itemOptionResponse.getOption1()) && option2.equals(itemOptionResponse.getOption2()));
        }
        setItemTotals(session);

        session.setAttribute("itemOptions", itemOptions);
        return ResponseEntity.ok(null);
    }
    @ApiOperation(value = "세션의 옵션 선택 상품 변경", notes = "")
    @PostMapping("/option/update")
    public ResponseEntity<?> updateOption(@Valid @RequestBody ItemOptionRequest request, HttpSession session) {

        @SuppressWarnings("unchecked")
        final List<ItemOptionResponse> itemOptions = (List<ItemOptionResponse>) session.getAttribute("itemOptions");
        final ItemOptionResponse itemOptionResponse = itemService.itemOptionUpdate(request);

        if (!itemOptions.isEmpty()) {
            for (ItemOptionResponse optionResponse : itemOptions) {
                if (optionResponse.getOption2().equals(request.getOption2()) && optionResponse.getOption1().equals(request.getOption1())) {

                    optionResponse.setItemPrice(itemOptionResponse.getItemPrice());
                    optionResponse.setItemQuantity(itemOptionResponse.getItemQuantity());
                }
            }
        }
        setItemTotals(session);
        session.setAttribute("itemOptions", itemOptions);
        
        return ResponseEntity.ok(itemOptionResponse);
    }
    @ApiOperation(value = "현재 세션의 상품 총합", notes = "세션에 있는 상품 가격의 총합과 개수를 반환합니다.")
    @GetMapping("/option/totals")
    public ResponseEntity<?> returnOptionTotals(HttpSession session) {
        final ItemOptionResponse itemTotalResponse = ItemOptionResponse.builder()
                .itemTotalPrice((Long) session.getAttribute("itemTotalPrice"))
                .itemTotalQuantity((Integer) session.getAttribute("itemTotalQuantity"))
                .build();

        return ResponseEntity.ok(itemTotalResponse);
    }

    private void setItemTotals(final HttpSession session) {
        @SuppressWarnings("unchecked")
        final List<ItemOptionResponse> itemOptions = (List<ItemOptionResponse>) session.getAttribute("itemOptions");

        final long itemTotalPrice = itemOptions.stream().mapToLong(ItemOptionResponse::getItemPrice).sum();
        final int itemTotalQuantity = itemOptions.stream().mapToInt(ItemOptionResponse::getItemQuantity).sum();
        session.setAttribute("itemTotalPrice", itemTotalPrice);
        session.setAttribute("itemTotalQuantity", itemTotalQuantity);
    }

}
