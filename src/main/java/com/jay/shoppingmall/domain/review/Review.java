package com.jay.shoppingmall.domain.review;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(20)
    @Column(nullable = false)
    private String description;

//    private Image image;

    @Column(name = "star", nullable = false)
    @Enumerated(EnumType.STRING)
    private Star star;

    //nto1
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //nto1
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
