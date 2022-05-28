package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.controller.common.CurrentUser;
import com.jay.shoppingmall.controller.common.SellerValidator;
import com.jay.shoppingmall.controller.common.UserValidator;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PasswordCheckRequest;
import com.jay.shoppingmall.dto.request.PasswordResetRequest;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.service.AuthService;
import com.jay.shoppingmall.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;
    private final SellerValidator sellerValidator;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    @GetMapping("/login")
    public String login(@RequestParam(name = "requestURI", required = false) String requestURI, @RequestParam(name = "scrollY", required = false) String scrollY, HttpServletRequest request, @CurrentUser User user) {
        request.getSession().setAttribute("requestURI", requestURI);
        request.getSession().setAttribute("scrollY", scrollY);
        if (user != null) {
            return "redirect:/";
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String loginAction(HttpServletRequest request, Model model, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
        model.addAttribute("username", request.getAttribute("username"));

        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(UserValidationRequest userValidationRequest, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
        return "auth/signup";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(PasswordResetRequest passwordResetRequest, @CurrentUser User user) {
        if (user != null) {
            return "redirect:/";
        }
        return "/auth/forgot-password";
    }

    @GetMapping("/reset")
    public String passwordResetTokenValidation(@ModelAttribute PasswordResetRequest passwordResetRequest, UserValidationRequest userValidationRequest, Model model) {
        authService.passwordTokenValidator(passwordResetRequest);

        model.addAttribute("email", passwordResetRequest.getEmail());
        return "/auth/new-password";
    }

    @PostMapping("/reset")
    public String passwordResetAction(@Valid UserValidationRequest userValidationRequest, BindingResult result) {

        if (!userValidationRequest.getPassword().equals(userValidationRequest.getRepeatPassword())) {
            result.rejectValue("password", "PasswordNotMatch", "비밀번호가 같지 않습니다.");
            result.rejectValue("repeatPassword", "PasswordNotMatch", "비밀번호가 같지 않습니다.");
        }

        if (result.hasErrors()) {
            return "auth/new-password";
        }

        authService.passwordUpdate(userValidationRequest);
        sessionUpdateToken(userValidationRequest);
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String signupAction(@Valid UserValidationRequest userValidationRequest, BindingResult result) {
        userValidator.validate(userValidationRequest, result);

        if (result.hasErrors()) {
            return "auth/signup";
        }

        authService.userRegistration(userValidationRequest);

//        sessionUpdateToken(userValidationRequest);

        return "redirect:/";
    }

    private void sessionUpdateToken(final UserValidationRequest userValidationRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userValidationRequest.getEmail(), userValidationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @GetMapping("/seller-signup")
    public String sellerSignup(UserValidationRequest userValidationRequest, @CurrentUser User user) {
//        if (user == null) {
//            return "redirect:/auth/login";
//        }
//        user.getAgree().getIsMandatoryAgree().equals(false);
        return "auth/seller-signup";
    }

    @PostMapping("/seller-signup")
    public String sellerSignupAction(@Valid UserValidationRequest userValidationRequest, BindingResult result, Model model) {
        sellerValidator.validate(userValidationRequest, result);

        if (result.hasErrors()) {
//            model.addAttribute("signupRequest", signupRequest);
            return "auth/seller-signup";
        }

        authService.sellerSignup(userValidationRequest);

        return "redirect:/auth/signup-done";
    }

    @GetMapping("/signup-done")
    public String privacyAgree() {

        return "auth/signup-done";
    }

}
