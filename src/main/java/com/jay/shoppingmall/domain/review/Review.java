package com.jay.shoppingmall.domain.review;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE review SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
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

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted = false;

    private LocalDateTime deletedDate;

    //nto1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //nto1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
}
