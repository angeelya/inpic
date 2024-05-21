package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.CommentNotification;
import angeelya.inPic.database.model.LikeNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentNotificationRepository extends CrudRepository<CommentNotification, Long> {
    List<CommentNotification> findByComment_User_Id(Long user_id);

    @Override
    <S extends CommentNotification> Iterable<S> saveAll(Iterable<S> entities);

    List<CommentNotification> findByComment_User_IdAndIsRead(Long user_id, boolean read);
}
