package com.wujunshen.tinyurl.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 自定义配置
 *
 * @author frankwoo
 */
@Data
@Component
@ConfigurationProperties(prefix = "tu")
public class TuProperties {
  /** 服务域名 */
  private String domain;
  /** 短链域名 */
  private String tinyUrlDomain;
}
