package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.UserDataForProfileResponse;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserImageService userImageService;
    private final UserRepository userRepository;
    public UserDataForProfileResponse getUserData(UserInformationRequest userInformationRequest) throws DatabaseNotFoundException {
        var userOptional=userRepository.findByIdAndUserImageNotNullOrUserImageNull(userInformationRequest.getUser_id());
        if(userOptional.isEmpty()) throw new DatabaseNotFoundException("User not found");
        User user = userOptional.get();
        return UserDataForProfileResponse.builder()
                .login(user.getLogin())
                .name(user.getName())
                .description(user.getDescription())
                .userImage(userImageService.getUserImage(user)).build();
    }
}
