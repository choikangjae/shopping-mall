package com.jay.shoppingmall.controller.image;

import com.jay.shoppingmall.service.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ImageController {

//    private final FileHandler fileHandler;
//
//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//
//        Resource file = fileHandler.getStringImage(filename);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFileName() + "\"").body(file);
//    }
//
//    @GetMapping(
//            value = "/thumbnail/{id}",
//            // 출력하고자 하는 데이터 포맷 정의
//            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
//    )
//    public ResponseEntity<byte[]> getThumbnail(@PathVariable Long id) throws IOException {
//
}
