package angeelya.inPic.recommedation;

import angeelya.inPic.database.model.Action;
import angeelya.inPic.database.model.CategoryRecommendation;
import angeelya.inPic.database.model.Recommendation;
import angeelya.inPic.database.repository.CategoryRecommendationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CategoryRecommendationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.CategoryService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
    private final Double MIN_RECOMMEND_GRADE=0.5;


    public void recommend(List<Action> actions, Long user_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        Map<Long, Double> recommendationsMap = slopeOne.beginSlopeOne(makeData(actions), user_id);
        List<CategoryRecommendation> categoryRecommendations = makeRecommendations(recommendationsMap, user_id);
        saveRecommendations(categoryRecommendations);
    }

    public List<CategoryRecommendationResponse> getRecommendations(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = findRecommendations(userInformationRequest.getUser_id());
        return recommendations.stream().map(recommendation ->
             CategoryRecommendationResponse.builder()
                    .category_id(recommendation.getId())
                    .category(recommendation.getCategory().getCategory())
                    .build()).collect(Collectors.toList());
    }

    private List<CategoryRecommendation> findRecommendations(Long user_id) throws NotFoundDatabaseException {
        userService.getUser(user_id);
        return categoryRecommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id,MIN_RECOMMEND_GRADE);
    }

    private void saveRecommendations(List<CategoryRecommendation> recommendations) throws NoAddDatabaseException {
        recommendations = categoryRecommendationRepository.saveAll(recommendations);
        if (recommendations.isEmpty()) throw new NoAddDatabaseException("Recommendation adding is failed");
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
