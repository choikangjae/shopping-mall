package com.jay.shoppingmall.domain.notification;

import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private NotificationType notificationType;

    private String message;

    private Boolean isRead;

    private LocalDateTime readAt;

    @CreationTimestamp
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public void readTrue() {
        this.readAt = LocalDateTime.now();
        this.isRead = true;
    }

    @PrePersist
    public void prePersist() {
        this.isRead = this.isRead != null && this.isRead;
    }
}
