package com.wujunshen.tinyurl.service;

import com.wujunshen.tinyurl.ApplicationTestBase;
import com.wujunshen.tinyurl.web.request.GenTinyUrlRequest;
import com.wujunshen.tinyurl.web.response.TinyUrlData;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.wujunshen.tinyurl.common.utils.Constants.SLASH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/4/27 2:38 下午 <br>
 */
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TinyUrlServiceTest extends ApplicationTestBase {
  @Resource private TinyUrlService tinyUrlService;

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
  @DisplayName("系统自动生成短链接")
  public void op1() {
    request.setOriginUrl("www.baidu.com");
    TinyUrlData data = tinyUrlService.genTinyUrl(request);
    log.info("\ndata is:{}", data);

    String tinyUrl = data.getTinyUrl();

    String result = tinyUrlService.getOriginUrl(tinyUrl.substring(tinyUrl.lastIndexOf(SLASH) + 1));
    log.info("\nresult is:{}", result);

    assertThat(result, equalTo("www.baidu.com"));
  }

  @Test
  @SneakyThrows
  @DisplayName("自定义短链接")
  public void op2() {
    request.setOriginUrl("www.sina.com.cn");
    request.setCustomTinyUrl("sad9ss22");
    TinyUrlData data = tinyUrlService.genTinyUrl(request);
    log.info("\ndata is:{}", data);

    String tinyUrl = data.getTinyUrl();

    String result = tinyUrlService.getOriginUrl(tinyUrl.substring(tinyUrl.lastIndexOf(SLASH) + 1));
    log.info("\nresult is:{}", result);

    assertThat(result, equalTo("www.sina.com.cn"));

    Thread.sleep(5001);

    result = tinyUrlService.getOriginUrl(tinyUrl.substring(tinyUrl.lastIndexOf(SLASH) + 1));
    log.info("\nresult is:{}", result);

    assertThat(result, equalTo("www.sina.com.cn"));
  }

  @Test
  @DisplayName("长链接已存在")
  public void op3() {
    request.setOriginUrl("www.baidu.com");
    TinyUrlData data = tinyUrlService.genTinyUrl(request);
    log.info("\ndata is:{}", data);

    String tinyUrl = data.getTinyUrl();

    String result = tinyUrlService.getOriginUrl(tinyUrl.substring(tinyUrl.lastIndexOf(SLASH) + 1));
    log.info("\nresult is:{}", result);

    assertThat(result, equalTo("www.baidu.com"));
  }

  @Test
  @DisplayName("短链接不存在,获取不到长链接")
  public void op4() {
    String result = tinyUrlService.getOriginUrl("BLLLLL");
    log.info("\nresult is:{}", result);

    assertThat(result, containsString("error/404"));
  }

  @Test
  @DisplayName("数据库中没有此短链接")
  public void op5() {
    boolean result = tinyUrlService.isExistedCustomTinyUrlInDb("BLLLLL");
    log.info("\nresult is:{}", result);

    assertThat(result, equalTo(false));
  }

  @Test
  @DisplayName("数据库中有此短链接")
  public void op6() {
    boolean result = tinyUrlService.isExistedCustomTinyUrlInDb("sad9ss22");
    log.info("\nresult is:{}", result);

    assertThat(result, equalTo(true));
  }
}
