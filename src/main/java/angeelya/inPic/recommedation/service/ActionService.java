package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.Action;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.ActionRepository;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.ImageService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ActionRepository actionRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final CategoryRecommendationService categoryRecommendationService;
    private final ImageRecommendationService imageRecommendationService;
    private final Double INCREASE_GRADE = 0.25, MAX_GRADE = 1.0;

    public List<Action> getAllActions() throws NotFoundDatabaseException {
        List<Action> actions = actionRepository.findAll();
        if (actions.isEmpty()) throw new NotFoundDatabaseException("No actions found");
        return actions;
    }

    public void setGrade(Long user_id, Long image_id, boolean isIncreaseGrade) throws NotFoundDatabaseException, NoAddDatabaseException {
        Image image = imageService.getImage(image_id);
        Action action = actionRepository.findByUser_IdAndImage_Id(user_id, image_id);
        if (image.getUser().getId().equals(user_id)) return;
        else if (isIncreaseGrade) {
            increaseGrade(action, user_id, image_id);
        } else {
            decreaseGrade(action);
        }
    }

    private void increaseGrade(Action action, Long user_id, Long image_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        if (action == null) addAction(user_id, image_id);
        else {
            if (action.getGrade().equals(MAX_GRADE)) return;
            updateAction(action, true);
        }
    }

    private void decreaseGrade(Action action) throws NotFoundDatabaseException, NoAddDatabaseException {
        if (action != null && !action.getGrade().equals(INCREASE_GRADE)) updateAction(action, false);
    }

    private void updateAction(Action action, boolean isIncrease) throws NotFoundDatabaseException, NoAddDatabaseException {
        Double newGrade = isIncrease ? action.getGrade() + INCREASE_GRADE : action.getGrade() - INCREASE_GRADE;
        action.setGrade(newGrade);
        action = saveAction(action);
        if (action == null || action.getGrade().equals(newGrade)) logger.error("Failed to update action");
        categoryRecommendationService.recommend(action.getUser().getId());
        imageRecommendationService.recommend(action.getUser().getId());
    }


    private void addAction(Long user_id, Long image_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(user_id);
        Image image = imageService.getImage(image_id);
        saveAction(Action.builder().user(user).image(image).grade(INCREASE_GRADE).build());
        categoryRecommendationService.recommend(user_id);
        imageRecommendationService.recommend(user_id);
    }

    private Action saveAction(Action action) {
        try {
            action = actionRepository.save(action);
        } catch (DataAccessException e) {
            logger.error("Action adding is failed " + e.getMessage());
        }
        return action;
    }
}
