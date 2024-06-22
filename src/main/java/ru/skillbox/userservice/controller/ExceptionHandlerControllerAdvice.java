package ru.skillbox.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.exception.*;

import java.sql.SQLException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    private static final String UNIQUE_VIOLATION_STATE = "23505";

    @ExceptionHandler({UserSubscriptionException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(getResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseDto> handleSqlException(SQLException ex) {
        if (ex.getSQLState().equals(UNIQUE_VIOLATION_STATE)) {
            return ResponseEntity.badRequest().body(getResponseDto(ex.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class, TownNotFoundException.class, GroupNotFoundException.class,
            UserGroupNotFoundException.class})
    public ResponseEntity<ResponseDto> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseDto(ex.getMessage()));
    }

    private ResponseDto getResponseDto(String message) {
        return ResponseDto.builder()
                .message(message)
                .result(false)
                .build();
    }
}
