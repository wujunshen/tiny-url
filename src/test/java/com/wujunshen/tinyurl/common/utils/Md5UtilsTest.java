package com.wujunshen.tinyurl.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/5/1 2:49 下午 <br>
 */
@Slf4j
class Md5UtilsTest {

  @Test
  @DisplayName("md5加密")
  void encode() {
    String result = Md5Utils.encode("www.bing.com");
    log.info("result is:{}", result);

    assertThat(result.length(), equalTo(32));

    result = Md5Utils.encode("www.baidu.com");
    log.info("result is:{}", result);

    assertThat(result.length(), equalTo(32));
  }
}
