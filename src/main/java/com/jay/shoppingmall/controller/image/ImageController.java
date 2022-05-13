package com.jay.shoppingmall.controller.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ImageController {
//
//    @GetMapping(
//            value = "/thumbnail/{id}",
//            // 출력하고자 하는 데이터 포맷 정의
//            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
//    )
//    public ResponseEntity<byte[]> getThumbnail(@PathVariable Long id) throws IOException {
//
}
