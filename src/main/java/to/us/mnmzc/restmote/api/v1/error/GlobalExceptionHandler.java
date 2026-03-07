package to.us.mnmzc.restmote.api.v1.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * A global exception handler for handling exceptions thrown by the controllers and returning appropriate responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiMessage> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String type = ex.getParameterType();
        String message = String.format("Missing required parameter: %s of type %s", name, type);
        return ResponseEntity.badRequest().body(new ApiMessage(message));
    }

    // catchall
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage> handleGenericException(Exception ex) {
        String message = "Internal server error.";
        log.error("Unexpected API error.", ex);
        return ResponseEntity.internalServerError().body(new ApiMessage(message));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiMessage> handleIllegalStateException(IllegalStateException ex) {
        String message = ex.getMessage();
        return ResponseEntity.badRequest().body(new ApiMessage(message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiMessage> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' is of the wrong type. Expected type: %s", name, type);
        return ResponseEntity.badRequest().body(new ApiMessage(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiMessage> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Malformed request.";
        return ResponseEntity.badRequest().body(new ApiMessage(message));
    }

}
