package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.AlbumRepository;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserService userService;
    private final ImageRepository imageRepository;

    public List<Album> getAllUserAlbum(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        List<Album> albums = albumRepository.findByUser_Id(userInformationRequest.getUser_id());
        if (albums.isEmpty()) throw new NotFoundDatabaseException("No albums found");
        return albums;
    }

    public String addAlbum(AlbumRequest albumRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user =userService.getUser(albumRequest.getUser_id());
        Album album = Album.builder().name(albumRequest.getName())
                .user(user)
                .security(albumRequest.getSecurity())
                .build();
        if (!album.getImages().isEmpty()) album = getImagesIntoAlbum(album, albumRequest.getImageRequests());
        album = albumRepository.save(album);
        if (album == null) throw new NoAddDatabaseException("Failed to add album");
        return "Album adding is successful";
    }
    public Album getAlbum(Long album_id) throws NotFoundDatabaseException {
        Optional<Album> albumOptional = albumRepository.findById(album_id);
        if(albumOptional.isEmpty()) throw new NotFoundDatabaseException("Album not found");
        return albumOptional.get();
    }

    public String updateAlbumData(AlbumUpdateRequest albumUpdateRequest) throws NotFoundDatabaseException, NoAddDatabaseException {
        Album album = getAlbum(albumUpdateRequest.getAlbum_id());
        album.setName(albumUpdateRequest.getName());
        album.setSecurity(albumUpdateRequest.getSecurity());
        album = albumRepository.save(album);
        if(!album.getName().equals(albumUpdateRequest.getName())&&!album.getSecurity().equals(albumUpdateRequest.getSecurity()))
            throw new NoAddDatabaseException("Failed to update album");
            return "Album updating is successful";
    }
    public String deleteAlbum(AlbumDeleteRequest albumDeleteRequest) throws DeleteDatabaseException {   Long album_id=albumDeleteRequest.getAlbum_id();
        albumRepository.deleteById(album_id);
        Optional<Album> album=albumRepository.findById(album_id);
        if(album.isPresent()) throw new DeleteDatabaseException("Failed to delete album");
        return "Album deleting is successful";
    }
    private Album getImagesIntoAlbum(Album album, List<ImageRequest> imageRequests) {
        List<Long> images = imageRequests.stream().map(imageRequest -> imageRequest.getImage_id()).collect(Collectors.toList());
        album.setImages(imageRepository.findAllById(images));
        return album;
    }

}
