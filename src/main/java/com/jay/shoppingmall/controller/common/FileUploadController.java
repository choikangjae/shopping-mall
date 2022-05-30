package com.jay.shoppingmall.controller.common;

import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final FileHandler fileHandler;

//    @PostMapping("/image")
//    public String image(MultipartFile image) {
//
//        return fileHandler.fileUpload(image);
//    }
}
