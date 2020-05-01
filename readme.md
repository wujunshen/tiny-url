# tiny-url
like https://app.bitly.com url shorten function with springboot

#### 什么是短链接?

就是把普通网址，转换成比较短的网址。比如：https://bit.ly/2sad9ss22 这种，在短消息推送这种限制字数的场景下。好处不言而喻。短、字符少、美观、便于发布、传播

#### 原理

假设浏览器里输入  https://bit.ly/2sad9ss22

1.DNS首先解析获得 https://bit.ly/ 的 IP 地址

2.DNS 获得 IP 地址以后（比如：192.168.0.1），会向这个地址发送 HTTP GET 请求，查询短链接 2sad9ss22

3.https://bit.ly/ 服务器会通过短链接后缀 2sad9ss22 获取对应的长链接

4.请求通过 HTTP 302 转到对应的长链接 https://cn.bing.com/

#### 为什么是 302 ?

301是永久重定向，302是临时重定向。短链接一经生成就不会变化，301虽然符合 http 语义。同时也对服务器压力也会有一定减少

但我们就无法统计短链接被点击的次数。不能进行后续的大数据统计分析。

而302可统计被点击次数,,虽然302会增加服务器压力，但方便后续大数据统计分析

#### 本项目实现算法
目前短链接服务有两种实现算法

1.自增序列算法 

2.摘要算法

本项目使用的是第1种自增序列算法

#### 自增序列算法说明

设置 id 自增，一个 10进制 id 对应一个 62进制的数值，1对1，也就不会出现重复的情况。这个利用的就是低进制转化为高进制时，字符数会减少的特性。

短址的长度一般设为 6 位，而每一位是由 [a - z, A - Z, 0 - 9] 总共 62 个字母组成的，所以 6 位的话，总共会有 62^6 ~= 568亿种组合

#### 项目流程图

本项目除了实现上述算法之外,另外学习bitly增加短链接自定义功能,且使用了redis缓存来减轻生成短链接时,对数据库的读取压力

下列流程图来源百度短链接服务实现流程图,和本项目代码略微有点出入,具体以代码为准

![image](https://github.com/wujunshen/tiny-url/blob/master/image/image2020-4-22_19-15-22.png)

#### 短链接自定义

下面具体说明一下怎么实现自定义短链接的

数据库表增加一个类型为url_type 字段，用来标记短链接是用户自定义生成的，还是系统自动生成的。

如果有自定义过短链接，把它的类型标记自定义。每次根据 id 计算短链接时，若发现对应的短链接被占用，可从类型为自定义的记录里选取一条记录，用它的 id 去计算短链接。

这样可区分哪些长连接是用户自定义还是系统自动生成的，还可以不浪费被自定义短链接占用的 id

短链接位数表

|位数         | 个数           | 区间 |
| :-------------: |:-------------:| :-----:|
| 1位     | 62 | 0 - 61 |
| 2位      | 3844      |   62 - 3843 |
| 3位 | 约 23万     |   3844 - 238327 |
| 4位     | 约 1400万 | 238328 - 14776335 |
| 5位      | 约 9.1亿      |   14776336 - 916132831 |
| 6位 | 约 568亿     |   916132832 - 56800235583 |

建议自定义短链接位数从6位开始自定义,这样短链接占用的可能性相对低点

#### 自增id顺序混淆

本项目使用的自增id序列算法,容易被人反推算出id,因此对id需要进行一定的混淆

具体可见[com.wujunshen.tinyurl.common.utils.EncodeUtils](https://github.com/wujunshen/tiny-url/blob/master/src/main/java/com/wujunshen/tinyurl/common/utils/EncodeUtils.java)
类实现,相当简单,再此不展开说明

#### 数据库表说明

新增数据库tiny_url,新建tb_url_mapping表

DDL文件如下:

````
create table tb_url_mapping
(
    url_id         bigint auto_increment comment '主键'
        primary key,
    origin_url     varchar(300)                        not null comment '原始长链接',
    origin_url_md5 varchar(32)                         not null comment '长链接md5值',
    tiny_url       varchar(10)                         not null comment '短链接',
    url_type       int(1)    default 0                 not null comment '是系统自动生成还是自定义的短链接类型,系统: “system”,自定义: “custom”
0为system,1为custom 缺省为0',
    create_time    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '生成时间',
    update_time    timestamp default CURRENT_TIMESTAMP not null comment '最后更新时间',
    constraint tb_url_mapping_origin_url_md5_uindex
        unique (origin_url_md5),
    constraint tb_url_mapping_tiny_url_uindex
        unique (tiny_url)
);
````

这里特别说明为啥会有origin_url_md5字段,以及做索引的目的:

因为需要防止多次相同的长链接生成不同的短链接 id 这种情况，所以需要每次先根据长链接在数据库中找db是否存在相关记录

一般做法肯定是长链接加索引，但索引空间会很大，因此对长链接md5字段做索引，索引就会小很多。这样根据长链接的 md5
查询相关记录即可。

#### Redis缓存使用

本项目redis缓存只是一个简单的key-value形式,key为短链接,value为长链接

主要是为了在点击短链接时,不需要从数据库,而是直接从redis缓存中获取原来的长链接,并做302转向