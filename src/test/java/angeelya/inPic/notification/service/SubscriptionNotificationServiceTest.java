package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Friend;
import angeelya.inPic.database.model.SubscriptionNotification;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.SubscriptionNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.SubscriptionNotificationResponse;
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
class SubscriptionNotificationServiceTest {
    @Mock
    SubscriptionNotificationRepository subscriptionNotificationRepository;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    SubscriptionNotificationService subscriptionNotificationService;

    @Test
    void testAddNotification() {
        Friend friend = Friend.builder().id(1L).build();
        SubscriptionNotification subscriptionNotification = SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build();
        SubscriptionNotification actual = subscriptionNotificationService.makeNotification(friend);
        assertEquals(subscriptionNotification, actual);
    }

    @Test
    void testCheckNotification() {
        Friend friend = Friend.builder().id(1L).build();
        SubscriptionNotification subscriptionNotification = SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build();
        List<SubscriptionNotification> subscriptionNotifications = List.of(subscriptionNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(subscriptionNotificationRepository.findByFriend_SubFriend_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(subscriptionNotifications);
        CheckNotificationResponse actual = subscriptionNotificationService.checkNotification(userInformationRequest);
        assertEquals(true, actual.getHaveNotification());
        Mockito.when(subscriptionNotificationRepository.findByFriend_SubFriend_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(List.of());
        actual = subscriptionNotificationService.checkNotification(userInformationRequest);
        assertEquals(false, actual.getHaveNotification());
    }


    @SneakyThrows
    @Test
    void testGetNotification() {
        Friend friend = Friend.builder().id(1L)
                .user(User.builder()
                        .id(1L)
                        .userImage(UserImage.builder().id(1L).name("image.jpg").build())
                        .build())
                .subFriend(User.builder().id(2L).build()).build();
        SubscriptionNotification subscriptionNotification = SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build();
        List<SubscriptionNotification> subscriptionNotifications = List.of(subscriptionNotification);
        List<SubscriptionNotificationResponse> subscriptionNotificationResponses = List.of(SubscriptionNotificationResponse.builder()
                .friend_id(friend.getUser().getId())
                .friendName(friend.getUser().getName())
                .friendImage("image")
                .build());
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(imageFileService.getImage("image.jpg")).thenReturn("image");
        Mockito.when(subscriptionNotificationRepository.findByFriend_SubFriend_Id(userInformationRequest.getUser_id()))
                .thenReturn(subscriptionNotifications);
        List<SubscriptionNotificationResponse> actual = subscriptionNotificationService.getNotification(userInformationRequest);
        assertEquals(subscriptionNotificationResponses, actual);
        Mockito.when(subscriptionNotificationRepository.findByFriend_SubFriend_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> subscriptionNotificationService.getNotification(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testReadNotification() {
        Friend friend = Friend.builder().id(1L)
                .user(User.builder()
                        .id(1L)
                        .userImage(UserImage.builder().id(1L).name("image.jpg").build())
                        .build())
                .user(User.builder().id(2L).build()).build();
        SubscriptionNotification subscriptionNotification = SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build(),exceptionSubscriptionNotification=SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build();
        List<SubscriptionNotification> subscriptionNotifications = List.of(subscriptionNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(subscriptionNotificationRepository.findByFriend_SubFriend_Id(userInformationRequest.getUser_id()))
                .thenReturn(subscriptionNotifications);
        Mockito.when(subscriptionNotificationRepository.saveAll(subscriptionNotifications)).thenReturn(subscriptionNotifications);
        String excepted = "Update subscription notification read is successful",
                actual = subscriptionNotificationService.readNotification(userInformationRequest);
        assertEquals(excepted, actual);
        Mockito.when(subscriptionNotificationRepository.saveAll(subscriptionNotifications)).thenReturn(List.of());
        assertThrows(NoAddDatabaseException.class, () -> subscriptionNotificationService.readNotification(userInformationRequest));
        Mockito.when(subscriptionNotificationRepository.saveAll(subscriptionNotifications)).thenReturn(List.of(exceptionSubscriptionNotification));
        assertThrows(NoAddDatabaseException.class, () -> subscriptionNotificationService.readNotification(userInformationRequest));
        Mockito.when(subscriptionNotificationRepository.findByFriend_SubFriend_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> subscriptionNotificationService.readNotification(userInformationRequest));
    }

}