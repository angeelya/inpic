package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.AlbumRepository;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.AlbumPageDataResponse;
import angeelya.inPic.dto.response.ImageResourceResponse;
import angeelya.inPic.dto.response.UserAlbumResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.user.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserGetService userGetService;
    private final ImageGetService imageGetService;
    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;
    private static final String MS_NOT_FOUND_ALBUMS = "No albums found", MS_SUCCESS_ADD = "Album adding is successful",
            MS_NOT_FOUND_ALBUM = "Album not found", MS_FAILED_UPDATE_IMAGE_LIST = "Failed to update album images list",

    MS_SUCCESS_IMAGE_LIST_ADD = "Images list updating is successful", MS_FAILED_UPDATE = "Failed to update album",
            MS_SUCCESS_UPDATE = "Album updating is successful", MS_FAILED_DELETE = "Failed to delete album",
            MS_SUCCESS_DELETE = "Album deleting is successful", MS_FAILED_SAVE = "Failed to save album",
    MS_NOT_FOUND_IMAGES="No images found";
    public List<Album> getAllUserAlbum(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        List<Album> albums = albumRepository.findByUser_Id(userInformationRequest.getUser_id());
        if (albums.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_ALBUMS);
        return albums;
    }

    public List<UserAlbumResponse> getUserAlbums(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException, FileException {
        List<Album> albums = getAllUserAlbum(userInformationRequest);
        List<UserAlbumResponse> userAlbumResponses = new ArrayList<>();
        for (Album album : albums) {
            UserAlbumResponse userAlbumResponse = UserAlbumResponse.builder()
                    .album_id(album.getId())
                    .name(album.getName()).build();
            Optional<Image> lastImage = album.getImages().stream()
                    .reduce((first, second) -> second);
            if (lastImage.isPresent())
                userAlbumResponse.setLastImage(imageFileService.getImage(lastImage.get().getImgName()));
            userAlbumResponses.add(userAlbumResponse);
        }
        return userAlbumResponses;
    }

    public String addAlbum(AlbumAddRequest albumAddRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userGetService.getUser(albumAddRequest.getUser_id());
        Album album = Album.builder().name(albumAddRequest.getName())
                .user(user)
                .security(albumAddRequest.getSecurity())
                .build();
        if (albumAddRequest.getImageRequests()!=null)
            album = getImagesIntoAlbum(album, albumAddRequest.getImageRequests());
        saveAlbum(album);
        return MS_SUCCESS_ADD;
    }

    public AlbumPageDataResponse getAlbumPageData(AlbumRequest albumRequest) throws NotFoundDatabaseException, FileException {
        Album album = getAlbum(albumRequest.getAlbum_id());
        List<Image> images = album.getImages();
        List<ImageResourceResponse> imageResourceResponses = new ArrayList<>();
        for (Image image : images) {
            imageResourceResponses.add(ImageResourceResponse.builder().
                    image_id(image.getId())
                    .imgName(image.getImgName())
                    .image(imageFileService.getImage(image.getImgName())).build());
        }
        return AlbumPageDataResponse.builder().album_id(album.getId())
                .name(album.getName())
                .imageResourceResponses(imageResourceResponses).build();
    }

    public Album getAlbum(Long album_id) throws NotFoundDatabaseException {
        Optional<Album> albumOptional = albumRepository.findById(album_id);
        if (albumOptional.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_ALBUM);
        return albumOptional.get();
    }

    public String deleteImageFromAlbum(DeleteImageFromAlbumRequest request) throws NotFoundDatabaseException, NoAddDatabaseException {
        Album album = getAlbum(request.getAlbum_id());
        Image img = imageGetService.getImage(request.getImage_id());
        List<Image> images = album.getImages();
        images.remove(img);
        album.setImages(images);
        album = saveAlbum(album);
        if (album.getImages().contains(img)) throw new NoAddDatabaseException(MS_FAILED_UPDATE_IMAGE_LIST);
        return MS_SUCCESS_IMAGE_LIST_ADD;
    }

    public String updateAlbumData(AlbumUpdateRequest albumUpdateRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        Album album = getAlbum(albumUpdateRequest.getAlbum_id());
        album.setName(albumUpdateRequest.getName());
        album.setSecurity(albumUpdateRequest.getSecurity());
        album = saveAlbum(album);
        if (!album.getSecurity().equals(albumUpdateRequest.getSecurity()) && !album.getName().equals(albumUpdateRequest.getName()))
            throw new NoAddDatabaseException(MS_FAILED_UPDATE);
        return MS_SUCCESS_UPDATE;
    }

    public String deleteAlbum(AlbumRequest albumRequest) throws DeleteDatabaseException {
        Long album_id = albumRequest.getAlbum_id();
        delete(album_id);
        Optional<Album> album = albumRepository.findById(album_id);
        if (album.isPresent()) throw new DeleteDatabaseException(MS_FAILED_DELETE);
        return MS_SUCCESS_DELETE;
    }

    private void delete(Long album_id) throws DeleteDatabaseException {
        try {
            albumRepository.deleteById(album_id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeleteDatabaseException(MS_FAILED_DELETE);
        }
    }

    private Album saveAlbum(Album album) throws NoAddDatabaseException {
        try {
            return albumRepository.save(album);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException(MS_FAILED_SAVE);
        }
    }

    private Album getImagesIntoAlbum(Album album, List<ImageRequest> imageRequests) throws NotFoundDatabaseException {
        List<Long> imagesLong = imageRequests.stream().map(ImageRequest::getImage_id).collect(Collectors.toList());
        List<Image> images = imageRepository.findAllByIdIn(imagesLong);
        if(images.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_IMAGES);
        album.setImages(images);
        return album;
    }

}
