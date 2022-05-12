package com.jay.shoppingmall.domain.item;

import com.jay.shoppingmall.domain.image.Image;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "item",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Image> imageList = new ArrayList<>();

    private Integer price;

    private Integer stock;

    //삭제.
    private boolean isDeleted;

    //임시저장.
    private boolean isTemporary;

    @Builder
    public Item(String name, String description, Integer price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    //Image 테이블에 item_id 필드값 할당.
    public void addImage(Image image) {
        imageList.add(image);

        if (image.getItem() != this)
            image.setItem(this);
    }


}
