package angeelya.inPic.exception_handling;

import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler({ValidationErrorsException.class,AuthException.class,PasswordUpdateException.class})
    public ResponseEntity handlerExceptionBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }
    @ExceptionHandler(DatabaseNotFoundException.class)
    public ResponseEntity handlerDatabaseNotFoundException(DatabaseNotFoundException e) {
        return new ResponseEntity(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({FileException.class, NoAddDatabaseException.class})
    public ResponseEntity handlerException(Exception e) {
        return new ResponseEntity(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
