# BigData Management Platform — 架构文档

## 项目概述

大数据任务管理与调度平台，提供集群配置、任务定义、调度编排、实例追踪、告警通知与用户管理等能力。

---

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Java 21 / Spring Boot 3.5 | REST API 服务 |
| ORM | MyBatis-Plus 3.5 | 数据持久化 |
| 数据库 | PostgreSQL | 主存储 + Quartz JobStore |
| 调度引擎 | Quartz 2.x | Cron 表达式驱动的分布式任务调度 |
| 计算引擎 | Apache Flink 1.17 | YARN Application 模式提交 |
| 构建工具 | Maven 3.9+ | 多模块项目构建 |
| 前端框架 | Vue 3 / Vite 6 / TypeScript | SPA 管理后台 |
| UI 组件 | Ant Design Vue 4 | 企业级组件库 |
| 状态管理 | Pinia | Vue 状态管理 |
| 样式方案 | UnoCSS | 原子化 CSS |
| 图表 | ECharts 5 | 数据可视化 |
| 容器化 | Docker | runtime-only 镜像部署 |

---

## 模块结构

```
bigdata-management/
├── bigdata-server/      # Spring Boot 主应用（Web/API/业务逻辑）
├── bigdata-api/         # SPI 接口与公共抽象（JobHandler, Converter, 枚举）
├── bigdata-flink/       # Flink 集成（支持 YARN / Standalone 等模式）
├── bigdata-scheduler/   # Quartz 调度封装
├── bigdata-alert/       # 告警通知（Sender SPI，默认钉钉实现）
├── bigdata-ui/          # Vue 3 管理后台
├── docker/              # Dockerfile 与构建脚本
└── docs/                # 架构与使用文档
```

### 模块职责

| 模块 | 职责 |
|------|------|
| `bigdata-server` | Web 入口、REST 控制器、业务服务、实体/Mapper、Quartz Job 定义 |
| `bigdata-api` | 引擎无关的 `JobHandler` / `JobParamConverter` 接口，供各引擎模块实现 |
| `bigdata-flink` | Flink 引擎的 `JobHandler` 实现，支持 YARN Application 等运行模式 |
| `bigdata-scheduler` | 基于 Quartz 的调度器提供者抽象 |
| `bigdata-alert` | 告警通知，通过 Sender 接口支持多通道扩展（如钉钉、邮件等） |
| `bigdata-ui` | Soybean Admin 模板体系的管理后台 SPA |

---

## 核心架构模式

### SPI 插件化引擎集成

通过 Java `ServiceLoader` + `@AutoService` 实现可插拔的引擎处理器，运行时自动发现注册：

```
JobHandlerIdentifier(clusterType, engineType, jobMode)
        │
        ├── JobParamConverterInvoker → 参数转换（集群配置 → 引擎参数）
        └── JobHandlerInvoker        → 作业操作（submit / cancel / status）
```

新增引擎只需实现 `JobHandler` 和 `JobParamConverter` 接口并标注 `@AutoService`，无需修改调度框架代码。

### 调度执行链路

```
用户启用调度
    ↓
ScheduleServiceImpl → Quartz Job 注册
    ↓
FlinkExecuteJob.executeInternal()
    ├── 读取 task_schedule / task_info / cluster_info
    ├── 反序列化集群与任务配置（JSON）
    ├── JobHandlerIdentifier 路由
    ├── Converter 转换 → 引擎参数
    ├── JobHandler.submit() → Flink YARN Client
    └── 返回 ApplicationId → 写入 task_instance
```

### 事件驱动告警

```
任务状态变更 → TaskEventPublisher 发布事件
    ↓
AlertEventConsumer 消费
    ├── 匹配告警策略（任务、条件）
    ├── 模板渲染
    └── Sender 接口发送（默认钉钉，可扩展邮件等）
```

---

## 数据库设计

### 业务表

| 表名 | 用途 |
|------|------|
| `task_info` | 任务定义（引擎类型、集群、参数配置） |
| `cluster_info` | 集群配置（类型、连接信息、健康状态） |
| `task_schedule` | 调度配置（Cron 表达式、启停、依赖关系） |
| `task_instance` | 运行实例（状态、ApplicationId、集群快照） |
| `alert_info` | 告警通道（钉钉 Webhook、模板） |
| `alert_policy` | 告警策略（匹配条件、通知对象） |
| `task_status_event` | 任务状态事件（状态变更日志） |
| `user` | 用户表（用户名、密码 bcrypt、联系方式、状态） |

### Quartz 表

标准 Quartz JDBC JobStore 表，支持集群模式：
`QRTZ_JOB_DETAILS` `QRTZ_TRIGGERS` `QRTZ_CRON_TRIGGERS` 等 11 张。

---

## 前端结构

```
bigdata-ui/src/views/
├── _builtin/login/          # 登录页
├── home/                    # Dashboard 首页（统计卡片、趋势图、状态饼图）
├── manage/                  # 运维中心
│   ├── task/                # 任务管理
│   ├── schedule/            # 调度管理
│   └── taskinstance/        # 实例追踪
├── system/
│   └── user/                # 用户管理
├── config/                  # 配置中心
│   ├── cluster/             # 集群管理
│   └── alert/instance/      # 告警配置
```

路由由 `elegant-router` 基于文件目录自动生成，URL 路径与目录结构一致。

---

## 部署

镜像为 **runtime-only** 模式：前端 dist 和后端 JAR 在宿主机构建后打入镜像。

```bash
# 一键构建
scripts/release/build-image.sh --base-url http://127.0.0.1:8080

# 运行
docker run -d --name bigdata-management -p 8080:8080 bigdata-management:latest
```

应用监听 8080 端口，前端 SPA 和后端 API 同域部署。
