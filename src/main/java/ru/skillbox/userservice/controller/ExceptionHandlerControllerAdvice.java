package ru.skillbox.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.exception.UserNotFoundException;
import ru.skillbox.userservice.exception.UserSubscriptionException;

import java.sql.SQLException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    private static final String UNIQUE_VIOLATION_STATE = "23505";

    @ExceptionHandler({UserNotFoundException.class, TownNotFoundException.class, UserSubscriptionException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseDto> exceptionHandler(Exception ex) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(ex.getMessage());
        responseDto.setResult(false);

        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseDto> exceptionHandler(SQLException ex) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResult(false);

        if (ex.getSQLState().equals(UNIQUE_VIOLATION_STATE)) {
            responseDto.setMessage(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDto);
        } else {
            responseDto.setMessage("Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }
}
