package com.wujunshen.tinyurl.provider;

import lombok.extern.slf4j.Slf4j;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/4/27 3:52 下午 <br>
 */
@Slf4j
public class UrlMappingProvider {
  public String findByTinyUrl(String tinyUrl) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(
        "select url_id as urlId,origin_url as originUrl,"
            + "tiny_url as tinyUrl,url_type as urlType,"
            + "create_time as createTime,update_time as updateTime ");
    stringBuilder.append("from tb_url_mapping ");
    stringBuilder.append("where tiny_url like '%").append(tinyUrl).append("%'");

    logSql(stringBuilder);
    return stringBuilder.toString();
  }

  public String findByOriginalUrlMd5(String originUrlMd5) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("select tiny_url as tinyUrl ");
    stringBuilder.append("from tb_url_mapping ");
    stringBuilder.append("where origin_url_md5 ='").append(originUrlMd5).append("'");

    logSql(stringBuilder);
    return stringBuilder.toString();
  }

  private void logSql(String str) {
    log.info("\nsql is:{}\n", str);
  }

  private void logSql(StringBuilder stringBuilder) {
    log.info("\nsql is:{}\n", stringBuilder.toString());
  }
}
