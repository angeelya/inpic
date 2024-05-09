package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.UserImage;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.ImagePageRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.file.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;
    private final String MS_FAILED = "Failed to load images", MS_NOT_FOUND="not found";

    public List<ImageResponse> getLikedImage(UserInformationRequest userInformationRequest) throws FileException, DatabaseNotFoundException {
        List<Image> images = imageRepository.findByLike_User_Id(userInformationRequest.getUser_id());
        if (images.isEmpty()) throw new DatabaseNotFoundException("Liked image "+MS_NOT_FOUND);
        List<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : images) {
            imageResponses.add(new ImageResponse(image.getId(), getImage(image.getPath(), image.getImgName()), image.getName()));
        }
        return imageResponses;
    }

    public ImagePageResponse getImageData(ImagePageRequest imagePageRequest) throws DatabaseNotFoundException, FileException {
        Optional<Image> imageOptional = imageRepository.findById(imagePageRequest.getImage_id());
        if (imageOptional.isEmpty()) throw new DatabaseNotFoundException("Image page data "+MS_NOT_FOUND);
        Image image = imageOptional.get();
        UserImage userImage = image.getUser().getUserImage();
        return ImagePageResponse.builder().
                user_id(image.getUser().getId()).
                imgName(image.getName()).
                imgSystemName(image.getImgName()).
                userName(image.getUser().getName()).
                imgDescription(image.getDescription())
                .image(getImage(image.getPath(),image.getImgName()))
                .userImg(getImage(userImage.getPath(), userImage.getName()))
                .likeCount(image.getLike().size()).build();
    }

    private byte[] getImage(String path, String imgName) throws FileException {
        try {
            return imageFileService.getImage(path, imgName);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new FileException(MS_FAILED);
        }
    }
}
