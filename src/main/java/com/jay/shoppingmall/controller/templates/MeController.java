package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.DeleteMeRequest;
import com.jay.shoppingmall.dto.request.PasswordCheckRequest;
import com.jay.shoppingmall.dto.response.ItemResponse;
import com.jay.shoppingmall.service.MeService;
import lombok.RequiredArgsConstructor;
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
public class MeController {

    private final MeService meService;

    @GetMapping
    public String me(@CurrentUser User user, Model model) {
        model.addAttribute("user", user);

        return "me/home";
    }

    @PostMapping("/privacy")
    public String showPersonalInformationAgreementForm(@CurrentUser User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        if (user.getAddress() != null || user.getName() != null || user.getPhoneNumber() != null) {
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
    public String reConfirm(PasswordCheckRequest passwordCheckRequest, @CurrentUser User user, Model model) {
        model.addAttribute("user", user);
        return "me/reconfirm";
    }

    @PostMapping("/reconfirm")
    public String reConfirmAction(@Valid PasswordCheckRequest passwordCheckRequest, BindingResult result, @CurrentUser User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

        if (passwordCheckRequest.getPassword().isEmpty()) {
            result.rejectValue("password", "NO_PASSWORD_FOUND", "비밀번호를 입력해주세요.");
        }
        if (!meService.passwordCheck(passwordCheckRequest.getPassword(), user)) {
            result.rejectValue("password", "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "me/reconfirm";
        }
        request.getSession().setAttribute("password", passwordCheckRequest.getPassword());
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
    public String deleteMeAction(DeleteMeRequest deleteMeRequest, BindingResult result, @CurrentUser User user, RedirectAttributes redirectAttributes) {
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            return "redirect:/";
        }
        meService.deleteMe(deleteMeRequest, user);
        if (result.hasErrors()) {
            return "redirect:/me/delete";
        }

        redirectAttributes.addFlashAttribute("message", "회원탈퇴가 완료되었습니다");
        return "redirect:/logout";
    }
}
