package angeelya.inPic.administration.service;

import angeelya.inPic.database.model.AdminNotification;
import angeelya.inPic.database.model.DeletedImage;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.DeletedImageRepository;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.ImageRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.image.service.ImageGetService;
import angeelya.inPic.notification.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;
    private final ImageGetService imageGetService;
    private final DeletedImageRepository deletedImageRepository;
    private final AdminNotificationService adminNotificationService;
    private static final String MS_NOT_FOUND_POSTS = "No image posts found", MS_FAILED_DELETE = "Failed to delete image",
            MS_SUCCESS_DELETE = "Image deleting is successful", MS_FAILED_ADD_DELETED_IMAGE = "Failed to add deleted image";

    public List<ImageResponse> getAllImagePost() throws NotFoundDatabaseException, FileException {
        List<Image> images = imageRepository.findAll();
        if (images.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_POSTS);
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(ImageResponse.builder()
                    .image_id(image.getId())
                    .imgName(image.getImgName())
                    .name(image.getName())
                    .image(imageFileService.getImage(image.getImgName())).build());
        }
        return imageResponses;
    }

    @Transactional
    public String deleteImagePost(ImageRequest imageRequest) throws DeleteDatabaseException, NotFoundDatabaseException, NoAddDatabaseException {
        Image image = imageGetService.getImage(imageRequest.getImage_id());
        User user = image.getUser();
        delete(image);
        Optional<Image> imageOptional = imageRepository.findById(imageRequest.getImage_id());
        if (imageOptional.isPresent()) throw new DeleteDatabaseException(MS_FAILED_DELETE);
        DeletedImage deletedImage = DeletedImage.builder().imgName(image.getImgName())
                .path(image.getPath()).build();
        AdminNotification adminNotification = adminNotificationService.addNotification(deletedImage, user);
        deletedImage.setAdminNotification(adminNotification);
        saveDeletedImage(deletedImage);
        return MS_SUCCESS_DELETE;
    }

    private void saveDeletedImage(DeletedImage deletedImage) throws NoAddDatabaseException {
        try {
             deletedImageRepository.save(deletedImage);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_ADD_DELETED_IMAGE);
        }
    }

    private void delete(Image image) throws DeleteDatabaseException {
        try {
            imageRepository.delete(image);
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE);
        }
    }
}
