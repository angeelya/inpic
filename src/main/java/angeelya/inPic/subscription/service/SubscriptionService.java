package angeelya.inPic.subscription.service;

import angeelya.inPic.database.model.Friend;
import angeelya.inPic.database.model.SubscriptionNotification;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.FriendRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.CountSubscriptionResponse;
import angeelya.inPic.dto.response.UserSubscriberResponse;
import angeelya.inPic.dto.response.UserSubscriptionResponse;
import angeelya.inPic.exception_handling.exception.*;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.notification.service.SubscriptionNotificationService;
import angeelya.inPic.user.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final FriendRepository friendRepository;
    private final UserGetService userGetService;
    private final ImageFileService imageFileService;
    private final SubscriptionNotificationService subscriptionNotificationService;
    private static final String MS_FAILED_ADD = "Failed to add friend", MS_SUCCESS_ADD = "Friend adding is successful",
            MS_FAILED_DELETE_SUBSCRIPTION = "Failed to delete subscription", MS_SUCCESS_DELETE_SUBSCRIPTION = "Subscription deleting is successful",
            MS_FAILED_DELETE_SUBSCRIBER = "Failed to delete subscriber", MS_SUCCESS_DELETE_SUBSCRIBER = "Subscriber deleting is successful",
            MS_NOT_FOUND = "No friend found",MS_FORBIDDEN="User can not add himself",MS_EXIST="Subscription already exists";

    @Transactional
    public String addFriend(FriendAddRequest friendAddRequest) throws NotFoundDatabaseException, NoAddDatabaseException, ForbiddenRequestException, ExistException {
        if (friendAddRequest.getFriend_id().equals(friendAddRequest.getUser_id()))
            throw new ForbiddenRequestException(MS_FORBIDDEN);
        else if (friendRepository.findBySubFriend_IdAndUser_Id(friendAddRequest.getFriend_id(), friendAddRequest.getUser_id()).isPresent())
            throw new ExistException(MS_EXIST);
        User user = userGetService.getUser(friendAddRequest.getUser_id()),
                friend = userGetService.getUser(friendAddRequest.getFriend_id());
        try {
            Friend subFriend = Friend.builder().
                    user(user)
                    .subFriend(friend).build();
            SubscriptionNotification subscriptionNotification = subscriptionNotificationService.makeNotification(subFriend);
            subFriend.setSubscriptionNotifications(subscriptionNotification);
            friendRepository.save(subFriend);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_ADD);
        }
        return MS_SUCCESS_ADD;
    }

    public CountSubscriptionResponse getCounts(UserInformationRequest userInformationRequest) {
        return CountSubscriptionResponse.builder().countSubscription(friendRepository.countByUser_Id(userInformationRequest.getUser_id()))
                .countSubscribes(friendRepository.countBySubFriend_Id(userInformationRequest.getUser_id())).build();
    }


    public List<UserSubscriptionResponse> getUserSubscriptions(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Friend> friends = getFriend(userInformationRequest.getUser_id(), false);
        List<UserSubscriptionResponse> userSubscriptionResponses = new ArrayList<>();
        for (Friend friend : friends) {
            UserImage userImage = friend.getSubFriend().getUserImage();
            UserSubscriptionResponse userSubscriptionResponse = UserSubscriptionResponse.builder()
                    .subscription_id(friend.getSubFriend().getId())
                    .subName(friend.getSubFriend().getName()).build();
            if (userImage != null) userSubscriptionResponse.setSubImage(imageFileService.getImage(userImage.getName()));
            userSubscriptionResponses.add(userSubscriptionResponse);
        }
        return userSubscriptionResponses;
    }

    public List<UserSubscriberResponse> getUserSubscribers(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Friend> friends = getFriend(userInformationRequest.getUser_id(), true);
        List<UserSubscriberResponse> userSubscriberResponses = new ArrayList<>();
        for (Friend friend : friends) {
            UserImage userImage = friend.getUser().getUserImage();
            UserSubscriberResponse userSubscriberResponse = UserSubscriberResponse.builder()
                    .subscriber_id(friend.getUser().getId())
                    .subName(friend.getUser().getName()).build();
            if (userImage != null) userSubscriberResponse.setSubImage(imageFileService.getImage(userImage.getName()));
            userSubscriberResponses.add(userSubscriberResponse);
        }
        return userSubscriberResponses;
    }

    @Transactional
    public String deleteSubscription(SubscriptionRequest subscriptionRequest) throws DeleteDatabaseException {
        try {
            friendRepository.deleteBySubFriend_IdAndUser_Id(subscriptionRequest.getSubscription_id(), subscriptionRequest.getUser_id());
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE_SUBSCRIPTION);
        }
        return MS_SUCCESS_DELETE_SUBSCRIPTION;
    }

    @Transactional
    public String deleteSubscriber(SubscriberRequest subscriberRequest) throws DeleteDatabaseException {
        try {
            friendRepository.deleteByUser_IdAndSubFriend_Id(subscriberRequest.getSubscriber_id(), subscriberRequest.getUser_id());
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE_SUBSCRIBER);
        }
        return MS_SUCCESS_DELETE_SUBSCRIBER;
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
        if (friends.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return friends;
    }
}
