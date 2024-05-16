package angeelya.inPic.database.repository;
import angeelya.inPic.database.model.Search;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends CrudRepository<Search,Long> {
    List<Search> findByUser_Id(Long user_id);
}
