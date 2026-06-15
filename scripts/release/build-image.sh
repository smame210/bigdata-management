#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"

IMAGE_NAME="bigdata-management"
IMAGE_TAG="latest"
DOCKERFILE="docker/Dockerfile"
BASE_URL="/"
NO_CACHE="false"

usage() {
  cat <<'EOF'
Usage: scripts/release/build-image.sh [options]

Options:
  --image <name>         Docker image name (default: bigdata-management)
  --tag <tag>            Docker image tag (default: latest)
  --base-url <url>       Override VITE_SERVICE_BASE_URL for frontend build
  --dockerfile <path>    Dockerfile path relative to repo root (default: docker/Dockerfile)
  --no-cache             Build docker image with --no-cache
  -h, --help             Show this help message
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --image)
      IMAGE_NAME="$2"
      shift 2
      ;;
    --tag)
      IMAGE_TAG="$2"
      shift 2
      ;;
    --base-url)
      BASE_URL="$2"
      shift 2
      ;;
    --dockerfile)
      DOCKERFILE="$2"
      shift 2
      ;;
    --no-cache)
      NO_CACHE="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      usage
      exit 1
      ;;
  esac
done

echo "[1/4] Install frontend dependencies"
(
  cd "$ROOT_DIR"
  pnpm -C bigdata-ui install
)

echo "[2/4] Build frontend dist (BASE_URL=$BASE_URL)"
(
  cd "$ROOT_DIR"
  VITE_SERVICE_BASE_URL="$BASE_URL" pnpm -C bigdata-ui build
)

echo "[3/4] Sync dist to server static resources"
rm -rf "$ROOT_DIR/bigdata-server/src/main/resources/static"
mkdir -p "$ROOT_DIR/bigdata-server/src/main/resources/static"
cp -R "$ROOT_DIR/bigdata-ui/dist/." "$ROOT_DIR/bigdata-server/src/main/resources/static/"

echo "[3/4] Package backend jar"
(
  cd "$ROOT_DIR"
  mvn -pl bigdata-server -am -DskipTests clean package
)

echo "[4/4] Build docker image"
DOCKER_BUILD_ARGS=(build)

if [[ "$NO_CACHE" == "true" ]]; then
  DOCKER_BUILD_ARGS+=(--no-cache)
fi

DOCKER_BUILD_ARGS+=(
  -f "$DOCKERFILE"
  -t "${IMAGE_NAME}:${IMAGE_TAG}"
  .
)

(
  cd "$ROOT_DIR"
  docker "${DOCKER_BUILD_ARGS[@]}"
)

echo "Done: ${IMAGE_NAME}:${IMAGE_TAG}"
