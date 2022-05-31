package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/search")
@Controller
public class SearchController {

    private final ItemService itemService;

    @GetMapping
    public String searchByKeyword(@RequestParam(value = "q", required = false) String keyword, Model model, Pageable pageable) {
        if (keyword == null || keyword.equals("")) {
            return "redirect:/";
        }
        final Page<ItemResponse> itemResponses = itemService.searchItemsByKeyword(keyword, pageable);
        model.addAttribute("items", itemResponses);
        model.addAttribute("result", keyword);
        return "/item/search-result";
    }

}
