package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Long> {
    @Override
    List<Category> findAll();

    Optional<Category> findByCategory(String category);
}
