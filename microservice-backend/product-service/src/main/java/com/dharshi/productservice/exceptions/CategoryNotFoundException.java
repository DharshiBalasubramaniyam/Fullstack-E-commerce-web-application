package com.dharshi.productservice.exceptions;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(String message){
        super(message);
    }

}
