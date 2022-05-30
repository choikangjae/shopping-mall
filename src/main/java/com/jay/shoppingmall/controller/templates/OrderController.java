package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.cart.CartOrderResponse;
import com.jay.shoppingmall.dto.response.OrderResultResponse;
import com.jay.shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/order")
@Controller
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/now")
    public String orderNow(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/auth/login";
        }
        CartOrderResponse cartOrderResponse = orderService.orderProcess(user);

        model.addAttribute("user", user);
        model.addAttribute("order", cartOrderResponse);

        return "/order/now";
    }
    @GetMapping("/process")
    public String orderProcess(@CurrentUser User user, Model model) {
        CartOrderResponse cartOrderResponse = null;
        try {
            cartOrderResponse = orderService.orderProcess(user);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart";
        }

        model.addAttribute("user", user);
        model.addAttribute("order", cartOrderResponse);

        return "/order/process";
    }
    @GetMapping("/payment")
    public String orderPayment(@CurrentUser User user, Model model) {
        CartOrderResponse cartOrderResponse = orderService.orderProcess(user);

        model.addAttribute("user", user);
        model.addAttribute("order", cartOrderResponse);

        return "/order/payment";
    }
    //payment정보.상품정보.받는사람정보. payment로 돈을 받았다고치고 카트에서 제거하고 상품을 불러와서 재고 깎고
    @PostMapping("/payment")
    public String paymentAction(@Valid PaymentRequest paymentRequest, @CurrentUser User user, RedirectAttributes redirectAttributes) {
        OrderResultResponse response = orderService.doOrderPaymentProcess(paymentRequest, user);

        redirectAttributes.addFlashAttribute("response", response);

        return "redirect:/order/payment-result";
    }

    @GetMapping("/payment-result")
    public String paymentResult(@ModelAttribute OrderResultResponse response, Model model) {
        model.addAttribute("response", response);
        //TODO 결제 진행창과 결제 완료창 템플릿 만들기.
        //TODO payment.html과 payment-result.html. Seller 만들고 나서.

        return "/order/payment-result";
    }
}
