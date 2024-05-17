package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Category;
import angeelya.inPic.database.repository.CategoryRepository;
import angeelya.inPic.dto.request.CategoryAddingRequest;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategory() throws NotFoundDatabaseException {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) throw new NotFoundDatabaseException("No categories found");
        return categories;
    }

    public String addCategory(CategoryAddingRequest categoryAddingRequest) throws NoAddDatabaseException {
        saveCategory(Category.builder().category(categoryAddingRequest.getCategory()).build());
        return "Category adding is successful";
    }

    public Category getCategory(Long category_id) throws NotFoundDatabaseException {
        Optional<Category> category = categoryRepository.findById(category_id);
        if (category.isEmpty()) throw new NotFoundDatabaseException("Category not found");
        return category.get();
    }

    private void saveCategory(Category category) throws NoAddDatabaseException {
        try {
            categoryRepository.save(category);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to save category");
        }
    }
}
