package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.Role;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.AlbumRepository;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.AlbumPageDataResponse;
import angeelya.inPic.dto.response.ImageResourceResponse;
import angeelya.inPic.dto.response.UserAlbumResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
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
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceTest {
    @Mock
    AlbumRepository albumRepository;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageGetService imageGetService;
    @Mock
    ImageRepository imageRepository;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    AlbumService albumService;


    @SneakyThrows
    @Test
    void testGetAllUserAlbum() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        List<Album> albums = List.of(Album.builder().id(1L)
                .user(User.builder().id(1L).build()).name("Album").security(true).build(), Album.builder().id(1L)
                .user(User.builder().id(2L).build()).name("Album").security(true).build());
        List<Album> exceptedAlbums = albums.stream().filter(album -> album.getUser().getId().equals(1L)).collect(Collectors.toList());
        Mockito.when(albumRepository.findByUser_Id(user_id)).thenReturn(exceptedAlbums);
        List<Album> actualAlbum = albumService.getAllUserAlbum(userInformationRequest);
        assertEquals(actualAlbum, exceptedAlbums);
        verify(albumRepository).findByUser_Id(user_id);
    }

    @Test()
    public void testGetAllUserAlbum_ThrowsException() {
        UserInformationRequest userInformationRequest = new UserInformationRequest();
        userInformationRequest.setUser_id(3L);
        when(albumRepository.findByUser_Id(userInformationRequest.getUser_id())).thenReturn(new ArrayList<>());
        assertThrows(NotFoundDatabaseException.class, () -> albumService.getAllUserAlbum(userInformationRequest));
        verify(albumRepository).findByUser_Id(userInformationRequest.getUser_id());
    }

    @SneakyThrows
    @Test
    void testGetUserAlbums() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        Image image = Image.builder().imgName("nature.jpg").name("Nature").path("/files").user(User.builder().id(1L).build()).build();
        List<Album> albums = List.of(Album.builder().id(1L)
                .user(User.builder().id(1L).build()).name("Album").images(List.of(image)).security(true).build(), Album.builder().id(2L)
                .user(User.builder().id(2L).build()).name("Album").security(true).build());
        List<Album> exceptedAlbums = albums.stream().filter(album -> album.getUser().getId().equals(user_id)).collect(Collectors.toList());
        Mockito.when(albumRepository.findByUser_Id(user_id)).thenReturn(exceptedAlbums);
        Mockito.when(imageFileService.getImage(image.getImgName())).thenReturn("image");
        List<UserAlbumResponse> exceptedUserAlbumResponses = List.of(UserAlbumResponse.builder().album_id(1L).name("Album").lastImage("image").build());
        List<UserAlbumResponse> actualUserAlbumResponses = albumService.getUserAlbums(userInformationRequest);
        assertEquals(actualUserAlbumResponses, exceptedUserAlbumResponses);
        verify(albumRepository).findByUser_Id(user_id);
        verify(imageFileService).getImage(image.getImgName());
    }

    @SneakyThrows
    @Test
    void testAddAlbum() {
        Long user_id = 1L;
        String excepted = "Album adding is successful";
        AlbumAddRequest albumAddRequest = AlbumAddRequest.builder().name("album").user_id(1L).security(true).imageRequests(
                List.of(ImageRequest.builder().image_id(1L).build())).build();
        List<Long> imagesLong = albumAddRequest.getImageRequests().stream().map(ImageRequest::getImage_id).collect(Collectors.toList());
        User user = User.builder()
                .id(1L).name("Angelina")
                .email("angelina@gmail.com")
                .password("1234").login("angel").role(Role.USER).build();
        List<Image> images = List.of(Image.builder().id(1L)
                .name("image")
                .imgName("image.jpg").user(user).build());
        Album album = Album.builder().name(albumAddRequest.getName()).security(albumAddRequest.getSecurity()).user(user).images(images).build();
        Mockito.when(userGetService.getUser(user_id)).thenReturn(user);
        Mockito.when(albumRepository.save(any(Album.class))).thenReturn(album);
        Mockito.when(imageRepository.findAllByIdIn(imagesLong)).thenReturn(images);
        String actual = albumService.addAlbum(albumAddRequest);
        assertEquals(actual, excepted);
        verify(userGetService).getUser(user_id);
        verify(albumRepository).save(album);
        verify(imageRepository).findAllByIdIn(imagesLong);
    }

    @SneakyThrows
    @Test
    void testGetAlbumPageData() {
        Long album_id = 1L;
        Image image = Image.builder()
                .id(1L)
                .imgName("nature.jpg")
                .name("Nature").path("/files")
                .user(User.builder().id(1L).build()).build();
        AlbumRequest albumRequest = AlbumRequest.builder().album_id(album_id).build();
        Album album = Album.builder().id(album_id)
                .user(User.builder().id(1L).build()).name("Album").images(List.of(image)).security(true).build();
        ImageResourceResponse imageResourceResponse = ImageResourceResponse.builder()
                .image_id(1L)
                .imgName("nature.jpg")
                .image("image").build();
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.ofNullable(album));
        Mockito.when(imageFileService.getImage(image.getImgName())).thenReturn("image");
        AlbumPageDataResponse expectedResponse = AlbumPageDataResponse.builder().album_id(album_id)
                .name("Album")
                .imageResourceResponses(List.of(imageResourceResponse)).build();
        AlbumPageDataResponse actualResponse = albumService.getAlbumPageData(albumRequest);
        assertEquals(actualResponse, expectedResponse);
        assertEquals(actualResponse.getImageResourceResponses(), expectedResponse.getImageResourceResponses());
        verify(albumRepository).findById(album_id);
        verify(imageFileService).getImage(image.getImgName());
    }

    @SneakyThrows
    @Test
    void testGetAlbum() {
        Long album_id = 1L;
        Album exceptedAlbum = Album.builder().id(album_id)
                .user(User.builder().id(1L).build()).name("Album").images(List.of(Image.builder()
                        .id(1L)
                        .imgName("nature.jpg")
                        .name("Nature").path("/files")
                        .user(User.builder().id(1L).build()).build())).security(true).build();
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.ofNullable(exceptedAlbum));
        Album actualAlbum = albumService.getAlbum(album_id);
        assertEquals(actualAlbum, exceptedAlbum);
        verify(albumRepository).findById(album_id);

    }

    @Test
    void testGetAlbum_ThrowsException() {
        Long album_id = 1L;
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class, () -> albumService.getAlbum(album_id));
        verify(albumRepository).findById(album_id);
    }

    @SneakyThrows
    @Test
    void testDeleteImageFromAlbum() {
        Long album_id = 1L;
        Image image = Image.builder().id(1L).build(), image2 = Image.builder().id(2L).build();
        List<Image> images = new ArrayList<>();
        images.add(image);
        images.add(image2);
        Album albumNormal = Album.builder().id(album_id)
                .user(User.builder().id(1L).build()).name("Album").images(images).security(true).build(),
                albumException = Album.builder().id(album_id)
                        .user(User.builder().id(1L).build()).name("Album").images(List.of(image)).security(true).build();
        DeleteImageFromAlbumRequest deleteImageFromAlbumRequest = DeleteImageFromAlbumRequest.builder()
                .image_id(1L)
                .album_id(album_id).build();
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.ofNullable(albumNormal));
        Mockito.when(imageGetService.getImage(image.getId())).thenReturn(image);
        Mockito.when(albumRepository.save(any(Album.class))).thenReturn(albumNormal);
        String expected = "Images list updating is successful",
                actual = albumService.deleteImageFromAlbum(deleteImageFromAlbumRequest);
        assertEquals(actual, expected);
        Mockito.when(albumRepository.save(any(Album.class))).thenReturn(albumException);
        assertThrows(NoAddDatabaseException.class, () -> albumService.deleteImageFromAlbum(deleteImageFromAlbumRequest));
    }

    @SneakyThrows
    @Test
    void testUpdateAlbumData() {
        Long album_id = 1L;
        Album album = Album.builder().id(album_id)
                .user(User.builder().id(1L).build()).name("Album").images(List.of(Image.builder()
                        .id(1L)
                        .imgName("nature.jpg")
                        .name("Nature").path("/files")
                        .user(User.builder().id(1L).build()).build())).security(true).build(),
        albumException=Album.builder().id(album_id)
                .user(User.builder().id(1L).build()).name("Album").images(List.of(Image.builder()
                        .id(1L)
                        .imgName("nature.jpg")
                        .name("Nature").path("/files")
                        .user(User.builder().id(1L).build()).build())).security(true).build();
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.ofNullable(album));
        Mockito.when(albumRepository.save(any(Album.class))).thenReturn(album);
        AlbumUpdateRequest albumUpdateRequest = new AlbumUpdateRequest(album_id,"album",false);
        String excepted ="Album updating is successful", actual=albumService.updateAlbumData(albumUpdateRequest);
        assertEquals(actual,excepted);
        Mockito.when(albumRepository.save(any(Album.class))).thenReturn(albumException);
        assertThrows(NoAddDatabaseException.class, () -> albumService.updateAlbumData(albumUpdateRequest));
    }

    @SneakyThrows
    @Test
    void testDeleteAlbum() {
        Long album_id=1L;
        AlbumRequest albumRequest = new AlbumRequest(album_id);
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.empty());
        String excepted ="Album deleting is successful",actual=albumService.deleteAlbum(albumRequest);
        assertEquals(actual,excepted);
        Mockito.when(albumRepository.findById(album_id)).thenReturn(Optional.of(Album.builder().id(album_id).build()));
        assertThrows(DeleteDatabaseException.class, () -> albumService.deleteAlbum(albumRequest));

    }
}