package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.UserImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends CrudRepository<UserImage,Long> {

}
