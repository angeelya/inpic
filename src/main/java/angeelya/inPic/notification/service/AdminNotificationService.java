package angeelya.inPic.notification.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.AdminNotificationRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.AdminNotificationResponse;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AdminNotificationRepository adminNotificationRepository;
    private final ImageFileService imageFileService;

    public void addNotification(DeletedImage deletedImage, User user) {
        try {
            adminNotificationRepository.save(AdminNotification.builder()
                    .user(user)
                    .deletedImage(deletedImage)
                    .isRead(false)
                    .build());
        } catch (DataAccessException e) {
            logger.error("Admin notification did not add");
        }
    }

    public CheckNotificationResponse checkNotification(UserInformationRequest userInformationRequest) {
        List<AdminNotification> adminNotifications = adminNotificationRepository.findByUser_IdAndRead(userInformationRequest.getUser_id(), false);
        if (adminNotifications.isEmpty()) return CheckNotificationResponse.builder().haveNotification(false).build();
        return CheckNotificationResponse.builder().haveNotification(true).build();
    }

    public List<AdminNotificationResponse> getNotification(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<AdminNotification> adminNotifications = getAdminNotifications(userInformationRequest.getUser_id());
        List<AdminNotificationResponse> adminNotificationResponses = new ArrayList<>();
        for (AdminNotification adminNotification : adminNotifications) {
            DeletedImage deletedImage = adminNotification.getDeletedImage();
            adminNotificationResponses.add(AdminNotificationResponse.builder()
                    .cause(deletedImage.getCause())
                    .isRead(adminNotification.isRead())
                    .deletedImage(imageFileService.getImage(deletedImage.getImgName())).build());
        }
        return adminNotificationResponses;
    }

    public String readNotification(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        List<AdminNotification> adminNotifications = getAdminNotifications(userInformationRequest.getUser_id());
        try {
            adminNotifications = adminNotificationRepository.saveAll(adminNotifications.stream().map(adminNotification -> {
                        adminNotification.setRead(true);
                        return adminNotification;
                    }
            ).collect(Collectors.toList()));
            if (adminNotifications.isEmpty()) throw new NoAddDatabaseException("Failed to update admin notification");
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to update admin notification");
        }
        return "Update admin notification read is successful";
    }
    private List<AdminNotification> getAdminNotifications(Long user_id) throws NotFoundDatabaseException {
        List<AdminNotification> adminNotifications = adminNotificationRepository.findByUser_Id(user_id);
        if (adminNotifications.isEmpty()) throw new NotFoundDatabaseException("No admin notifications found");
        return adminNotifications;
    }
}
