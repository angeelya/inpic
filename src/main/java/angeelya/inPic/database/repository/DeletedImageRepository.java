package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.DeletedImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedImageRepository extends CrudRepository<DeletedImage,Long> {

}
