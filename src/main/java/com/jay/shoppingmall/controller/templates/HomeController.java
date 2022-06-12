package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping(
            value = "/",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public String write(Model model, @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @CurrentUser User user) {
        PageDto responses = itemService.itemAll(user, pageable);
        model.addAttribute("items", responses);

//        System.out.println("------------------------------------------------------------------------------------");
//        System.out.println("responses.getCustomPage().getSize() = " + responses.getCustomPage().getSize());
//        System.out.println("responses.getCustomPage().getTotalElements() = " + responses.getCustomPage().getTotalElements());
//        System.out.println("responses.getCustomPage().getOffset() = " + responses.getCustomPage().getOffset());
//        System.out.println("responses.getCustomPage().getTotalPages() = " + responses.getCustomPage().getTotalPages());
//        System.out.println("responses.getCustomPage().getNumber() = " + responses.getCustomPage().getNumber());
//        System.out.println("------------------------------------------------------------------------------------");
        
        return "home";
    }
    @GetMapping("/index")
    public String index() {
        return "/chat/index";
    }
}
