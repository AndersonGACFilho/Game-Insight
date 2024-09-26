package br.ufg.ceia.gameinsight.userservice.exceptions;


import br.ufg.ceia.gameinsight.userservice.exceptions.erros.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;

/**
 * This class represents a global exception handler.
 * <p>
 * This class is responsible for handling exceptions that are not handled by the controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle the ResourceNotFoundException.
     * @param ex the exception.
     * @param request the request.
     * @return the response entity.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<StandardError> handleResourceNotFoundException(ResourceNotFoundException ex,
        HttpServletRequest request)
    {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle the DataIntegrityViolationException.
     * @param ex the exception.
     * @param request the request.
     * @return the response entity.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
        HttpServletRequest request)
    {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Data integrity violation",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
