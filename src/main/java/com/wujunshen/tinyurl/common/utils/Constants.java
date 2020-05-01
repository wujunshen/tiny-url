package com.wujunshen.tinyurl.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/3/15 1:44 上午 <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  public static final String HTTP_LINK = "http://";
  public static final String HTTPS_LINK = "https://";
  public static final String COMMA = ",";
  public static final String SLASH = "/";
  public static final Long EXPIRED_TIME = 5L;
}
