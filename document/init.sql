-- auto-generated definition
create table tb_url_mapping
(
    url_id         bigint auto_increment comment '主键'
        primary key,
    origin_url     varchar(300)                        not null comment '原始长链接',
    origin_url_md5 varchar(32)                         not null comment '长链接md5值',
    tiny_url       varchar(10)                         not null comment '短链接',
    url_type       int(1)    default 0                 not null comment '是系统自动生成还是自定义的短码类型,系统: “system”,自定义: “custom”
0为system,1为custom 缺省为0',
    create_time    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '生成时间',
    update_time    timestamp default CURRENT_TIMESTAMP not null comment '最后更新时间',
    constraint tb_url_mapping_origin_url_md5_uindex
        unique (origin_url_md5),
    constraint tb_url_mapping_tiny_url_uindex
        unique (tiny_url)
);

