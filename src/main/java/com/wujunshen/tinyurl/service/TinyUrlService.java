package com.wujunshen.tinyurl.service;

import com.wujunshen.redis.wrapper.MyRedisTemplate;
import com.wujunshen.snowflake.service.IdService;
import com.wujunshen.tinyurl.common.utils.EncodeUtils;
import com.wujunshen.tinyurl.common.utils.Md5Utils;
import com.wujunshen.tinyurl.entity.UrlMapping;
import com.wujunshen.tinyurl.mapper.UrlMappingMapper;
import com.wujunshen.tinyurl.properties.TuProperties;
import com.wujunshen.tinyurl.web.request.GenTinyUrlRequest;
import com.wujunshen.tinyurl.web.response.TinyUrlData;
import com.wujunshen.tinyurl.web.response.UrlType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.wujunshen.tinyurl.common.utils.Constants.EXPIRED_TIME;

/**
 * 短网址
 *
 * @author frankwoo
 */
@Service
public class TinyUrlService {
  @Resource private IdService idService;

  @Resource private TuProperties tuProperties;

  @Resource private UrlMappingMapper urlMappingMapper;

  @Resource private MyRedisTemplate myRedisTemplate;

  /**
   * 获取短链接重定向地址
   *
   * @param tinyUrl 短链接
   * @return 重定向地址
   */
  public String getOriginUrl(String tinyUrl) {
    // 先从缓存中获取
    String originUrl = myRedisTemplate.getBy(tinyUrl);

    if (!StringUtils.isBlank(originUrl)) {
      return getLongUrlOr404(originUrl);
    }

    // 如果缓存中没有，则查询一下数据库
    UrlMapping urlMapping = urlMappingMapper.findByTinyUrl(tinyUrl);
    if (urlMapping != null) {
      originUrl = urlMapping.getOriginUrl();

      // 放入缓存,防止缓存穿透
      myRedisTemplate.setExpire(tinyUrl, originUrl, EXPIRED_TIME);
    }

    // 如果都没找到，则调转到404页面
    return getLongUrlOr404(originUrl);
  }

  /**
   * 获取404 页面或者 url页面
   *
   * @param url 长链接网址
   * @return 404 页面或者 url页面
   */
  private String getLongUrlOr404(String url) {
    return StringUtils.isBlank(url) ? "error/404" : url;
  }

  /**
   * 生成短链接
   *
   * @param request GenTinyUrlRequest
   * @return TinyUrlData
   */
  public TinyUrlData genTinyUrl(GenTinyUrlRequest request) {
    // 获取短链接域名
    String tinyUrlDomain = tuProperties.getTinyUrlDomain();
    String originUrl = request.getOriginUrl();
    String customTinyUrl = request.getCustomTinyUrl();

    // 查询数据库是否存在该长链接url
    UrlMapping urlMapping = urlMappingMapper.findByOriginalUrlMd5(Md5Utils.encode(originUrl));

    // 如果存在
    if (urlMapping != null) {
      String tinyUrl = urlMapping.getTinyUrl();

      // 放入缓存,防止缓存穿透
      myRedisTemplate.setExpire(tinyUrl, originUrl, EXPIRED_TIME);

      return TinyUrlData.builder()
          .originUrl(originUrl)
          .tinyUrl(tinyUrlDomain + tinyUrl)
          .urlType(UrlType.getByCode(urlMapping.getUrlType()))
          .build();
    }

    // 自定义短链接不存在,则执行系统自动生成短链接逻辑
    if (StringUtils.isBlank(customTinyUrl)) {
      return genSystem(originUrl);
    }

    // TinyUrlController中已判断短链接是否存在数据库里,这里肯定不存在,所以自定义生成短链接到数据库中去
    UrlMapping insertData = new UrlMapping();
    long urlId = Long.parseLong(EncodeUtils.convertBase62ToDecimal(customTinyUrl));
    insertData.setUrlId(urlId);
    insertData.setOriginUrl(originUrl);
    insertData.setOriginUrlMd5(Md5Utils.encode(originUrl));
    insertData.setTinyUrl(customTinyUrl);
    insertData.setUrlType(UrlType.CUSTOM.getCode());

    urlMappingMapper.insert(insertData);

    // 放入缓存,防止缓存穿透
    myRedisTemplate.setExpire(customTinyUrl, originUrl, EXPIRED_TIME);

    return TinyUrlData.builder()
        .originUrl(originUrl)
        .tinyUrl(tinyUrlDomain + customTinyUrl)
        .urlType(UrlType.CUSTOM)
        .build();
  }

  /**
   * @param customTinyUrl 自定义短链接
   * @return 数据库表中是否存在短链接
   */
  public boolean isExistedCustomTinyUrlInDb(String customTinyUrl) {
    return urlMappingMapper.findByTinyUrl(customTinyUrl) != null;
  }

  /**
   * 系统自动生成短链接
   *
   * @param originUrl 原始长链接
   * @return TinyUrlData
   */
  private TinyUrlData genSystem(String originUrl) {
    String tinyUrlDomain = tuProperties.getTinyUrlDomain();

    // 发号器
    long urlId = idService.genId();
    // 根据id计算对应的短链接
    String tinyUrl = EncodeUtils.convert10to62(urlId, 6);

    // 短链接数据库里不存在,则插入到数据库中
    if (!isExistedCustomTinyUrlInDb(tinyUrl)) {
      UrlMapping insertData = new UrlMapping();

      insertData.setUrlId(urlId);
      insertData.setOriginUrl(originUrl);
      insertData.setOriginUrlMd5(Md5Utils.encode(originUrl));
      insertData.setTinyUrl(tinyUrl);
      insertData.setUrlType(UrlType.SYSTEM.getCode());

      urlMappingMapper.insert(insertData);

      // 放入缓存,防止缓存穿透
      myRedisTemplate.setExpire(tinyUrl, originUrl, EXPIRED_TIME);

      return TinyUrlData.builder().originUrl(originUrl).tinyUrl(tinyUrlDomain + tinyUrl).build();
    } else {
      return genSystem(originUrl);
    }
  }
}
