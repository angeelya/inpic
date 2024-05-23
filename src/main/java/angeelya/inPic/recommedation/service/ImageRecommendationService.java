package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.Action;
import angeelya.inPic.database.model.Recommendation;
import angeelya.inPic.database.repository.RecommendationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImageRecommendationResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.image.service.ImageGetService;
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
public class ImageRecommendationService {
    private final SlopeOne slopeOne;
    private final UserGetService userGetService;
    private final ImageGetService imageGetService;
    private final RecommendationRepository recommendationRepository;
    private final ImageFileService imageFileService;
    private static final Double MIN_RECOMMEND_GRADE = 0.5;
    @Getter
    private List<Recommendation> recommendations;
    private static final String MS_NOT_FOUND_LIST = "No image recommendations found", MS_FAILED_ADD_LIST = "Recommendations adding is failed",
            MS_FAILED_UPDATE = "Failed to update recommendations", MS_FAILED_SAVE = "Failed to save recommendation";


    public void recommend(List<Action> actions, Long user_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        Map<Long, Double> recommendationMap = slopeOne.beginSlopeOne(makeData(actions), user_id);
        recommendations = makeRecommendations(recommendationMap, user_id);
        writeRecommendation(recommendations);
    }


    public List<ImageRecommendationResponse> getRecommendations(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Recommendation> recommendations = findRecommendations(userInformationRequest.getUser_id());
        List<ImageRecommendationResponse> imageRecommendationResponses = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            imageRecommendationResponses.add(ImageRecommendationResponse.builder()
                    .image_id(recommendation.getImage().getId())
                    .imgName(recommendation.getImage().getImgName())
                    .image(imageFileService.getImage(recommendation.getImage().getImgName()))
                    .name(recommendation.getImage().getName()).build());
        }
        return imageRecommendationResponses;
    }

    private List<Recommendation> findRecommendations(Long user_id) throws NotFoundDatabaseException {
        userGetService.getUser(user_id);
        List<Recommendation> recommendations = recommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, MIN_RECOMMEND_GRADE);
        if (recommendations.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_LIST);
        return recommendations;
    }

    private void writeRecommendation(List<Recommendation> recommendations) throws NoAddDatabaseException {
        saveRecommendations(recommendations.stream().filter(recommendation -> {
            Recommendation recommendationSearch = recommendationRepository.findByUser_IdAndImage_Id(recommendation.getUser().getId(), recommendation.getImage().getId());
            return recommendationSearch == null;
        }).collect(Collectors.toList()));
        updateRecommendation(recommendations.stream().filter(recommendation -> {
            Recommendation recommendationSearch = recommendationRepository.findByUser_IdAndImage_Id(recommendation.getUser().getId(), recommendation.getImage().getId());
            return recommendationSearch != null;
        }).collect(Collectors.toList()));
    }

    private void updateRecommendation(List<Recommendation> recommendations) throws NoAddDatabaseException {
        for (Recommendation recommendation : recommendations) {
            Recommendation recommendationUpdate = recommendationRepository.findByUser_IdAndImage_Id(recommendation.getUser().getId(), recommendation.getImage().getId());
            recommendationUpdate.setGrade(recommendation.getGrade());
            recommendationUpdate = saveRecommendation(recommendationUpdate);
            if (!recommendationUpdate.getGrade().equals(recommendation.getGrade()))
                throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        }
    }

    private Recommendation saveRecommendation(Recommendation recommendation) throws NoAddDatabaseException {
        try {
            return recommendationRepository.save(recommendation);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }

    private void saveRecommendations(List<Recommendation> recommendations) throws NoAddDatabaseException {
        try {
            recommendations = (List<Recommendation>) recommendationRepository.saveAll(recommendations);
            if (recommendations.isEmpty()) throw new NoAddDatabaseException(MS_FAILED_ADD_LIST);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }

    private List<Recommendation> makeRecommendations(Map<Long, Double> recommendationMap, Long user_id) throws NotFoundDatabaseException {
        List<Recommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : recommendationMap.entrySet()) {
            recommendations.add(Recommendation.builder().user(userGetService.getUser(user_id))
                    .image(imageGetService.getImage(entry.getKey())).grade(entry.getValue()).build());
        }
        return recommendations;
    }

    private Map<Long, Map<Long, Double>> makeData(List<Action> actions) {
        return actions.stream()
                .collect(Collectors.groupingBy(
                        action -> action.getUser().getId(),
                        Collectors.toMap(
                                action -> action.getImage().getId(),
                                Action::getGrade)));
    }
}
