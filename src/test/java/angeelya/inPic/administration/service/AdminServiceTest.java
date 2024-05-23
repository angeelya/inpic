package angeelya.inPic.administration.service;

import angeelya.inPic.database.model.AdminNotification;
import angeelya.inPic.database.model.DeletedImage;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.DeletedImageRepository;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.ImageRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.image.service.ImageGetService;
import angeelya.inPic.notification.service.AdminNotificationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServiceTest {
    @Mock
    ImageRepository imageRepository;
    @Mock
    ImageFileService imageFileService;
    @Mock
    ImageGetService imageGetService;
    @Mock
    DeletedImageRepository deletedImageRepository;
    @Mock
    AdminNotificationService adminNotificationService;
    @InjectMocks
    AdminService adminService;
    @SneakyThrows
    @Test
    void testGetAllImagePost() {
        Image image = Image.builder().id(1L).name("nature").imgName("nature.jpg").build();
        List<Image> images = List.of(image);
        List<ImageResponse> exceptedImageResponses = List.of(ImageResponse.builder()
                .image_id(1L)
                .name("nature")
                .imgName("nature.jpg").image("image").build());
        Mockito.when(imageRepository.findAll()).thenReturn(images);
        Mockito.when(imageFileService.getImage(image.getImgName())).thenReturn("image");
        assertEquals(exceptedImageResponses,adminService.getAllImagePost());
        Mockito.when(imageRepository.findAll()).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->adminService.getAllImagePost());

    }

    @SneakyThrows
    @Test
    void testDeleteImagePost() {
        ImageRequest imageRequest = ImageRequest.builder().image_id(1L).build();
        Image image = Image.builder().id(1L).name("nature").imgName("nature.jpg").build();
        Mockito.when(imageGetService.getImage(imageRequest.getImage_id())).thenReturn(image);
        Mockito.when( imageRepository.findById(imageRequest.getImage_id())).thenReturn(Optional.empty());
        User user = User.builder().id(1L).build();
        DeletedImage deletedImage = DeletedImage.builder().imgName(image.getImgName())
                .path(image.getPath()).build();
        AdminNotification adminNotification=AdminNotification.builder()
                .user(user)
                .deletedImage(deletedImage).build();
        Mockito.when(adminNotificationService.addNotification(deletedImage, user)).thenReturn(adminNotification);
        assertEquals("Image deleting is successful",adminService.deleteImagePost(imageRequest));
        Mockito.when( imageRepository.findById(imageRequest.getImage_id())).thenReturn(Optional.of(image));
        assertThrows(DeleteDatabaseException.class,()->adminService.deleteImagePost(imageRequest));


    }
}