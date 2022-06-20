package com.jay.shoppingmall.service.handler;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.exception.exceptions.FileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE_TIME;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

    @InjectMocks
    FileHandler fileHandler;
    @Mock
    ImageRepository imageRepository;

    Image image;

    @Test
    void whenUploadPngImage_ImageWillBeSaved_parseFilesInfo() throws IOException {
        FileInputStream inputFile = new FileInputStream("C:\\Users\\choik\\Desktop\\QR코드.png");
        MockMultipartFile file = new MockMultipartFile("file", "QR코드", "image/png", inputFile);
        image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L);

        assertThat(image.getFileExtension()).isEqualTo(".png");
        assertThat(image.getOriginalFileName()).isEqualTo("QR코드");
        assertThat(image.getForeignId()).isEqualTo(0L);
        assertThat(image.getFileSize()).isNotNull();
        assertThat(image.getFilePath()).contains("images");
        assertThat(image.getFilePath()).contains(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        inputFile.close();
        fileHandler.deleteImage(image);
    }

    @Test
    void whenUploadJpegImage_ImageWillBeSaved_parseFilesInfo() throws IOException {

        FileInputStream inputFile = new FileInputStream("C:\\Users\\choik\\Desktop\\QR코드.jpg");
        MockMultipartFile file = new MockMultipartFile("file", "QR코드", "image/jpeg", inputFile);
        image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L);

        assertThat(image.getFileExtension()).isEqualTo(".jpg");
        assertThat(image.getOriginalFileName()).isEqualTo("QR코드");
        assertThat(image.getForeignId()).isEqualTo(0L);
        assertThat(image.getFileSize()).isNotNull();
        assertThat(image.getFilePath()).contains("images");
        assertThat(image.getFilePath()).contains(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        inputFile.close();
        fileHandler.deleteImage(image);
    }

    @Test
    void whenUploadNotJpegOrPng_ThrowFileException_parseFilesInfo() {
        try {
            FileInputStream inputFile = new FileInputStream("C:\\Users\\choik\\Desktop\\QR코드");
            MockMultipartFile file = new MockMultipartFile("file", "QR코드", MediaType.TEXT_PLAIN_VALUE, inputFile);

            assertThrows(FileException.class, () -> fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L));
            inputFile.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Test
    void getStringImage() throws IOException {
        FileInputStream inputFile = new FileInputStream("C:\\Users\\choik\\Desktop\\QR코드.png");
        MockMultipartFile file = new MockMultipartFile("file", "QR코드", MediaType.IMAGE_PNG_VALUE, inputFile);
        image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L);
        inputFile.close();

        final String stringImage = fileHandler.getStringImage(image);
        final boolean isBase64Encoded = stringImage.matches("^[a-zA-Z0-9+/]*={0,2}$");

        assertThat(isBase64Encoded).isTrue();

        fileHandler.deleteImage(image);
    }
}