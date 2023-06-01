package com.mikhail.tarasevich.socialmedia.service.exception;

public class IncorrectRequestDataException extends IllegalArgumentException{

    public IncorrectRequestDataException(String errMessage){
        super(errMessage);
    }

}
