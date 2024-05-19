package angeelya.inPic.like.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.Like;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.LikeRepository;
import angeelya.inPic.dto.request.LikeRequest;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.ImageService;
import angeelya.inPic.notification.service.LikeNotificationService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final ActionService actionService;
    private final LikeNotificationService likeNotificationService;
    private final String MS_FAILED_DELETE = "Failed to delete like", MS_FAILED_ADD = "Failed to add like",
            MS_SUCCESSFUL_ADD = "Like adding is successful", MS_SUCCESSFUL_DELETE = "Like deleting is successful";

    public String addLike(LikeRequest likeRequest) throws NoAddDatabaseException, NotFoundDatabaseException {
        Image image = imageService.getImage(likeRequest.getImage_id());
        User user = userService.getUser(likeRequest.getUser_id());
        try {
            Like like =likeRepository.save(Like.builder().user(user).image(image).build());
            likeNotificationService.addNotification(like);
            actionService.setGrade(likeRequest.getUser_id(), likeRequest.getImage_id(), true);
            return MS_SUCCESSFUL_ADD;
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_ADD);
        }
    }
    public String deleteLike(LikeRequest likeRequest) throws DeleteDatabaseException, NotFoundDatabaseException, NoAddDatabaseException {
        delete(likeRequest.getUser_id(), likeRequest.getImage_id());
        Optional<Like> like = likeRepository.findByUser_IdAndImage_Id(likeRequest.getUser_id(), likeRequest.getImage_id());
        if (like.isPresent()) throw new DeleteDatabaseException(MS_FAILED_DELETE);
        actionService.setGrade(likeRequest.getUser_id(), likeRequest.getImage_id(), false);
        return MS_SUCCESSFUL_DELETE;
    }

    private void delete(Long user_id, Long image_id) throws DeleteDatabaseException {
        try {
            likeRepository.deleteByUser_IdAndImage_Id(user_id, image_id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE);
        }
    }

}
