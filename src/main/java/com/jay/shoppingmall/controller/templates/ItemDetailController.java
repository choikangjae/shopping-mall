package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.ItemDetailResponse;
import com.jay.shoppingmall.dto.response.QnaResponse;
import com.jay.shoppingmall.dto.response.QnaResponseWithPagination;
import com.jay.shoppingmall.service.ItemService;
import com.jay.shoppingmall.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemDetailController {

    private final ItemService itemService;
    private final QnaService qnaService;

    @GetMapping(
            value = "/item/details/{id}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public String itemDetailByItemId(@PathVariable(name = "id",required = false) Long id,
                                     @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                     Model model, @CurrentUser User user) {

        ItemDetailResponse response = itemService.itemDetail(user, id);
        QnaResponseWithPagination qnaResponseWithPagination = qnaService.getQnaListByPaging(id, user, pageable);

        model.addAttribute("response", response);
        model.addAttribute("qnaResponseWithPagination", qnaResponseWithPagination);
        model.addAttribute("user", user);
        return "item/detail";
    }
}
