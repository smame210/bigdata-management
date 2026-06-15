<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { message } from 'ant-design-vue';
import { DownloadOutlined } from '@ant-design/icons-vue';
import { downloadFile, fetchGetClusterById, fetchGetClusterList, fetchGetTaskById } from '@/service/api';
import { $t } from '@/locales';
import { enableTaskStatusRecord } from '@/constants/business';
import { useTabStore } from '@/store/modules/tab';
import { useRouterPush } from '@/hooks/common/router';

const { routerPushByKey } = useRouterPush();
const { removeActiveTab } = useTabStore();

// 路由相关
interface Props {
  id: string;
}

const props = defineProps<Props>();

// 任务详情数据
const taskDetail = ref<Api.TaskManage.Task>();
const tagMap: Record<number, string> = {
  0: 'default',
  1: 'processing',
  2: 'success',
  3: 'error',
  4: 'warning'
};

// 集群选项
const clusterOptions = ref<{ label: string; value: number }[]>([]);

// 根据 clusterId 查找集群名称
const clusterName = computed(() => {
  if (!taskDetail.value?.clusterId) return '-';
  const cluster = clusterOptions.value.find(item => item.value === Number(taskDetail.value?.clusterId));
  return cluster?.label || String(taskDetail.value.clusterId);
});

// 加载状态
const loading = ref<boolean>(true);

// 获取集群列表
async function fetchClusterOptions() {
  const { error, data } = await fetchGetClusterList();
  if (!error && data) {
    clusterOptions.value = data.map((item: any) => ({
      label: item.clusterName,
      value: item.id
    }));
  }
}

async function ensureCurrentClusterOption() {
  if (!taskDetail.value?.clusterId) return;

  const exists = clusterOptions.value.some(item => item.value === Number(taskDetail.value?.clusterId));
  if (exists) return;

  const { error, data } = await fetchGetClusterById(Number(taskDetail.value.clusterId));
  if (!error && data) {
    clusterOptions.value.push({
      label: data.clusterName,
      value: data.id
    });
  }
}

// 获取任务详情
async function fetchTaskDetail() {
  loading.value = true;
  const { error, data, response } = await fetchGetTaskById(Number(props.id));
  if (error) {
    loading.value = false;
    message.error(response?.data?.msg || '获取任务详情失败');
  } else {
    loading.value = false;
    taskDetail.value = { ...data };
    taskDetail.value.config = { ...JSON.parse(data.config) };
    await ensureCurrentClusterOption();
  }
}

async function handleDownloadFile(filePath: string, fileName?: string) {
  try {
    const { error, data } = await downloadFile(filePath);
    if (error) {
      message.error('下载失败');
      return;
    }

    // 创建 Blob URL
    const blob = new Blob([data], { type: 'application/octet-stream' });
    const url = window.URL.createObjectURL(blob);

    // 创建下载链接并触发下载
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName || 'download.file';
    document.body.appendChild(link);
    link.click();

    // 清理
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch {
    message.error('下载失败');
  }
}

// 返回列表页
function goBack() {
  removeActiveTab();
  routerPushByKey('manage_task');
}

// 查看启动历史
function goLaunchHistory() {
  const taskName = taskDetail.value?.taskName;
  if (taskName) {
    routerPushByKey('manage_taskinstance', { query: { taskName } });
  }
}

// 初始化加载数据
onMounted(() => {
  Promise.all([fetchClusterOptions(), fetchTaskDetail()]);
});
</script>

<template>
  <div class="task-form-container">
    <ACard :title="$t('page.manage.task.detail')" :bordered="false" class="mx-auto max-w-800px w-full">
      <ASkeleton :loading="loading" active>
        <template v-if="taskDetail">
          <AForm layout="vertical" :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }">
            <!-- 基本信息部分 -->
            <ADivider orientation="left">基本信息</ADivider>

            <AFormItem label="任务名称">
              <AInput :value="taskDetail.taskName" disabled />
            </AFormItem>

            <AFormItem label="引擎类型">
              <AInput :value="taskDetail.engineType" disabled />
            </AFormItem>

            <AFormItem label="任务模式">
              <AInput :value="taskDetail.taskMode" disabled />
            </AFormItem>

            <AFormItem label="任务状态">
              <template v-if="taskDetail.taskStatus !== null">
                <ATag :color="tagMap[taskDetail.taskStatus]">
                  {{ $t(enableTaskStatusRecord[taskDetail.taskStatus]) }}
                </ATag>
              </template>
              <template v-else>-</template>
            </AFormItem>

            <AFormItem label="集群">
              <AInput :value="clusterName" disabled />
            </AFormItem>

            <AFormItem label="创建时间">
              <AInput :value="taskDetail.createTime || '-'" disabled />
            </AFormItem>

            <AFormItem label="最近启动时间">
              <AInput :value="taskDetail.latestLaunchTime || '-'" disabled />
            </AFormItem>

            <!-- Flink 配置部分 -->
            <template v-if="taskDetail.engineType === 'flink' && taskDetail.config">
              <ADivider orientation="left">Flink 配置</ADivider>

              <AFormItem label="Flink 版本">
                <AInput :value="taskDetail.config.version" disabled />
              </AFormItem>

              <AFormItem label="任务类型">
                <AInput :value="taskDetail.config.taskType || '-'" disabled />
              </AFormItem>

              <AFormItem v-if="taskDetail.config.taskType === 'standalone'" label="JobManager 地址">
                <AInput :value="taskDetail.config.jobManagerUrl || '-'" disabled />
              </AFormItem>

              <AFormItem label="JAR 路径">
                <AInput :value="taskDetail.config.jarPath || '-'" disabled />
              </AFormItem>

              <AFormItem label="依赖库路径">
                <AInput :value="taskDetail.config.libPath || '-'" disabled />
              </AFormItem>

              <AFormItem label="Flink 配置文件">
                <div
                  v-if="!taskDetail.config.flinkConfPath || taskDetail.config.flinkConfPath === '-'"
                  class="text-gray-400"
                >
                  -
                </div>
                <div v-else class="file-download-item">
                  <span>flink-conf.yaml</span>
                  <AButton
                    type="link"
                    size="small"
                    @click="handleDownloadFile(taskDetail.config.flinkConfPath, 'flink-conf.yaml')"
                  >
                    <template #icon><DownloadOutlined /></template>
                    下载
                  </AButton>
                </div>
              </AFormItem>

              <AFormItem label="Log4j 配置文件">
                <div
                  v-if="!taskDetail.config.logConfPath || taskDetail.config.logConfPath === '-'"
                  class="text-gray-400"
                >
                  -
                </div>
                <div v-else class="file-download-item">
                  <span>log4j.properties</span>
                  <AButton
                    type="link"
                    size="small"
                    @click="handleDownloadFile(taskDetail.config.logConfPath, 'log4j.properties')"
                  >
                    <template #icon><DownloadOutlined /></template>
                    下载
                  </AButton>
                </div>
              </AFormItem>

              <AFormItem label="主类">
                <AInput :value="taskDetail.config.mainClass || '-'" disabled />
              </AFormItem>

              <AFormItem label="程序参数">
                <div
                  v-if="!taskDetail.config.args || Object.keys(taskDetail.config.args).length === 0"
                  class="text-gray-400"
                >
                  -
                </div>
                <div v-else class="flex flex-col gap-8px">
                  <div v-for="(value, key) in taskDetail.config.args" :key="key" class="flex items-center gap-8px">
                    <AInput :value="key" disabled class="w-160px" />
                    <AInput :value="value" disabled class="flex-1" />
                  </div>
                </div>
              </AFormItem>

              <ARow :gutter="16">
                <ACol :span="12">
                  <AFormItem label="并行度">
                    <AInputNumber :value="taskDetail.config.parallelism || '-'" disabled class="w-full" />
                  </AFormItem>
                </ACol>
                <ACol :span="12">
                  <AFormItem label="TaskManager Slots">
                    <AInputNumber :value="taskDetail.config.taskManagerSlots || '-'" disabled class="w-full" />
                  </AFormItem>
                </ACol>
              </ARow>

              <ARow :gutter="16">
                <ACol :span="12">
                  <AFormItem label="JobManager 内存 (MB)">
                    <AInputNumber :value="taskDetail.config.jobManagerMemory || '-'" disabled class="w-full" />
                  </AFormItem>
                </ACol>
                <ACol :span="12">
                  <AFormItem label="TaskManager 内存 (MB)">
                    <AInputNumber :value="taskDetail.config.taskManagerMemory || '-'" disabled class="w-full" />
                  </AFormItem>
                </ACol>
              </ARow>

              <ARow :gutter="16">
                <ACol :span="12">
                  <AFormItem label="TaskManager Managed Memory Fraction">
                    <AInputNumber
                      :value="taskDetail.config.taskManagerMemoryManagedFraction || '-'"
                      disabled
                      class="w-full"
                    />
                  </AFormItem>
                </ACol>
              </ARow>
            </template>

            <!-- Spark 配置部分 -->
            <template v-else-if="taskDetail.engineType === 'spark' && taskDetail.config">
              <ADivider orientation="left">Spark 配置</ADivider>
              <AAlert message="Spark 配置暂未实现" type="info" />
            </template>

            <template v-else>
              <ADivider orientation="left">任务配置</ADivider>
              <AEmpty description="暂无配置信息" />
            </template>

            <!-- 按钮区域 -->
            <AFormItem class="form-buttons flex justify-center">
              <AButton type="primary" class="mr-4" @click="goBack()">
                {{ $t('common.back') }}
              </AButton>
              <AButton @click="goLaunchHistory()">查看启动历史</AButton>
            </AFormItem>
          </AForm>
        </template>
      </ASkeleton>
    </ACard>
  </div>
</template>

<style scoped>
.task-form-container {
  padding: 24px;
}

.form-buttons {
  margin-top: 24px;
}

.mr-4 {
  margin-right: 16px;
}

.max-w-800px {
  max-width: 800px;
}

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.w-full {
  width: 100%;
}

.file-download-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 11px;
  background-color: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  min-height: 32px;
}

.file-download-item span {
  color: rgba(0, 0, 0, 0.65);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
</style>
