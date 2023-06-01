package com.mikhail.tarasevich.socialmedia.util;

import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BindingResultValidator {

    public static void checkErrorsInBindingResult(BindingResult bindingResult, Class<? extends RuntimeException> exceptionClass) {
        if (bindingResult.hasErrors()) {
            StringBuilder errMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            try {
                throw exceptionClass.getConstructor(String.class).newInstance(errMsg.toString());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new IncorrectRequestDataException("Something went wrong..." + e);
            }
        }
    }

}
