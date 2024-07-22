package br.ufg.ceia.gameinsight.userservice.exceptions;

/**
 * This class represents an exception thrown when the credentials are invalid.
 */
public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
