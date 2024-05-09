package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.database.repository.AlbumRepository;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    public List<Album> getAllUserAlbum(UserInformationRequest userInformationRequest) throws DatabaseNotFoundException {
       List<Album> albums= albumRepository.findByUser_Id(userInformationRequest.getUser_id());
       if(albums.isEmpty()) throw new DatabaseNotFoundException("No albums found");
       return albums;
    }
}
