package com.jay.shoppingmall.service.handler;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.exception.exceptions.FileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

    @InjectMocks
    FileHandler fileHandler;
//    @Mock
//    ImageRepository imageRepository;

    Image image;
//웹훅테스트
    @Test
    void whenUploadPngImage_ImageWillBeSaved_parseFilesInfo() {
        byte[] content = new byte['q'];

        MockMultipartFile file = new MockMultipartFile("file", "원본파일이름", "image/png", content);

        image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L);

        assertThat(image.getFileExtension()).isEqualTo(".png");
        assertThat(image.getOriginalFileName()).isEqualTo("원본파일이름");
        assertThat(image.getForeignId()).isEqualTo(0L);
        assertThat(image.getFileSize()).isNotNull();
        assertThat(image.getFilePath()).contains("images");
        assertThat(image.getFilePath()).contains(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        fileHandler.deleteImage(image);
    }

    @Test
    void whenUploadJpegImage_ImageWillBeSaved_parseFilesInfo() {
        byte[] content = new byte['q'];

        MockMultipartFile file = new MockMultipartFile("file", "원본파일이름", "image/jpeg", content);
        image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L);

        assertThat(image.getFileExtension()).isEqualTo(".jpg");
        assertThat(image.getOriginalFileName()).isEqualTo("원본파일이름");
        assertThat(image.getForeignId()).isEqualTo(0L);
        assertThat(image.getFileSize()).isNotNull();
        assertThat(image.getFilePath()).contains("images");
        assertThat(image.getFilePath()).contains(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        fileHandler.deleteImage(image);
    }

    @Test
    void whenUploadNotJpegOrPng_ThrowFileException_parseFilesInfo() {
        try {
            byte[] content = new byte['q'];
            MockMultipartFile file = new MockMultipartFile("file", "원본파일이름", MediaType.TEXT_PLAIN_VALUE, content);

            assertThrows(FileException.class, () -> fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Test
    void getStringImage() {
        byte[] content = new byte['q'];
        MockMultipartFile file = new MockMultipartFile("file", "원본파일이름", MediaType.IMAGE_PNG_VALUE, content);
        image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, 0L);

        final String stringImage = fileHandler.getStringImage(image);
        final boolean isBase64Encoded = stringImage.matches("^[a-zA-Z0-9+/]*={0,2}$");

        assertThat(isBase64Encoded).isTrue();

        fileHandler.deleteImage(image);
    }
}