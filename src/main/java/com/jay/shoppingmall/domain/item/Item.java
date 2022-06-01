package com.jay.shoppingmall.domain.item;

import com.jay.shoppingmall.common.BaseTimeEntity;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE item SET is_deleted = 1, deleted_date = NOW() WHERE id = ?")
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String mainOption;

    private String subOption;

    @OneToMany(mappedBy = "item",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private final List<Image> imageList = new ArrayList<>();

    private Integer price;

    private Integer salePrice;

    @Setter
    private Integer stock;

    @Setter
    private Integer zzim;

    private Integer viewCount;

    @Column(columnDefinition = "boolean default 0")
    private Boolean isDeleted = false;

    private LocalDateTime deletedDate;

    public void stockMinusQuantity(Integer quantity) {
        if (quantity > stock) {
            throw new ItemNotFoundException("주문량이 재고보다 많습니다");
        }
        this.stock -= quantity;
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    public void viewCountUp() {
        if (this.getViewCount() == null) {
            this.viewCount = 1;
        } else {
            this.viewCount += 1;
        }
    }

    @Builder
    public Item(String name, String description, Integer price, Integer salePrice, Integer stock, Seller seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.stock = stock;
        this.seller = seller;
    }

    //Image 테이블에 item_id 필드값 할당.
    public void addImage(Image image) {
        imageList.add(image);

        if (image.getItem() != this)
            image.setItem(this);
    }

}
