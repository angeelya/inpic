package angeelya.inPic.image.controller;

import angeelya.inPic.database.model.Album;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
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
    public ResponseEntity<List<Album>> getAllUserAlbums(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, DatabaseNotFoundException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(albumService.getAllUserAlbum(userInformationRequest));
    }
}
