package angeelya.inPic.subscription.service;

import angeelya.inPic.database.model.Friend;
import angeelya.inPic.database.model.SubscriptionNotification;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.FriendRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.CountSubscriptionResponse;
import angeelya.inPic.dto.response.UserSubscriberResponse;
import angeelya.inPic.dto.response.UserSubscriptionResponse;
import angeelya.inPic.exception_handling.exception.ExistException;
import angeelya.inPic.exception_handling.exception.ForbiddenRequestException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.notification.service.SubscriptionNotificationService;
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
class SubscriptionServiceTest {
    @Mock
    FriendRepository friendRepository;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageFileService imageFileService;
    @Mock
    SubscriptionNotificationService subscriptionNotificationService;
    @InjectMocks
    SubscriptionService subscriptionService;
    @SneakyThrows
    @Test
    void testAddFriend() {
        Long user_id = 1L, friend_id = 2L;
        User user = User.builder().id(user_id).build(),
                subFriend = User.builder().id(friend_id).build();
        FriendAddRequest friendAddRequest =
                new FriendAddRequest(user_id, friend_id);
        Friend friend = Friend.builder().subFriend(subFriend).user(user).build();
        SubscriptionNotification subscriptionNotification=SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build();
        Mockito.when(friendRepository.findBySubFriend_IdAndUser_Id(friendAddRequest.getFriend_id(), friendAddRequest.getUser_id()))
                .thenReturn(Optional.empty());
        Mockito.when(userGetService.getUser(friendAddRequest.getUser_id())).thenReturn(user);
        Mockito.when(userGetService.getUser(friendAddRequest.getFriend_id())).thenReturn(subFriend);
        Mockito.when(subscriptionNotificationService.makeNotification(friend)).thenReturn(subscriptionNotification);
        assertEquals("Friend adding is successful",subscriptionService.addFriend(friendAddRequest));
        Mockito.when(friendRepository.findBySubFriend_IdAndUser_Id(friendAddRequest.getFriend_id(), friendAddRequest.getUser_id()))
                .thenReturn(Optional.of(friend));
        assertThrows(ExistException.class,()->subscriptionService.addFriend(friendAddRequest));
        FriendAddRequest friendAddRequestException =new FriendAddRequest(1L,1L);
        assertThrows(ForbiddenRequestException.class,()->subscriptionService.addFriend(friendAddRequestException));

    }

    @Test
    void testGetCounts() {
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        CountSubscriptionResponse countSubscriptionResponse = new CountSubscriptionResponse(12,13);
        Mockito.when(friendRepository.countByUser_Id(userInformationRequest.getUser_id())).thenReturn(12);
        Mockito.when(friendRepository.countBySubFriend_Id(userInformationRequest.getUser_id())).thenReturn(13);
        assertEquals(countSubscriptionResponse,subscriptionService.getCounts(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testGetUserSubscriptions() {
        Long user_id=1L,friend_id=2L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        User user = User.builder().id(user_id).build(),
                subFriend = User.builder().id(friend_id).build();
        Friend friend = Friend.builder().subFriend(subFriend).user(user).build();
        UserSubscriptionResponse userSubscriptionResponse = UserSubscriptionResponse.builder()
                .subscription_id(friend.getSubFriend().getId())
                .subName(friend.getSubFriend().getName()).build();
        Mockito.when(friendRepository.findByUser_Id(user_id)).thenReturn(List.of(friend));
        assertEquals(List.of(userSubscriptionResponse),subscriptionService.getUserSubscriptions(userInformationRequest));
        Mockito.when(friendRepository.findByUser_Id(user_id)).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->subscriptionService.getUserSubscriptions(userInformationRequest));

    }

    @SneakyThrows
    @Test
    void testGetUserSubscribers() {
        Long user_id=1L,friend_id=2L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        User user = User.builder().id(user_id).build(),
                subFriend = User.builder().id(friend_id).build();
        Friend friend = Friend.builder().subFriend(subFriend).user(user).build();
        UserSubscriberResponse userSubscriberResponse = UserSubscriberResponse.builder()
                .subscriber_id(friend.getUser().getId())
                .subName(friend.getUser().getName()).build();
        Mockito.when(friendRepository.findBySubFriend_Id(user_id)).thenReturn(List.of(friend));
        assertEquals(List.of(userSubscriberResponse),subscriptionService.getUserSubscribers(userInformationRequest));
        Mockito.when(friendRepository.findBySubFriend_Id(user_id)).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->subscriptionService.getUserSubscribers(userInformationRequest));


    }

    @SneakyThrows
    @Test
    void testDeleteSubscription() {
        SubscriptionRequest subscriptionRequest= SubscriptionRequest.builder()
                .subscription_id(2L)
                .user_id(1L).build();
        assertEquals("Subscription deleting is successful",subscriptionService.deleteSubscription(subscriptionRequest));
    }

    @SneakyThrows
    @Test
    void testDeleteSubscriber() {
        SubscriberRequest subscriberRequest= SubscriberRequest.builder()
                .subscriber_id(1L)
                .user_id(2L).build();
        assertEquals("Subscriber deleting is successful",subscriptionService.deleteSubscriber(subscriberRequest));
    }

    @Test
    void testCheckSubscription() {
        User user = User.builder().id(1L).build(),
                subFriend = User.builder().id(2L).build();
        Friend friend = Friend.builder().subFriend(subFriend).user(user).build();

        SubscriptionRequest subscriptionRequest= SubscriptionRequest.builder()
                .subscription_id(2L)
                .user_id(1L).build();
        Mockito.when(friendRepository.findBySubFriend_IdAndUser_Id(subscriptionRequest.getSubscription_id(), subscriptionRequest.getUser_id()))
                .thenReturn(Optional.of(friend));
        CheckFriendResponse checkFriendResponse = new CheckFriendResponse(true);
        assertEquals(checkFriendResponse,subscriptionService.checkSubscription(subscriptionRequest));
        Mockito.when(friendRepository.findBySubFriend_IdAndUser_Id(subscriptionRequest.getSubscription_id(), subscriptionRequest.getUser_id()))
                .thenReturn(Optional.empty());
         checkFriendResponse = new CheckFriendResponse(false);
        assertEquals(checkFriendResponse,subscriptionService.checkSubscription(subscriptionRequest));

    }

    @Test
    void testCheckSubscriber() {
        User user = User.builder().id(1L).build(),
                subFriend = User.builder().id(2L).build();
        Friend friend = Friend.builder().subFriend(subFriend).user(user).build();
        SubscriberRequest subscriberRequest= SubscriberRequest.builder()
                .subscriber_id(1L)
                .user_id(2L).build();
        Mockito.when( friendRepository.findByUser_IdAndSubFriend_Id(subscriberRequest.getSubscriber_id(), subscriberRequest.getUser_id()))
                .thenReturn(Optional.of(friend));
        CheckFriendResponse checkFriendResponse = new CheckFriendResponse(true);
        assertEquals(checkFriendResponse,subscriptionService.checkSubscriber(subscriberRequest));
        Mockito.when( friendRepository.findByUser_IdAndSubFriend_Id(subscriberRequest.getSubscriber_id(), subscriberRequest.getUser_id()))
                .thenReturn(Optional.empty());
        checkFriendResponse = new CheckFriendResponse(false);
        assertEquals(checkFriendResponse,subscriptionService.checkSubscriber(subscriberRequest));

    }
}