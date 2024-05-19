package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.AdminNotification;
import angeelya.inPic.database.model.CommentNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminNotificationRepository extends CrudRepository<AdminNotification,Long> {
    List<AdminNotification> findByUser_Id(Long user_id);

    List<AdminNotification> saveAll(List<AdminNotification> adminNotifications);

    List<AdminNotification> findByUser_IdAndRead(Long user_id, boolean read);
}
