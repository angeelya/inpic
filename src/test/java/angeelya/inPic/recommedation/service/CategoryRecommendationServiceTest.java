package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.CategoryRecommendationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CategoryRecommendationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.CategoryService;
import angeelya.inPic.user.service.UserGetService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryRecommendationServiceTest {
    @Mock
    SlopeOne slopeOne;
    @Mock
    UserGetService userGetService;
    @Mock
    CategoryRecommendationRepository categoryRecommendationRepository;
    @Mock
    CategoryService categoryService;
    @InjectMocks
    CategoryRecommendationService categoryRecommendationService;

    @SneakyThrows
    @Test
    void testRecommend() {
        Long user_id = 1L;
        List<Action> actions = new ArrayList<>();
        User user = User.builder().id(user_id).build();
        Category category1 = Category.builder().id(1L).category("cat").build(),
                category2 = Category.builder().id(2L).category("dog").build();
        actions.add(Action.builder().user(User.builder().id(2L).build())
                .image(Image.builder().id(1L).
                        category(category1).build())
                .grade(0.5)
                .build());
        actions.add(Action.builder().user(User.builder().id(2L).build())
                .image(Image.builder()
                        .category(category2).id(2L).build())
                .grade(0.25).build());
        actions.add(Action.builder().user(user)
                .image(Image.builder().id(1L)
                        .category(category1).build())
                .grade(0.75).build());
        Map<Long, Map<Long, Double>> data = new HashMap<>();
        Map<Long,Double>user1=new HashMap<>(),user2=new HashMap<>();
        user1.put(1L,0.75);
        user2.put(1L,0.5);
        user2.put(2L,0.25);
        data.put(1L,user1);
        data.put(2L,user2);
        Map<Long, Double> recommendationsMap = new HashMap<>();
        recommendationsMap.put(1L, 0.75);
        recommendationsMap.put(2L, 0.25);
        CategoryRecommendation categoryRecommendation =  CategoryRecommendation.builder()
                .category(category1).user(user).grade(0.75).build(), categoryRecommendation2= CategoryRecommendation.builder()
                .category(category2).user(user).grade(0.25).build();
        List<CategoryRecommendation> categoryRecommendations = List.of(
               categoryRecommendation,
               categoryRecommendation2);
        Mockito.when(slopeOne.beginSlopeOne(data, user_id)).thenReturn(recommendationsMap);
        Mockito.when(categoryRecommendationRepository.findByUser_IdAndCategory_Id(user_id, categoryRecommendation.getCategory().getId()))
                .thenReturn(categoryRecommendation);
        Mockito.when(categoryRecommendationRepository.saveAll(anyList())).thenReturn(List.of(categoryRecommendation2));
        Mockito.when(categoryRecommendationRepository.save(categoryRecommendation)).thenReturn(categoryRecommendation);
        Mockito.when(categoryService.getCategory(categoryRecommendation.getCategory().getId())).thenReturn(category1);
        Mockito.when(categoryService.getCategory(categoryRecommendation2.getCategory().getId())).thenReturn(category2);
        Mockito.when(userGetService.getUser(user_id)).thenReturn(user);
        categoryRecommendationService.recommend(actions, user_id);
        assertEquals(categoryRecommendations,categoryRecommendationService.getCategoryRecommendations());
        Mockito.when(categoryRecommendationRepository.saveAll(anyList())).thenReturn(List.of());
        assertThrows(NoAddDatabaseException.class,()->categoryRecommendationService.recommend(actions,user_id));
        Mockito.when(categoryRecommendationRepository.save(categoryRecommendation)).thenReturn(CategoryRecommendation.builder().grade(1.0).build());
        assertThrows(NoAddDatabaseException.class,()->categoryRecommendationService.recommend(actions,user_id));
    }

    @SneakyThrows
    @Test
    void testGetPopularRecommendations() {
        Long user_id = 1L;
        User user = User.builder().id(user_id).build();
        Category category1 = Category.builder().id(1L).category("cat").build(),
                category2 = Category.builder().id(2L).category("dog").build();
        CategoryRecommendation categoryRecommendation =  CategoryRecommendation.builder()
                .category(category1).user(user).grade(0.75).build(), categoryRecommendation2= CategoryRecommendation.builder()
                .category(category2).user(user).grade(0.25).build();
        List<CategoryRecommendation> categoryRecommendations = List.of(
                categoryRecommendation,
                categoryRecommendation2);
        List<CategoryRecommendationResponse>categoryRecommendationResponses=
                List.of(CategoryRecommendationResponse.builder()
                                .category_id(category1.getId())
                                .category(category1.getCategory()).build(),
                        CategoryRecommendationResponse.builder()
                                .category_id(category2.getId())
                                .category(category2.getCategory()).build());
        Mockito.when(categoryRecommendationRepository.findByGradeGreaterThanEqual(0.5)).thenReturn(categoryRecommendations);
        assertEquals(categoryRecommendationResponses,categoryRecommendationService.getPopularRecommendations());
        Mockito.when(categoryRecommendationRepository.findByGradeGreaterThanEqual(0.5)).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->categoryRecommendationService.getPopularRecommendations());
    }

    @SneakyThrows
    @Test
    void testGetRecommendations() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        User user = User.builder().id(user_id).build();
        Category category1 = Category.builder().id(1L).category("cat").build(),
                category2 = Category.builder().id(2L).category("dog").build();
        CategoryRecommendation categoryRecommendation =  CategoryRecommendation.builder()
                .category(category1).user(user).grade(0.75).build(), categoryRecommendation2= CategoryRecommendation.builder()
                .category(category2).user(user).grade(0.25).build();
        List<CategoryRecommendation> categoryRecommendations = List.of(
                categoryRecommendation,
                categoryRecommendation2);
        List<CategoryRecommendationResponse>categoryRecommendationResponses=
                List.of(CategoryRecommendationResponse.builder()
                                .category_id(category1.getId())
                                .category(category1.getCategory()).build(),
                        CategoryRecommendationResponse.builder()
                                .category_id(category2.getId())
                                .category(category2.getCategory()).build());
        Mockito.when(categoryRecommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, 0.5)).thenReturn(categoryRecommendations);
        assertEquals(categoryRecommendationResponses,categoryRecommendationService.getRecommendations(userInformationRequest));
        Mockito.when(categoryRecommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, 0.5)).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->categoryRecommendationService.getRecommendations(userInformationRequest));
    }
}