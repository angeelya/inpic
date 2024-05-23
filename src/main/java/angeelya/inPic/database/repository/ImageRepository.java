package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends CrudRepository<Image,Long> {
    List<Image> findByNameIsLikeIgnoreCaseOrCategory_CategoryIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(String name,String category, String description);
    List<Image> findByLike_User_Id(Long user_id);
    List<Image> findAllByIdIn(List<Long> ids);
    List<Image> findByUser_Id(Long user_id);
    List<Image> findAll();
}

