<script setup lang="tsx">
import { onMounted, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { $t } from '@/locales';
import { enableAlterStatusRecord } from '@/constants/business';
import { fetchAlertDelete, fetchAlertList, fetchAlertUpdateStatus } from '@/service/api';
import dingdingImg from '@/assets/imgs/dingding.png';
import AlertSearch from '@/views/config/alert/instance/modules/alert-search.vue';
import AlertAdd from '@/views/config/alert/instance/modules/alert-add.vue';
import AlertEdit from '@/views/config/alert/instance/modules/alert-update.vue';

const tagMap: Record<Api.AlertManage.AlertStatus, string> = {
  '0': 'error',
  '1': 'success'
};

// 告警类型选项
const alertTypeMap: Record<Api.AlertManage.AlertType, string> = {
  dingding: '钉钉'
};

// 告警类型图标映射
const alertTypeIconMap: Record<string, string> = {
  email: 'mdi:email-outline',
  sms: 'mdi:message-text'
};

// 告警类型图片映射
const alertTypeImageMap: Record<string, string> = {
  dingding: dingdingImg
};

// 告警类型颜色映射
const alertTypeGradientMap: Record<string, string> = {
  dingding: 'linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(59, 130, 246, 0.32))',
  email: 'linear-gradient(135deg, #f59e0b, #fbbf24)',
  sms: 'linear-gradient(135deg, #10b981, #34d399)'
};

const items = ref<Api.AlertManage.Alert[]>([]);
// 页面加载状态
const loading = ref(false);
// 添加控制弹窗显示的状态
const showAddModal = ref(false);
const currentAlertId = ref<number | null>(null);
const showEditModal = ref(false);
// 搜索参数
const searchParams = reactive<Api.AlertManage.AlertSearchParams>({
  name: '',
  type: undefined,
  status: undefined
});

// 获取集群列表数据
async function getAlterList() {
  loading.value = true;
  const { error, data } = await fetchAlertList(searchParams);
  if (!error) {
    items.value = data || [];
  } else {
    message.error('获取集群列表失败');
  }
  loading.value = false;
}

// 搜索处理
function handleSearch() {
  getAlterList();
}

// 重置处理
function handleReset() {
  searchParams.name = '';
  searchParams.type = undefined;
  searchParams.status = undefined;
  handleSearch();
}

// 添加配置
function handleAdd() {
  showAddModal.value = true;
}

function handleEdit(id: number) {
  currentAlertId.value = id;
  showEditModal.value = true;
}

// 添加处理新增成功的方法
function handleAddSuccess() {
  getAlterList(); // 重新加载列表
}

// 添加处理编辑成功的方法
function handleEditSuccess() {
  getAlterList(); // 重新加载列表
  currentAlertId.value = null;
}

// 启用停用
async function handleStatus(id: number, status: number) {
  const { error } = await fetchAlertUpdateStatus(id, status);
  if (!error) {
    message.success(status === 1 ? $t('common.enableSuccess') : $t('common.disableSuccess'));
    await getAlterList();
  } else {
    message.error('操作失败');
  }
}

async function handleDelete(id: number) {
  const { error } = await fetchAlertDelete(id);
  if (!error) {
    message.success('删除成功！');
    await getAlterList();
  } else {
    message.error('删除失败');
  }
}

onMounted(() => {
  getAlterList();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <AlertSearch v-model:model="searchParams" @search="handleSearch" @reset="handleReset" @add="handleAdd" />

    <ACard
      :bordered="false"
      :body-style="{ display: 'flex', flexDirection: 'column', flex: 1, overflow: 'hidden', padding: '0' }"
      class="flex-col-stretch sm:flex-1-hidden"
    >
      <div class="alert-list-container">
        <div class="alert-grid">
          <div v-for="item in items" :key="item.id" class="alert-card" :data-status="item.status">
            <!-- 卡片主体 -->
            <div class="card-body">
              <div class="card-header-row">
                <!-- 左侧图标 -->
                <div
                  class="icon-circle"
                  :style="{
                    background: alertTypeGradientMap[item.type] || 'linear-gradient(135deg, #64748b, #94a3b8)'
                  }"
                >
                  <img
                    v-if="alertTypeImageMap[item.type]"
                    :src="alertTypeImageMap[item.type]"
                    alt=""
                    class="alert-type-image"
                  />
                  <SvgIcon v-else :icon="alertTypeIconMap[item.type] || 'mdi:bell-outline'" class="text-5 text-white" />
                </div>

                <!-- 右侧信息 -->
                <div class="info-area">
                  <div class="name-row">
                    <span class="alert-name">{{ item.name }}</span>
                    <ASwitch
                      :checked="item.status === 1"
                      size="small"
                      @change="handleStatus(item.id, item.status === 1 ? 0 : 1)"
                    />
                  </div>
                  <div class="type-row">
                    <span class="type-label">{{ alertTypeMap[item.type] || item.type }}</span>
                    <ATag :color="tagMap[item.status]" size="small">
                      {{ $t(enableAlterStatusRecord[item.status]) }}
                    </ATag>
                  </div>
                </div>
              </div>

              <!-- 分割线 -->
              <div class="divider" />

              <!-- 底部信息 -->
              <div class="footer-row">
                <div class="time-info">
                  <SvgIcon icon="mdi:clock-outline" class="time-icon" />
                  <span class="time-text">{{ item.createTime }}</span>
                </div>
                <div class="actions">
                  <AButton type="link" size="small" :disabled="item.status === 1" @click="handleEdit(item.id)">
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
    </ACard>

    <AlertAdd v-model:open="showAddModal" @success="handleAddSuccess" />
    <AlertEdit
      v-if="currentAlertId !== null"
      :id="currentAlertId"
      v-model:open="showEditModal"
      @success="handleEditSuccess"
    />
  </div>
</template>

<style scoped>
/* 列表容器 */
.alert-list-container {
  flex: 1;
  min-height: 0;
  width: 100%;
  background: rgb(var(--layout-bg-color));
  padding: 20px;
  overflow-y: auto;
  scrollbar-width: none;
}

/* 网格布局 */
.alert-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

@media (min-width: 1440px) {
  .alert-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1920px) {
  .alert-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 告警卡片 */
.alert-card {
  position: relative;
  background: rgb(var(--container-bg-color));
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.08),
    0 4px 12px rgba(0, 0, 0, 0.05);
}

.alert-card:hover {
  transform: translateY(-4px);
  box-shadow:
    0 12px 24px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 卡片主体 */
.card-body {
  padding: 20px;
}

/* 头部行 */
.card-header-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

/* 图标圆形 */
.icon-circle {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.alert-type-image {
  width: 36px;
  height: 36px;
  object-fit: contain;
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
  margin-bottom: 8px;
}

.alert-name {
  font-size: 16px;
  font-weight: 600;
  color: rgb(var(--base-text-color));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.type-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-label {
  font-size: 13px;
  color: rgba(var(--base-text-color), 0.72);
  font-weight: 500;
}

/* 分割线 */
.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(var(--base-text-color), 0.16), transparent);
  margin: 16px 0;
}

/* 底部行 */
.footer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-icon {
  color: rgba(var(--base-text-color), 0.55);
  font-size: 12px;
}

.time-text {
  color: rgba(var(--base-text-color), 0.72);
  font-size: 12px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.actions :deep(.ant-btn) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  line-height: 1;
  height: 28px;
  padding: 0 10px;
}

.actions :deep(.ant-btn .anticon),
.actions :deep(.ant-btn svg) {
  display: block;
}

/* 禁用状态 */
.alert-card[data-status='0'] {
  opacity: 0.85;
}

.alert-card[data-status='0'] .icon-circle {
  filter: grayscale(0.4);
}
</style>
