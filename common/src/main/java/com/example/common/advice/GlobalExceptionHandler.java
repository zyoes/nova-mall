package com.example.common.advice;

import com.example.common.exception.CustomException;
import com.example.common.exception.CustomForbiddenException;
import com.example.common.exception.CustomUnauthorizedException;
import com.example.common.exception.CustomValidationException;
import com.example.common.response.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NoSuchElementException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Object> XXX(Exception e) {
        R<Object> r = new R<>(404, "Not Found", null);
        return r;
    }

    /**
     * 捕捉到 Spring 框架 BindException 校验异常的统一处理
     *
     * @param e 异常
     * @return R 统一返回结果
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<Object> bindExceptionHandler(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return R.failValid(errorMessage);
    }

    @ExceptionHandler(value = CustomValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<Object> customValidationExceptionHandler(CustomValidationException e) {
        return R.failValid(e.getMessage());
    }

    @ExceptionHandler(value = CustomUnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Object> customTokenValidationExceptionHandler(CustomUnauthorizedException e) {
        return R.fail(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(value = CustomForbiddenException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Object> customForbiddenExceptionHandler(CustomForbiddenException e) {
        return R.failValid(e.getMessage());
    }

    /**
     * 捕捉到 CustomException 自定义异常的统一处理
     * 这是自定义类型异常的兜底操作，建议针对明确的异常类型做特定处理，方便返回更合适的状态码和消息
     *
     * @param e 异常
     * @return R 统一返回结果
     */
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> customExceptionHandler(Exception e) {
        return R.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
