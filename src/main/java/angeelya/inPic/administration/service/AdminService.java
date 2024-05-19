package angeelya.inPic.administration.service;

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
import angeelya.inPic.image.service.ImageService;
import angeelya.inPic.notification.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;
    private final ImageService imageService;
    private final DeletedImageRepository deletedImageRepository;
    private final AdminNotificationService adminNotificationService;

    public List<ImageResponse> getAllImagePost() throws NotFoundDatabaseException, FileException {
        List<Image> images = imageRepository.findAll();
        if (images.isEmpty()) throw new NotFoundDatabaseException("No image posts found");
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(ImageResponse.builder()
                    .image_id(image.getId())
                    .name(image.getName())
                    .image(imageFileService.getImage(image.getImgName())).build());
        }
        return imageResponses;
    }

    public String deleteImagePost(ImageRequest imageRequest) throws DeleteDatabaseException, NotFoundDatabaseException, FileException, NoAddDatabaseException {
        Image image = imageService.getImage(imageRequest.getImage_id());
        User user = image.getUser();
        delete(image);
        Optional<Image> imageOptional = imageRepository.findById(imageRequest.getImage_id());
        if (imageOptional.isPresent()) throw new DeleteDatabaseException("Failed to delete image");
        DeletedImage deletedImage=saveDeletedImage(DeletedImage.builder().imgName(image.getImgName())
                .path(image.getPath()).build());
        adminNotificationService.addNotification(deletedImage, user);
        return "Image deleting is successful";
    }

    private DeletedImage saveDeletedImage(DeletedImage deletedImage) throws NoAddDatabaseException {
        try {
            return deletedImageRepository.save(deletedImage);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to add deleted image");
        }
    }

    private void delete(Image image) throws DeleteDatabaseException {
        try {
            imageRepository.delete(image);
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException("Failed to delete image");
        }
    }
}
