package com.jay.shoppingmall.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverInfoTemporarySave {

    @NotBlank
    private String receiverFullName;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String address;

    @NotBlank
    private String detailAddress;

    @NotBlank
    private String extraAddress;

    @NotBlank
    private String receiverPhoneNumber;

    @NotBlank
    private String receiverEmail;
}
