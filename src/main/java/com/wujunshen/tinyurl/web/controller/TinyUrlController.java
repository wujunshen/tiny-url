package com.wujunshen.tinyurl.web.controller;

import com.wujunshen.swagger.annotation.ApiVersion;
import com.wujunshen.tinyurl.properties.TuProperties;
import com.wujunshen.tinyurl.service.TinyUrlService;
import com.wujunshen.tinyurl.web.request.GenTinyUrlRequest;
import com.wujunshen.tinyurl.web.response.BaseResponse;
import com.wujunshen.tinyurl.web.response.ResultCode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * 短链
 *
 * @author frankwoo
 */
@Slf4j
@Api(value = "/")
@RequestMapping(value = "/")
@RestController
public class TinyUrlController extends BaseController {
  @Resource private TuProperties tuProperties;
  @Resource private TinyUrlService tinyUrlService;

  /**
   * @param request 生成短链接请求对象
   * @return 返回对象包含原始长链接,生成短链接以及短链接生成类型(系统生成还是自定义的短链接)
   */
  @ResponseBody
  @PostMapping("/genTinyUrl")
  @ApiOperation(
      value = "生成短链接",
      httpMethod = "POST",
      notes = "生成短链接",
      response = BaseResponse.class)
  @ApiVersion(version = "v1.0")
  public BaseResponse genTinyUrl(
      @Valid @ApiParam(value = "请求对象", required = true) @RequestBody GenTinyUrlRequest request,
      BindingResult bindingResult) {
    // 入参校验
    BaseResponse<ResultCode> baseResponse = getValidatedResult(bindingResult);
    if (baseResponse != null) {
      return baseResponse;
    }

    // 自定义短链接如果数据库中已持久化,则提示用户自定义短链接已存在
    String customTinyUrl = request.getCustomTinyUrl();
    if (!StringUtils.isBlank(customTinyUrl)
        && tinyUrlService.isExistedCustomTinyUrlInDb(customTinyUrl)) {
      return BaseResponse.error(ResultCode.EXISTED_TINY_URL);
    }

    // 用户输入的是短链接,则提示用户不能输入短链接
    String originUrl = request.getOriginUrl();
    if (originUrl.startsWith(tuProperties.getTinyUrlDomain())) {
      return BaseResponse.error(ResultCode.FORBIDDEN_INPUT_TINY_URL);
    }

    request.setOriginUrl(getOriginalUrl(originUrl));

    return BaseResponse.success(tinyUrlService.genTinyUrl(request));
  }
}
