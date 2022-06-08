package com.jay.shoppingmall.domain.browse_history;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BrowseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime browseAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
