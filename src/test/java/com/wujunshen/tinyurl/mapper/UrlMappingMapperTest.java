package com.wujunshen.tinyurl.mapper;

import com.wujunshen.snowflake.service.IdService;
import com.wujunshen.tinyurl.ApplicationTestBase;
import com.wujunshen.tinyurl.common.utils.EncodeUtils;
import com.wujunshen.tinyurl.common.utils.Md5Utils;
import com.wujunshen.tinyurl.entity.UrlMapping;
import com.wujunshen.tinyurl.web.response.UrlType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/4/27 5:17 下午 <br>
 */
@Slf4j
class UrlMappingMapperTest extends ApplicationTestBase {
  @Resource private IdService idService;
  @Resource private UrlMappingMapper urlMappingMapper;

  @Test
  @DisplayName("添加数据")
  public void insert() {
    long urlId = idService.genId();
    String originUrl = "www.bing.com";
    String tinyUrl = EncodeUtils.convert10to62(urlId, 6);

    UrlMapping insertData = new UrlMapping();
    insertData.setUrlId(urlId);
    insertData.setOriginUrl(originUrl);
    insertData.setOriginUrlMd5(Md5Utils.encode(originUrl));
    insertData.setTinyUrl(tinyUrl);
    insertData.setUrlType(UrlType.SYSTEM.getCode());
    int result = urlMappingMapper.insert(insertData);

    log.info("result is:{}", result);

    assertThat(result, equalTo(1));
  }

  @Sql("classpath:sql/test-query.sql")
  @Test
  @DisplayName("根据短链接查询")
  public void findByTinyUrl() {
    String tinyUrl = "9AJ73oyWaF";

    UrlMapping result = urlMappingMapper.findByTinyUrl(tinyUrl);

    log.info("result is:{}", result);

    assertThat(result, notNullValue());
  }

  @Sql("classpath:sql/test-query.sql")
  @Test
  @DisplayName("根据md5查询")
  public void findByOriginalUrl() {
    String originUrlMd5 = Md5Utils.encode("www.bing.com");

    UrlMapping result = urlMappingMapper.findByOriginalUrlMd5(originUrlMd5);

    log.info("result is:{}", result);

    assertThat(result, notNullValue());
  }
}
