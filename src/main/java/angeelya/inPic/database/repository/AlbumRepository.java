package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Album;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends CrudRepository<Album,Long> {
    List<Album> findByUser_Id(Long user_id);
}
