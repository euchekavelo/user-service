package ru.tw1.euchekavelo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import ru.tw1.euchekavelo.dto.response.ResponseDto;
import ru.tw1.euchekavelo.exception.*;

import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String UNIQUE_VIOLATION_STATE = "23505";

    @ExceptionHandler({UserSubscriptionException.class, MethodArgumentNotValidException.class,
            IncorrectFileFormatException.class, IOException.class})
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
            UserGroupNotFoundException.class, PhotoNotFoundException.class, UserSubscriptionNotFoundException.class})
    public ResponseEntity<ResponseDto> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ResponseDto> handeResourceAccessDeniedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ResponseDto> handleExceptionsFromAuthService(RestClientException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getResponseDto(ex.getMessage()));
    }

    private ResponseDto getResponseDto(String message) {
        return ResponseDto.builder()
                .message(message)
                .result(false)
                .build();
    }
}
