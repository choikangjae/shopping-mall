//package com.jay.shoppingmall.domain.category;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//class CategoryRepositoryTest {
//
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @BeforeEach
//    void setUp() {
//        Category parent = Category.builder()
//                .categoryName("영화")
//                .build();
//        categoryRepository.save(parent);
//        Category child = Category.builder()
//                .categoryName("액션")
//                .parent(parent)
//                .build();
//        categoryRepository.save(child);
//    }
//
//    @Test
//    void findByCategoryName() {
//        final Optional<Category> category = categoryRepository.findByCategoryName("영화");
//
//        assertThat(category.get().getCategoryName()).isEqualTo("영화");
//        assertThat(category.get().getCategoryName()).doesNotContain("액션");
//    }
//
//    @Test
//    void findAllByParentIdIsNull() {
//        final List<Category> categories = categoryRepository.findAllByParentIdIsNull();
//
//        assertThat(categories.size()).isEqualTo(1);
//        assertThat(categories.get(0).getParent()).isNull();
//    }
//}