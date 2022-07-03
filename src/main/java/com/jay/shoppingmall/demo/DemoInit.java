package com.jay.shoppingmall.demo;

import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class DemoInit {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private boolean init() {
        if (userRepository.findByEmail("demo@user").isPresent()) {
            return false;

        }
        if (userRepository.findByEmail("demo@seller").isPresent() || userRepository.findByEmail("demo@seller2").isPresent()) {
            return false;

        }
        if (userRepository.findByEmail("demo@admin").isPresent()) {
            return false;

        }
//        final Agree agree = Agree.builder().isMarketingAgree(true).isMandatoryAgree(true).build();
//        PhoneNumber phoneNumber = PhoneNumber.builder()
//                .first("010")
//                .middle("1234")
//                .last("1234")
//                .build();
//        Name name = Name.builder()
//                .last("홍")
//                .first("길동")
//                .build();

        User user = User.builder()
                .email("demo@user")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_USER)
                .isDeleted(false)
//                .address(address)
//                .name(name)
//                .phoneNumber(phoneNumber)
//                .agree(agree)
                .build();
        User seller = User.builder()
                .email("demo@seller")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_SELLER)
                .isDeleted(false)
//                .address(address)
//                .name(name)
//                .phoneNumber(phoneNumber)
//                .agree(agree)
                .build();
        User seller2 = User.builder()
                .email("demo@seller2")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_SELLER)
                .isDeleted(false)
//                .address(address)
//                .name(name)
//                .phoneNumber(phoneNumber)
//                .agree(agree)
                .build();
        User admin = User.builder()
                .email("demo@admin")
                .password(passwordEncoder.encode("specialPassWorD123"))
                .role(Role.ROLE_ADMIN)
                .isDeleted(false)
//                .address(address)
//                .name(name)
//                .phoneNumber(phoneNumber)
//                .agree(agree)
                .build();

        userRepository.save(user);
        userRepository.save(admin);
        userRepository.save(seller);
        userRepository.save(seller2);
        Address address = Address
                .builder()
                .address("경기 성남시 분당구 경부고속도로 409")
                .detailAddress("집")
                .zipcode("13473")
                .build();

        Seller seller1 = Seller.builder()
                .userId(seller.getId())
                .companyName("판매자 계정1")
                .defaultDeliveryCompany("CJ")
                .shippingFeeDefault(2000)
                .shippingFeeFreePolicy(50000)
                .returnShippingFeeDefault(2000)
                .itemReleaseAddress(address)
                .itemReturnAddress(address)
                .isSellerAgree(true)
                .isActivated(true)
                .isLawAgree(true)
                .build();

        Seller seller3 = Seller.builder()
                .userId(seller2.getId())
                .companyName("판매자 계정2")
                .defaultDeliveryCompany("대한통운")
                .shippingFeeDefault(3000)
                .shippingFeeFreePolicy(30000)
                .returnShippingFeeDefault(3000)
                .itemReleaseAddress(address)
                .itemReturnAddress(address)
                .isSellerAgree(true)
                .isActivated(true)
                .isLawAgree(true)
                .build();
        sellerRepository.save(seller1);
        sellerRepository.save(seller3);
        return true;
    }
}
