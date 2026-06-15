<script setup lang="tsx">
import { onMounted, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { $t } from '@/locales';
import { useRouterPush } from '@/hooks/common/router';
import { deleteCluster, fetchGetClusterList, updateClusterStatus } from '@/service/api';
import ClusterSearch from './modules/cluster-search.vue';

const { routerPushByKey } = useRouterPush();

const clusterIconMap: Record<string, { icon: string; color: string; label: string }> = {
  yarn: { icon: 'simple-icons:apachehadoop', color: '#3b82f6', label: 'YARN' },
  k8s: { icon: 'simple-icons:kubernetes', color: '#326ce5', label: 'Kubernetes' },
  flink_standalone: { icon: 'simple-icons:apacheflink', color: '#e6522c', label: 'Flink Standalone' },
  standalone: { icon: 'simple-icons:apacheflink', color: '#e6522c', label: 'Standalone' }
};

const items = ref<Api.ClusterManage.Cluster[]>([]);

function getHealthColor(healthStatus?: Api.ClusterManage.ClusterHealthStatus) {
  switch (healthStatus) {
    case 1:
      return '#10b981';
    case 2:
      return '#f59e0b';
    case 3:
      return '#ef4444';
    default:
      return '#94a3b8';
  }
}

function getHealthText(healthStatus?: Api.ClusterManage.ClusterHealthStatus) {
  switch (healthStatus) {
    case 1:
      return '集群健康';
    case 2:
      return '集群降级';
    case 3:
      return '集群异常';
    default:
      return '未知';
  }
}
// 页面加载状态
const loading = ref(false);
// 搜索参数
const searchParams = reactive({
  clusterName: '',
  clusterType: undefined,
  clusterStatus: undefined
});

// 获取集群列表数据
async function fetchClusterList() {
  loading.value = true;
  const { error, data } = await fetchGetClusterList(searchParams);
  if (!error) {
    items.value = data || [];
  } else {
    message.error('获取集群列表失败');
  }
  loading.value = false;
}

// 搜索处理
function handleSearch() {
  fetchClusterList();
}

// 重置处理
function handleReset() {
  searchParams.clusterName = '';
  searchParams.clusterType = undefined;
  searchParams.clusterStatus = undefined;
  handleSearch();
}

// 添加配置
function handleAdd() {
  fetchGetClusterList();
}

// 启用/停用单个集群
async function handleStatus(id: number, status: number) {
  const res = await updateClusterStatus({ [id]: status });
  if (!res.error) {
    message.success(status === 1 ? '启用成功' : '停用成功');
    await fetchClusterList();
  } else {
    message.error('操作失败');
  }
}

onMounted(() => {
  fetchClusterList();
});

async function handleView(id: number) {
  routerPushByKey('config_cluster-detail', { params: { id: String(id) } });
}

async function handleEdit(id: number) {
  routerPushByKey('config_cluster-update', { params: { id: String(id) } });
}

async function handleDelete(id: number) {
  const { error, response: resp } = await deleteCluster(id);
  if (!error) {
    message.success('删除成功！');
    await fetchClusterList();
  } else {
    message.error(resp.data.msg);
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ClusterSearch v-model:model="searchParams" @search="handleSearch" @reset="handleReset" @add="handleAdd" />

    <div class="cluster-list-container">
      <div class="cluster-grid">
        <div v-for="item in items" :key="item.id" class="cluster-card">
          <!-- 卡片主体 -->
          <div class="card-body">
            <div class="card-header-row">
              <!-- 左侧图标 -->
              <div
                class="cluster-icon"
                :style="{
                  background: `linear-gradient(135deg, ${clusterIconMap[item.clusterType]?.color || '#64748b'}20, ${clusterIconMap[item.clusterType]?.color || '#64748b'}40)`,
                  border: `2px solid ${clusterIconMap[item.clusterType]?.color || '#64748b'}30`
                }"
              >
                <SvgIcon
                  :icon="clusterIconMap[item.clusterType]?.icon || 'ant-design:cloud-server-outlined'"
                  :style="{ color: clusterIconMap[item.clusterType]?.color || '#64748b' }"
                  class="text-6"
                />
              </div>

              <!-- 右侧信息 -->
              <div class="info-area">
                <div class="name-row">
                  <span class="cluster-name">{{ item.clusterName }}</span>
                  <ASwitch
                    :checked="item.clusterStatus === 1"
                    size="small"
                    @change="handleStatus(item.id, item.clusterStatus === 1 ? 0 : 1)"
                  />
                </div>
                <div class="type-row">
                  <span
                    class="type-badge"
                    :style="{
                      background: `${clusterIconMap[item.clusterType]?.color || '#64748b'}15`,
                      color: clusterIconMap[item.clusterType]?.color || '#64748b',
                      border: `1px solid ${clusterIconMap[item.clusterType]?.color || '#64748b'}25`
                    }"
                  >
                    <SvgIcon
                      :icon="clusterIconMap[item.clusterType]?.icon || 'ant-design:cloud-server-outlined'"
                      class="mr-1 text-3"
                    />
                    {{ clusterIconMap[item.clusterType]?.label || item.clusterType }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 分割线 -->
            <div class="divider" />

            <!-- 集群信息 -->
            <div class="meta-section">
              <div class="info-item">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ item.createTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">健康状态</span>
                <div class="health-indicator">
                  <span
                    class="health-dot"
                    :class="{ 'health-pulse': item.healthStatus === 1 }"
                    :style="{ backgroundColor: getHealthColor(item.healthStatus) }"
                  />
                  <span class="health-text" :style="{ color: getHealthColor(item.healthStatus) }">
                    {{ getHealthText(item.healthStatus) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 分割线 -->
            <div class="divider" />

            <!-- 底部操作 -->
            <div class="card-actions">
              <AButton type="link" size="small" @click="handleView(item.id)">
                <SvgIcon icon="mdi:eye-outline" class="mr-1 text-3.5" />
                {{ $t('common.detail') }}
              </AButton>
              <AButton type="link" size="small" :disabled="item.clusterStatus === 1" @click="handleEdit(item.id)">
                <SvgIcon icon="mdi:pencil-outline" class="mr-1 text-3.5" />
                {{ $t('common.edit') }}
              </AButton>
              <APopconfirm :title="$t('common.confirmDelete')" @confirm="handleDelete(item.id)">
                <AButton type="link" danger size="small">
                  <SvgIcon icon="mdi:delete-outline" class="mr-1 text-3.5" />
                  {{ $t('common.delete') }}
                </AButton>
              </APopconfirm>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 列表容器 */
.cluster-list-container {
  width: 100%;
  flex: 1;
  min-height: 0;
  background: rgb(var(--layout-bg-color));
  padding: 16px;
  overflow-y: auto;
  scrollbar-width: none;
}

/* 网格布局 */
.cluster-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

@media (min-width: 1440px) {
  .cluster-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1920px) {
  .cluster-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 集群卡片 */
.cluster-card {
  position: relative;
  background: rgb(var(--container-bg-color));
  border-radius: 14px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.08),
    0 4px 12px rgba(0, 0, 0, 0.05);
}

.cluster-card:hover {
  transform: translateY(-2px);
  box-shadow:
    0 12px 24px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 卡片主体 */
.card-body {
  padding: 16px;
}

/* 头部行 */
.card-header-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

/* 集群图标 */
.cluster-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* 信息区域 */
.info-area {
  flex: 1;
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}

.cluster-name {
  font-size: 15px;
  font-weight: 600;
  color: rgb(var(--base-text-color));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.type-row {
  display: flex;
  align-items: center;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}

/* 分割线 */
.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(var(--base-text-color), 0.16), transparent);
  margin: 12px 0;
}

/* 信息区域 */
.meta-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  font-size: 12px;
  color: rgba(var(--base-text-color), 0.54);
  font-weight: 500;
  min-width: 72px;
}

.info-value {
  font-size: 12px;
  color: rgba(var(--base-text-color), 0.78);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 健康状态 */
.health-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
}

.health-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.health-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4);
  }
  50% {
    opacity: 0.8;
    box-shadow: 0 0 0 6px rgba(16, 185, 129, 0);
  }
}

.health-text {
  font-size: 12px;
  font-weight: 600;
}

/* 底部操作 */
.card-actions {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 6px;
  flex-wrap: wrap;
  margin: 0 -16px -16px;
  padding: 10px 16px;
  background: rgba(var(--base-text-color), 0.03);
  border-top: 1px solid rgba(var(--base-text-color), 0.1);
}

.card-actions :deep(.ant-btn) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  line-height: 1;
  height: 24px;
  padding: 0 8px;
}

.card-actions :deep(.ant-btn .anticon),
.card-actions :deep(.ant-btn svg) {
  display: block;
}
</style>
