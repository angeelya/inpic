package angeelya.inPic.user.service;

import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.UserDataForProfileResponse;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.database.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserImageService userImageService;
    private final UserGetService userGetService;
    public UserDataForProfileResponse getUserData(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        User user = userGetService.getUser(userInformationRequest.getUser_id());
        return UserDataForProfileResponse.builder()
                .login(user.getLogin())
                .name(user.getName())
                .description(user.getDescription())
                .userImage(userImageService.getUserImage(user)).build();
    }
}
