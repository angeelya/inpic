package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.Action;
import angeelya.inPic.database.model.CategoryRecommendation;
import angeelya.inPic.database.repository.CategoryRecommendationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CategoryRecommendationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.CategoryService;
import angeelya.inPic.user.service.UserGetService;
import lombok.Getter;
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
    private final UserGetService userGetService;
    private final CategoryRecommendationRepository categoryRecommendationRepository;
    private final CategoryService categoryService;
    @Getter
    private  List<CategoryRecommendation> categoryRecommendations;
    private final Double MIN_RECOMMEND_GRADE = 0.5;
    private static final String MS_NOT_FOUND_LIST="No category recommendations found",MS_FAILED_ADD_LIST="Recommendations adding is failed",
            MS_FAILED_UPDATE="Failed to update category recommendations",MS_FAILED_SAVE="Failed to save category recommendation";


    public void recommend(List<Action> actions, Long user_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        Map<Long,Map<Long,Double>>data=makeData(actions);
        Map<Long, Double> recommendationsMap = slopeOne.beginSlopeOne(data, user_id);
        categoryRecommendations = makeRecommendations(recommendationsMap, user_id);
        writeRecommendation(categoryRecommendations);
    }

    public List<CategoryRecommendationResponse> getPopularRecommendations() throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = categoryRecommendationRepository.findByGradeGreaterThanEqual(MIN_RECOMMEND_GRADE);
        if (recommendations.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_LIST);
        return makeCategoryRecommendationResponses(recommendations);
    }

    public List<CategoryRecommendationResponse> getRecommendations(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = findRecommendations(userInformationRequest.getUser_id());
        return makeCategoryRecommendationResponses(recommendations);
    }

    private List<CategoryRecommendationResponse> makeCategoryRecommendationResponses(List<CategoryRecommendation> recommendations) {
        return recommendations.stream().map(recommendation ->
                CategoryRecommendationResponse.builder()
                        .category_id(recommendation.getCategory().getId())
                        .category(recommendation.getCategory().getCategory())
                        .build()).collect(Collectors.toList());
    }

    private List<CategoryRecommendation> findRecommendations(Long user_id) throws NotFoundDatabaseException {
        userGetService.getUser(user_id);
        List<CategoryRecommendation> list = categoryRecommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, MIN_RECOMMEND_GRADE);
        if (list.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_LIST);
        return list;
    }

    private void saveRecommendations(List<CategoryRecommendation> recommendations) throws NoAddDatabaseException {
        try {
            recommendations = (List<CategoryRecommendation>) categoryRecommendationRepository.saveAll(recommendations);
            if (recommendations.isEmpty()) throw new NoAddDatabaseException(MS_FAILED_ADD_LIST);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_ADD_LIST);
        }
    }
    private void writeRecommendation(List<CategoryRecommendation> recommendations) throws NoAddDatabaseException {
        saveRecommendations(recommendations.stream().filter(recommendation -> {
            CategoryRecommendation recommendationSearch = categoryRecommendationRepository.findByUser_IdAndCategory_Id(recommendation.getUser().getId(), recommendation.getCategory().getId());
            return recommendationSearch == null;
        }).collect(Collectors.toList()));
        updateRecommendation(recommendations.stream().filter(recommendation -> {
            CategoryRecommendation recommendationSearch = categoryRecommendationRepository.findByUser_IdAndCategory_Id(recommendation.getUser().getId(), recommendation.getCategory().getId());
            return recommendationSearch != null;
        }).collect(Collectors.toList()));
    }

    private void updateRecommendation(List<CategoryRecommendation> recommendations) throws NoAddDatabaseException {
        for (CategoryRecommendation recommendation : recommendations) {
            CategoryRecommendation recommendationUpdate = categoryRecommendationRepository.findByUser_IdAndCategory_Id(recommendation.getUser().getId(), recommendation.getCategory().getId());
            recommendationUpdate.setGrade(recommendation.getGrade());
            recommendationUpdate=saveRecommendation(recommendationUpdate);
            if(!recommendationUpdate.getGrade().equals(recommendation.getGrade())) throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        }
    }

    private CategoryRecommendation saveRecommendation(CategoryRecommendation recommendationUpdate) throws NoAddDatabaseException {
        try {
            return categoryRecommendationRepository.save(recommendationUpdate);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }

    private List<CategoryRecommendation> makeRecommendations(Map<Long, Double> recommendationsMap, Long user_id) throws NotFoundDatabaseException {
        List<CategoryRecommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : recommendationsMap.entrySet()) {
            recommendations.add(CategoryRecommendation.builder()
                    .category(categoryService.getCategory(entry.getKey()))
                    .user(userGetService.getUser(user_id))
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
