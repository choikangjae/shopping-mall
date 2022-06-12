package com.jay.shoppingmall.controller.templates;

import com.jay.shoppingmall.common.CurrentUser;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.review.ReviewResponse;
import com.jay.shoppingmall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/my")
@Controller
@PreAuthorize("hasRole('USER')")
public class ReviewController {

}
