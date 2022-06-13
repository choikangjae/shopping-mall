package com.jay.shoppingmall.domain.category;

import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    public void setParent(final Category parent) {
        this.parent = parent;
    }
}
