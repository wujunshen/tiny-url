package com.wujunshen.tinyurl.common.handler;

import static com.wujunshen.tinyurl.common.utils.Constants.COMMA;

import com.fasterxml.jackson.core.JsonParseException;
import com.wujunshen.tinyurl.web.response.BaseResponse;
import com.wujunshen.tinyurl.web.response.ResultCode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 *
 * @author frankwoo
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler { // 添加全局异常处理流程，根据需要设置需要处理的异常
  /** 处理自定义异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public BaseResponse<ResultCode> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    // 按需重新封装需要返回的错误信息
    List<String> invalidArguments = new ArrayList<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      invalidArguments.add(error.getDefaultMessage());
    }
    return BaseResponse.error(ResultCode.PARAM_ERROR, StringUtils.join(COMMA, invalidArguments));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public BaseResponse<ResultCode> handlerNoHandlerFoundException(Exception e) {
    log.error("未找到资源异常", e);
    return BaseResponse.error(ResultCode.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public BaseResponse<ResultCode> handleDuplicateKeyException(DuplicateKeyException e) {
    log.error("重复主键异常", e);
    return BaseResponse.error(ResultCode.DUPLICATE_KEY);
  }

  @ExceptionHandler(JsonParseException.class)
  public BaseResponse<ResultCode> handleJsonParseException(JsonParseException e) {
    log.error("入参格式异常", e);
    return BaseResponse.error(ResultCode.PARAM_FORMAT_ERROR);
  }

  @ExceptionHandler(value = Exception.class)
  public BaseResponse<ResultCode> handlerException(Exception e) {
    log.error("全局异常", e);
    return BaseResponse.error(ResultCode.UNKNOWN_EXCEPTION);
  }
}
