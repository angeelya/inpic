package angeelya.inPic.search.service;

import angeelya.inPic.database.model.Category;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.Search;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.database.repository.SearchRepository;
import angeelya.inPic.dto.request.SearchImageRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
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
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SearchServiceTest {
    @Mock
    ImageRepository imageRepository;
    @Mock
    ImageFileService imageFileService;
    @Mock
    SearchRepository searchRepository;
    @Mock
    UserGetService userGetService;
    @InjectMocks
    SearchService searchService;

    @SneakyThrows
    @Test
    void searchImage() {
        String key = "hello";
        Long user_id=1L;
        SearchImageRequest searchImageRequest = SearchImageRequest.builder().user_id(user_id)
                .key("hello").build();
        User user = User.builder().id(user_id).build();
        Image image = Image.builder()
                .id(1L)
                .category(Category.builder().id(1L).category("hello").build())
                .imgName("hello.jpg")
                .name("name")
                .build(), image2 = Image.builder()
                .id(2L)
                .category(Category.builder().id(1L).category("hello").build())
                .imgName("name.jpg")
                .name("dog")
                .build();
        List<Image> images = List.of(image, image2);
        ImageResponse imageResponse1 = ImageResponse.builder()
                .image_id(image.getId())
                .image("image")
                .imgName(image.getImgName())
                .name(image.getName()).build(),
                imageResponse2 = ImageResponse.builder()
                        .image_id(image2.getId())
                        .image("image")
                        .imgName(image2.getImgName())
                        .name(image2.getName()).build();
        List<ImageResponse> imageResponses = List.of(imageResponse1,imageResponse2);
        Mockito.when(imageRepository.findByNameIsLikeIgnoreCaseOrCategory_CategoryIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(key + "%", key + "%", key + "%")).thenReturn(images);
        Mockito.when(imageFileService.getImage(any(String.class))).thenReturn("image");
        Mockito.when(userGetService.getUser(user_id)).thenReturn(user);
        assertEquals(imageResponses,searchService.searchImage(searchImageRequest));
        Mockito.when(imageFileService.getImage(any(String.class))).thenThrow(FileException.class);
        assertThrows(FileException.class,()->searchService.searchImage(searchImageRequest));
        Mockito.when(imageRepository.findByNameIsLikeIgnoreCaseOrCategory_CategoryIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(key + "%", key + "%", key + "%")).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->searchService.searchImage(searchImageRequest));
    }

    @SneakyThrows
    @Test
    void getSearchesHistory() {
        Long user_id=1L;
        List<Search> searches=List.of(Search.builder().id(1L).build());
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        Mockito.when(searchRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(searches);
        assertEquals(searches,searchService.getSearchesHistory(userInformationRequest));
        Mockito.when(searchRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->searchService.getSearchesHistory(userInformationRequest));
    }
}