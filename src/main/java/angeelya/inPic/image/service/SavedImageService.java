package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.SavedImage;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.SavedImageRepository;
import angeelya.inPic.dto.request.SavedImageAddRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.ForbiddenRequestException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedImageService {
    private final SavedImageRepository savedImageRepository;
    private final UserGetService userGetService;
    private final ImageGetService imageGetService;
    private final ImageService imageService;
    private final AlbumService albumService;
    private final ActionService actionService;
    private final ImageFileService imageFileService;
    private static final String MS_FORBIDDEN = "User cannot save his image", MS_SUCCESS_ADD = "Saved image adding is successful",
            MS_FAILED_SAVE = "Failed to add saved image", MS_NOT_FOUND = "No saved and created images found";


    public String addSavedImage(SavedImageAddRequest savedImageAddRequest) throws NotFoundDatabaseException, NoAddDatabaseException, ForbiddenRequestException {
        User user = userGetService.getUser(savedImageAddRequest.getUser_id());
        Image image = imageGetService.getImage(savedImageAddRequest.getImage_id());
        if (image.getUser().getId().equals(savedImageAddRequest.getUser_id()))
            throw new ForbiddenRequestException(MS_FORBIDDEN);
        if (savedImageAddRequest.getAlbum_id() != null)
            image.setAlbums(List.of(albumService.getAlbum(savedImageAddRequest.getAlbum_id())));
        try {
            savedImageRepository.save(SavedImage.builder().image(image).user(user).build());
            actionService.setGrade(savedImageAddRequest.getUser_id(), savedImageAddRequest.getImage_id(), true);
            return MS_SUCCESS_ADD;
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }

    public List<ImageResponse> getSavedAndCreatedUserImage(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<ImageResponse> createdImages, savedImages = getSavedImages(userInformationRequest);
        try {
            createdImages = imageService.getUserCreatedImages(userInformationRequest);
            if (savedImages.isEmpty()) return createdImages;
            else {
                savedImages.addAll(createdImages);
                return savedImages.stream()
                        .sorted(Comparator.comparingLong(ImageResponse::getImage_id))
                        .collect(Collectors.toList());
            }
        } catch (NotFoundDatabaseException e) {
            if (!savedImages.isEmpty())
                return savedImages;
            else throw new NotFoundDatabaseException(MS_NOT_FOUND);
        }
    }

    private List<ImageResponse> getSavedImages(UserInformationRequest userInformationRequest) throws FileException {
        List<SavedImage> savedImages = savedImageRepository.findByUser_Id(userInformationRequest.getUser_id());
        List<ImageResponse> imageResponses = new ArrayList<>();
        if (savedImages.isEmpty()) return imageResponses;
        for (SavedImage savedImage : savedImages) {
            imageResponses.add(ImageResponse.builder()
                    .image_id(savedImage.getImage().getId())
                    .imgName(savedImage.getImage().getImgName())
                    .name(savedImage.getImage().getName())
                    .image(imageFileService.getImage(savedImage.getImage().getImgName())).build());
        }
        return imageResponses;
    }
}
