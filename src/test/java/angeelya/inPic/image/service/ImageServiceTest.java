package angeelya.inPic.image.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.ImageAddRequest;
import angeelya.inPic.dto.request.ImagePageRequest;
import angeelya.inPic.dto.request.ImageUpdateRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.ForbiddenRequestException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageServiceTest {
    @Mock
    ImageRepository imageRepository;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageFileService imageFileService;
    @Mock
    CategoryService categoryService;
    @Mock
    AlbumService albumService;
    @Mock
    ActionService actionService;
    @Mock
    ImageGetService imageGetService;
    @InjectMocks
    ImageService imageService;

    @SneakyThrows
    @Test
    void testGetLikedImage() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        Image image = Image.builder().id(1L).name("nature").imgName("nature.jpg").build();
        List<Image> images = List.of(image);
        Mockito.when(imageRepository.findByLike_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(images);
        Mockito.when(imageFileService.getImage(image.getImgName())).thenReturn("image");
        List<ImageResponse> exceptedImageResponses = List.of(ImageResponse.builder()
                .image_id(1L)
                .name("nature")
                .imgName("nature.jpg").image("image").build()), actualImageResponses = imageService.getLikedImage(userInformationRequest);
        assertEquals(actualImageResponses, exceptedImageResponses);
        Mockito.when(imageRepository.findByLike_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(new ArrayList<>());
        assertThrows(NotFoundDatabaseException.class, () -> imageService.getLikedImage(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testGetImageData() {
        Long user_id = 1L, image_id = 1L;
        ImagePageRequest imagePageRequest = new ImagePageRequest(image_id, user_id);
        UserImage userImage = UserImage.builder()
                .id(1L).name("userImage.jpg").build();
        User user = User.builder().id(user_id)
                .userImage(userImage)
                .name("angel").build();
        Image image = Image.builder()
                .id(image_id)
                .user(user)
                .name("image")
                .like(List.of(Like.builder().id(1L).build()))
                .imgName("image.jpg")
                .description("hello")
                .build();
        Mockito.when(imageGetService.getImage(imagePageRequest.getImage_id())).thenReturn(image);
        Mockito.when(imageFileService.getImage(image.getImgName())).thenReturn("image");
        Mockito.when(imageFileService.getImage(userImage.getName())).thenReturn("image");
        ImagePageResponse imagePageResponse = ImagePageResponse.builder()
                .user_id(user_id)
                .imgDescription(image.getDescription())
                .userName(user.getName())
                .imgSystemName(image.getImgName())
                .image("image")
                .name(image.getName())
                .userImg("image")
                .likeCount(image.getLike().size()).build(),
                actualImageResponse = imageService.getImageData(imagePageRequest);
        assertEquals(imagePageResponse, actualImageResponse);
    }

    @SneakyThrows
    @Test
    void testAddImage() {
        Long user_id = 1L;
        byte[] imageBytes = {1, 3, 4};
        MultipartFile multipartFile = new MockMultipartFile("hello.jpg", imageBytes);
        Category category = Category.builder().category("category").id(1L).build();
        Album album = Album.builder().id(1L).build();
        ImageAddRequest imageAddRequest = ImageAddRequest.builder()
                .user_id(user_id)
                .name("nature")
                .description("hello")
                .album_id(1L)
                .category_id(1L).build();
        User user = User.builder().id(user_id).build();
        Image image = Image.builder().user(user)
                .category(category)
                .description(imageAddRequest.getDescription())
                .name(imageAddRequest.getName())
                .imgName(multipartFile.getOriginalFilename())
                .path("files").build();
        Mockito.when(userGetService.getUser(imageAddRequest.getUser_id())).thenReturn(user);
        Mockito.when(categoryService.getCategory(imageAddRequest.getCategory_id())).thenReturn(category);
        Mockito.when(imageFileService.getDirectoryPath()).thenReturn("files");
        Mockito.when(albumService.getAlbum(1L)).thenReturn(album);
        Mockito.when(imageRepository.save(image)).thenReturn(image);
        String excepted = "Image adding is successful",
                actual = imageService.addImage(imageAddRequest, multipartFile);
        assertEquals(actual, excepted);
    }

    @SneakyThrows
    @Test
    void testGetUserCreatedImages() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        Image image = Image.builder().id(1L).name("nature").imgName("nature.jpg").build();
        List<Image> images = List.of(image);
        Mockito.when(imageRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(images);
        Mockito.when(imageFileService.getImage(image.getImgName())).thenReturn("image");
        List<ImageResponse> exceptedImageResponses = List.of(ImageResponse.builder()
                .image_id(1L)
                .name("nature")
                .imgName("nature.jpg").image("image").build()),
                actualImageResponses = imageService.getUserCreatedImages(userInformationRequest);
        assertEquals(actualImageResponses,exceptedImageResponses);
        Mockito.when(imageRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(new ArrayList<>());
        assertThrows(NotFoundDatabaseException.class,()->imageService.getUserCreatedImages(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testUpdateImageData() {
        Long image_id=1L;
        ImageUpdateRequest updateRequest = ImageUpdateRequest.builder()
                .image_id(image_id)
                .name("image")
                .description("hello")
                .user_id(1L)
                .category_id(1L).build();
        Category category = Category.builder().id(1L).category("dog").build();
        Image imageNormal=Image.builder()
                .id(image_id)
                .name(updateRequest.getName())
                .category(category)
                .description(updateRequest.getDescription())
                .user(User.builder().id(1L).build())
                .build(),imageForbidden=Image.builder()
                .id(image_id)
                .name(updateRequest.getName())
                .category(category)
                .description(updateRequest.getDescription())
                .user(User.builder().id(2L).build())
                .build(),
                imageNotFound=Image.builder()
                        .id(image_id)
                        .name("cat")
                        .category(Category.builder().id(2L).category("cat").build())
                        .description("cat does not like dog")
                        .user(User.builder().id(1L).build())
                        .build();
        Mockito.when(imageGetService.getImage(updateRequest.getImage_id())).thenReturn(imageNormal);
        Mockito.when(imageRepository.save(imageNormal)).thenReturn(imageNormal);
        String excepted="Image updating is successful", actual = imageService.updateImageData(updateRequest);
        assertEquals(actual,excepted);
        Mockito.when(imageGetService.getImage(updateRequest.getImage_id())).thenReturn(imageForbidden);
        assertThrows(ForbiddenRequestException.class,()->imageService.updateImageData(updateRequest));
        Mockito.when(imageGetService.getImage(updateRequest.getImage_id())).thenReturn(imageNormal);
        Mockito.when(imageRepository.save(imageNormal)).thenReturn(imageNotFound);
        assertThrows(NoAddDatabaseException.class,()->imageService.updateImageData(updateRequest));
    }
}