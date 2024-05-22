package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Category;
import angeelya.inPic.database.repository.CategoryRepository;
import angeelya.inPic.dto.request.CategoryAddingRequest;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.recommedation.service.CategoryRecommendationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {
    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    CategoryService categoryService;

    @SneakyThrows
    @Test
    void getAllCategory() {
        Long category_id=1L;
        List<Category> exceptedCategories = List.of(Category.builder()
                .id(category_id)
                .category("category").build());
        Mockito.when(categoryRepository.findAll()).thenReturn(exceptedCategories);
        List<Category> actualCategories= categoryService.getAllCategory();
        assertEquals(actualCategories,exceptedCategories);
        Mockito.when(categoryRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(NotFoundDatabaseException.class,() ->categoryService.getAllCategory());
    }

    @SneakyThrows
    @Test
    void addCategory() {
        CategoryAddingRequest categoryAddingRequest =
                CategoryAddingRequest.builder().category("Category").build();
        Category category = Category.builder().category("Category").build();
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        String excepted ="Category adding is successful",
                actual= categoryService.addCategory(categoryAddingRequest);
        assertEquals(actual,excepted);
    }

    @SneakyThrows
    @Test
    void getCategory() {
        Long category_id=1L;
        Category category = Category.builder().id(category_id).category("Category").build();
        Mockito.when(categoryRepository.findById(category_id)).thenReturn(Optional.of(category));
        Category actualCategory = categoryService.getCategory(category_id);
        assertEquals(actualCategory,category);
        Mockito.when(categoryRepository.findById(category_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class,() ->categoryService.getCategory(category_id));
    }
}