package com.mikhail.tarasevich.socialmedia.service.exception;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }

}
