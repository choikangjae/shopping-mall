package com.jay.shoppingmall.domain.user;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.user.model.*;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
//@Builder
//@AllArgsConstructor
@Where(clause = "is_deleted = 0")
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

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted = false;

    public User(final Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updatePassword(final String password) {
        this.password = password;
    }

    @Builder
    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void updateUserRole(final Role role) {
        this.role = role;
    }

    public void userUpdate(final Address address, final Name name, final Agree agree, final PhoneNumber phoneNumber) {
        this.address = address;
        this.name = name;
        this.agree = agree;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        int indexOfAt = email.indexOf("@");
        return email.substring(0, indexOfAt);
    }

}
