package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.RecommendationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImageRecommendationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.image.service.ImageGetService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageRecommendationServiceTest {
    @Mock
    SlopeOne slopeOne;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageGetService imageGetService;
    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    ImageRecommendationService imageRecommendationService;

    @SneakyThrows
    @Test
    void recommend() {
        Long user_id = 1L;
        List<Action> actions = new ArrayList<>();
        User user = User.builder().id(user_id).build();
        Category category1 = Category.builder().id(1L).category("cat").build(),
                category2 = Category.builder().id(2L).category("dog").build();
        Image image1 = Image.builder().id(1L).
                category(category1)
                .imgName("hello.jpg")
                .name("hello").build(),
                image2 = Image.builder()
                        .category(category2).id(2L).imgName("dog.jpg")
                        .name("dog").build();
        actions.add(Action.builder().user(User.builder().id(2L).build())
                .image(image1)
                .grade(0.5)
                .build());
        actions.add(Action.builder().user(User.builder().id(2L).build())
                .image(image2)
                .grade(0.25).build());
        actions.add(Action.builder().user(user)
                .image(image1)
                .grade(0.75).build());
        Map<Long, Map<Long, Double>> data = new HashMap<>();
        Map<Long, Double> user1 = new HashMap<>(), user2 = new HashMap<>();
        user1.put(1L, 0.75);
        user2.put(1L, 0.5);
        user2.put(2L, 0.25);
        data.put(1L, user1);
        data.put(2L, user2);
        Map<Long, Double> recommendationsMap = new HashMap<>();
        recommendationsMap.put(1L, 0.75);
        recommendationsMap.put(2L, 0.25);
        Recommendation recommendation = Recommendation.builder()
                .image(image1).user(user).grade(0.75).build(), recommendation2 = Recommendation.builder()
                .image(image2).user(user).grade(0.25).build();
        List<Recommendation> recommendations = List.of(
                recommendation,
                recommendation2);
        Mockito.when(slopeOne.beginSlopeOne(data, user_id)).thenReturn(recommendationsMap);
        Mockito.when(recommendationRepository.findByUser_IdAndImage_Id(user_id, recommendation.getImage().getId()))
                .thenReturn(recommendation);
        Mockito.when(recommendationRepository.saveAll(anyList())).thenReturn(List.of(recommendation2));
        Mockito.when(recommendationRepository.save(recommendation)).thenReturn(recommendation);
        Mockito.when(imageGetService.getImage(recommendation.getImage().getId())).thenReturn(image1);
        Mockito.when(imageGetService.getImage(recommendation2.getImage().getId())).thenReturn(image2);
        Mockito.when(userGetService.getUser(user_id)).thenReturn(user);
        imageRecommendationService.recommend(actions, user_id);
        assertEquals(recommendations, imageRecommendationService.getRecommendations());
        Mockito.when(recommendationRepository.saveAll(anyList())).thenReturn(List.of());
        assertThrows(NoAddDatabaseException.class, () -> imageRecommendationService.recommend(actions, user_id));
        Mockito.when(recommendationRepository.save(recommendation)).thenReturn(Recommendation.builder().grade(1.0).build());
        assertThrows(NoAddDatabaseException.class, () -> imageRecommendationService.recommend(actions, user_id));
    }

    @SneakyThrows
    @Test
    void getRecommendations() {
        Long user_id = 1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        User user = User.builder().id(user_id).build();
        Category category1 = Category.builder().id(1L).category("cat").build(),
                category2 = Category.builder().id(2L).category("dog").build();
        Image image1 = Image.builder().id(1L).
                category(category1)
                .imgName("hello.jpg")
                .name("hello").build(),
                image2 = Image.builder()
                        .category(category2).id(2L).imgName("dog.jpg")
                        .name("dog").build();
        Map<Long, Map<Long, Double>> data = new HashMap<>();
        Map<Long, Double> user1 = new HashMap<>(), user2 = new HashMap<>();
        user1.put(1L, 0.75);
        user2.put(1L, 0.5);
        user2.put(2L, 0.25);
        data.put(1L, user1);
        data.put(2L, user2);
        Map<Long, Double> recommendationsMap = new HashMap<>();
        recommendationsMap.put(1L, 0.75);
        recommendationsMap.put(2L, 0.25);
        Recommendation recommendation = Recommendation.builder()
                .image(image1).user(user).grade(0.75).build(), recommendation2 = Recommendation.builder()
                .image(image2).user(user).grade(0.25).build();
        List<Recommendation> recommendations = List.of(
                recommendation,
                recommendation2);

        List<ImageRecommendationResponse> imageRecommendationResponses = List.of(ImageRecommendationResponse.builder()
                        .image_id(recommendation.getImage().getId())
                        .imgName(recommendation.getImage().getImgName())
                        .image("image")
                        .name(recommendation.getImage().getName()).build()
                , ImageRecommendationResponse.builder()
                        .image_id(recommendation2.getImage().getId())
                        .imgName(recommendation2.getImage().getImgName())
                        .image("image")
                        .name(recommendation2.getImage().getName()).build());
        Mockito.when(imageFileService.getImage(any(String.class))).thenReturn("image");
        Mockito.when(recommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, 0.5)).thenReturn(recommendations);
        assertEquals(imageRecommendationResponses, imageRecommendationService.getRecommendations(userInformationRequest));
        Mockito.when(recommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, 0.5)).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class, () -> imageRecommendationService.getRecommendations(userInformationRequest));
    }
}