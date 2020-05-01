package com.wujunshen.tinyurl.web.controller;

import com.wujunshen.tinyurl.service.TinyUrlService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/4/27 10:39 下午 <br>
 */
@Slf4j
@Controller
@RequestMapping("/t")
public class RedirectController extends BaseController {
  @Resource private TinyUrlService tinyUrlService;

  /**
   * 获取短网址重定向地址
   *
   * @param tinyUrl 短链接
   * @return 原来长链接
   */
  @ApiIgnore
  @GetMapping("/{tinyUrl}")
  public String getOriginUrl(@PathVariable("tinyUrl") String tinyUrl) {
    return "redirect:" + getOriginalUrl(tinyUrlService.getOriginUrl(tinyUrl));
  }
}
