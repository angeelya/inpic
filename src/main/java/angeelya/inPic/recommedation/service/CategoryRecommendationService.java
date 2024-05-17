package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.Action;
import angeelya.inPic.database.model.CategoryRecommendation;
import angeelya.inPic.database.repository.CategoryRecommendationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CategoryRecommendationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.CategoryService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryRecommendationService {
    private final SlopeOne slopeOne;
    private final UserService userService;
    private final CategoryRecommendationRepository categoryRecommendationRepository;
    private final CategoryService categoryService;
    private final ActionService actionService;
    private final Double MIN_RECOMMEND_GRADE = 0.5;


    public void recommend(Long user_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<Action> actions = actionService.getAllActions();
        Map<Long, Double> recommendationsMap = slopeOne.beginSlopeOne(makeData(actions), user_id);
        List<CategoryRecommendation> categoryRecommendations = makeRecommendations(recommendationsMap, user_id);
        saveRecommendations(categoryRecommendations);
    }

    public List<CategoryRecommendationResponse> getPopularRecommendations() throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = categoryRecommendationRepository.findByGradeGreaterThanEqual(MIN_RECOMMEND_GRADE);
        if (recommendations.isEmpty()) throw new NotFoundDatabaseException("No category recommendation found");
        return makeCategoryRecommendationResponses(recommendations);
    }

    public List<CategoryRecommendationResponse> getRecommendations(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = findRecommendations(userInformationRequest.getUser_id());
        return makeCategoryRecommendationResponses(recommendations);
    }

    private List<CategoryRecommendationResponse> makeCategoryRecommendationResponses(List<CategoryRecommendation> recommendations) {
        return recommendations.stream().map(recommendation ->
                CategoryRecommendationResponse.builder()
                        .category_id(recommendation.getId())
                        .category(recommendation.getCategory().getCategory())
                        .build()).collect(Collectors.toList());
    }

    private List<CategoryRecommendation> findRecommendations(Long user_id) throws NotFoundDatabaseException {
        userService.getUser(user_id);
        List<CategoryRecommendation> list = categoryRecommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, MIN_RECOMMEND_GRADE);
        if (list.isEmpty()) throw new NotFoundDatabaseException("No category recommendation found");
        return list;
    }

    private void saveRecommendations(List<CategoryRecommendation> recommendations) throws NoAddDatabaseException {
        try {
            recommendations = categoryRecommendationRepository.saveAll(recommendations);
            if (recommendations.isEmpty()) throw new NoAddDatabaseException("Recommendation adding is failed");
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to save");
        }
    }

    private List<CategoryRecommendation> makeRecommendations(Map<Long, Double> recommendationsMap, Long user_id) throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : recommendationsMap.entrySet()) {
            recommendations.add(CategoryRecommendation.builder()
                    .category(categoryService.getCategory(entry.getKey()))
                    .user(userService.getUser(user_id))
                    .grade(entry.getValue()).build());
        }
        return recommendations;
    }

    private Map<Long, Map<Long, Double>> makeData(List<Action> actions) {
        return actions.stream()
                .collect(Collectors.groupingBy(
                        action -> action.getUser().getId(),
                        Collectors.groupingBy(
                                action -> action.getImage().getCategory().getId(),
                                Collectors.averagingDouble(Action::getGrade))));
    }
}
