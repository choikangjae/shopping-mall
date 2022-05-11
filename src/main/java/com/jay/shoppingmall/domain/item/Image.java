package com.jay.shoppingmall.domain.item;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.util.UUID;

@Getter
@Embeddable
@ToString(of = {"id", "path"})
public class Image {

    private UUID id;
    private String path;


}
