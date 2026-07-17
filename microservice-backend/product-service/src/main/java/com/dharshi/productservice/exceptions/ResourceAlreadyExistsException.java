package com.dharshi.productservice.exceptions;

public class ResourceAlreadyExistsException extends Exception{
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}