package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.common.SQLSoftDeletion;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.option.ItemOption;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.exception.exceptions.CartEmptyException;
import com.jay.shoppingmall.exception.exceptions.QuantityException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE cart SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer quantity;

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted = false;

    private LocalDateTime deletedDate;

    public void isDeletedTrue() {
        if (this.isDeleted) {
            throw new CartEmptyException("이미 삭제되어있는 물품입니다");
        }
        this.isDeleted = true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Cart(final Integer quantity, final Item item, final User user, final ItemOption itemOption) {
        this.quantity = quantity;
        this.item = item;
        this.user = user;
        this.itemOption = itemOption;
    }

    public Integer manipulateQuantity(Integer quantity) {
        if (quantity < 1 || quantity > item.getStock()) {
            throw new QuantityException("주문할 수 있는 재고 "+ item.getStock() +"개를 넘어섰습니다.");
        }
        this.quantity = quantity;
        return quantity;
    }
}
