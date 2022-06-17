package com.jay.shoppingmall.domain.entitybuilder;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;

public class EntityBuilder {
    static Item item = Item.builder()
            .brandName("나이키")
            .name("상품명")
            .description("설명")
            .build();

    static Item item2 = Item.builder()
            .brandName("나이키")
            .name("상품명")
            .description("설명")
            .build();

    static User user = User.builder()
            .email("qwe@qwe")
            .role(Role.ROLE_USER)
            .password(("qweqweqwe1"))
            .build();

    static User user2 = User.builder()
            .email("123@123")
            .role(Role.ROLE_USER)
            .password(("ewqefewqfqwfe123"))
            .build();

    static Seller seller = Seller.builder()

            .build();

    static ItemOption itemOption = ItemOption.builder()
            .isOptionMainItem(true)
            .item(item)
            .option1("option1")
            .option2("option2")
            .build();

    static Address address = Address.builder()
            .address("서울시 종로")
            .detailAddress("집")
            .zipcode("01010")
            .extraAddress("")
            .build();

    static PhoneNumber phoneNumber = PhoneNumber.builder()
            .first("010")
            .middle("1234")
            .last("5678")
            .build();

    static Name name = Name.builder()
            .last("홍")
            .first("길동")
            .build();

    static Agree agree = Agree.builder()
            .isMandatoryAgree(true)
            .isMarketingAgree(true)
            .build();

    public static ItemOption getItemOption() {
        return itemOption;
    }

    public static User getUser() {
        return user;
    }
    public static User getUser2() {
        return user2;
    }

    public static Address getAddress() {
        return address;
    }

    public static PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public static Name getName() {
        return name;
    }

    public static Agree getAgree() {
        return agree;
    }

    public static Item getItem() {
        return item;
    }
    public static Item getItem2() {
        return item2;
    }
    public static Seller getSeller() {
        return seller;
    }

}
