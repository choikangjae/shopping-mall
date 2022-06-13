package com.jay.shoppingmall.domain.item.item_view_history;

import com.jay.shoppingmall.domain.item.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate viewDate;

    private LocalTime viewTime;

    private Integer viewCountPerDay;

    public void viewCountPerDayUp() {
        this.viewCountPerDay += 1;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

}
