package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.AdminNotification;
import angeelya.inPic.database.model.DeletedImage;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.AdminNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.AdminNotificationResponse;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminNotificationServiceTest {
    @Mock
    AdminNotificationRepository adminNotificationRepository;
    @Mock
    ImageFileService imageFileService;
    @InjectMocks
    AdminNotificationService adminNotificationService;

    @Test
    void testAddNotification() {
        DeletedImage deletedImage = DeletedImage.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        AdminNotification adminNotification = AdminNotification.builder()
                .user(user)
                .deletedImage(deletedImage)
                .isRead(false)
                .build();
        AdminNotification actual = adminNotificationService.addNotification(deletedImage, user);
        assertEquals(adminNotification, actual);
    }

    @Test
    void testCheckNotification() {
        DeletedImage deletedImage = DeletedImage.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        AdminNotification adminNotification = AdminNotification.builder()
                .user(user)
                .deletedImage(deletedImage)
                .isRead(false)
                .build();
        List<AdminNotification> adminNotifications = List.of(adminNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(adminNotificationRepository.findByUser_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(adminNotifications);
        CheckNotificationResponse actual = adminNotificationService.checkNotification(userInformationRequest);
        assertEquals(true, actual.getHaveNotification());
        Mockito.when(adminNotificationRepository.findByUser_IdAndIsRead(userInformationRequest.getUser_id(), false))
                .thenReturn(List.of());
        actual = adminNotificationService.checkNotification(userInformationRequest);
        assertEquals(false, actual.getHaveNotification());
    }

    @SneakyThrows
    @Test
    void testGetNotification() {
        DeletedImage deletedImage = DeletedImage.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        AdminNotification adminNotification = AdminNotification.builder()
                .user(user)
                .deletedImage(deletedImage)
                .isRead(false)
                .build();
        List<AdminNotification> adminNotifications = List.of(adminNotification);
        List<AdminNotificationResponse> adminNotificationResponses = List.of(AdminNotificationResponse.builder()
                .cause(deletedImage.getCause())
                .isRead(adminNotification.getIsRead())
                .deletedImage("image").build());
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(imageFileService.getImage(deletedImage.getImgName())).thenReturn("image");
        Mockito.when(adminNotificationRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(adminNotifications);
        List<AdminNotificationResponse> actual = adminNotificationService.getNotification(userInformationRequest);
        assertEquals(adminNotificationResponses, actual);
        Mockito.when(adminNotificationRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->adminNotificationService.getNotification(userInformationRequest));
    }

    @SneakyThrows
    @Test
    void testReadNotification() {
        DeletedImage deletedImage = DeletedImage.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        AdminNotification adminNotification = AdminNotification.builder()
                .user(user)
                .deletedImage(deletedImage)
                .isRead(false)
                .build(), exceptionAdminNotification=AdminNotification.builder()
                .user(user)
                .deletedImage(deletedImage)
                .isRead(false)
                .build();
        List<AdminNotification> adminNotifications = List.of(adminNotification);
        UserInformationRequest userInformationRequest = new UserInformationRequest(1L);
        Mockito.when(adminNotificationRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(adminNotifications);
        Mockito.when(adminNotificationRepository.saveAll(adminNotifications)).thenReturn(adminNotifications);
        String excepted ="Update admin notification read is successful",
                actual = adminNotificationService.readNotification(userInformationRequest);
            assertEquals(excepted,actual);
        Mockito.when(adminNotificationRepository.saveAll(adminNotifications)).thenReturn(List.of());
        assertThrows(NoAddDatabaseException.class,()->adminNotificationService.readNotification(userInformationRequest));
        Mockito.when(adminNotificationRepository.saveAll(adminNotifications)).thenReturn(List.of(exceptionAdminNotification));
        assertThrows(NoAddDatabaseException.class,()->adminNotificationService.readNotification(userInformationRequest));
        Mockito.when(adminNotificationRepository.findByUser_Id(userInformationRequest.getUser_id()))
                .thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->adminNotificationService.readNotification(userInformationRequest));
    }
}