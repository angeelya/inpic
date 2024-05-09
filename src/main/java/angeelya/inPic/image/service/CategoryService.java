package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Category;
import angeelya.inPic.database.repository.CategoryRepository;
import angeelya.inPic.dto.request.CategoryAddingRequest;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategory() throws DatabaseNotFoundException {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) throw new DatabaseNotFoundException("No categories found");
        return categories;
    }
    public String addCategory(CategoryAddingRequest categoryAddingRequest) throws NoAddDatabaseException {
        Category category=categoryRepository.save(Category.builder().category(categoryAddingRequest.getCategory()).build());
        if(category==null) throw new NoAddDatabaseException("Failed to add category");
        return "Category adding is successful";
    }
}
