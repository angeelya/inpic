package angeelya.inPic.like.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.Like;
import angeelya.inPic.database.model.LikeNotification;
import angeelya.inPic.notification.service.LikeNotificationService;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.LikeRepository;
import angeelya.inPic.dto.request.LikeRequest;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.ExistException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.ImageGetService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserGetService;
import jakarta.persistence.TransactionRequiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ImageGetService imageGetService;

    private final UserGetService userGetService;
    private final ActionService actionService;
    private final LikeNotificationService likeNotificationService;
    private static final String MS_FAILED_DELETE = "Failed to delete like", MS_FAILED_ADD = "Failed to add like",
            MS_SUCCESSFUL_ADD = "Like adding is successful", MS_SUCCESSFUL_DELETE = "Like deleting is successful", MS_EXIST = "Like already exists";
@Transactional
    public String addLike(LikeRequest likeRequest) throws NoAddDatabaseException, NotFoundDatabaseException, ExistException {
        Image image = imageGetService.getImage(likeRequest.getImage_id());
        User user = userGetService.getUser(likeRequest.getUser_id());
        if (likeRepository.findByUser_IdAndImage_Id(user.getId(), image.getId()).isPresent())
            throw new ExistException(MS_EXIST);
        try {
            Like like = Like.builder().user(user).image(image).build();
            LikeNotification likeNotification = likeNotificationService.makeNotification(like);
            like.setLikeNotification(likeNotification);
            likeRepository.save(like);
            actionService.setGrade(likeRequest.getUser_id(), likeRequest.getImage_id(), true);
            return MS_SUCCESSFUL_ADD;
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_ADD);
        }
    }

    @Transactional
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
        } catch (EmptyResultDataAccessException | TransactionRequiredException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE);
        }
    }

}
