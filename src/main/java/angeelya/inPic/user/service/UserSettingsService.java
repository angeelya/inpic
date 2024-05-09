package angeelya.inPic.user.service;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.dto.request.DescriptionUpdateRequest;
import angeelya.inPic.dto.request.EmailUpdateRequest;
import angeelya.inPic.dto.request.NameUpdateRequest;
import angeelya.inPic.dto.request.PasswordUpdateRequest;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.PasswordUpdateException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSettingsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String MS_SUCCESS = "updating is successful", MS_FAILED = "Failed to update", MS_FAILED_ADD = "Failed to add to BD";


    public String updateEmail(EmailUpdateRequest emailUpdateRequest) throws DatabaseNotFoundException, NoAddDatabaseException {
        User user = getUser(emailUpdateRequest.getUser_id());
        user.setEmail(emailUpdateRequest.getEmail());
        user = userRepository.save(user);
        if (!user.getEmail().equals(emailUpdateRequest.getEmail())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " email");
        }
        return "Email " + MS_SUCCESS;
    }

    public String updateName(NameUpdateRequest nameUpdateRequest) throws DatabaseNotFoundException, NoAddDatabaseException {
        User user = getUser(nameUpdateRequest.getUser_id());
        user.setName(nameUpdateRequest.getName());
        user = userRepository.save(user);
        if (!user.getName().equals(nameUpdateRequest.getName())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " name");
        }
        return "Name " + MS_SUCCESS;
    }

    public String updateDescription(DescriptionUpdateRequest descriptionUpdateRequest) throws DatabaseNotFoundException, NoAddDatabaseException {
        User user = getUser(descriptionUpdateRequest.getUser_id());
        user.setDescription(descriptionUpdateRequest.getDescription());
        user = userRepository.save(user);
        if (!user.getName().equals(descriptionUpdateRequest.getDescription())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + "description");
        }
        return "Description " + MS_SUCCESS;
    }

    public String updatePassword(PasswordUpdateRequest passwordUpdateRequest) throws PasswordUpdateException, DatabaseNotFoundException, NoAddDatabaseException {
        User user = getUser(passwordUpdateRequest.getUser_id());
        String oldPass = passwordUpdateRequest.getOldPassword(),
                newPass = passwordUpdateRequest.getNewPassword(), realPass = user.getPassword();
        if (passwordEncoder.matches(oldPass, realPass) || passwordEncoder.matches(newPass, realPass))
            throw new PasswordUpdateException("Old password does not match or new password is the same as old one ");
        user.setPassword(passwordEncoder.encode(newPass));
        user = userRepository.save(user);
        if (passwordEncoder.matches(newPass, user.getPassword())) {
            logger.warn(MS_FAILED_ADD);
            throw new NoAddDatabaseException(MS_FAILED + " password");
        }
        return "Password " + MS_SUCCESS;
    }

    private User getUser(Long user_id) throws DatabaseNotFoundException {
        Optional<User> userOptional = userRepository.findByIdAndUserImageNotNullOrUserImageNull(user_id);
        if (userOptional.isEmpty()) throw new DatabaseNotFoundException("User not found");
        return userOptional.get();
    }

}
