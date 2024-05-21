package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Like;
import angeelya.inPic.database.model.LikeNotification;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.LikeNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.LikeNotificationResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeNotificationService {
    private final LikeNotificationRepository likeNotificationRepository;
    private final ImageFileService imageFileService;
    private static final String MS_FAILED_UPDATE = "Failed to update like notification",
            MS_SUCCESS_UPDATE = "Update like notification read is successful", MS_NOT_FOUND = "No like notifications found";


    public LikeNotification makeNotification(Like like) {
        return
                LikeNotification.builder()
                        .like(like)
                        .isRead(false)
                        .build();
    }

    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest) {
        List<LikeNotification> likeNotifications = likeNotificationRepository.findByLike_User_IdAndIsRead(userInformationRequest.getUser_id(), false);
        if (likeNotifications.isEmpty()) return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }

    public List<LikeNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<LikeNotification> likeNotifications = getLikeNotifications(userInformationRequest.getUser_id());
        List<LikeNotificationResponse> likeNotificationResponses = new ArrayList<>();
        for (LikeNotification likeNotification : likeNotifications) {
            Like like = likeNotification.getLike();
            UserImage userImage = like.getUser().getUserImage();
            LikeNotificationResponse likeNotificationResponse = LikeNotificationResponse.builder()
                    .actor_id(like.getUser().getId())
                    .actorName(like.getUser().getName())
                    .image_id(like.getImage().getId())
                    .imgName(like.getImage().getImgName())
                    .image(imageFileService.getImage(like.getImage().getImgName()))
                    .isRead(likeNotification.isRead()).build();
            if (userImage != null)
                likeNotificationResponse.setActorImage(imageFileService.getImage(userImage.getName()));
            likeNotificationResponses.add(likeNotificationResponse);
        }
        return likeNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<LikeNotification> likeNotifications = getLikeNotifications(userInformationRequest.getUser_id());
        try {
            likeNotifications = (List<LikeNotification>) likeNotificationRepository.saveAll(likeNotifications.stream().map(likeNotification -> {
                        likeNotification.setRead(true);
                        return likeNotification;
                    }
            ).collect(Collectors.toList()));
            if (likeNotifications.isEmpty()) throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        }
        return MS_SUCCESS_UPDATE;
    }

    private List<LikeNotification> getLikeNotifications(Long user_id) throws NotFoundDatabaseException {
        List<LikeNotification> likeNotifications = likeNotificationRepository.findByLike_User_Id(user_id);
        if (likeNotifications.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return likeNotifications;
    }
}
