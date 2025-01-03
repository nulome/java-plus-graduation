package ru.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.exception.ConflictException;
import ru.exception.FeignException;
import ru.exception.IncorrectDateException;
import ru.exception.NotFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Error 400 {} \n {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.error("Error 400 {} \n {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.error("Error 400 {} \n {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.error("Error 404 {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public ApiError handleFeignException(final FeignException e) {
        log.error("Error 418 {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler({IncorrectDateException.class, ConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RuntimeException e) {
        log.error("Error 409 {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.error("Error Exception 500 {}", e.getMessage());
        return handleResponseCreate(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiError handleResponseCreate(Exception e, HttpStatus status) {
        String classInit = Arrays.stream(e.getStackTrace()).findAny().get().toString();
        String localMessage = e.getLocalizedMessage() + " \n Class: " + classInit;

        e.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        return new ApiError(status, localMessage, e.getMessage(), stackTrace);
    }
}
