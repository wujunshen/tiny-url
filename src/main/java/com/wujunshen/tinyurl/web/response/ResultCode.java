package com.wujunshen.tinyurl.web.response;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 返回状态代码标识
 *
 * @author frankwoo
 */
@Getter
public enum ResultCode {
  // 操作成功
  SUCCESS(true, 200, "操作成功！"),

  // 不存在
  NOT_FOUND(false, 4010, "接口不存在"),
  FORBIDDEN_INPUT_TINY_URL(false, 4020, "不能输入短链接"),
  EXISTED_TINY_URL(false, 4020, "自定义短链接已存在,不需要重新定义"),
  // 异常
  PARAM_ERROR(false, 5010, "入参异常"),
  PARAM_FORMAT_ERROR(false, 5090, "入参格式异常"),
  DECRYPT_ERROR(false, 5610, "入参解密异常"),
  DUPLICATE_KEY(false, 5710, "数据库中已存在该记录"),
  UNKNOWN_EXCEPTION(false, 5999, "未知异常"),

  PARAMETER_VALIDATION(false, 4030, "入参验证失败-{0}"),
  ;

  /** 操作是否成功 */
  private final boolean success;
  /** 操作代码 */
  private final int code;
  /** 提示信息 */
  private final String message;
  /** 异常时间 */
  private final String date;

  ResultCode(boolean success, int code, String message) {
    this.success = success;
    this.code = code;
    this.message = message;
    this.date = LocalDateTime.now().toString();
  }
}
