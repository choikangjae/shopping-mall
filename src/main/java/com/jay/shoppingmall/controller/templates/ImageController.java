package com.jay.shoppingmall.controller.templates;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

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
