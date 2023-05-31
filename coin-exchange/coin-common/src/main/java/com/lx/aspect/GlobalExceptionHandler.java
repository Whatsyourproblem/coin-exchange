package com.lx.aspect;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.lx.model.BusinessException;
import com.lx.model.R;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 内部API调用的异常处理
     **/
    @ExceptionHandler(value = ApiException.class)
    public R handleApiException(ApiException e) {
        IErrorCode errorCode = e.getErrorCode();
        if (errorCode == null) {
            return R.fail(errorCode);
        }
        return R.fail(e.getMessage());
    }

    /**
     * 2. 方法参数校验失败的异常
     **/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        // 获取异常绑定结果
        BindingResult bindingResult = exception.getBindingResult();
        // 如果结果中有错误
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(exception.getMessage());
    }

    /**
     * 3.对象内部使用Validate 没有校验成功的异常
     **/
    @ExceptionHandler(value = BindException.class)
    public R handlerBindException(BindException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(bindException.getMessage());
    }

    /**
     * 自定义运行时异常
     */
    @ExceptionHandler(value = BusinessException.class)
    public R handleRRException(BusinessException e) {
        return R.fail(e.getCode(), e.getMsg());
    }
}
