package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.DeleteMeRequest;
import com.jay.shoppingmall.dto.request.PasswordRequest;
import com.jay.shoppingmall.dto.response.SimpleOrderResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.service.MeService;
import com.jay.shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/me")
@Controller
@PreAuthorize("hasRole('USER')")
public class MeController {

    private final MeService meService;
    private final OrderService orderService;

    @GetMapping
    public String me(@CurrentUser User user, Model model, Pageable pageable) {

        List<SimpleOrderResponse> simpleOrderResponses = orderService.showOrders(user, pageable);
        if (!simpleOrderResponses.isEmpty()) {
            model.addAttribute("orders", simpleOrderResponses);
        }

        model.addAttribute("user", user);
        return "me/home";
    }

    @PostMapping("/privacy")
    public String showPersonalInformationAgreementForm(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "me/agreement";
    }

    @GetMapping("/update")
    public String meUpdate(@CurrentUser User user, Model model) {
        model.addAttribute("user", user);
        return "me/update";
    }

    @GetMapping("/reconfirm")
    public String reConfirm(PasswordRequest passwordRequest, @CurrentUser User user, Model model) {
        model.addAttribute("user", user);
        return "me/reconfirm";
    }

    @PostMapping("/reconfirm")
    public String reConfirmAction(@Valid PasswordRequest passwordRequest, BindingResult result, @CurrentUser User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        if (passwordRequest.getPassword() == null) {
            result.rejectValue("password", "NO_PASSWORD_FOUND", "비밀번호를 입력해주세요.");
        }
        if (!meService.passwordCheck(passwordRequest.getPassword(), user)) {
            result.rejectValue("password", "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "me/reconfirm";
        }
        request.getSession().setAttribute("password", passwordRequest.getPassword());
        return "redirect:/me/update";
    }
    @GetMapping("/zzim")
    public String showMeZzim(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        List<ItemResponse> itemResponses = meService.getAllMeZzim(user);
        model.addAttribute("items", itemResponses);
        return "me/zzim";
    }
    @GetMapping("/order/detail/{id}")
    public String showOrderDetail(@PathVariable("id") Long orderId, @CurrentUser User user) {
        orderService.showOrderDetail(orderId, user);

        return "/order/detail";
    }

    @GetMapping("/delete")
    public String deleteMe(DeleteMeRequest deleteMeRequest, @CurrentUser User user, Model model) {
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            return "redirect:/";
        }
        model.addAttribute("deleteMeRequest", deleteMeRequest);
        model.addAttribute("user", user);
        return "me/delete";
    }

    @PostMapping("delete")
    public String deleteMeAction(DeleteMeRequest deleteMeRequest, BindingResult result, @CurrentUser User user, Model model, RedirectAttributes redirectAttributes) {
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            return "redirect:/";
        }
        if (!meService.passwordCheck(deleteMeRequest.getPassword(), user)) {
            result.rejectValue("password", "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("user", user);
            return "me/delete";
        }
        //TODO 데모용.
        if (user.getEmail().equals("demo@user") || user.getEmail().equals("demo@admin") || user.getEmail().equals("demo@seller")) {
            result.rejectValue("password", "PASSWORD_MISMATCH", "데모 계정은 삭제가 불가능합니다..!");
            model.addAttribute("user", user);
            return "me/delete";
        }
        meService.deleteMe(deleteMeRequest, user);

        redirectAttributes.addFlashAttribute("message", "회원탈퇴가 완료되었습니다");
        return "redirect:/logout";
    }
}
