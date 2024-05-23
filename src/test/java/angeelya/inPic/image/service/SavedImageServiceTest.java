package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.SavedImage;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.SavedImageRepository;
import angeelya.inPic.dto.request.SavedImageAddRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.ForbiddenRequestException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserGetService;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SavedImageServiceTest {
    @Mock
    SavedImageRepository savedImageRepository;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageGetService imageGetService;
    @Mock
    ImageService imageService;
    @Mock
    AlbumService albumService;
    @Mock
    ActionService actionService;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    SavedImageService savedImageService;

    @SneakyThrows
    @Test
    void testAddSavedImage() {
        SavedImageAddRequest savedImageAddRequest = SavedImageAddRequest.builder()
                .image_id(1L)
                .user_id(1L)
                .album_id(1L).build();
        User user = User.builder().id(1L).build();
        Image image = Image.builder().id(1L).user(User.builder().id(2L).build()).build(),
                imageException = Image.builder().id(2L).user(user).build();
        Album album = Album.builder().id(1L).build();
        Mockito.when(userGetService.getUser(savedImageAddRequest.getUser_id())).thenReturn(user);
        Mockito.when(imageGetService.getImage(savedImageAddRequest.getImage_id())).thenReturn(image);
        Mockito.when(albumService.getAlbum(savedImageAddRequest.getAlbum_id())).thenReturn(album);
        String excepted = "Saved image adding is successful",
                actual = savedImageService.addSavedImage(savedImageAddRequest);
        assertEquals(actual, excepted);
        Mockito.when(imageGetService.getImage(savedImageAddRequest.getImage_id())).thenReturn(imageException);
        assertThrows(ForbiddenRequestException.class, () -> savedImageService.addSavedImage(savedImageAddRequest));
    }

    @SneakyThrows
    @Test
    void testGetSavedAndCreatedUserImage() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        List<SavedImage> savedImages = List.of(SavedImage.builder().id(1L)
                .image(Image.builder().id(1L).build()).user(User.builder().id(user_id).build()).build());
        List<ImageResponse> createdImageResponses = List.of(ImageResponse.builder()
                .image_id(1L)
                .imgName("nature")
                .name("nature.jpg")
                .image("image").build()),
                savedImageResponses = List.of(ImageResponse.builder()
                        .image_id(1L)
                        .image("image").build());
        Mockito.when(savedImageRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(savedImages);
        Mockito.when(imageFileService.getImage(savedImages.get(0).getImage().getImgName())).thenReturn("image");
        Mockito.when(imageService.getUserCreatedImages(userInformationRequest)).thenReturn(createdImageResponses);
        List<ImageResponse> imageResponses = savedImageService.getSavedAndCreatedUserImage(userInformationRequest);
        assertTrue(imageResponses.containsAll(createdImageResponses));
        Mockito.when(savedImageRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        imageResponses = savedImageService.getSavedAndCreatedUserImage(userInformationRequest);
        assertEquals(imageResponses, createdImageResponses);
        Mockito.when(imageService.getUserCreatedImages(userInformationRequest)).thenThrow(new NotFoundDatabaseException("No created images found"));
        Mockito.when(savedImageRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(savedImages);
         imageResponses = savedImageService.getSavedAndCreatedUserImage(userInformationRequest);
        assertEquals(savedImageResponses, imageResponses);
        Mockito.when(savedImageRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> savedImageService.getSavedAndCreatedUserImage(userInformationRequest));
    }
}