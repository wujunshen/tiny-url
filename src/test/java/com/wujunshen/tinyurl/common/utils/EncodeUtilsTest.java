package com.wujunshen.tinyurl.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/4/28 1:29 上午 <br>
 */
@Slf4j
class EncodeUtilsTest {
  @Test
  @DisplayName("10进制和62进制互换")
  void convert10to62() {
    // 结果是混淆过的,不是10进制35174605对应的62进制2NaWL
    String result = EncodeUtils.convert10to62(35174605, 6);
    log.info("result is:{}", result);

    result = EncodeUtils.convertBase62ToDecimal("2NaWL");
    log.info("result is:{}", result);

    assertThat(result, equalTo("35174605"));
  }
}
