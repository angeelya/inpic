package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image,Long> {
    List<Image> findByNameIsLikeIgnoreCaseOrCategory_CategoryIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(String name,String category, String description);
    List<Image> findByLike_User_Id(Long user_id);
    List<Image> findAllById(List<Long> ids);
    List<Image> findByUser_Id(Long user_id);
    List<Image> findAll();
}

