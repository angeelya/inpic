package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment,Long> {
    Optional<Comment> findByUser_IdAndImage_Id(Long user_id, Long image_id);
    List<Comment> findByImage_Id(Long image_id);
}
