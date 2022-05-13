package com.jay.shoppingmall.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    //나중에 embeddable로 변경해서 사용할 것.
    private String address;

    private String fullName;

    //나중에 embeddable로 변경해서 사용할 것.
    private String phoneNumber;

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


}
