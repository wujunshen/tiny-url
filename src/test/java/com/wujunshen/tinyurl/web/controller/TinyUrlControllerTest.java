package com.wujunshen.tinyurl.web.controller;

import com.wujunshen.tinyurl.ApplicationTestBase;
import com.wujunshen.tinyurl.web.request.GenTinyUrlRequest;
import com.wujunshen.tinyurl.web.response.BaseResponse;
import com.wujunshen.tinyurl.web.response.ResultCode;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import lombok.extern.slf4j.Slf4j;

import static com.wujunshen.tinyurl.common.utils.Constants.HTTP_LINK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/4/28 12:21 上午 <br>
 */
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TinyUrlControllerTest extends ApplicationTestBase {
  @Autowired private TestRestTemplate template;

  private GenTinyUrlRequest request;

  @BeforeEach
  public void init() {
    request = new GenTinyUrlRequest();
  }

  @AfterEach
  public void clear() {
    request = null;
  }

  @Test
  @DisplayName("原始长链接不能为空")
  public void genTinyUrl1() {
    BaseResponse actual = template.postForObject("/genTinyUrl", request, BaseResponse.class);

    log.info("actual is:{}", actual);

    assertThat(actual.getCode(), equalTo(ResultCode.PARAMETER_VALIDATION.getCode()));
    assertThat(actual.getMessage(), containsString("原始长链接不能为空"));
  }

  @Test
  @DisplayName("长链接最大长度不能超过300个字符")
  public void genTinyUrl2() {
    request.setOriginUrl(HTTP_LINK + RandomStringUtils.randomAscii(301));

    BaseResponse actual = template.postForObject("/genTinyUrl", request, BaseResponse.class);

    log.info("actual is:{}", actual);

    assertThat(actual.getCode(), equalTo(ResultCode.PARAMETER_VALIDATION.getCode()));
    assertThat(actual.getMessage(), containsString("长链接最大长度不能超过300个字符"));
  }

  @Test
  @DisplayName("长链接必须以http或https打头")
  public void genTinyUrl3() {
    request.setOriginUrl(RandomStringUtils.randomAscii(10));

    BaseResponse actual = template.postForObject("/genTinyUrl", request, BaseResponse.class);

    log.info("actual is:{}", actual);

    assertThat(actual.getCode(), equalTo(ResultCode.PARAMETER_VALIDATION.getCode()));
    assertThat(actual.getMessage(), containsString("长链接必须以http或https打头"));
  }

  @Test
  @DisplayName("自定义短链接已存在,不需要重新定义")
  public void genTinyUrl4() {
    request.setOriginUrl("http://www.google.com");
    request.setCustomTinyUrl("sad9ss22");
    BaseResponse actual = template.postForObject("/genTinyUrl", request, BaseResponse.class);

    log.info("actual is:{}", actual);

    assertThat(actual.getCode(), equalTo(ResultCode.EXISTED_TINY_URL.getCode()));
    assertThat(actual.getMessage(), equalTo(ResultCode.EXISTED_TINY_URL.getMessage()));
  }

  @Test
  @DisplayName("不能输入短链接")
  public void genTinyUrl5() {
    request.setOriginUrl("http://localhost:8012/t/sad9ss22");
    BaseResponse actual = template.postForObject("/genTinyUrl", request, BaseResponse.class);

    log.info("actual is:{}", actual);

    assertThat(actual.getCode(), equalTo(ResultCode.FORBIDDEN_INPUT_TINY_URL.getCode()));
    assertThat(actual.getMessage(), equalTo(ResultCode.FORBIDDEN_INPUT_TINY_URL.getMessage()));
  }

  @Test
  @DisplayName("操作成功")
  public void genTinyUrl6() {
    request.setOriginUrl("https://time.geekbang.org/column/article/80850");
    BaseResponse actual = template.postForObject("/genTinyUrl", request, BaseResponse.class);

    log.info("actual is:{}", actual);

    assertThat(actual.getCode(), equalTo(ResultCode.SUCCESS.getCode()));
    assertThat(actual.getMessage(), equalTo(ResultCode.SUCCESS.getMessage()));
  }
}
