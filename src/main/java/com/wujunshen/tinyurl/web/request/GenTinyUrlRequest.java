package com.wujunshen.tinyurl.web.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

/**
 * 生成短链接请求对象
 *
 * @author frankwoo
 */
@Data
public class GenTinyUrlRequest {
  /** 长链接 */
  @NotBlank(message = "原始长链接不能为空")
  @Length(max = 300, message = "长链接最大长度不能超过300个字符")
  @Pattern(regexp = "^(http|https)://([^/:]+)(:\\d*)?(.*)$", message = "长链接必须以http或https打头")
  private String originUrl;

  /** 自定义短链接 */
  private String customTinyUrl;
}
