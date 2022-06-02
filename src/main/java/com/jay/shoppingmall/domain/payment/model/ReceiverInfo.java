package com.jay.shoppingmall.domain.payment.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReceiverInfo {

    @NotEmpty
    private String receiverAddress;

    @NotEmpty
    private String receiverEmail;

    @NotEmpty
    private String receiverName;

    @NotEmpty
    private String receiverPostcode;

    @NotEmpty
    private String receiverPhoneNumber;
}
