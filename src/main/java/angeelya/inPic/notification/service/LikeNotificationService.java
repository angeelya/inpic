package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Like;
import angeelya.inPic.database.model.LikeNotification;
import angeelya.inPic.database.repository.LikeNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.LikeNotificationResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeNotificationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LikeNotificationRepository likeNotificationRepository;
    private final ImageFileService imageFileService;

    public void addNotification(Like like) {
        try {
            likeNotificationRepository.save(LikeNotification.builder()
                    .like(like)
                    .isRead(false)
                    .build());
        } catch (DataAccessException e) {
            logger.error("Like notification did not add");
        }
    }

    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest) {
        List<LikeNotification> likeNotifications = likeNotificationRepository.findByLike_User_IdAndRead(userInformationRequest.getUser_id(),false);
        if(likeNotifications.isEmpty()) return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }

    public List<LikeNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<LikeNotification> likeNotifications = getLikeNotifications(userInformationRequest.getUser_id());
        List<LikeNotificationResponse> likeNotificationResponses = new ArrayList<>();
        for (LikeNotification likeNotification : likeNotifications) {
            Like like = likeNotification.getLike();
            likeNotificationResponses.add(LikeNotificationResponse.builder()
                    .actor_id(like.getUser().getId())
                    .actorName(like.getUser().getName())
                    .actorImage(imageFileService.getImage(like.getUser().getUserImage().getName()))
                    .image_id(like.getImage().getId())
                    .image(imageFileService.getImage(like.getImage().getImgName()))
                    .isRead(likeNotification.isRead()).build());
        }
        return likeNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<LikeNotification> likeNotifications = getLikeNotifications(userInformationRequest.getUser_id());
        try {
            likeNotifications = likeNotificationRepository.saveAll(likeNotifications.stream().map(likeNotification -> {
                        likeNotification.setRead(true);
                        return likeNotification;
                    }
            ).collect(Collectors.toList()));
            if (likeNotifications.isEmpty()) throw new NoAddDatabaseException("Failed to update like notification");
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to update like notification");
        }
        return "Update like notification read is successful";
    }
    private List<LikeNotification> getLikeNotifications(Long user_id) throws NotFoundDatabaseException {
        List<LikeNotification> likeNotifications = likeNotificationRepository.findByLike_User_Id(user_id);
        if (likeNotifications.isEmpty()) throw new NotFoundDatabaseException("No like notifications found");
        return likeNotifications;
    }
}
