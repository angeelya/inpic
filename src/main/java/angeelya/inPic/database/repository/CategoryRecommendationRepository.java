package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.CategoryRecommendation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRecommendationRepository extends CrudRepository<CategoryRecommendation,Long> {
    List<CategoryRecommendation> saveAll( List<CategoryRecommendation> categoryRecommendations);
    List<CategoryRecommendation> findByUser_IdAndGradeGreaterThanEqual(Long user_id,Double minGrade);
    List<CategoryRecommendation> findByGradeGreaterThanEqual(Double minGrade);

}
