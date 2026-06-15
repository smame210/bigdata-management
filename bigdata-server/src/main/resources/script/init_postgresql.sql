DROP TABLE IF EXISTS public.task_info;
DROP TABLE IF EXISTS public.task_launch_history;
DROP TABLE IF EXISTS public.cluster_info;
DROP TABLE IF EXISTS public.task_schedule;
DROP TABLE IF EXISTS public.task_status_event;
DROP TABLE IF EXISTS public."user";


CREATE TABLE public.task_info
(
    id          SERIAL PRIMARY KEY,
    task_name   VARCHAR(100),
    engine_type VARCHAR(100),
    task_type   VARCHAR(100),
    task_status INTEGER,
    task_mode   varchar(20),
    cluster_id  INTEGER,
    metadata    text,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted  INTEGER   DEFAULT 0
);

COMMENT ON TABLE public.task_info IS '任务信息表';

-- 字段注释
COMMENT ON COLUMN public.task_info.id IS '主键';
COMMENT ON COLUMN public.task_info.task_name IS '任务名称';
COMMENT ON COLUMN public.task_info.engine_type IS '引擎类型 spark/flink';
COMMENT ON COLUMN public.task_info.task_status IS '任务状态：0-未启动，1-运行中，2-已完成，3-失败，4-已终止';
COMMENT ON COLUMN public.task_info.task_mode IS '任务运行模式 streaming/batch';
COMMENT ON COLUMN public.task_info.cluster_id IS '关联的集群ID，指定任务运行的集群环境';
COMMENT ON COLUMN public.task_info.metadata IS '元数据，包括任务配置等';
COMMENT ON COLUMN public.task_info.create_time IS '记录创建时间';
COMMENT ON COLUMN public.task_info.update_time IS '记录更新时间';
COMMENT ON COLUMN public.task_info.is_deleted IS '逻辑删除标记：0-未删除，1-已删除';


CREATE TABLE public.cluster_info
(
    id             SERIAL PRIMARY KEY,
    cluster_name   VARCHAR(100) NOT NULL,
    cluster_type   VARCHAR(100) NOT NULL,
    cluster_status INTEGER      NOT NULL,
    health_status  INTEGER   DEFAULT 0,
    health_checked_at TIMESTAMP,
    metadata       text,
    create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted     INTEGER   DEFAULT 0
);

-- cluster_info表注释
COMMENT ON TABLE public.cluster_info IS '集群信息表';

-- cluster_info字段注释
COMMENT ON COLUMN public.cluster_info.id IS '主键';
COMMENT ON COLUMN public.cluster_info.cluster_name IS '集群名称';
COMMENT ON COLUMN public.cluster_info.cluster_type IS '集群类型，如YARN/K8S等';
COMMENT ON COLUMN public.cluster_info.cluster_status IS '集群状态：0-停用，1-启动';
COMMENT ON COLUMN public.cluster_info.health_status IS '健康状态：0-未知，1-健康，2-降级，3-异常';
COMMENT ON COLUMN public.cluster_info.health_checked_at IS '最近健康检查时间';
COMMENT ON COLUMN public.cluster_info.metadata IS '集群元数据，JSON格式存储集群配置信息';
COMMENT ON COLUMN public.cluster_info.create_time IS '记录创建时间';
COMMENT ON COLUMN public.cluster_info.update_time IS '记录更新时间';
COMMENT ON COLUMN public.cluster_info.is_deleted IS '逻辑删除标记：0-未删除，1-已删除';


CREATE TABLE public.task_schedule
(
    id                     SERIAL PRIMARY KEY,
    task_id                INTEGER      NOT NULL, -- 关联的任务ID
    schedule_name          VARCHAR(100) NOT NULL, -- 调度名称
    schedule_instance_name varchar(100),          -- 调度实例名称 quartz中的job名称
    schedule_status        INTEGER,               -- 调度状态：0-禁用 1-启用
    schedule_frequency     varchar(100),          -- 调度频率：一次性、每分钟、每小时、每天、每周、每月、自定义
    cron_expression        VARCHAR(100),          -- Cron表达式（当frequency为CUSTOM时使用）
    next_run_time          TIMESTAMP,             -- 下次运行时间
    last_run_time          TIMESTAMP,             -- 最后运行时间
    start_time             TIMESTAMP,             -- 调度生效开始时间
    end_time               TIMESTAMP,             -- 调度生效结束时间
    max_retry_times        INTEGER   DEFAULT 0,   -- 最大重试次数
    retry_interval         INTEGER   DEFAULT 0,   -- 重试间隔（秒）
    timeout_seconds        INTEGER,               -- 任务超时时间（秒）
    dependency_task_ids    VARCHAR(255),          -- 依赖的任务IDs（逗号分隔的任务ID列表）
    params                 TEXT,                  -- 任务参数（JSON格式）
    create_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted             INTEGER   DEFAULT 0
);

-- 表注释
COMMENT ON TABLE public.task_schedule IS '任务调度表';

-- 字段注释
COMMENT ON COLUMN public.task_schedule.id IS '主键';
COMMENT ON COLUMN public.task_schedule.task_id IS '关联的任务ID';
COMMENT ON COLUMN public.task_schedule.schedule_name IS '调度名称';
COMMENT ON COLUMN public.task_schedule.schedule_instance_name IS '调度实例名称';
COMMENT ON COLUMN public.task_schedule.schedule_status IS '调度状态：true-启用，false-禁用';
COMMENT ON COLUMN public.task_schedule.schedule_frequency IS '调度频率：ONCE-一次性, MINUTELY-每分钟, HOURLY-每小时, DAILY-每天, WEEKLY-每周, MONTHLY-每月, CUSTOM-自定义cron表达式';
COMMENT ON COLUMN public.task_schedule.cron_expression IS 'Cron表达式，当调度频率为CUSTOM时使用';
COMMENT ON COLUMN public.task_schedule.next_run_time IS '下次计划运行时间';
COMMENT ON COLUMN public.task_schedule.last_run_time IS '最后一次运行时间';
COMMENT ON COLUMN public.task_schedule.start_time IS '调度生效的开始时间';
COMMENT ON COLUMN public.task_schedule.end_time IS '调度生效的结束时间';
COMMENT ON COLUMN public.task_schedule.max_retry_times IS '任务失败后最大重试次数';
COMMENT ON COLUMN public.task_schedule.retry_interval IS '重试间隔时间（秒）';
COMMENT ON COLUMN public.task_schedule.timeout_seconds IS '任务执行超时时间（秒）';
COMMENT ON COLUMN public.task_schedule.dependency_task_ids IS '依赖的任务IDs，逗号分隔的任务ID列表';
COMMENT ON COLUMN public.task_schedule.create_time IS '记录创建时间';
COMMENT ON COLUMN public.task_schedule.update_time IS '记录更新时间';
COMMENT ON COLUMN public.task_schedule.is_deleted IS '逻辑删除标记：0-未删除，1-已删除';


CREATE TABLE public.task_instance
(
    id                     SERIAL PRIMARY KEY,
    task_id                INTEGER,
    task_instance_id       varchar(255),
    task_name              VARCHAR(100),
    task_type              VARCHAR(100),
    task_mode              varchar(20),
    task_status            INTEGER,
    task_metadata          text,
    cluster_id             INTEGER,
    cluster_type           VARCHAR(100),
    cluster_metadata       text,
    schedule_id            INTEGER,
    schedule_name          VARCHAR(100),
    schedule_instance_name varchar(100),
    launch_time            TIMESTAMP,
    create_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    retry_count            INTEGER   DEFAULT 0,
    root_instance_id       INTEGER,
    parent_instance_id     INTEGER,
    retry_scheduled        BOOLEAN   DEFAULT FALSE,
    is_deleted             INTEGER   DEFAULT 0
);

-- task_instance表注释
COMMENT ON TABLE public.task_instance IS '任务实例记录表';

-- task_instance字段注释
COMMENT ON COLUMN public.task_instance.id IS '主键';
COMMENT ON COLUMN public.task_instance.task_id IS '任务ID，关联task_info表';
COMMENT ON COLUMN public.task_instance.task_instance_id IS '任务实例id';
COMMENT ON COLUMN public.task_instance.task_name IS '任务名称';
COMMENT ON COLUMN public.task_instance.task_type IS '任务类型，如SESSION/APPLICATION/PRE_JOB';
COMMENT ON COLUMN public.task_instance.task_mode IS '任务运行模式，如STREAMING/BATCH';
COMMENT ON COLUMN public.task_instance.task_status IS '任务状态：0-未启动，1-运行中，2-已完成，3-失败，4-已终止';
COMMENT ON COLUMN public.task_instance.task_metadata IS '元数据，包括任务配置等';
COMMENT ON COLUMN public.task_instance.cluster_id IS '关联的集群ID';
COMMENT ON COLUMN public.task_instance.cluster_type IS '集群类型，如YARN/K8S等';
COMMENT ON COLUMN public.task_instance.cluster_metadata IS '集群元数据，JSON格式存储集群配置信息';
COMMENT ON COLUMN public.task_instance.schedule_id IS '调度id';
COMMENT ON COLUMN public.task_schedule.schedule_name IS '调度名称';
COMMENT ON COLUMN public.task_instance.schedule_instance_name IS '调度实例名称';
COMMENT ON COLUMN public.task_instance.retry_count IS '重试次数（第几次执行，首次为0）';
COMMENT ON COLUMN public.task_instance.root_instance_id IS '重试链路的根实例id';
COMMENT ON COLUMN public.task_instance.parent_instance_id IS '上一次失败的父实例id';
COMMENT ON COLUMN public.task_instance.retry_scheduled IS '是否已为本次失败创建过重试任务';
COMMENT ON COLUMN public.task_instance.launch_time IS '任务启动时间';
COMMENT ON COLUMN public.task_instance.create_time IS '记录创建时间';
COMMENT ON COLUMN public.task_instance.update_time IS '记录更新时间';
COMMENT ON COLUMN public.task_instance.is_deleted IS '逻辑删除标记：0-未删除，1-已删除';


CREATE TABLE public.task_status_event
(
    id           SERIAL PRIMARY KEY,
    instance_id  INTEGER      NOT NULL,
    task_id      INTEGER      NOT NULL,
    event_type   VARCHAR(32)  NOT NULL,
    old_status   INTEGER,
    new_status   INTEGER,
    source       VARCHAR(32)  NOT NULL,
    payload      text,
    event_status VARCHAR(16)  NOT NULL DEFAULT 'NEW',
    error_msg    text,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP
);

COMMENT ON TABLE  public.task_status_event IS '任务状态变化事件表';
COMMENT ON COLUMN public.task_status_event.instance_id IS '任务实例 id';
COMMENT ON COLUMN public.task_status_event.task_id IS '任务 id';
COMMENT ON COLUMN public.task_status_event.event_type IS '事件类型：status-changed / timeout';
COMMENT ON COLUMN public.task_status_event.old_status IS '变化前状态';
COMMENT ON COLUMN public.task_status_event.new_status IS '变化后状态';
COMMENT ON COLUMN public.task_status_event.source IS '事件来源：submit / status_sync / timeout_check / manual';
COMMENT ON COLUMN public.task_status_event.payload IS '扩展字段 JSON';
COMMENT ON COLUMN public.task_status_event.event_status IS '事件状态：NEW / PROCESSING / DONE / FAILED';
COMMENT ON COLUMN public.task_status_event.error_msg IS '处理失败时的错误信息';
COMMENT ON COLUMN public.task_status_event.created_at IS '记录创建时间';
COMMENT ON COLUMN public.task_status_event.updated_at IS '记录更新时间';


CREATE TABLE public.alert_info
(
    id          SERIAL PRIMARY KEY,
    name        varchar(255),
    type        varchar(255),
    status      INTEGER,
    metadata    text,
    template    text,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted  INTEGER   DEFAULT 0
);

COMMENT ON TABLE public.alert_info IS '告警配置表';

COMMENT ON COLUMN public.alert_info.id IS '主键ID';
COMMENT ON COLUMN public.alert_info.name IS '告警名称';
COMMENT ON COLUMN public.alert_info.type IS '告警类型 sms、dingding等';
COMMENT ON COLUMN public.alert_info.status IS '告警状态 0-停用 1-启用';
COMMENT ON COLUMN public.alert_info.metadata IS '告警配置的元数据';
COMMENT ON COLUMN public.alert_info.template IS '告警模版';
COMMENT ON COLUMN public.alert_info.create_time IS '记录创建时间';
COMMENT ON COLUMN public.alert_info.update_time IS '记录更新时间';
COMMENT ON COLUMN public.alert_info.is_deleted IS '逻辑删除标记：0-未删除，1-已删除';


CREATE TABLE public.alert_policy
(
    id          SERIAL PRIMARY KEY,
    name        varchar(255),
    alert_id    INTEGER,
    task_id     INTEGER,
    conditions  text,
    status      INTEGER,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted  INTEGER   DEFAULT 0
);

COMMENT ON TABLE public.alert_policy IS '告警策略表';

COMMENT ON COLUMN public.alert_policy.id IS '主键ID';
COMMENT ON COLUMN public.alert_policy.name IS '告警策略名称';
COMMENT ON COLUMN public.alert_policy.alert_id IS '告警表id';
COMMENT ON COLUMN public.alert_policy.task_id IS '任务表id';
COMMENT ON COLUMN public.alert_policy.conditions IS '告警条件';
COMMENT ON COLUMN public.alert_policy.status IS '状态 0-停用 1-启用';
COMMENT ON COLUMN public.alert_policy.create_time IS '记录创建时间';
COMMENT ON COLUMN public.alert_policy.update_time IS '记录更新时间';
COMMENT ON COLUMN public.alert_policy.is_deleted IS '逻辑删除标记：0-未删除，1-已删除';


---- quartz
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS
(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    JOB_NAME          VARCHAR(200) NOT NULL,
    JOB_GROUP         VARCHAR(200) NOT NULL,
    DESCRIPTION       VARCHAR(250) NULL,
    JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
    IS_DURABLE        BOOL         NOT NULL,
    IS_NONCONCURRENT  BOOL         NOT NULL,
    IS_UPDATE_DATA    BOOL         NOT NULL,
    REQUESTS_RECOVERY BOOL         NOT NULL,
    JOB_DATA          BYTEA        NULL,
    PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
(
    SCHED_NAME     VARCHAR(120) NOT NULL,
    TRIGGER_NAME   VARCHAR(200) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    JOB_NAME       VARCHAR(200) NOT NULL,
    JOB_GROUP      VARCHAR(200) NOT NULL,
    DESCRIPTION    VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT       NULL,
    PREV_FIRE_TIME BIGINT       NULL,
    PRIORITY       INTEGER      NULL,
    TRIGGER_STATE  VARCHAR(16)  NOT NULL,
    TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
    START_TIME     BIGINT       NOT NULL,
    END_TIME       BIGINT       NULL,
    CALENDAR_NAME  VARCHAR(200) NULL,
    MISFIRE_INSTR  SMALLINT     NULL,
    JOB_DATA       BYTEA        NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME      VARCHAR(120) NOT NULL,
    TRIGGER_NAME    VARCHAR(200) NOT NULL,
    TRIGGER_GROUP   VARCHAR(200) NOT NULL,
    REPEAT_COUNT    BIGINT       NOT NULL,
    REPEAT_INTERVAL BIGINT       NOT NULL,
    TIMES_TRIGGERED BIGINT       NOT NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
    SCHED_NAME      VARCHAR(120) NOT NULL,
    TRIGGER_NAME    VARCHAR(200) NOT NULL,
    TRIGGER_GROUP   VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID    VARCHAR(80),
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME    VARCHAR(120)   NOT NULL,
    TRIGGER_NAME  VARCHAR(200)   NOT NULL,
    TRIGGER_GROUP VARCHAR(200)   NOT NULL,
    STR_PROP_1    VARCHAR(512)   NULL,
    STR_PROP_2    VARCHAR(512)   NULL,
    STR_PROP_3    VARCHAR(512)   NULL,
    INT_PROP_1    INT            NULL,
    INT_PROP_2    INT            NULL,
    LONG_PROP_1   BIGINT         NULL,
    LONG_PROP_2   BIGINT         NULL,
    DEC_PROP_1    NUMERIC(13, 4) NULL,
    DEC_PROP_2    NUMERIC(13, 4) NULL,
    BOOL_PROP_1   BOOL           NULL,
    BOOL_PROP_2   BOOL           NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_NAME  VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA     BYTEA        NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
    SCHED_NAME    VARCHAR(120) NOT NULL,
    CALENDAR_NAME VARCHAR(200) NOT NULL,
    CALENDAR      BYTEA        NOT NULL,
    PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
);


CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    ENTRY_ID          VARCHAR(95)  NOT NULL,
    TRIGGER_NAME      VARCHAR(200) NOT NULL,
    TRIGGER_GROUP     VARCHAR(200) NOT NULL,
    INSTANCE_NAME     VARCHAR(200) NOT NULL,
    FIRED_TIME        BIGINT       NOT NULL,
    SCHED_TIME        BIGINT       NOT NULL,
    PRIORITY          INTEGER      NOT NULL,
    STATE             VARCHAR(16)  NOT NULL,
    JOB_NAME          VARCHAR(200) NULL,
    JOB_GROUP         VARCHAR(200) NULL,
    IS_NONCONCURRENT  BOOL         NULL,
    REQUESTS_RECOVERY BOOL         NULL,
    PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    INSTANCE_NAME     VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT       NOT NULL,
    CHECKIN_INTERVAL  BIGINT       NOT NULL,
    PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40)  NOT NULL,
    PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY
    ON QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP
    ON QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J
    ON QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG
    ON QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C
    ON QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G
    ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE
    ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE
    ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE
    ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME
    ON QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST
    ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE
    ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE
    ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
    ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME
    ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
    ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G
    ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG
    ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G
    ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG
    ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);


CREATE TABLE public."user"
(
    id          SERIAL PRIMARY KEY,
    user_name   VARCHAR(64)  NOT NULL UNIQUE,
    password    VARCHAR(256) NOT NULL,
    nick_name   VARCHAR(64),
    gender      SMALLINT     DEFAULT 0,
    phone       VARCHAR(20),
    email       VARCHAR(128),
    avatar      VARCHAR(256),
    status      SMALLINT     NOT NULL DEFAULT 1,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP
);

COMMENT ON TABLE  public."user" IS '系统用户表';
COMMENT ON COLUMN public."user".id          IS '主键ID';
COMMENT ON COLUMN public."user".user_name   IS '用户名（登录账号）';
COMMENT ON COLUMN public."user".password    IS '密码（bcrypt）';
COMMENT ON COLUMN public."user".nick_name   IS '昵称';
COMMENT ON COLUMN public."user".gender      IS '性别：0-未知 1-男 2-女';
COMMENT ON COLUMN public."user".phone       IS '手机号';
COMMENT ON COLUMN public."user".email       IS '邮箱';
COMMENT ON COLUMN public."user".avatar      IS '头像URL';
COMMENT ON COLUMN public."user".status      IS '状态：1-启用 2-禁用';
COMMENT ON COLUMN public."user".create_time IS '记录创建时间';
COMMENT ON COLUMN public."user".update_time IS '记录更新时间';

CREATE UNIQUE INDEX idx_user_user_name ON public."user"(user_name);

-- 默认管理员（密码：admin123，bcrypt 密文由 Java 代码生成后替换）
INSERT INTO public."user" (user_name, password, nick_name, status)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 1);
