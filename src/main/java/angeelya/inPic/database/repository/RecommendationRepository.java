package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Recommendation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends CrudRepository<Recommendation,Long> {

    @Override
    <S extends Recommendation> Iterable<S> saveAll(Iterable<S> entities);

    List<Recommendation> findByUser_IdAndGradeGreaterThanEqual(Long user_id, Double minGrade);
    Recommendation findByUser_IdAndImage_Id(Long user_id, Long image_id);
}
