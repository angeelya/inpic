package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.CommentNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.CommentNotificationResponse;
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
class CommentNotificationServiceTest {

    @Mock
    CommentNotificationRepository commentNotificationRepository;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    CommentNotificationService commentNotificationService;

    @Test
    void testAddNotification() {
        Comment comment = Comment.builder().id(1L).build();
        CommentNotification commentNotification = CommentNotification.builder()
                .comment(comment)
                .isRead(false)
                .build();
        CommentNotification actual = commentNotificationService.makeNotification(comment);
        assertEquals(commentNotification, actual);
    }

    @Test
    void testCheckNotification() {
        Comment comment = Comment.builder().id(1L).build();
        CommentNotification commentNotification = CommentNotification.builder()
                .comment(comment)
                .isRead(false)
                .build();
        List<CommentNotification> commentNotifications = List.of(commentNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(commentNotificationRepository.findByComment_User_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(commentNotifications);
        CheckNotificationResponse actual = commentNotificationService.checkNotification(userInformationRequest);
        assertEquals(true, actual.getHaveNotification());
        Mockito.when(commentNotificationRepository.findByComment_User_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(List.of());
        actual = commentNotificationService.checkNotification(userInformationRequest);
        assertEquals(false, actual.getHaveNotification());
    }


    @SneakyThrows
    @Test
    void testGetNotification() {
        Comment comment = Comment.builder().id(1L)
                .user(User.builder()
                        .id(1L)
                        .userImage(UserImage.builder().id(1L).name("image.jpg").build())
                        .build())
                .image(Image.builder().id(1L).build()).build();
        CommentNotification commentNotification = CommentNotification.builder()
                .comment(comment)
                .isRead(false)
                .build();
        List<CommentNotification> commentNotifications = List.of(commentNotification);
        List<CommentNotificationResponse> commentNotificationResponses = List.of(CommentNotificationResponse.builder()
                .actor_id(comment.getUser().getId())
                .actorName(comment.getUser().getName())
                .actorImage("image")
                .image_id(comment.getImage().getId())
                .text(comment.getText()).build());
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(imageFileService.getImage("image.jpg")).thenReturn("image");
        Mockito.when(commentNotificationRepository.findByComment_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(commentNotifications);
        List<CommentNotificationResponse> actual = commentNotificationService.getNotification(userInformationRequest);
        assertEquals(commentNotificationResponses, actual);
        Mockito.when(commentNotificationRepository.findByComment_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> commentNotificationService.getNotification(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testReadNotification() {
        Comment comment = Comment.builder().id(1L)
                .user(User.builder().id(1L).build())
                .image(Image.builder().id(1L).build()).build();
        CommentNotification commentNotification = CommentNotification.builder()
                .comment(comment)
                .isRead(false)
                .build(), exceptionCommentNotification = CommentNotification.builder()
                .comment(comment)
                .isRead(false)
                .build();
        List<CommentNotification> commentNotifications = List.of(commentNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(commentNotificationRepository.findByComment_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(commentNotifications);
        Mockito.when(commentNotificationRepository.saveAll(commentNotifications)).thenReturn(commentNotifications);
        String excepted = "Update comment notification read is successful",
                actual = commentNotificationService.readNotification(userInformationRequest);
        assertEquals(excepted, actual);
        Mockito.when(commentNotificationRepository.saveAll(commentNotifications)).thenReturn(List.of());
        assertThrows(NoAddDatabaseException.class, () -> commentNotificationService.readNotification(userInformationRequest));
        Mockito.when(commentNotificationRepository.saveAll(commentNotifications)).thenReturn(List.of(exceptionCommentNotification));
        assertThrows(NoAddDatabaseException.class, () -> commentNotificationService.readNotification(userInformationRequest));
        Mockito.when(commentNotificationRepository.findByComment_User_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> commentNotificationService.readNotification(userInformationRequest));
    }

}