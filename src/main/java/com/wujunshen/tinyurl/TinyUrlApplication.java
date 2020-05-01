package com.wujunshen.tinyurl;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** @author frankwoo */
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.wujunshen.tinyurl.mapper")
public class TinyUrlApplication {
  public static void main(String[] args) {
    SpringApplication.run(TinyUrlApplication.class, args);
  }
}
