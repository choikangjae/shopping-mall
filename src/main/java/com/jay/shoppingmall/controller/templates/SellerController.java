package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.item.temporary.ItemTemporary;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.item.ItemTemporaryResponse;
import com.jay.shoppingmall.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/seller")
@Controller
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final SellerService sellerService;
    @GetMapping
    public String sellerHome() {
        return "seller/seller-home";
    }
    @GetMapping("/start")
    public String sellerStart(@CurrentUser User user) {
        if (user.getRole().equals(Role.ROLE_SELLER)) {
            return "redirect:/seller";
        }
        return "seller/seller-start";
    }
    @GetMapping("/write")
    public String sellerWrite(WriteItemRequest writeItemRequest, Model model, @CurrentUser User user) {
        if (user != null) {
            List<ItemTemporaryResponse> itemTemporaries = sellerService.retrieveItemTemporaries(user);
            model.addAttribute("itemTemporaries", itemTemporaries);
        }

        return "seller/seller-write-item";
    }
    @PostMapping("/write")
    public String sellerWriteAction(@Valid WriteItemRequest writeItemRequest,
                                   BindingResult result,
                                   @RequestParam("mainImage") MultipartFile file,
                                   @RequestParam(value = "descriptionImage", required = false) List<MultipartFile> files,
                                   @CurrentUser User user) {
        if (file.isEmpty()) {
            result.rejectValue("mainImage", "MAIN_NOT_EXISTS","대표 사진을 첨부해주세요");
            return "/seller/seller-write-item";
        }
        if ((file.getSize() / (1024 * 1024)) >= 5) {
            result.rejectValue("mainImage", "EXCEED_MAXIMUM_SIZE", "대표 사진 용량은 5MB를 넘을 수 없습니다");
            return "/seller/seller-write-item";
        }
        if (result.hasErrors()) {
            return "/seller/seller-write-item";
        }
        //MultiPartFile이 들어오지 않으면 " " 공백 한칸만 들어온다.
        sellerService.writeItem(writeItemRequest, file, files, user);
        return "redirect:/";
    }
    @PostMapping("/temporary-save")
    public String sellerItemTemporarySave(WriteItemRequest writeItemRequest, @CurrentUser User user) {

        sellerService.temporarySave(writeItemRequest, user);

        return "redirect:/";
    }

    @GetMapping("/agree")
    public String sellerAgree(@CurrentUser User user, Model model) {
        if (user.getRole().equals(Role.ROLE_SELLER)) {
            return "redirect:/seller";
        }
        model.addAttribute("user", user);
        return "seller/seller-agreement";
    }
    @GetMapping("/items")
    public String sellerItems(@CurrentUser User user, Model model, Pageable pageable) {
        Page<ItemResponse> itemResponses = sellerService.showItemsBySeller(user, pageable);

        model.addAttribute("items", itemResponses);

        return "seller/items";
    }
}

