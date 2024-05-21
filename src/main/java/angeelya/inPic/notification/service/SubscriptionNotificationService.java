package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Friend;
import angeelya.inPic.database.model.SubscriptionNotification;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.SubscriptionNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.SubscriptionNotificationResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionNotificationService {
    private final ImageFileService imageFileService;

    private final SubscriptionNotificationRepository subscriptionNotificationRepository;
    private static final String MS_FAILED_UPDATE = "Failed to update subscription notification",
            MS_SUCCESS_UPDATE = "Update subscription notification read is successful", MS_NOT_FOUND = "No subscription notifications found";


    public SubscriptionNotification makeNotification(Friend friend) {

        return SubscriptionNotification.builder()
                .friend(friend)
                .isRead(false)
                .build();
    }

    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest) {
        List<SubscriptionNotification> subscriptionNotifications = subscriptionNotificationRepository.findByFriend_SubFriend_IdAndIsRead(userInformationRequest.getUser_id(), false);
        if (subscriptionNotifications.isEmpty())
            return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }

    public List<SubscriptionNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<SubscriptionNotification> subscriptionNotifications = getSubscriptionsNotifications(userInformationRequest.getUser_id());
        List<SubscriptionNotificationResponse> subscriptionNotificationResponses = new ArrayList<>();
        for (SubscriptionNotification subscriptionNotification : subscriptionNotifications) {
            Friend friend = subscriptionNotification.getFriend();
            UserImage userImage = friend.getUser().getUserImage();
            SubscriptionNotificationResponse subscriptionNotificationResponse = SubscriptionNotificationResponse.builder()
                    .friend_id(friend.getUser().getId())
                    .friendName(friend.getUser().getName()).build();
            if (userImage != null)
                subscriptionNotificationResponse.setFriendImage(imageFileService.getImage(userImage.getName()));
            subscriptionNotificationResponses.add(subscriptionNotificationResponse);
        }
        return subscriptionNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<SubscriptionNotification> subscriptionNotifications = getSubscriptionsNotifications(userInformationRequest.getUser_id());
        try {
            subscriptionNotifications = (List<SubscriptionNotification>) subscriptionNotificationRepository.saveAll(subscriptionNotifications.stream().map(subscriptionNotification -> {
                        subscriptionNotification.setRead(true);
                        return subscriptionNotification;
                    }
            ).collect(Collectors.toList()));
            if (subscriptionNotifications.isEmpty()) throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        }
        return MS_SUCCESS_UPDATE;
    }

    private List<SubscriptionNotification> getSubscriptionsNotifications(Long user_id) throws NotFoundDatabaseException {
        List<SubscriptionNotification> subscriptionNotifications = subscriptionNotificationRepository.findByFriend_SubFriend_Id(user_id);
        if (subscriptionNotifications.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return subscriptionNotifications;
    }
}
