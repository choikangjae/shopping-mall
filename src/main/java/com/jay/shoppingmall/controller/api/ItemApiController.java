package com.jay.shoppingmall.controller.api;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ItemOptionRequest;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.exception.exceptions.OptionDuplicatedException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/item")
public class ItemApiController {

    private final ItemService itemService;

    @PostMapping("/zzim")
    public ResponseEntity<?> itemZzim(@Valid @RequestBody ItemZzimRequest request, @CurrentUser User user) {
        if (user == null) {
            throw new UserNotFoundException("로그인이 필요한 서비스입니다");
        }

        ZzimResponse zzimResponse = itemService.itemZzim(request, user);
        return ResponseEntity.ok(zzimResponse);
    }
    @PostMapping("/option")
    public ResponseEntity<?> itemOptionCheck(@Valid @RequestBody ItemOptionRequest request, @CurrentUser User user, HttpSession session) {
        if (user == null) {
            throw new UserNotFoundException("로그인이 필요한 서비스입니다");
        }
        final ItemOptionResponse itemOptionResponse = itemService.itemOptionChange(request, user);

        if (session.getAttribute("responses") == null) {
            List<ItemOptionResponse> itemOptionResponses = new ArrayList<>();
            itemOptionResponses.add(itemOptionResponse);
            session.setAttribute("responses", itemOptionResponses);
        } else {
            final List<ItemOptionResponse> responses = (List<ItemOptionResponse>) session.getAttribute("responses");
            for (ItemOptionResponse optionResponse : responses) {
                if (optionResponse.getOption2().equals(itemOptionResponse.getOption2()) && optionResponse.getOption1().equals(itemOptionResponse.getOption1())) {
                    throw new OptionDuplicatedException("같은 옵션이 선택되어 있습니다");
                }
            }
            responses.add(itemOptionResponse);
        }
        
        final List<ItemOptionResponse> responses = (List<ItemOptionResponse>) session.getAttribute("responses");
        for (ItemOptionResponse optionResponse : responses) {
        }
            return ResponseEntity.ok(itemOptionResponse);
    }
    @PostMapping("/option/delete")
    public ResponseEntity<?> deleteOption(@Valid @RequestBody ItemOptionRequest request, HttpSession session) {
        final String option1 = request.getOption1();
        final String option2 = request.getOption2();

        final List<ItemOptionResponse> responses = (List<ItemOptionResponse>) session.getAttribute("responses");
        if (!responses.isEmpty()) {
            responses.removeIf(itemOptionResponse -> option1.equals(itemOptionResponse.getOption1()) && option2.equals(itemOptionResponse.getOption2()));
        }
        session.setAttribute("responses", responses);
        return ResponseEntity.ok(null);
    }

}
