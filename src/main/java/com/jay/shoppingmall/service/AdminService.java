package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.category.Category;
import com.jay.shoppingmall.domain.category.CategoryRepository;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.model.page.CustomPage;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.admin.category.CategoryAddRequest;
import com.jay.shoppingmall.dto.response.admin.category.CategoryResponse;
import com.jay.shoppingmall.dto.response.user.UserDetailResponse;
import com.jay.shoppingmall.exception.exceptions.CategoryNotFoundException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public PageDto showUserList(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        CustomPage customPage = new CustomPage(users, "");

        List<UserDetailResponse> userDetailResponses = new ArrayList<>();
        for (User user : users) {
            userDetailResponses.add(UserDetailResponse.builder()
                    .email(user.getEmail())
                    .fullName(user.getName().getFullName())
                    .fullPhoneNumber(user.getPhoneNumber().getFullNumber())
                    .fullAddress(user.getAddress().getFullAddress())
                    .role(user.getRole())
                    .isMandatoryAgree(user.getAgree().getIsMandatoryAgree())
                    .isMarketingAgree(user.getAgree().getIsMarketingAgree())
                    .build());
        }
        log.info("Admin queried user list.");
        return PageDto.builder()
                .customPage(customPage)
                .content(userDetailResponses)
                .build();
    }
    @Transactional(readOnly = true)
    public List<UserDetailResponse> searchUsersByEmail(String email) {
        List<User> users = userRepository.findByEmailContaining(email);

        List<UserDetailResponse> userDetailResponses = new ArrayList<>();

        if (users.isEmpty()) return userDetailResponses;

        for (User user : users) {
            userDetailResponses.add(UserDetailResponse.builder()
                    .email(user.getEmail())
                    .fullName(user.getName().getFullName())
                    .fullPhoneNumber(user.getPhoneNumber().getFullNumber())
                    .fullAddress(user.getAddress().getFullAddress())
                    .role(user.getRole())
                    .isMandatoryAgree(user.getAgree().getIsMandatoryAgree())
                    .isMarketingAgree(user.getAgree().getIsMarketingAgree())
                    .build());
        }
        log.info("Admin queried users by email. email = '{}'", email);
        return userDetailResponses;
    }

    /**
     * 카테고리를 생성합니다. 부모 카테고리가 null일시 null로 저장되며 부모 카테고리가 null이 아니면 부모 카테고리와 자식 카테고리가 같이 저장됩니다.
     * 자식 카테고리는 null일 수 없습니다.
     * @param request
     * @return
     */
    public CategoryResponse categoryAppend(final CategoryAddRequest request) {
        final Category parent = categoryRepository.findByCategoryName(request.getParentCategory())
                .orElseGet(() -> Category.builder()
                        .categoryName(request.getParentCategory())
                        .build());
        categoryRepository.save(parent);

        Category child = Category.builder()
                .categoryName(request.getChildCategory())
                .parent(parent)
                .build();
        categoryRepository.save(child);

        log.info("Category has been appended by admin. parent = '{}', child = '{}'", request.getParentCategory(), request.getChildCategory());
        return CategoryResponse.builder()
                .parentCategory(parent.getCategoryName())
                .childCategory(child.getCategoryName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllRootCategories() {
        final List<Category> categories = categoryRepository.findAllByParentIdIsNull();

        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            categoryResponses.add(CategoryResponse.builder()
                    .parentCategory(category.getCategoryName())
                    .build());
        }
        log.info("Admin queried all root categories.");
        return categoryResponses;
    }
}
