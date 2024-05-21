package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Comment;
import angeelya.inPic.database.model.CommentNotification;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.CommentNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.CommentNotificationResponse;
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
public class CommentNotificationService {
    private final CommentNotificationRepository commentNotificationRepository;
    private final ImageFileService imageFileService;
    private static final String MS_FAILED_UPDATE = "Failed to update comment notification",
            MS_SUCCESS_UPDATE = "Update comment notification read is successful", MS_NOT_FOUND = "No comment notifications found";


    public CommentNotification makeNotification(Comment comment) {
        return CommentNotification.builder()
                .comment(comment)
                .isRead(false)
                .build();
    }

    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest) {
        List<CommentNotification> commentNotifications = commentNotificationRepository.findByComment_User_IdAndIsRead(userInformationRequest.getUser_id(), false);
        if (commentNotifications.isEmpty()) return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }


    public List<CommentNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<CommentNotification> commentNotifications = getCommentNotifications(userInformationRequest.getUser_id());
        List<CommentNotificationResponse> commentNotificationResponses = new ArrayList<>();
        for (CommentNotification commentNotification : commentNotifications) {
            Comment comment = commentNotification.getComment();
            UserImage userImage = comment.getUser().getUserImage();
            CommentNotificationResponse commentNotificationResponse = CommentNotificationResponse.builder()
                    .actor_id(comment.getUser().getId())
                    .actorName(comment.getUser().getName())
                    .image_id(comment.getImage().getId())
                    .text(comment.getText()).build();
            if (userImage != null)
                commentNotificationResponse.setActorImage(imageFileService.getImage(userImage.getName()));
            commentNotificationResponses.add(commentNotificationResponse);
        }
        return commentNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<CommentNotification> commentNotifications = getCommentNotifications(userInformationRequest.getUser_id());
        try {
            commentNotifications = (List<CommentNotification>) commentNotificationRepository.saveAll(commentNotifications.stream().map(commentNotification -> {
                        commentNotification.setRead(true);
                        return commentNotification;
                    }
            ).collect(Collectors.toList()));
            if (commentNotifications.isEmpty())
                throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        }
        return MS_SUCCESS_UPDATE;
    }

    private List<CommentNotification> getCommentNotifications(Long user_id) throws NotFoundDatabaseException {
        List<CommentNotification> commentNotifications = commentNotificationRepository.findByComment_User_Id(user_id);
        if (commentNotifications.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return commentNotifications;
    }
}
