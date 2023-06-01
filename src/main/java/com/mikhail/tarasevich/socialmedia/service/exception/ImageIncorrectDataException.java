package com.mikhail.tarasevich.socialmedia.service.exception;

public class ImageIncorrectDataException extends IllegalArgumentException{

    public ImageIncorrectDataException(String errMessage){
        super(errMessage);
    }

}
