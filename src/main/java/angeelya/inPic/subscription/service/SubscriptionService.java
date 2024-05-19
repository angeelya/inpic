package angeelya.inPic.subscription.service;

import angeelya.inPic.database.model.Friend;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.FriendRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.CountSubscriptionResponse;
import angeelya.inPic.dto.response.UserSubscriberResponse;
import angeelya.inPic.dto.response.UserSubscriptionResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.notification.service.SubscriptionNotificationService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final FriendRepository friendRepository;
    private final UserService userService;
    private final ImageFileService imageFileService;
    private final SubscriptionNotificationService subscriptionNotificationService;

    public String addFriend(FriendAddRequest friendAddRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(friendAddRequest.getUser_id()),
                friend = userService.getUser(friendAddRequest.getFriend_id());
        try {
           Friend subFriend= friendRepository.save(Friend.builder().
                    user(user)
                    .subFriend(friend).build());
            subscriptionNotificationService.addNotification(subFriend);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to add friend");
        }
        return "Friend adding is successful";
    }

    public CountSubscriptionResponse getCounts(UserInformationRequest userInformationRequest) {
        return CountSubscriptionResponse.builder().countSubscription(friendRepository.countByUser_Id(userInformationRequest.getUser_id()))
                .countSubscribes(friendRepository.countBySubFriend_Id(userInformationRequest.getUser_id())).build();
    }


    public List<UserSubscriptionResponse> getUserSubscriptions(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Friend> friends = getFriend(userInformationRequest.getUser_id(), false);
        List<UserSubscriptionResponse> userSubscriptionResponses = new ArrayList<>();
        for (Friend friend : friends) {
            userSubscriptionResponses.add(UserSubscriptionResponse.builder()
                    .subscription_id(friend.getSubFriend().getId())
                    .subName(friend.getSubFriend().getName())
                    .subImage(imageFileService.getImage(friend.getSubFriend().getUserImage().getName())).build());
        }
        return userSubscriptionResponses;
    }

    public List<UserSubscriberResponse> getUserSubscribers(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Friend> friends = getFriend(userInformationRequest.getUser_id(), true);
        List<UserSubscriberResponse> userSubscriberResponses = new ArrayList<>();
        for (Friend friend : friends) {
            userSubscriberResponses.add(UserSubscriberResponse.builder()
                    .subscriber_id(friend.getUser().getId())
                    .subName(friend.getUser().getName())
                    .subImage(imageFileService.getImage(friend.getUser().getUserImage().getName())).build());
        }
        return userSubscriberResponses;
    }

    public String deleteSubscription(SubscriptionRequest subscriptionRequest) throws DeleteDatabaseException {
        try {
            friendRepository.deleteBySubFriend_IdAndUser_Id(subscriptionRequest.getSubscription_id(), subscriptionRequest.getUser_id());
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException("Failed to delete subscription");
        }
        return "Subscription deleting is successful";
    }

    public String deleteSubscriber(SubscriberRequest subscriberRequest) throws DeleteDatabaseException {
        try {
            friendRepository.deleteByUser_IdAndSubFriend_Id(subscriberRequest.getSubscriber_id(), subscriberRequest.getUser_id());
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException("Failed to delete subscriber");
        }
        return "Subscriber deleting is successful";
    }

    public CheckFriendResponse checkSubscription(SubscriptionRequest subscriptionRequest) {
        Optional<Friend> friend = friendRepository.findBySubFriend_IdAndUser_Id(subscriptionRequest.getSubscription_id(), subscriptionRequest.getUser_id());
        if (friend.isEmpty()) return CheckFriendResponse.builder().haveFriend(false).build();
        return CheckFriendResponse.builder().haveFriend(true).build();
    }
    public CheckFriendResponse checkSubscriber(SubscriberRequest subscriberRequest) {
        Optional<Friend> friend = friendRepository.findByUser_IdAndSubFriend_Id(subscriberRequest.getSubscriber_id(), subscriberRequest.getUser_id());
        if (friend.isEmpty()) return CheckFriendResponse.builder().haveFriend(false).build();
        return CheckFriendResponse.builder().haveFriend(true).build();
    }
    private List<Friend> getFriend(Long user_id, Boolean isSubscriber) throws NotFoundDatabaseException {
        List<Friend> friends = isSubscriber ? friendRepository.findBySubFriend_Id(user_id) : friendRepository.findByUser_Id(user_id);
        if (friends.isEmpty()) throw new NotFoundDatabaseException("No subscribers found");
        return friends;
    }
}
