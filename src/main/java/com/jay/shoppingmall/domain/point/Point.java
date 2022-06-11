package com.jay.shoppingmall.domain.point;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.point.point_history.PointHistory;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer pointNow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void minusPoint(final Integer pointNumber) {
        this.pointNow -= pointNumber;
        if (this.getPointNow() < 0) {
            throw new NotValidException("포인트가 부족합니다");
        }
    }
    public void plusPoint(final Integer pointNumber) {
        this.pointNow += pointNumber;
    }
}
