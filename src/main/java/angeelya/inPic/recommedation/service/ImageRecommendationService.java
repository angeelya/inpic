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
import angeelya.inPic.image.service.ImageService;
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
public class ImageRecommendationService {
    private final SlopeOne slopeOne;
    private final UserService userService;
    private final ImageService imageService;
    private final RecommendationRepository recommendationRepository;
    private final ImageFileService imageFileService;
    private final ActionService actionService;
    private final Double MIN_RECOMMEND_GRADE = 0.5;

    public void recommend(Long user_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<Action> actions = actionService.getAllActions();
        Map<Long, Double> recommendationMap = slopeOne.beginSlopeOne(makeData(actions), user_id);
        List<Recommendation> recommendations = makeRecommendations(recommendationMap, user_id);
        writeRecommendation(recommendations);
    }


    public List<ImageRecommendationResponse> getRecommendations(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Recommendation> recommendations = findRecommendations(userInformationRequest.getUser_id());
        List<ImageRecommendationResponse> imageRecommendationResponses = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            imageRecommendationResponses.add(ImageRecommendationResponse.builder().image_id(recommendation.getImage().getId())
                    .image(imageFileService.getImage(recommendation.getImage().getImgName()))
                    .name(recommendation.getImage().getName()).build());
        }
        return imageRecommendationResponses;
    }

    private List<Recommendation> findRecommendations(Long user_id) throws NotFoundDatabaseException {
        userService.getUser(user_id);
        List<Recommendation> recommendations = recommendationRepository.findByUser_IdAndGradeGreaterThanEqual(user_id, MIN_RECOMMEND_GRADE);
        if (recommendations.isEmpty()) throw new NotFoundDatabaseException("No images recommendation found");
        return recommendations;
    }
    private void writeRecommendation(List<Recommendation> recommendations) throws NoAddDatabaseException {
        saveRecommendations(recommendations.stream().filter(recommendation -> {
            Recommendation recommendationSearch = recommendationRepository.findByUser_IdAndImage_Id(recommendation.getUser().getId(), recommendation.getImage().getId());
            return recommendationSearch != null ? false : true;
        }).collect(Collectors.toList()));
        updateRecommendation(recommendations.stream().filter(recommendation -> {
            Recommendation recommendationSearch = recommendationRepository.findByUser_IdAndImage_Id(recommendation.getUser().getId(), recommendation.getImage().getId());
            return recommendationSearch != null ? true : false;
        }).collect(Collectors.toList()));
    }

    private void updateRecommendation(List<Recommendation> recommendations) throws NoAddDatabaseException {
        for (Recommendation recommendation : recommendations) {
            Recommendation recommendationUpdate = recommendationRepository.findByUser_IdAndImage_Id(recommendation.getUser().getId(), recommendation.getImage().getId());
            recommendationUpdate.setGrade(recommendation.getGrade());
            recommendationUpdate=saveRecommendation(recommendationUpdate);
            if(!recommendationUpdate.getGrade().equals(recommendation.getGrade())) throw new NoAddDatabaseException("Failed to update recommendation");
        }
    }

    private Recommendation saveRecommendation(Recommendation recommendation) throws NoAddDatabaseException {
        try {
            return recommendationRepository.save(recommendation);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to save recommendation");
        }
    }

    private void saveRecommendations(List<Recommendation> recommendations) throws NoAddDatabaseException {
        try {
            recommendations = recommendationRepository.saveAll(recommendations);
            if (recommendations.isEmpty()) throw new NoAddDatabaseException("Recommendation adding is failed");
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to save recommendations");
        }
    }

    private List<Recommendation> makeRecommendations(Map<Long, Double> recommendationMap, Long user_id) throws NotFoundDatabaseException {
        List<Recommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : recommendationMap.entrySet()) {
            recommendations.add(Recommendation.builder().user(userService.getUser(user_id))
                    .image(imageService.getImage(entry.getKey())).grade(entry.getValue()).build());
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
