package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Friend;
import angeelya.inPic.database.model.Like;
import angeelya.inPic.database.model.LikeNotification;
import angeelya.inPic.database.model.SubscriptionNotification;
import angeelya.inPic.database.repository.SubscriptionNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.LikeNotificationResponse;
import angeelya.inPic.dto.response.SubscriptionNotificationResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionNotificationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ImageFileService imageFileService;

    private final SubscriptionNotificationRepository subscriptionNotificationRepository;

    public void addNotification(Friend friend) {
        try {
            subscriptionNotificationRepository.save(SubscriptionNotification.builder()
                    .friend(friend)
                    .isRead(false)
                    .build());
        } catch (DataAccessException e) {
            logger.error("Subscription notification did not add");
        }
    }

    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest) {
        List<SubscriptionNotification> subscriptionNotifications = subscriptionNotificationRepository.findByFriend_SubFriend_IdAndRead(userInformationRequest.getUser_id(),false);
        if(subscriptionNotifications.isEmpty()) return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }

    public List<SubscriptionNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<SubscriptionNotification> subscriptionNotifications = getSubscriptionsNotifications(userInformationRequest.getUser_id());
        List<SubscriptionNotificationResponse> subscriptionNotificationResponses = new ArrayList<>();
        for (SubscriptionNotification subscriptionNotification : subscriptionNotifications) {
            Friend friend = subscriptionNotification.getFriend();
            subscriptionNotificationResponses.add(SubscriptionNotificationResponse.builder()
                    .friend_id(friend.getUser().getId())
                    .friendName(friend.getUser().getName())
                    .friendImage(imageFileService.getImage(friend.getUser().getUserImage().getName())).build());
        }
        return subscriptionNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<SubscriptionNotification> subscriptionNotifications = getSubscriptionsNotifications(userInformationRequest.getUser_id());
        try {
            subscriptionNotifications = subscriptionNotificationRepository.saveAll(subscriptionNotifications.stream().map(subscriptionNotification -> {
                        subscriptionNotification.setRead(true);
                        return subscriptionNotification;
                    }
            ).collect(Collectors.toList()));
            if (subscriptionNotifications.isEmpty()) throw new NoAddDatabaseException("Failed to update subscription notification");
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to update subscription notification");
        }
        return "Update subscription notification read is successful";
    }
    private List<SubscriptionNotification> getSubscriptionsNotifications(Long user_id) throws NotFoundDatabaseException {
        List<SubscriptionNotification> subscriptionNotifications = subscriptionNotificationRepository.findByFriend_SubFriend_Id(user_id);
        if (subscriptionNotifications.isEmpty()) throw new NotFoundDatabaseException("No subscription notifications found");
        return subscriptionNotifications;
    }
}
