# 镜像构建指南

本文说明如何在当前项目中完成前后端产物构建并生成 Docker 镜像。

## 前置依赖

- Docker
- JDK 17+
- Maven 3.9+
- Node.js 18+
- pnpm

## 一键构建脚本

项目提供脚本：`scripts/release/build-image.sh`

### 1) 赋予执行权限（首次）

```bash
chmod +x scripts/release/build-image.sh
```

### 2) 使用默认参数构建

```bash
scripts/release/build-image.sh
```

默认输出镜像：`bigdata-management:latest`

## 常用参数

```bash
scripts/release/build-image.sh \
  --image bigdata-management \
  --tag latest \
  --base-url http://127.0.0.1:8080 \
  --no-cache
```

- `--image`：镜像名，默认 `bigdata-management`
- `--tag`：镜像 tag，默认 `latest`
- `--base-url`：覆盖前端构建时 `VITE_SERVICE_BASE_URL`
- `--dockerfile`：指定 Dockerfile 路径（相对仓库根目录）
- `--no-cache`：Docker 构建不使用缓存

## 脚本执行内容

脚本会按顺序执行：

1. 前端构建：`pnpm -C bigdata-ui build`
2. 同步前端产物到后端静态目录
3. 后端打包：`mvn -pl bigdata-server -am -DskipTests package`
4. 构建镜像：`docker build -f docker/Dockerfile -t <image:tag> .`

## 运行容器（示例）

```bash
docker rm -f bigdata-verify || true
docker run -d --name bigdata-verify -p 8080:8080 bigdata-management:latest
```

## 验证

```bash
curl -I http://127.0.0.1:8080
docker logs --tail 100 bigdata-verify
```
