package com.jay.shoppingmall.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String password;

    //나중에 embeddable로 변경해서 사용할 것.
    private String address;

    private String fullName;

    //나중에 embeddable로 변경해서 사용할 것.
    private String phoneNumber;

    public void justSignup(String email, String password) {
        this.email = email;
        this.password = password;
    }


}
