package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping(
            value = "/",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public String write(Model model, @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @CurrentUser User user) {
        PageDto responses = itemService.getAllItems(user, pageable);
        model.addAttribute("items", responses);

        return "home";
    }
    @GetMapping("/index")
    public String index() {
        return "chat/index";
    }
}
