package com.jay.shoppingmall.domain.item;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    //embeddable로 변경? 파일명, path.
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "image_name", unique = true)),
            @AttributeOverride(name = "path", column = @Column(name = "image_path", unique = true))

    })
    private Image image;

    private Integer price;

    private Integer stock;

    //삭제.
    private boolean isDeleted;

    //임시저장.
    private boolean isTemporary;


}
