package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.PasswordUpdateException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserSettingsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final UserGetService userService;
    private final ImageFileService imageFileService;
    private final PasswordEncoder passwordEncoder;
    private static final String MS_SUCCESS = "updating is successful",
            MS_FAILED = "Failed to update",
            MS_FAILED_ADD = "Failed to add to BD",MS_FAILED_UPDATE_PASSWORD="Old password does not match or new password is the same as old one ";

    public String updateEmail(EmailUpdateRequest emailUpdateRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(emailUpdateRequest.getUser_id());
        user.setEmail(emailUpdateRequest.getEmail());
        user = saveUser(user);
        if (!user.getEmail().equals(emailUpdateRequest.getEmail())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " email");
        }
        return "Email " + MS_SUCCESS;
    }

    public String updateName(NameUpdateRequest nameUpdateRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(nameUpdateRequest.getUser_id());
        user.setName(nameUpdateRequest.getName());
        user = saveUser(user);
        if (!user.getName().equals(nameUpdateRequest.getName())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " name");
        }
        return "Name " + MS_SUCCESS;
    }

    public String updateDescription(DescriptionUpdateRequest descriptionUpdateRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(descriptionUpdateRequest.getUser_id());
        user.setDescription(descriptionUpdateRequest.getDescription());
        user = saveUser(user);
        if (!user.getDescription().equals(descriptionUpdateRequest.getDescription())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " description");
        }
        return "Description " + MS_SUCCESS;
    }

    public String updatePassword(PasswordUpdateRequest passwordUpdateRequest) throws PasswordUpdateException, NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(passwordUpdateRequest.getUser_id());
        String oldPass = passwordUpdateRequest.getOldPassword(),
                newPass = passwordUpdateRequest.getNewPassword(), realPass = user.getPassword();
        if (!passwordEncoder.matches(oldPass, realPass) || passwordEncoder.matches(newPass, realPass))
            throw new PasswordUpdateException(MS_FAILED_UPDATE_PASSWORD);
        user.setPassword(passwordEncoder.encode(newPass));
        user = saveUser(user);
        if (!passwordEncoder.matches(newPass, user.getPassword())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " password");
        }
        return "Password " + MS_SUCCESS;
    }

    public String updateUserImage(MultipartFile multipartFile, UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException, FileException {
        User user = userService.getUser(userInformationRequest.getUser_id());
        UserImage userImage = user.getUserImage();
        if (userImage == null) userImage = UserImage.builder().user(user).build();
        userImage.setPath(imageFileService.getDirectoryPath());
        userImage.setName(multipartFile.getOriginalFilename());
        user.setUserImage(userImage);
        saveUser(user);
        imageFileService.saveImage(multipartFile);
        if (!user.getUserImage().getName().equals(userImage.getName())) throw new NoAddDatabaseException(MS_FAILED + " user image");
        return "User image " + MS_SUCCESS;
    }

    private User saveUser(User user) throws NoAddDatabaseException {
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED);
        }
    }
}
