package com.jay.shoppingmall.controller.common;

import com.jay.shoppingmall.service.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

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
