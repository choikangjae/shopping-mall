package com.jay.shoppingmall.domain.zzim;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE zzim SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class Zzim extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    private Boolean isZzimed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted;

    private LocalDateTime deletedDate;

    @PrePersist
    public void prePersist() {
        this.isZzimed = this.isZzimed != null && this.isZzimed;
        this.isDeleted = this.isDeleted != null && this.isDeleted;
    }
}