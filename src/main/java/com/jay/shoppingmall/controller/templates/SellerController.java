package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.dto.response.order.payment.RecentPaymentPerSellerResponse;
import com.jay.shoppingmall.dto.response.seller.SellerDefaultSettingsResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.item.ItemTemporaryResponse;
import com.jay.shoppingmall.service.ItemService;
import com.jay.shoppingmall.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/seller")
@Controller
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final SellerService sellerService;
    private final ItemService itemService;

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
    public String sellerWrite(WriteItemRequest writeItemRequest, Model model, @CurrentUser User user, RedirectAttributes redirectAttributes) {
        if (!sellerService.sellerDefaultSettingCheck(user)) {
            redirectAttributes.addFlashAttribute("message", "판매자 기본 설정부터 작성해주세요");

            return "redirect:/seller/settings";
        }
            List<ItemTemporaryResponse> itemTemporaries = sellerService.retrieveItemTemporaries(user);
            model.addAttribute("itemTemporaries", itemTemporaries);

        return "seller/seller-write-item";
    }
    @PostMapping("/write")
    public String sellerWriteAction(@Valid WriteItemRequest writeItemRequest,
                                   BindingResult result,
                                   @RequestParam("mainImage") MultipartFile file,
                                   @RequestParam(value = "descriptionImage", required = false) List<MultipartFile> files,
                                   @CurrentUser User user) {
        if (writeItemRequest.getDescription().length() > 200) {
            result.rejectValue("description", "EXCEED_LIMIT","설명은 200글자까지만 가능합니다");
            return "/seller/seller-write-item";
        }
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
    @GetMapping("/settings")
    public String sellerDefaultSettings(@CurrentUser User user, Model model) {
        final SellerDefaultSettingsResponse sellerDefaultSettingsResponse = sellerService.sellerDefaultSettings(user);
        model.addAttribute("response", sellerDefaultSettingsResponse);

        return "/seller/settings";
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
//        Page<ItemResponse> itemResponses = sellerService.showItemsBySeller(user, pageable);
        final PageDto itemResponses = itemService.showItemsBySeller(user, pageable);

        model.addAttribute("items", itemResponses);

        return "seller/items";
    }
    @PostMapping("/delete/item")
    public String deleteItem(@CurrentUser User user, @RequestParam("deleteItem") Long itemId) {
        sellerService.itemDelete(user, itemId);

        return "redirect:/seller/items";
    }
    @PostMapping("/update/item")
    public String updateItem(@CurrentUser User user, @RequestParam("id") Long itemId) {
//        sellerService.itemDelete(user, itemId);

        return "redirect:/seller/items";
    }
    @GetMapping("/recent-orders")
    public String getSellerRecentOrders(@CurrentUser User user, Pageable pageable, Model model) {
        final List<RecentPaymentPerSellerResponse> recentOrders = sellerService.getSellerRecentOrders(user, pageable);

        model.addAttribute("recentOrders", recentOrders);
        return "seller/seller-recent-orders";
    }
    @GetMapping("/order/{id}")
    public String showSellerOrderDetail(@PathVariable("id") Long orderId, Model model, @CurrentUser User user) {
        final OrderDetailResponse orderDetailResponse = sellerService.treatOrders(orderId, user);

        model.addAttribute("orderDetailResponse", orderDetailResponse);
        return "/seller/order-detail";
    }

}

