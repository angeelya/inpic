package angeelya.inPic.like.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.Like;
import angeelya.inPic.database.model.LikeNotification;
import angeelya.inPic.database.repository.LikeRepository;
import angeelya.inPic.dto.request.LikeRequest;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.ExistException;
import angeelya.inPic.image.service.ImageGetService;
import angeelya.inPic.notification.service.LikeNotificationService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserGetService;
import angeelya.inPic.database.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LikeServiceTest {
    @Mock
    LikeRepository likeRepository;
    @Mock
    ImageGetService imageGetService;
    @Mock
    UserGetService userGetService;
    @Mock
    ActionService actionService;
    @Mock
    LikeNotificationService likeNotificationService;
    @InjectMocks
    LikeService likeService;

    @SneakyThrows
    @Test
    void testAddLike() {
        LikeRequest likeRequest = new LikeRequest(1L,1L);
        Image image = Image.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        Like like = Like.builder().id(1L).user(user).image(image).build();
        LikeNotification likeNotification = LikeNotification.builder().id(1L).like(like).build();
        Mockito.when(imageGetService.getImage(likeRequest.getImage_id())).thenReturn(image);
        Mockito.when(userGetService.getUser(likeRequest.getUser_id())).thenReturn(user);
        Mockito.when(likeRepository.findByUser_IdAndImage_Id(user.getId(), image.getId())).thenReturn(Optional.empty());
        Mockito.when(likeNotificationService.makeNotification(like)).thenReturn(likeNotification);
        String excepted = "Like adding is successful",
                actual = likeService.addLike(likeRequest);
        assertEquals(excepted,actual);
        Mockito.when(likeRepository.findByUser_IdAndImage_Id(user.getId(), image.getId())).thenReturn(Optional.of(like));
        assertThrows(ExistException.class,()->likeService.addLike(likeRequest));
    }

    @SneakyThrows
    @Test
    void testDeleteLike() {
        LikeRequest likeRequest = new LikeRequest(1L,1L);
        Image image = Image.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        Like like = Like.builder().id(1L).user(user).image(image).build();
        String excepted="Like deleting is successful",
                actual=likeService.deleteLike(likeRequest);
        assertEquals(excepted,actual);
        Mockito.when(likeRepository.findByUser_IdAndImage_Id(likeRequest.getUser_id(),likeRequest.getImage_id())).thenReturn(Optional.of(like));
        assertThrows(DeleteDatabaseException.class,()->likeService.deleteLike(likeRequest));
    }
}