package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.DeleteMeRequest;
import com.jay.shoppingmall.dto.request.password.PasswordRequest;
import com.jay.shoppingmall.dto.response.notification.MeNotificationResponse;
import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.dto.response.order.SimpleOrderResponse;
import com.jay.shoppingmall.dto.response.review.ReviewResponse;
import com.jay.shoppingmall.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final ItemService itemService;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    @GetMapping
    public String me(@CurrentUser User user, Model model, @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        List<SimpleOrderResponse> simpleOrderResponses = orderService.showOrders(user, pageable);
        if (!simpleOrderResponses.isEmpty()) {
            model.addAttribute("orders", simpleOrderResponses);
        }
        final List<MeNotificationResponse> myRecentNotifications = notificationService.getMyRecentNotifications(user);

        model.addAttribute("user", user);
        model.addAttribute("myRecentNotifications", myRecentNotifications);
        return "me/home";
    }

    @GetMapping("/privacy")
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
    public String reConfirm(PasswordRequest passwordRequest, @CurrentUser User user, Model model, RedirectAttributes redirectAttributes) {
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
    public String showMeZzim(@CurrentUser User user, Model model, Pageable pageable) {

        PageDto itemResponses = itemService.getAllMeZzim(user, pageable);
        model.addAttribute("items", itemResponses);
        return "me/zzim";
    }

    @GetMapping("/order/detail/{id}")
    public String showOrderDetail(@PathVariable("id") Long orderId, Model model, @CurrentUser User user) {
        final OrderDetailResponse orderDetailResponse = orderService.showOrderDetail(orderId, user);

        model.addAttribute("orderDetailResponse", orderDetailResponse);
        return "order/detail";
    }
    //TODO 기본 틀만 작성
    @GetMapping("/orders")
    public String showOrders(@CurrentUser User user, Pageable pageable, Model model) {
        List<SimpleOrderResponse> simpleOrderResponses = orderService.showOrders(user, pageable);
        if (!simpleOrderResponses.isEmpty()) {
            model.addAttribute("orders", simpleOrderResponses);
        }

        return "/me/orders";
    }
    //TODO 기본 틀만 작성
    @GetMapping("/cancel-item-list")
    public String showCancels(@CurrentUser User user, Pageable pageable, Model model) {
        List<SimpleOrderResponse> simpleOrderResponses = orderService.showOrders(user, pageable);
        if (!simpleOrderResponses.isEmpty()) {
            model.addAttribute("orders", simpleOrderResponses);
        }

        return "me/cancel-item-list";
    }

    @GetMapping("/reviews")
    public String showMyReviewList(@CurrentUser User user, Model model) {
        final List<ReviewResponse> reviewResponses = reviewService.getMyRecentReviews(user);

        if (!reviewResponses.isEmpty()) {
            model.addAttribute("reviews", reviewResponses);
        }

        return "me/reviews";
    }

    @GetMapping("/browse-history")
    public String showBrowseHistoriesByUser(@CurrentUser User user, Model model, Pageable pageable) {
        final PageDto browseHistories = itemService.getMyBrowseHistories(user, pageable);
        model.addAttribute("items", browseHistories);

        return "me/browse-history";
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
