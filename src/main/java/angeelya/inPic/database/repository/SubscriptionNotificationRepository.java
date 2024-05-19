package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.LikeNotification;
import angeelya.inPic.database.model.SubscriptionNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionNotificationRepository extends CrudRepository<SubscriptionNotification,Long> {
    List<SubscriptionNotification> findByFriend_SubFriend_Id(Long user_id);
    List<SubscriptionNotification> saveAll(List<SubscriptionNotification> likeNotifications);
    List<SubscriptionNotification> findByFriend_SubFriend_IdAndRead(Long user_id, Boolean isRead);
}
