package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.LikeNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.LikeNotificationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LikeNotificationServiceTest {

    @Mock
    LikeNotificationRepository likeNotificationRepository;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    LikeNotificationService likeNotificationService;

    @Test
    void testAddNotification() {
        Like like = Like.builder().id(1L).build();
        LikeNotification likeNotification = LikeNotification.builder()
                .like(like)
                .isRead(false)
                .build();
        LikeNotification actual = likeNotificationService.makeNotification(like);
        assertEquals(likeNotification, actual);
    }

    @Test
    void testCheckNotification() {
        Like like = Like.builder().id(1L).build();
        LikeNotification likeNotification = LikeNotification.builder()
                .like(like)
                .isRead(false)
                .build();
        List<LikeNotification> likeNotifications = List.of(likeNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(likeNotificationRepository.findByLike_User_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(likeNotifications);
        CheckNotificationResponse actual = likeNotificationService.checkNotification(userInformationRequest);
        assertEquals(true, actual.getHaveNotification());
        Mockito.when(likeNotificationRepository.findByLike_User_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(List.of());
        actual = likeNotificationService.checkNotification(userInformationRequest);
        assertEquals(false, actual.getHaveNotification());
    }


    @SneakyThrows
    @Test
    void testGetNotification() {
        Like like = Like.builder().id(1L)
                .user(User.builder()
                        .id(1L)
                        .userImage(UserImage.builder().id(1L).name("image.jpg").build())
                        .build())
                .image(Image.builder().id(1L).build()).build();
        LikeNotification likeNotification = LikeNotification.builder()
                .like(like)
                .isRead(false)
                .build();
        List<LikeNotification> likeNotifications = List.of(likeNotification);
        List<LikeNotificationResponse> likeNotificationResponses = List.of(LikeNotificationResponse.builder()
                .actor_id(like.getUser().getId())
                .actorName(like.getUser().getName())
                        .actorImage("image")
                .image_id(like.getImage().getId())
                .imgName(like.getImage().getImgName())
                .isRead(likeNotification.getIsRead()).build());
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(imageFileService.getImage("image.jpg")).thenReturn("image");
        Mockito.when(likeNotificationRepository.findByLike_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(likeNotifications);
        List<LikeNotificationResponse> actual = likeNotificationService.getNotification(userInformationRequest);
        assertEquals(likeNotificationResponses, actual);
        Mockito.when(likeNotificationRepository.findByLike_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> likeNotificationService.getNotification(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testReadNotification() {
        Like like = Like.builder().id(1L)
                .user(User.builder().id(1L).build())
                .image(Image.builder().id(1L).build()).build();
        LikeNotification likeNotification = LikeNotification.builder()
                .like(like)
                .isRead(false)
                .build(), exceptionLikeNotification = LikeNotification.builder()
                .like(like)
                .isRead(false)
                .build();
        List<LikeNotification> likeNotifications = List.of(likeNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(likeNotificationRepository.findByLike_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(likeNotifications);
        Mockito.when(likeNotificationRepository.saveAll(likeNotifications)).thenReturn(likeNotifications);
        String excepted = "Update like notification read is successful",
                actual = likeNotificationService.readNotification(userInformationRequest);
        assertEquals(excepted, actual);
        Mockito.when(likeNotificationRepository.saveAll(likeNotifications)).thenReturn(List.of());
        assertThrows(NoAddDatabaseException.class, () ->  likeNotificationService.readNotification(userInformationRequest));
        Mockito.when(likeNotificationRepository.saveAll(likeNotifications)).thenReturn(List.of(exceptionLikeNotification));
        assertThrows(NoAddDatabaseException.class, () -> likeNotificationService.readNotification(userInformationRequest));
        Mockito.when(likeNotificationRepository.findByLike_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> likeNotificationService.readNotification(userInformationRequest));
    }

}