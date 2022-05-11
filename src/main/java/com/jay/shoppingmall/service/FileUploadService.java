package com.jay.shoppingmall.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
public class FileUploadService {

    public String fileUpload(MultipartFile image) {
        UUID id = UUID.randomUUID();
        String imageFileName = id + "_" + image.getOriginalFilename();

        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        File saveFile = new File(path, imageFileName);

        try {
            image.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFileName;
    }
}
