package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Like;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like,Long> {
    void deleteByUser_IdAndImage_Id(Long user_id,Long image_id);
    Optional<Like> findByUser_IdAndImage_Id(Long user_id, Long image_id);
}
