package com.jay.shoppingmall.domain.notification.me_notification;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.notification.Notification;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String originalMessage;

    @PrePersist
    public void prePersist() {
        super.prePersist();
    }

}
