package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.AdminNotification;
import angeelya.inPic.database.model.CommentNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminNotificationRepository extends CrudRepository<AdminNotification,Long> {
    List<AdminNotification> findByUser_Id(Long user_id);

    @Override
    <S extends AdminNotification> Iterable<S> saveAll(Iterable<S> entities);
    List<AdminNotification> findByUser_IdAndIsRead(Long user_id, boolean read);
}
