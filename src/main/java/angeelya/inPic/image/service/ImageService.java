package angeelya.inPic.image.service;

import angeelya.inPic.database.model.*;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.ImageAddRequest;
import angeelya.inPic.dto.request.ImageUpdateRequest;
import angeelya.inPic.dto.request.ImagePageRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.*;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.recommedation.service.ActionService;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserService userService;
    private final ImageFileService imageFileService;
    private final CategoryService categoryService;
    private final AlbumService albumService;
    private final ActionService actionService;

    private final String MS_NOT_FOUND = "not found", MS_FAILED_DELETE = "Failed to delete image",
            MS_SUCCESS_DELETE = "Image deleting is successful", MS_SUCCESS_ADD = "Image adding is successful", MS_FAILED_SAVE = "Failed to save image",
            MS_FORBIDDEN = "User cannot delete other user image", MS_FAILED_UPDATE = "Failed to update image data",
            MS_SUCCESS_UPDATE = "Image updating is successful";

    public List<ImageResponse> getLikedImage(UserInformationRequest userInformationRequest) throws FileException, NotFoundDatabaseException {
        List<Image> images = imageRepository.findByLike_User_Id(userInformationRequest.getUser_id());
        if (images.isEmpty()) throw new NotFoundDatabaseException("Liked image " + MS_NOT_FOUND);
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(new ImageResponse(image.getId(), imageFileService.getImage(image.getImgName()), image.getName()));
        }
        return imageResponses;
    }

    public ImagePageResponse getImageData(ImagePageRequest imagePageRequest) throws NotFoundDatabaseException, FileException {
        Image image = getImage(imagePageRequest.getImage_id());
        UserImage userImage = image.getUser().getUserImage();
        actionService.setGrade(image.getId(), imagePageRequest.getUser_id(), true);
        return ImagePageResponse.builder().
                user_id(image.getUser().getId()).
                imgName(image.getName()).
                imgSystemName(image.getImgName()).
                userName(image.getUser().getName()).
                imgDescription(image.getDescription())
                .image(imageFileService.getImage(image.getImgName()))
                .userImg(imageFileService.getImage(userImage.getName()))
                .likeCount(image.getLike().size()).build();
    }

    public String addImage(ImageAddRequest imageAddRequest, MultipartFile multipartFile) throws NotFoundDatabaseException, FileException, NoAddDatabaseException {
        User user = userService.getUser(imageAddRequest.getUser_id());
        imageFileService.saveImage(multipartFile);
        Image image = Image.builder().user(user)
                .category(categoryService.getCategory(imageAddRequest.getCategory_id()))
                .description(imageAddRequest.getDescription())
                .name(imageAddRequest.getName())
                .imgName(multipartFile.getOriginalFilename())
                .path(imageFileService.getDirectoryPath()).build();
        if (imageAddRequest.getAlbum_id() != null) image = getAlbumsIntoImage(image, imageAddRequest.getAlbum_id());
        saveImage(image);
        return MS_SUCCESS_ADD;
    }

    public Image getImage(Long image_id) throws NotFoundDatabaseException {
        Optional<Image> image = imageRepository.findById(image_id);
        if (image.isEmpty()) throw new NotFoundDatabaseException("Image data" + MS_NOT_FOUND);
        return image.get();
    }

    public List<ImageResponse> getUserCreatedImages(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Image> images = imageRepository.findByUser_Id(userInformationRequest.getUser_id());
        if (images.isEmpty()) throw new NotFoundDatabaseException("No created images found");
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(ImageResponse.builder()
                    .image_id(image.getId())
                    .name(image.getName())
                    .image(imageFileService.getImage(image.getImgName())).build());
        }
        return imageResponses;
    }

    public String updateImageData(ImageUpdateRequest imageUpdateRequest) throws NotFoundDatabaseException, ForbiddenRequestException, NoAddDatabaseException {
        Image image = getImage(imageUpdateRequest.getImage_id());
        if (!image.getUser().equals(imageUpdateRequest.getUser_id())) throw new ForbiddenRequestException(MS_FORBIDDEN);
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

    private Image getAlbumsIntoImage(Image image, Long album_id) throws NotFoundDatabaseException {
        List<Album> albums = List.of(albumService.getAlbum(album_id));
        image.setAlbums(albums);
        return image;
    }
}
