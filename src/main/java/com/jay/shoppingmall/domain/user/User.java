package com.jay.shoppingmall.domain.user;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.domain.user.model.Name;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Embedded
    @Setter
    private Agree agree;

    @Embedded
    private Address address;

    @Embedded
    private Name name;

    @Embedded
    private PhoneNumber phoneNumber;

    @Builder
    public User(String email, String password, Agree agree, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void userUpdate(final Address address, final Name name, final Agree agree, final PhoneNumber phoneNumber) {
        this.address = address;
        this.name = name;
        this.agree = agree;
        this.phoneNumber = phoneNumber;
    }
}
