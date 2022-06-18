package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.category.CategoryRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
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
    void showUserList() {
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
        adminService.searchUsersByEmail("");
    }

    @Test
    void categoryAppend() {
    }

    @Test
    void getCategoryByParent() {
    }
}