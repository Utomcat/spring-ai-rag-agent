DROP DATABASE IF EXISTS knowledge_database_rag;
CREATE DATABASE knowledge_database_rag DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE knowledge_database_rag;

-- 用户表（管理员 ADMIN / 普通用户 USER）
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user
(
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username        VARCHAR(64)  NOT NULL COMMENT '登录名',
    password        VARCHAR(255) NOT NULL COMMENT '加密密码（BCrypt/MD5）',
    real_name       VARCHAR(64)           DEFAULT NULL COMMENT '真实姓名',
    avatar          VARCHAR(512)          DEFAULT NULL COMMENT '头像相对路径（对应 /files/ 下资源）',
    role            VARCHAR(16)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN / USER',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_by       BIGINT                DEFAULT NULL COMMENT '创建人ID',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT                DEFAULT NULL COMMENT '更新人ID',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';
INSERT INTO t_user (username, password, real_name, role, status, create_by, create_time, update_by, update_time)
VALUES ('admin', '$2a$10$mz4rExfImEzO4yoxMaTAreywcjF8xxpl0bZVILGGgVKWZVVPwGaf2', '系统管理员', 'ADMIN', 1, 1, current_timestamp, 1, current_timestamp);

-- 知识库分类表
DROP TABLE IF EXISTS t_kb_category;
CREATE TABLE t_kb_category
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(128) NOT NULL COMMENT '分类名称',
    description VARCHAR(512)          DEFAULT NULL COMMENT '描述',
    icon        VARCHAR(64)           DEFAULT NULL COMMENT '图标标识',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_by   BIGINT                DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT                DEFAULT NULL COMMENT '更新人ID',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='知识库分类';
INSERT INTO t_kb_category (id, name, description, icon, sort_order, create_by, create_time, update_by, update_time)
VALUES (1, '文本文档文件', 'txt、text文本文件', 'Folder', 1, 1, current_timestamp, 1, current_timestamp)
     , (2, '文档文件', 'doc、docx类型文件', 'Folder', 2, 1, current_timestamp, 1, current_timestamp)
     , (3, 'markdown文件', 'md类型文件', 'Folder', 3, 1, current_timestamp, 1, current_timestamp)
     , (4, 'PDF文档文件', 'pdf 文档文件', 'Folder', 4, 1, current_timestamp, 1, current_timestamp);


-- 知识文档元数据表
DROP TABLE IF EXISTS t_kb_document;
CREATE TABLE t_kb_document
(
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id    BIGINT       NOT NULL COMMENT '分类ID',
    title          VARCHAR(255) NOT NULL COMMENT '显示标题',
    file_name      VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path      VARCHAR(512) NOT NULL COMMENT '磁盘相对路径（相对 uploads 根）',
    absolute_path  varchar(512) NOT NULL COMMENT '上传的知识库数据文档存放的磁盘绝对路径',
    file_type      VARCHAR(16)  NOT NULL COMMENT '扩展名小写',
    file_size      BIGINT       NOT NULL DEFAULT 0 COMMENT '字节大小',
    status         VARCHAR(16)  NOT NULL DEFAULT 'PROCESSING' COMMENT 'PROCESSING/SUCCESS/FAIL',
    vector_count   INT          NOT NULL DEFAULT 0 COMMENT '向量块数量',
    upload_user_id BIGINT                DEFAULT NULL COMMENT '上传用户ID',
    create_by      BIGINT                DEFAULT NULL COMMENT '创建人ID',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      BIGINT                DEFAULT NULL COMMENT '更新人ID',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category_id),
    KEY idx_status (status),
    KEY idx_upload_user (upload_user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='知识文档';

-- 问答会话
DROP TABLE IF EXISTS t_chat_session;
CREATE TABLE t_chat_session
(
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    title       VARCHAR(255)      DEFAULT NULL COMMENT '会话标题',
    create_by   BIGINT            DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT            DEFAULT NULL COMMENT '更新人ID',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='聊天会话';

-- 问答消息
DROP TABLE IF EXISTS t_chat_message;
CREATE TABLE t_chat_message
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    session_id  BIGINT      NOT NULL COMMENT '会话ID',
    role        VARCHAR(16) NOT NULL COMMENT 'USER / ASSISTANT',
    content     MEDIUMTEXT  NOT NULL COMMENT '消息内容',
    refs        JSON                 DEFAULT NULL COMMENT '引用文档JSON',
    create_by   BIGINT               DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT               DEFAULT NULL COMMENT '更新人ID',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_session (session_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='聊天消息';

-- 系统日志（统计用）
DROP TABLE IF EXISTS t_system_log;
CREATE TABLE t_system_log
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT                DEFAULT NULL COMMENT '用户ID',
    action      VARCHAR(128) NOT NULL COMMENT '动作描述',
    ip          VARCHAR(64)           DEFAULT NULL COMMENT 'IP',
    create_by   BIGINT                DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   BIGINT                DEFAULT NULL COMMENT '更新人ID',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_time (create_time),
    KEY idx_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统日志';
