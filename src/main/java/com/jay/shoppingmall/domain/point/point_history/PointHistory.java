package com.jay.shoppingmall.domain.point.point_history;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.point.Point;
import com.jay.shoppingmall.domain.point.point_history.model.PointStatus;
import com.jay.shoppingmall.domain.point.point_history.model.PointType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer pointNumber;

    @Enumerated(EnumType.STRING)
    private PointType pointType;

    @Enumerated(EnumType.STRING)
    private PointStatus pointStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;
}
