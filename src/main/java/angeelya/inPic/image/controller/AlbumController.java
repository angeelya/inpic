package angeelya.inPic.image.controller;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.dto.request.AlbumDeleteRequest;
import angeelya.inPic.dto.request.AlbumRequest;
import angeelya.inPic.dto.request.AlbumUpdateRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.DeleteDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.image.service.AlbumService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {
    private final ValidationErrorsService validationErrorsService;
    private final AlbumService albumService;

    @PostMapping("/all/by/user")
    public ResponseEntity<List<Album>> getAllUserAlbums(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(albumService.getAllUserAlbum(userInformationRequest));
    }
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addAlbum(@RequestBody @Valid AlbumRequest albumRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.addAlbum(albumRequest)));
    }
    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateAlbumData(@RequestBody @Valid AlbumUpdateRequest albumUpdateRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.updateAlbumData(albumUpdateRequest)));
    }
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deleteAlbum(@RequestBody @Valid AlbumDeleteRequest albumDeleteRequest, BindingResult bindingResult) throws ValidationErrorsException, DeleteDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.deleteAlbum(albumDeleteRequest)));
    }
}
