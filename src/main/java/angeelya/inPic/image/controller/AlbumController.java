package angeelya.inPic.image.controller;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.AlbumPageDataResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.dto.response.UserAlbumResponse;
import angeelya.inPic.exception_handling.exception.*;
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
    public ResponseEntity<MessageResponse> addAlbum(@RequestBody @Valid AlbumAddRequest albumAddRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.addAlbum(albumAddRequest)));
    }
    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateAlbumData(@RequestBody @Valid AlbumUpdateRequest albumUpdateRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.updateAlbumData(albumUpdateRequest)));
    }
    @PostMapping("/delete/image")
    public ResponseEntity<MessageResponse> deleteImageFromAlbum(@RequestBody @Valid DeleteImageFromAlbumRequest deleteImageFromAlbumRequest, BindingResult bindingResult) throws NotFoundDatabaseException, NoAddDatabaseException, ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.deleteImageFromAlbum(deleteImageFromAlbumRequest)));
    }
    @PostMapping("/page/data")
    public ResponseEntity<AlbumPageDataResponse> getPageData(@RequestBody @Valid AlbumRequest albumRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(albumService.getAlbumPageData(albumRequest));
    }
    @PostMapping("/all/by/user/with/image")
    public ResponseEntity<List<UserAlbumResponse>> getUserAlbumsWithImage(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(albumService.getUserAlbums(userInformationRequest));
    }
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deleteAlbum(@RequestBody @Valid AlbumRequest albumRequest, BindingResult bindingResult) throws ValidationErrorsException, DeleteDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(albumService.deleteAlbum(albumRequest)));
    }
}
