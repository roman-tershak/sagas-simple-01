package rt.sagas.orderservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ErrorControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<ApiError> handleException(MethodArgumentNotValidException e) {
        return getApiErrorResponseEntity(
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<ApiError> handleException(Exception e) {
        return getApiErrorResponseEntity(
                "Internal server error - " + e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> getApiErrorResponseEntity(String errorMessage, HttpStatus status) {
        return new ResponseEntity<>(
                new ApiError(errorMessage), status);
    }

}
