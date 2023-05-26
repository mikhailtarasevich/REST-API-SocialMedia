package com.mikhail.tarasevich.socialmedia.controller;

import com.mikhail.tarasevich.socialmedia.service.exception.AuthenticationDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.ImageIncorrectDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotFoundException;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotValidDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotFoundException;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotValidDataException;
import com.mikhail.tarasevich.socialmedia.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userNotValidDataException(UserNotValidDataException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> postNotFoundException(PostNotFoundException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> postNotValidDataException(PostNotValidDataException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> imageIncorrectDataException(ImageIncorrectDataException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> incorrectRequestDataException(IncorrectRequestDataException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> authenticationDataException(AuthenticationDataException exception) {

        return new ResponseEntity<>(ErrorResponse.builder()
                .withMessage(exception.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build(),
                HttpStatus.UNAUTHORIZED);
    }

}
