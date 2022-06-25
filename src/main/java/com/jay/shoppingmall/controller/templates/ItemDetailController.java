package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.item.ItemDetailResponse;
import com.jay.shoppingmall.service.ItemService;
import com.jay.shoppingmall.service.QnaService;
import com.jay.shoppingmall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class ItemDetailController {

    private final ItemService itemService;
    private final QnaService qnaService;
    private final ReviewService reviewService;

    @GetMapping(
            value = "/item/details/{id}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public String itemDetailByItemId(@PathVariable(name = "id",required = false) Long itemId,
                                     @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                                     Model model, @CurrentUser User user, HttpServletRequest request) {
        String targetPage = "qna";

        ItemDetailResponse response = itemService.getItemDetail(user, itemId);
        final PageDto sellerOtherItems = itemService.getSellerOtherItems(itemId);
        PageDto itemQnas = qnaService.getQnas(itemId, targetPage, user, pageable);
        final PageDto itemReviews = reviewService.getItemReviews(itemId, pageable);

        model.addAttribute("itemReviews", itemReviews);
        model.addAttribute("response", response);
        model.addAttribute("items", sellerOtherItems);
        model.addAttribute("itemQnas", itemQnas);
        model.addAttribute("user", user);
        return "item/detail";
    }
}
