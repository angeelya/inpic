package angeelya.inPic.user.service;

import angeelya.inPic.dto.request.*;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.PasswordUpdateException;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.file.service.ImageFileService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserSettingsServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserGetService userGetService;
    @Mock
    ImageFileService imageFileService;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserSettingsService userSettingsService;

    @SneakyThrows
    @Test
    void testUpdateEmail() {
        User user = User.builder()
                .id(1L)
                .email("email12@gmail.com")
                .build(), userException = User.builder()
                .id(1L)
                .email("email12@gmail.com")
                .build();
        EmailUpdateRequest emailUpdateRequest = EmailUpdateRequest
                .builder().user_id(1L)
                .email("email@gmail.com").build();
        Mockito.when(userGetService.getUser(emailUpdateRequest.getUser_id())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        String excepted = "Email updating is successful";
        assertEquals(excepted, userSettingsService.updateEmail(emailUpdateRequest));
        Mockito.when(userRepository.save(user)).thenReturn(userException);
        assertThrows(NoAddDatabaseException.class, () -> userSettingsService.updateEmail(emailUpdateRequest));

    }

    @SneakyThrows
    @Test
    void testUpdateName() {
        User user = User.builder()
                .id(1L)
                .name("ile")
                .build(), userException = User.builder()
                .id(1L)
                .name("ile")
                .build();
        NameUpdateRequest nameUpdateRequest = NameUpdateRequest
                .builder().user_id(1L)
                .name("ile5").build();
        Mockito.when(userGetService.getUser(nameUpdateRequest.getUser_id())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        String excepted = "Name updating is successful";
        assertEquals(excepted, userSettingsService.updateName(nameUpdateRequest));
        Mockito.when(userRepository.save(user)).thenReturn(userException);
        assertThrows(NoAddDatabaseException.class, () -> userSettingsService.updateName(nameUpdateRequest));

    }

    @SneakyThrows
    @Test
    void testUpdateDescription() {
        User user = User.builder()
                .id(1L)
                .description("hello")
                .build(), userException = User.builder()
                .id(1L)
                .description("hello")
                .build();
        DescriptionUpdateRequest descriptionUpdateRequest = DescriptionUpdateRequest
                .builder().user_id(1L)
                .description("hello, its me").build();
        Mockito.when(userGetService.getUser(descriptionUpdateRequest.getUser_id())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        String excepted = "Description updating is successful";
        assertEquals(excepted, userSettingsService.updateDescription(descriptionUpdateRequest));
        Mockito.when(userRepository.save(user)).thenReturn(userException);
        assertThrows(NoAddDatabaseException.class, () -> userSettingsService.updateDescription(descriptionUpdateRequest));

    }

    @SneakyThrows
    @Test
    void testUpdatePassword() {
        String newPass="ile5", oldPass="12345",realPass="12345";
        User user = User.builder()
                .id(1L)
                .password(realPass)
                .build();
        PasswordUpdateRequest passwordUpdateRequest = PasswordUpdateRequest
                .builder().user_id(1L)
                .newPassword(newPass)
                .oldPassword(oldPass).build();
        Mockito.when(passwordEncoder.matches("12345", "12345")).thenReturn(true);
        Mockito.when(passwordEncoder.encode(newPass)).thenReturn(newPass);
        Mockito.when(passwordEncoder.matches("ile5", "12345")).thenReturn(false);
        Mockito.when(passwordEncoder.matches("ile5", "ile5")).thenReturn(true);
        Mockito.when(userGetService.getUser(passwordUpdateRequest.getUser_id())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        String excepted = "Password updating is successful";
        assertEquals(excepted, userSettingsService.updatePassword(passwordUpdateRequest));
        Mockito.when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        assertThrows(PasswordUpdateException.class, () -> userSettingsService.updatePassword(passwordUpdateRequest));

    }

    @SneakyThrows
    @Test
    void testUpdateUserImage() {
        byte[] imageBytes = {1, 3, 4};
        MultipartFile multipartFile = new MockMultipartFile("hello.jpg", imageBytes);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        User user = User.builder()
                .id(1L)
                .userImage(UserImage.builder().name("hello").build())
                .build(), userException = User.builder()
                .id(1L)
                .userImage(UserImage.builder().name("hello").build())
                .build();
        Mockito.when(userGetService.getUser(userInformationRequest.getUser_id())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        String excepted = "User image updating is successful";
        assertEquals(excepted, userSettingsService.updateUserImage(multipartFile,userInformationRequest));
        Mockito.when(userRepository.save(user)).thenReturn(userException);
        assertThrows(NoAddDatabaseException.class, () -> userSettingsService.updateUserImage(multipartFile,userInformationRequest));
    }
}