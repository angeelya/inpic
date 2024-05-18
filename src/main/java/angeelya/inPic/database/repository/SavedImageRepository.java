package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.SavedImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedImageRepository extends CrudRepository<SavedImage,Long> {
    List<SavedImage> findByUser_Id(Long user_id);
}
