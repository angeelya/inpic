package angeelya.inPic.comment;

import angeelya.inPic.database.model.Comment;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.CommentRepository;
import angeelya.inPic.dto.request.CommentAddRequest;
import angeelya.inPic.dto.request.CommentDeleteRequest;
import angeelya.inPic.dto.response.CommentResponse;
import angeelya.inPic.dto.request.ImageRequest;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.ForbiddenRequestException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.ImageService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final ActionService actionService;
    private final String MS_FAILED_DELETE = "Failed to delete comment", MS_FAILED_ADD = "Failed to add comment",
            MS_NOT_FOUND = "Comment not found", MS_SUCCESS_DELETE = "Comment deleting is successful", MS_NOT_FOUND_LIST = "No comments found",
            MS_SUCCESS_ADD = "Comment adding is successful", MS_FORBIDDEN = "User cannot delete  other user comment",
    MS_FAILED_UPDATE="Failed to update comment", MS_SUCCESS_UPDATE="Comment updating is successful";

    public String addComment(CommentAddRequest commentAddRequest) throws NoAddDatabaseException, NotFoundDatabaseException {
        Image image = imageService.getImage(commentAddRequest.getImage_id());
        User user = userService.getUser(commentAddRequest.getUser_id());
        saveComment(Comment.builder()
                .user(user)
                .image(image)
                .text(commentAddRequest.getText()).build());
        actionService.setGrade(commentAddRequest.getUser_id(), commentAddRequest.getImage_id(), true);
        return MS_SUCCESS_ADD;
    }
    public List<CommentResponse> getImageComments(ImageRequest imageRequest) throws NotFoundDatabaseException {
        List<Comment> comments = commentRepository.findByImage_Id(imageRequest.getImage_id());
        if (comments.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_LIST);
        return comments.stream().map(comment ->
                CommentResponse.builder().image_id(comment.getImage().getId())
                        .user_id(comment.getUser().getId())
                        .text(comment.getText()).build()
        ).collect(Collectors.toList());
    }

    public String deleteComment(CommentDeleteRequest commentDeleteRequest) throws NotFoundDatabaseException, ForbiddenRequestException, DeleteDatabaseException {
        Comment comment = getComment(commentDeleteRequest.getUser_id(), commentDeleteRequest.getImage_id());
        if (!comment.getUser().equals(commentDeleteRequest.getUser_id()))
            throw new ForbiddenRequestException(MS_FORBIDDEN);
        delete(comment.getId());
        comment = getComment(commentDeleteRequest.getUser_id(), commentDeleteRequest.getImage_id());
        if (comment != null) throw new DeleteDatabaseException(MS_FAILED_DELETE);
        actionService.setGrade(commentDeleteRequest.getUser_id(), commentDeleteRequest.getImage_id(),false);
        return MS_SUCCESS_DELETE;
    }

    public Comment getComment(Long user_id, Long image_id) throws NotFoundDatabaseException {
        Optional<Comment> commentOptional = commentRepository.findByUser_IdAndImage_Id(user_id, image_id);
        if (commentOptional.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return commentOptional.get();
    }

    public String updateComment(CommentAddRequest commentAddRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        Comment comment = getComment(commentAddRequest.getUser_id(), commentAddRequest.getImage_id());
        comment.setText(commentAddRequest.getText());
        comment=saveComment(comment);
        if(!comment.getText().equals(commentAddRequest.getText())) throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        return MS_SUCCESS_UPDATE;
    }
    private Comment saveComment(Comment comment) throws NoAddDatabaseException {
        try {
           return commentRepository.save(comment);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_ADD);
        }
    }
    private void delete(Long id) throws DeleteDatabaseException {
        try {
            commentRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE);
        }
    }
}
