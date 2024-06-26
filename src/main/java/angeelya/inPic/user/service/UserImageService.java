package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final ImageFileService imageFileService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public String getUserImage(User user) {
        UserImage userImage = user.getUserImage();
        if (userImage == null) return null;
        try {
            return imageFileService.getImage(userImage.getName());
        } catch (FileException e) {
            logger.error(this.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

}