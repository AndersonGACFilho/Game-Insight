package br.ufg.ceia.gameinsight.userservice.exceptions;

/**
 * This class represents an exception thrown when a resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
