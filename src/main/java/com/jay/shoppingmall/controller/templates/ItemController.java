package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.dto.request.CartRequest;
import com.jay.shoppingmall.dto.response.ItemDetailResponse;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(
            value = "/item/details/{id}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public String searchById(@PathVariable(name = "id",required = false) Long id, Model model) {
        ItemDetailResponse response = itemService.itemDetail(id);
        model.addAttribute("response", response);

        return "item/detail";
    }
}
