package angeelya.inPic.user.service;

import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.UserDataForProfileResponse;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserProfileServiceTest {
    @Mock
    UserImageService userImageService;
    @Mock
    UserGetService userGetService;
    @InjectMocks
    UserProfileService userProfileService;

    @SneakyThrows
    @Test
    void testGetUserData() {
        Long user_id=1L;
        UserInformationRequest userInformationRequest = new UserInformationRequest(user_id);
        User user=User.builder()
                .id(user_id)
                .login("jdom")
                .name("Angel")
                .description("hello")
                .userImage(UserImage.builder().name("hello").build())
                .build();
        UserDataForProfileResponse excepted=UserDataForProfileResponse.builder()
                .login(user.getLogin())
                .name(user.getName())
                .description(user.getDescription())
                .userImage("hello").build();
        Mockito.when(userGetService.getUser(userInformationRequest.getUser_id())).thenReturn(user);
        Mockito.when(userImageService.getUserImage(user)).thenReturn("hello");
        assertEquals(excepted,userProfileService.getUserData(userInformationRequest));
    }
}