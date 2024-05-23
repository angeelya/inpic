package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.file.service.ImageFileService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserImageServiceTest {
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    UserImageService userImageService;
    @SneakyThrows
    @Test
    void testGetUserImage() {
        User user = User.builder()
                .id(1L)
                .userImage(UserImage.builder().name("hello.jpg").build()).build();
        Mockito.when(imageFileService.getImage(any(String.class))).thenReturn("image");
        assertEquals("image",userImageService.getUserImage(user));
    }
}