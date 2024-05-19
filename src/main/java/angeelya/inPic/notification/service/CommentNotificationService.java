package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.Comment;
import angeelya.inPic.database.model.CommentNotification;
import angeelya.inPic.database.repository.CommentNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.CommentNotificationResponse;
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
public class CommentNotificationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CommentNotificationRepository commentNotificationRepository;
    private final ImageFileService imageFileService;

    public void addNotification(Comment comment) {
        try {
            commentNotificationRepository.save(CommentNotification.builder()
                    .comment(comment)
                    .isRead(false)
                    .build());
        } catch (DataAccessException e) {
            logger.error("Comment notification did not add");
        }
    }
    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest)
    {
        List<CommentNotification> commentNotifications = commentNotificationRepository.findByComment_User_IdAndRead(userInformationRequest.getUser_id(),false);
        if(commentNotifications.isEmpty()) return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }


    public List<CommentNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<CommentNotification> commentNotifications = getCommentNotifications(userInformationRequest.getUser_id());
        List<CommentNotificationResponse> commentNotificationResponses = new ArrayList<>();
        for (CommentNotification commentNotification : commentNotifications) {
            Comment comment = commentNotification.getComment();
            commentNotificationResponses.add(CommentNotificationResponse.builder()
                    .actor_id(comment.getUser().getId())
                    .actorName(comment.getUser().getName())
                    .actorImage(imageFileService.getImage(comment.getUser().getUserImage().getName()))
                    .image_id(comment.getImage().getId())
                    .text(comment.getText()).build());
        }
        return commentNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<CommentNotification> commentNotifications = getCommentNotifications(userInformationRequest.getUser_id());
        try {
            commentNotifications = commentNotificationRepository.saveAll(commentNotifications.stream().map(commentNotification -> {
                        commentNotification.setRead(true);
                        return commentNotification;
                    }
            ).collect(Collectors.toList()));
            if (commentNotifications.isEmpty())
                throw new NoAddDatabaseException("Failed to update comment notification");
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to update comment notification");
        }
        return "Update comment notification read is successful";
    }
    private List<CommentNotification> getCommentNotifications(Long user_id) throws NotFoundDatabaseException {
        List<CommentNotification> commentNotifications = commentNotificationRepository.findByComment_User_Id(user_id);
        if (commentNotifications.isEmpty()) throw new NotFoundDatabaseException("No comment notifications found");
        return commentNotifications;
    }
}
