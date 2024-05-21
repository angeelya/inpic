package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Action;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends CrudRepository<Action,Long> {
    List<Action> findAll();
    Action findByUser_IdAndImage_Id(@Param("user_id") Long user_id,@Param("image_id") Long image_id);
}
