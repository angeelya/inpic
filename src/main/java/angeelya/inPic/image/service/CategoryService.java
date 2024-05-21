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
    private  static final String MS_NOT_FOUND_LIST ="No categories found", MS_SUCCESS_ADD="Category adding is successful",
            MS_NOT_FOUND = "Category not found",MS_FAILED_SAVE="Failed to save category";


    public List<Category> getAllCategory() throws NotFoundDatabaseException {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_LIST);
        return categories;
    }

    public String addCategory(CategoryAddingRequest categoryAddingRequest) throws NoAddDatabaseException {
        saveCategory(Category.builder().category(categoryAddingRequest.getCategory()).build());
        return MS_SUCCESS_ADD;
    }

    public Category getCategory(Long category_id) throws NotFoundDatabaseException {
        Optional<Category> category = categoryRepository.findById(category_id);
        if (category.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return category.get();
    }

    private void saveCategory(Category category) throws NoAddDatabaseException {
        try {
            categoryRepository.save(category);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }
}
