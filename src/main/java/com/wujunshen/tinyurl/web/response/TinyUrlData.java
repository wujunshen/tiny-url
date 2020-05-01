package com.wujunshen.tinyurl.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链实体
 *
 * @author frankwoo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinyUrlData {
  /** 原始链接 */
  private String originUrl;
  /** 短链接 */
  private String tinyUrl;
  /** 是系统自动生成还是自定义的短链接类型 */
  @Default private UrlType urlType = UrlType.SYSTEM;
}
