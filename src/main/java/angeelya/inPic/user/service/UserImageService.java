package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.UserImageRepository;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final ImageFileService imageFileService;
    private final UserImageRepository userImageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String MS_FAILED_ADD = "Failed to add user image";


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