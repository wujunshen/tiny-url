package com.wujunshen.tinyurl.web.controller;

import static com.wujunshen.tinyurl.common.utils.Constants.HTTPS_LINK;
import static com.wujunshen.tinyurl.common.utils.Constants.HTTP_LINK;

import com.wujunshen.tinyurl.web.response.BaseResponse;
import com.wujunshen.tinyurl.web.response.ResultCode;
import java.text.MessageFormat;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/** @author frankwoo */
public class BaseController {
  public BaseResponse<ResultCode> getValidatedResult(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      List<FieldError> errors = bindingResult.getFieldErrors();
      StringBuilder stringBuilder = new StringBuilder();
      for (FieldError error : errors) {
        stringBuilder
            .append(error.getField())
            .append(":")
            .append(error.getDefaultMessage())
            .append(" ,");
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);

      String formattedMessage =
          MessageFormat.format(ResultCode.PARAMETER_VALIDATION.getMessage(), stringBuilder);

      return BaseResponse.error(ResultCode.PARAMETER_VALIDATION, formattedMessage);
    }
    return null;
  }

  public String getOriginalUrl(String originUrl) {
    if (!originUrl.startsWith(HTTP_LINK) && !originUrl.startsWith(HTTPS_LINK)) {
      originUrl = HTTP_LINK + originUrl;
    }
    return originUrl;
  }
}
