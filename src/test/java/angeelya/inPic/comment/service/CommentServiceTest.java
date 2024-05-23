package angeelya.inPic.comment.service;

import angeelya.inPic.database.model.Comment;
import angeelya.inPic.database.model.CommentNotification;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.CommentRepository;
import angeelya.inPic.dto.request.CommentAddRequest;
import angeelya.inPic.dto.request.CommentDeleteRequest;
import angeelya.inPic.dto.request.ImageRequest;
import angeelya.inPic.dto.response.CommentResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.ImageGetService;
import angeelya.inPic.notification.service.CommentNotificationService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageGetService imageGetService;
    @Mock
    ActionService actionService;
    @Mock
    CommentNotificationService commentNotificationService;
    @InjectMocks
    CommentService commentService;

    @SneakyThrows
    @Test
    void testAddComment() {
        CommentAddRequest commentAddRequest = CommentAddRequest
                .builder().image_id(1L)
                .user_id(1L).text("Hello").build();
        Image image =Image.builder().id(1L).build();
        User user= User.builder().id(1L).build();
        Comment comment = Comment.builder().user(user).image(image)
                        .text("Hello").build();
        CommentNotification commentNotification = CommentNotification.builder()
                .comment(comment).build();
        Mockito.when(userGetService.getUser(commentAddRequest.getUser_id())).thenReturn(user);
        Mockito.when(imageGetService.getImage(commentAddRequest.getImage_id()))
                .thenReturn(image);
        Mockito.when(commentNotificationService.makeNotification(comment)).thenReturn(commentNotification);
        Mockito.when( commentRepository.save(comment)).thenReturn(comment);
        assertEquals("Comment adding is successful",commentService.addComment(commentAddRequest));
    }

    @SneakyThrows
    @Test
    void testGetImageComments() {
        ImageRequest imageRequest =ImageRequest.builder().image_id(1L).build();
        Image image =Image.builder().id(1L).build();
        User user= User.builder().id(1L).build();
        Comment comment = Comment.builder().user(user).image(image)
                .text("Hello").build();
        CommentResponse commentResponse =CommentResponse.builder().image_id(comment.getImage().getId())
                .user_id(comment.getUser().getId())
                .text(comment.getText()).build();
        Mockito.when(commentRepository.findByImage_Id(imageRequest.getImage_id())).thenReturn(List.of(comment));
        assertEquals(List.of(commentResponse),commentService.getImageComments(imageRequest));
        Mockito.when(commentRepository.findByImage_Id(imageRequest.getImage_id())).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->commentService.getImageComments(imageRequest));
    }

    @SneakyThrows
    @Test
    void testDeleteComment() {
        Long user_id=1L,image_id=1L;
        CommentDeleteRequest commentDeleteRequest =CommentDeleteRequest
                .builder().image_id(image_id).user_id(user_id).build();
        Image image =Image.builder().id(image_id).build();
        User user= User.builder().id(user_id).build();
        Comment comment = Comment.builder().user(user).image(image)
                .text("Hello").build();
        Mockito.when(commentRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(Optional.of(comment));
        assertThrows(DeleteDatabaseException.class,()->commentService.deleteComment(commentDeleteRequest));
        Mockito.when(commentRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class,()->commentService.deleteComment(commentDeleteRequest));
    }

    @SneakyThrows
    @Test
    void testGetComment() {
        Long user_id=1L,image_id=1L;
        Image image =Image.builder().id(image_id).build();
        User user= User.builder().id(user_id).build();
        Comment comment = Comment.builder().user(user).image(image)
                .text("Hello").build();
        Mockito.when(commentRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(Optional.of(comment));
        assertEquals(comment,commentService.getComment(user_id,image_id));
        Mockito.when(commentRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class,()->commentService.getComment(user_id,image_id));
    }

    @SneakyThrows
    @Test
    void testUpdateComment() {
        Long user_id=1L,image_id=1L;
        Image image =Image.builder().id(image_id).build();
        User user= User.builder().id(user_id).build();
        CommentAddRequest commentAddRequest = CommentAddRequest
                .builder().image_id(1L)
                .user_id(1L).text("Hello12").build();
        Comment comment = Comment.builder().user(user).image(image)
                .text("Hello").build(),
        commentException=Comment.builder().user(user).image(image)
                .text("Hello").build();
        Mockito.when(commentRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(Optional.of(comment));
        Mockito.when(commentRepository.save(comment)).thenReturn(comment);
        assertEquals("Comment updating is successful",commentService.updateComment(commentAddRequest));
        Mockito.when(commentRepository.save(comment)).thenReturn(commentException);
        assertThrows(NoAddDatabaseException.class,()->commentService.updateComment(commentAddRequest));
        Mockito.when(commentRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class,()->commentService.updateComment(commentAddRequest));
    }
}