package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.request.ReceiverInfoTemporarySave;
import com.jay.shoppingmall.dto.response.order.OrderResultResponse;
import com.jay.shoppingmall.dto.response.cart.CartPriceTotalResponse;
import com.jay.shoppingmall.dto.response.item.ItemAndQuantityResponse;
import com.jay.shoppingmall.dto.response.seller.SellerResponse;
import com.jay.shoppingmall.service.CartService;
import com.jay.shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/order")
@Controller
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping("/now")
    public String orderNow(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/auth/login";
        }
        final Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = orderService.orderProcess(user);

        model.addAttribute("user", user);
        model.addAttribute("sellerResponseListMap", sellerResponseListMap);

        return "/order/now";
    }

    @GetMapping("/process")
    public String orderProcess(@CurrentUser User user, Model model, RedirectAttributes redirectAttributes) {
        if (user.getAddress().getAddress() == null || user.getPhoneNumber().getMiddle() == null || !user.getAgree().getIsMandatoryAgree()) {
            redirectAttributes.addFlashAttribute("message", "개인정보를 입력해주세요");
            return "redirect:/cart";
        }

        final Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = orderService.orderProcess(user);
        if (sellerResponseListMap.isEmpty()) {
            return "redirect:/cart";
        }

        final CartPriceTotalResponse cartPriceTotalResponse = cartService.cartPriceTotal(user);

        model.addAttribute("user", user);
        model.addAttribute("sellerResponseListMap", sellerResponseListMap);
        model.addAttribute("cartPriceTotalResponse", cartPriceTotalResponse);

        return "/order/process";
    }

    @PostMapping("/process")
    public String receiverInfoFetch(ReceiverInfoTemporarySave receiverInfoTemporarySave, RedirectAttributes redirectAttributes) {
        String fullAddress = "";
        if (receiverInfoTemporarySave != null) {
            if (receiverInfoTemporarySave.getExtraAddress() == null) {
                fullAddress = String.format("%s %s", receiverInfoTemporarySave.getAddress(), receiverInfoTemporarySave.getDetailAddress());
            }
            fullAddress = String.format("%s %s %s", receiverInfoTemporarySave.getAddress(), receiverInfoTemporarySave.getDetailAddress(), receiverInfoTemporarySave.getExtraAddress());
        }
        redirectAttributes.addFlashAttribute("fullAddress", fullAddress);
        redirectAttributes.addFlashAttribute("info", receiverInfoTemporarySave);

        return "redirect:/order/payment";
    }

    @GetMapping("/payment")
    public String orderPayment(@CurrentUser User user, Model model) {
        final Map<SellerResponse, List<ItemAndQuantityResponse>> sellerResponseListMap = orderService.orderProcess(user);
        final CartPriceTotalResponse cartPriceTotalResponse = cartService.cartPriceTotal(user);

        model.addAttribute("user", user);
        model.addAttribute("sellerResponseListMap", sellerResponseListMap);
        model.addAttribute("cartPriceTotalResponse", cartPriceTotalResponse);


        return "/order/payment";
    }

    @PostMapping("/payment")
    public String paymentAction(@Valid PaymentRequest paymentRequest, @CurrentUser User user, RedirectAttributes redirectAttributes) {
//        OrderResultResponse response = orderService.doOrderPaymentProcess(paymentRequest, user);

        //redirect 작동하지 않음. 다른 방법 생각해볼 것.
//        redirectAttributes.addFlashAttribute("status", "success");
        return "redirect:/order/payment-result";
    }

    @GetMapping("/payment-result")
    public String paymentResult(@ModelAttribute OrderResultResponse response, Model model) {

//        if (model.getAttribute("status") == null) {
//            return "redirect:/";
//        }
        model.addAttribute("response", response);

        return "/order/payment-result";
    }
}
