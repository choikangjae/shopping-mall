package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.category.Category;
import com.jay.shoppingmall.domain.category.CategoryRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.admin.category.CategoryAddRequest;
import com.jay.shoppingmall.dto.response.admin.category.CategoryResponse;
import com.jay.shoppingmall.dto.response.user.UserDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.swing.text.html.parser.Entity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    AdminService adminService;
    @Mock
    UserRepository userRepository;
    @Mock
    CategoryRepository categoryRepository;

    Pageable pageable = PageRequest.of(0, 10);
    User user;
    User user2;
    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        user2 = EntityBuilder.getUser2();
    }

    @Test
    void retrieveAllUsers_showUserList() {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        Page<User> users = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findAll(pageable)).thenReturn(users);
        final PageDto pageDto = adminService.showUserList(pageable);

        assertThat(pageDto.getContent().size()).isEqualTo(2);
        assertThat(pageDto.getCustomPage().getTotalElements()).isEqualTo(2);
        assertThat(pageDto.getCustomPage().getTotalPages()).isEqualTo(1);
    }

    @Test
    void searchUsersByEmail() {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        when(userRepository.findByEmailContaining("qwe")).thenReturn(userList);

        final List<UserDetailResponse> userDetailResponses = adminService.searchUsersByEmail("qwe");

        assertThat(userDetailResponses.size()).isEqualTo(2);
        assertThat(userDetailResponses.get(0).getEmail()).contains("qwe");
    }

    @Test
    void whenParentIsNull_ChildWillBeAdded_categoryAppend() {
        CategoryAddRequest request = new CategoryAddRequest(null, "영화");

        final CategoryResponse categoryResponse = adminService.categoryAppend(request);

        assertThat(categoryResponse.getChildCategory()).isEqualTo("영화");
        assertThat(categoryResponse.getParentCategory()).isNull();
    }
    @Test
    void whenParentIsNotNull_ChildWillBeAddedToParent_categoryAppend() {
        CategoryAddRequest request = new CategoryAddRequest("영화", "액션");

        final CategoryResponse categoryResponse = adminService.categoryAppend(request);

        assertThat(categoryResponse.getChildCategory()).isEqualTo("액션");
        assertThat(categoryResponse.getParentCategory()).isEqualTo("영화");
    }

    @Test
    void getCategoryByParent() {
        List<Category> categories = new ArrayList<>();
        Category category1 = Category.builder()
                .categoryName("영화")
                .build();
        Category category2 = Category.builder()
                .categoryName("책")
                .build();
        categories.add(category1);
        categories.add(category2);

        when(categoryRepository.findAllByParentIdIsNull()).thenReturn(categories);

        final List<CategoryResponse> allRootCategories = adminService.getAllRootCategories();

        assertThat(allRootCategories.size()).isEqualTo(2);
    }
}