package angeelya.inPic.image.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.ForbiddenRequestException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserGetService userGetService;
    private final ImageFileService imageFileService;
    private final CategoryService categoryService;
    private final AlbumService albumService;
    private final ActionService actionService;
    private final ImageGetService imageGetService;

    private static final String MS_NOT_FOUND_LIKED_IMAGE = "Liked image not found", MS_SUCCESS_ADD = "Image adding is successful", MS_FAILED_SAVE = "Failed to save image",
            MS_FORBIDDEN = "User cannot update other user image", MS_FAILED_UPDATE = "Failed to update image data",
            MS_SUCCESS_UPDATE = "Image updating is successful", MS_NOT_FOUND_CREATED_IMAGE = "No created images found";


    public List<ImageResponse> getLikedImage(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<Image> images = imageRepository.findByLike_User_Id(userInformationRequest.getUser_id());
        if (images.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_LIKED_IMAGE);
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(new ImageResponse(image.getId(), image.getImgName(), imageFileService.getImage(image.getImgName()), image.getName()));
        }
        return imageResponses;
    }

    public ImagePageResponse getImageData(ImagePageRequest imagePageRequest) throws NotFoundDatabaseException, FileException, NoAddDatabaseException {
        Image image = imageGetService.getImage(imagePageRequest.getImage_id());
        UserImage userImage = image.getUser().getUserImage();
        actionService.setGrade(imagePageRequest.getUser_id(), image.getId(), true);
        ImagePageResponse imagePageResponse = ImagePageResponse.builder()
                .user_id(image.getUser().getId())
                .name(image.getName())
                .imgSystemName(image.getImgName())
                .userName(image.getUser().getName())
                .imgDescription(image.getDescription())
                .image(imageFileService.getImage(image.getImgName()))
                .likeCount(image.getLike().size()).build();
        if (userImage != null) imagePageResponse.setUserImg(imageFileService.getImage(userImage.getName()));
        return imagePageResponse;
    }

    public String addImage(ImageAddRequest imageAddRequest, MultipartFile multipartFile) throws NotFoundDatabaseException, FileException, NoAddDatabaseException {
        User user = userGetService.getUser(imageAddRequest.getUser_id());
        imageFileService.saveImage(multipartFile);
        Image image = Image.builder().user(user)
                .category(categoryService.getCategory(imageAddRequest.getCategory_id()))
                .description(imageAddRequest.getDescription())
                .name(imageAddRequest.getName())
                .imgName(multipartFile.getOriginalFilename())
                .path(imageFileService.getDirectoryPath()).build();
        if (imageAddRequest.getAlbum_id() != null) image.setAlbums(getAlbumsIntoImage(imageAddRequest.getAlbum_id()));
        saveImage(image);
        return MS_SUCCESS_ADD;
    }

    public List<ImageResponse> getUserCreatedImages(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Image> images = imageRepository.findByUser_Id(userInformationRequest.getUser_id());
        if (images.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_CREATED_IMAGE);
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(ImageResponse.builder()
                    .imgName(image.getImgName())
                    .image_id(image.getId())
                    .name(image.getName())
                    .image(imageFileService.getImage(image.getImgName())).build());
        }
        return imageResponses;
    }

    public String updateImageData(ImageUpdateRequest imageUpdateRequest) throws NotFoundDatabaseException, ForbiddenRequestException, NoAddDatabaseException {
        Image image = imageGetService.getImage(imageUpdateRequest.getImage_id());
        if (!image.getUser().getId().equals(imageUpdateRequest.getUser_id()))
            throw new ForbiddenRequestException(MS_FORBIDDEN);
        image.setCategory(categoryService.getCategory(imageUpdateRequest.getCategory_id()));
        image.setDescription(imageUpdateRequest.getDescription());
        image.setName(imageUpdateRequest.getName());
        image = saveImage(image);
        if (!image.getName().equals(imageUpdateRequest.getName()) &&
                !image.getDescription().equals(imageUpdateRequest.getDescription()) &&
                !image.getCategory().getId().equals(imageUpdateRequest.getCategory_id()))
            throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        return MS_SUCCESS_UPDATE;
    }

    private Image saveImage(Image image) throws NoAddDatabaseException {
        try {
            return imageRepository.save(image);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }

    private List<Album> getAlbumsIntoImage(Long album_id) throws NotFoundDatabaseException {
        return List.of(albumService.getAlbum(album_id));
    }
}
