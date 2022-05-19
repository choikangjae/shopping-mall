package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.dto.ItemDetailResponse;
import com.jay.shoppingmall.dto.ItemResponse;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/details/{id}")
    public String searchById(@PathVariable Long id, Model model) {
        ItemDetailResponse response = itemService.itemDetail(id);
        model.addAttribute("response", response);

        return "item/detail";
    }
}
