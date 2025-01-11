package gr.aueb.cf.inventorymanagementsystem.core.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("supplier")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Cannot delete supplier because it is associated with other records.");
        } else if (ex.getMessage().contains("category")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Cannot delete category because it is associated with other records.");
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Data integrity violation occurred.");
    }
}