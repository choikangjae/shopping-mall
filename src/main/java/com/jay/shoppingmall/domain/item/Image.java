package com.jay.shoppingmall.domain.item;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class Image {

    private String path;
    private UUID id;

}
