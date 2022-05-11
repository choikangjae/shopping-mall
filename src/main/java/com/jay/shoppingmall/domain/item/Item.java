package com.jay.shoppingmall.domain.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    //embeddable로 변경? 파일명, path.
    @Embedded
    private Image image;

    private Integer price;

    private Integer stock;

    //삭제.
    private boolean isDeleted;

    //임시저장.
    private boolean isTemporary;


}
