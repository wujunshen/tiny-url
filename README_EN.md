# tiny-url
like https://app.bitly.com url shorten function with springboot

## What are short links?

Short links are shortened versions of long URLs. For example:https://bit.ly/2sad9ss22. They are useful in situations with character limits, like short messaging. The benefits are obvious - short, concise, aesthetically pleasing, easy to publish and propagate.

## How it works

For example, when entering https://bit.ly/2sad9ss22 in a browser:

1.DNS first resolves the IP address of https://bit.ly/

2.After obtaining the IP address (e.g. 192.168.0.1), the browser sends a HTTP GET request to this address to query the short link 2sad9ss22

3.The https://bit.ly/ server looks up the corresponding long URL based on the short link suffix 2sad9ss22

4.The request is redirected via a HTTP 302 to the long URL https://cn.bing.com/

### Why use 302?

301 is permanent redirect, 302 is temporary redirect. Although 301 semantically fits better for short links which are fixed once generated, it also puts more pressure on the server.

302 allows counting click times for analytics. Although 302 increases server load, it enables better analytics.

## Implementation
Currently there are 2 algorithms to generate short links:

1.Auto-incrementing ID

2.Hash digest

This project uses the first approach.

### Auto-incrementing ID algorithm

It auto-increments an ID, maps each base-10 ID to a base-62 number 1:1 so there are no collisions. This utilizes the characteristic that converting a lower base to a higher base will result in fewer digits.

Short links are usually 6 digits. Each digit is one of 62 characters [a-z A-Z 0-9]. So with 6 digits, there are 62^6 = 568 billion possible combinations.

#### workflow

In addition to the algorithm, this project also implements custom short links like Bitly, and uses Redis caching to reduce database load when generating short links.

The workflow diagram below is based on Baidu's short link service, with some differences from the actual project code.

![image](https://github.com/wujunshen/tiny-url/blob/master/image/image2020-4-22_19-15-22.png)

#### Custom short links

A url_type field is added to the database table to mark whether a short link is user-customized or system-generated.

If a short link has been customized before, its type is marked as customized. When generating short links by ID, if the calculated short link is already taken, the system can pick a record with type customized, and use its ID to calculate the short link.

This differentiates whether a long URL is user-customized or system-generated. It also avoids wasting IDs occupied by customized short links.

Short link digits table:

|Digits         | Count           | Range |
| :-------------: |:-------------:| :-----:|
| 1     | 62 | 0 - 61 |
| 2      | 3844      |   62 - 3843 |
| 3 | ~230,000     |   3844 - 238327 |
| 4     | ~14 million | 238328 - 14776335 |
| 5      | ~910 million      |   14776336 - 916132831 |
| 6 | ~56.8 billion     |   916132832 - 56800235583 |

It is recommended to start custom short links at 6 digits to minimize collisions.

#### Obfuscating the incrementing ID

The auto-incrementing ID is easily reversible, so the project does some obfuscation. See [com.wujunshen.tinyurl.common.utils.EncodeUtils](https://github.com/wujunshen/tiny-url/blob/master/src/main/java/com/wujunshen/tinyurl/common/utils/EncodeUtils.java) for details.

#### Database schema

A new tiny_url database contains table tb_url_mapping:

DDL file:

````
create table tb_url_mapping
(
    url_id         bigint auto_increment comment 'primary key'
        primary key,
    origin_url     varchar(300)                        not null comment 'original long url',
    origin_url_md5 varchar(32)                         not null comment 'md5 of long url',
    tiny_url       varchar(10)                         not null comment 'short link',
    url_type       int(1)    default 0                 not null comment 'custom or system generated short link type, system: 0, custom: 1, default: 0',
    create_time    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'create time',
    update_time    timestamp default CURRENT_TIMESTAMP not null comment 'last update time',
    constraint tb_url_mapping_origin_url_md5_uindex
        unique (origin_url_md5),
    constraint tb_url_mapping_tiny_url_uindex
        unique (tiny_url)
);
````

Purpose of the origin_url_md5 field and index:

To prevent the same long URL from generating different short link IDs, the system needs to first check if a record already exists for the long URL in the database.

The common approach would be to put an index on the long URL itself. However, that index would be very large in size.

Instead, an index is created on the md5 hash of the long URL. This index is much smaller in size.

Looking up records by the md5 of the long URL allows finding existing records for a long URL efficiently.

#### Redis caching

Redis caching uses a simple key-value store, with short link as key and long link as value.

This allows redirecting from the short link by looking up the long link directly in Redis cache instead of the database.

## Additional

This project uses Spring Boot 2.2.6, Hikari connection pool, and Lombok.

The related starters used are custom developed, the specific code and documentation can be found at:[https://gitee.com/darkranger/my-springboot-starter](https://gitee.com/darkranger/my-springboot-starter)

These include the following:

* ID auto increment uses the Snowflake algorithm
* Caching uses Redis Cluster
* The API for generating short links is documented with Swagger


````
<dependency>
	<groupId>com.wujunshen</groupId>
	<artifactId>swagger-spring-boot-starter</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
	<groupId>com.wujunshen</groupId>
	<artifactId>snowflake-spring-boot-starter</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
	<groupId>com.wujunshen</groupId>
	<artifactId>redis-spring-boot-starter</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>	    
````

It also uses JUnit 5 for testing.