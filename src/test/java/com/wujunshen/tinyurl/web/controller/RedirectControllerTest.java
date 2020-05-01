package com.wujunshen.tinyurl.web.controller;

import com.wujunshen.tinyurl.ApplicationTestBase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;

import lombok.SneakyThrows;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/5/1 5:04 下午 <br>
 */
@AutoConfigureMockMvc
public class RedirectControllerTest extends ApplicationTestBase {
  @Resource private MockMvc mvc;

  @Sql("classpath:sql/test-query.sql")
  @Test
  @DisplayName("重定向短链接")
  @SneakyThrows
  void getOriginUrl() {
    mvc.perform(MockMvcRequestBuilders.get("/t/{tinyUrl}", "9AJ73oyWaF"))
        .andExpect(status().isFound())
        .andExpect(view().name(containsString("redirect:")));
  }

  @Test
  @DisplayName("重定向短链接not found")
  @SneakyThrows
  void getOriginUrlError404() {
    mvc.perform(MockMvcRequestBuilders.get("/t/{tinyUrl}", "sdajdasjkldas"))
        .andExpect(status().isFound())
        .andExpect(view().name(containsString("error/404")));
  }
}
