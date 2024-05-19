package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.LikeNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeNotificationRepository extends CrudRepository<LikeNotification,Long> {
    List<LikeNotification> findByLike_User_Id(Long user_id);
    List<LikeNotification> saveAll(List<LikeNotification> likeNotifications);
    List<LikeNotification> findByLike_User_IdAndRead(Long user_id, Boolean isRead);
}
