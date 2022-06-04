//package com.jay.shoppingmall.demo;
//
//import com.jay.shoppingmall.domain.seller.Seller;
//import com.jay.shoppingmall.domain.seller.SellerRepository;
//import com.jay.shoppingmall.domain.user.Role;
//import com.jay.shoppingmall.domain.user.User;
//import com.jay.shoppingmall.domain.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Component
//@RequiredArgsConstructor
//public class DemoInit {
//
//    private final UserRepository userRepository;
//    private final SellerRepository sellerRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    private void init() {
//        if (userRepository.findByEmail("demo@user").isPresent()) {
//            User user = userRepository.findByEmail("demo@user").get();
//            userRepository.delete(user);
//        }
//        if (userRepository.findByEmail("demo@seller").isPresent()) {
//            User seller = userRepository.findByEmail("demo@seller").get();
//            final Seller seller1 = sellerRepository.findByUserIdAndIsActivatedTrue(seller.getId()).get();
//            sellerRepository.delete(seller1);
//            userRepository.delete(seller);
//        }
//        if (userRepository.findByEmail("demo@admin").isPresent()) {
//            User admin = userRepository.findByEmail("demo@admin").get();
//            userRepository.delete(admin);
//        }
//
//        User user = User.builder()
//                .email("demo@user")
//                .password(passwordEncoder.encode("password123"))
//                .role(Role.ROLE_USER)
//                .build();
//        User seller = User.builder()
//                .email("demo@seller")
//                .password(passwordEncoder.encode("password123"))
//                .role(Role.ROLE_SELLER)
//                .build();
//        User admin = User.builder()
//                .email("demo@admin")
//                .password(passwordEncoder.encode("specialPassWorD123"))
//                .role(Role.ROLE_ADMIN)
//                .build();
//
//        userRepository.save(user);
//        userRepository.save(admin);
//        userRepository.saveAndFlush(seller);
//
//        Seller seller1 = Seller.builder()
//                .userId(seller.getId())
//                .isSellerAgree(true)
//                .isActivated(true)
//                .isLawAgree(true)
//                .build();
//        sellerRepository.save(seller1);
//    }
//}
