<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { Spin as ASpin, message } from 'ant-design-vue';
import { DownloadOutlined, FileTextOutlined, RollbackOutlined } from '@ant-design/icons-vue';
import { downloadFile, fetchGetClusterById } from '@/service/api';
import { $t } from '@/locales';
import { useRouterPush } from '@/hooks/common/router';
import { useTabStore } from '@/store/modules/tab';

const { routerPushByKey } = useRouterPush();
const { removeActiveTab } = useTabStore();

// 路由相关
interface Props {
  id: string;
}

const props = defineProps<Props>();

// 加载状态
const loading = ref(true);

type FileType = 'coreSitePath' | 'hdfsSitePath' | 'yarnSitePath' | 'yarnResourceManagerUrl';

// 集群数据
const clusterData = ref({
  id: 0,
  clusterName: '',
  clusterType: '',
  clusterStatus: 1,
  createTime: '',
  updateTime: '',
  hadoopConfigs: reactive<Record<FileType, string>>({
    coreSitePath: '',
    hdfsSitePath: '',
    yarnSitePath: '',
    yarnResourceManagerUrl: ''
  })
});
const configTypeOptions: Record<string, string> = { yarn: $t('page.config.cluster.cluster_type.yarn') };

// 获取集群类型文本
function getClusterTypeText(type: string) {
  return configTypeOptions[type] || type;
}

// 获取集群详情
async function fetchClusterDetail() {
  loading.value = true;
  const { error, data } = await fetchGetClusterById(Number(props.id));
  if (error) {
    loading.value = false;
    message.error('获取集群详情失败');
    return;
  }

  clusterData.value = {
    id: data.id,
    clusterName: data?.clusterName || '',
    clusterType: data?.clusterType || '',
    clusterStatus: data?.clusterStatus ?? 1,
    createTime: data?.createTime || '',
    updateTime: data?.updateTime || '',
    hadoopConfigs: {
      coreSitePath: clusterData.value.hadoopConfigs.coreSitePath,
      hdfsSitePath: clusterData.value.hadoopConfigs.hdfsSitePath,
      yarnSitePath: clusterData.value.hadoopConfigs.yarnSitePath,
      yarnResourceManagerUrl: clusterData.value.hadoopConfigs.yarnResourceManagerUrl
    }
  };

  if (data.clusterType === 'yarn') {
    let hadoopConfigsJson: Partial<Record<FileType, string>> = {};
    if (data.metadata) {
      hadoopConfigsJson = JSON.parse(data.metadata);
    }
    clusterData.value.hadoopConfigs = {
      coreSitePath: hadoopConfigsJson.coreSitePath || '',
      hdfsSitePath: hadoopConfigsJson.hdfsSitePath || '',
      yarnSitePath: hadoopConfigsJson.yarnSitePath || '',
      yarnResourceManagerUrl: hadoopConfigsJson.yarnResourceManagerUrl || ''
    };
  }
  loading.value = false;
}

// 下载配置文件
async function handleDownload(filePath: string, fileName: string) {
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
    link.download = fileName;
    document.body.appendChild(link);
    link.click();

    // 清理
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (err) {
    console.error(err);
    message.error('下载失败');
  }
}

// 返回按钮点击事件
function handleBack() {
  removeActiveTab();
  routerPushByKey('config_cluster');
}

// 组件挂载时获取详情
onMounted(() => {
  fetchClusterDetail();
});
</script>

<template>
  <div class="cluster-detail-container">
    <ACard :title="$t('page.config.cluster_detail.title')" :bordered="false" class="mx-auto max-w-800px w-full">
      <template #extra>
        <AButton class="button ml-2" @click="handleBack">
          <template #icon><RollbackOutlined /></template>
          {{ $t('common.back') }}
        </AButton>
      </template>

      <ASpin :spinning="loading">
        <ADescriptions :column="{ xs: 1, sm: 2 }" bordered>
          <ADescriptionsItem :label="$t('page.config.cluster.clusterName')">
            {{ clusterData.clusterName }}
          </ADescriptionsItem>

          <ADescriptionsItem :label="$t('page.config.cluster.clusterType')">
            {{ getClusterTypeText(clusterData.clusterType) }}
          </ADescriptionsItem>

          <ADescriptionsItem :label="$t('page.config.cluster.clusterStatus')">
            <ATag :color="clusterData.clusterStatus === 1 ? 'success' : 'error'">
              {{
                clusterData.clusterStatus === 1
                  ? $t('page.config.common.status.enable')
                  : $t('page.config.common.status.disable')
              }}
            </ATag>
          </ADescriptionsItem>

          <ADescriptionsItem :label="$t('page.config.cluster.createTime')">
            {{ clusterData.createTime || '-' }}
          </ADescriptionsItem>

          <ADescriptionsItem :label="$t('page.config.cluster.updateTime')" :span="2">
            {{ clusterData.updateTime || '-' }}
          </ADescriptionsItem>

          <!-- Yarn 特有字段 -->
          <template v-if="clusterData.clusterType === 'yarn'">
            <ADescriptionsItem label="YARN ResourceManager 地址" :span="2">
              <a
                v-if="clusterData.hadoopConfigs?.yarnResourceManagerUrl"
                :href="clusterData.hadoopConfigs.yarnResourceManagerUrl"
                target="_blank"
                rel="noopener noreferrer"
                class="text-primary"
              >
                {{ clusterData.hadoopConfigs.yarnResourceManagerUrl }}
              </a>
              <span v-else>-</span>
            </ADescriptionsItem>

            <ADescriptionsItem label="Hadoop 配置文件" :span="2">
              <ADivider orientation="left">Hadoop 配置</ADivider>

              <div class="hadoop-config mb-4">
                <div class="file-download-item mb-2 flex items-center justify-between">
                  <span class="font-medium">core-site.xml</span>
                  <AButton
                    v-if="clusterData.hadoopConfigs?.coreSitePath"
                    type="primary"
                    size="small"
                    @click="() => handleDownload(clusterData.hadoopConfigs.coreSitePath, 'core-site.xml')"
                  >
                    <template #icon><DownloadOutlined /></template>
                    下载
                  </AButton>
                </div>
                <div class="config-file-preview">
                  <div v-if="clusterData.hadoopConfigs.coreSitePath" class="file-exists">
                    <FileTextOutlined />
                    core-site.xml
                  </div>
                  <div v-else class="file-not-exists">未上传配置文件</div>
                </div>
              </div>

              <div class="hadoop-config mb-4">
                <div class="file-download-item mb-2 flex items-center justify-between">
                  <span class="font-medium">hdfs-site.xml</span>
                  <AButton
                    v-if="clusterData.hadoopConfigs?.hdfsSitePath"
                    type="primary"
                    size="small"
                    @click="() => handleDownload(clusterData.hadoopConfigs.hdfsSitePath, 'hdfs-site.xml')"
                  >
                    <template #icon><DownloadOutlined /></template>
                    下载
                  </AButton>
                </div>
                <div class="config-file-preview">
                  <div v-if="clusterData.hadoopConfigs?.hdfsSitePath" class="file-exists">
                    <FileTextOutlined />
                    hdfs-site.xml
                  </div>
                  <div v-else class="file-not-exists">未上传配置文件</div>
                </div>
              </div>

              <div class="hadoop-config">
                <div class="file-download-item mb-2 flex items-center justify-between">
                  <span class="font-medium">yarn-site.xml</span>
                  <AButton
                    v-if="clusterData.hadoopConfigs?.yarnSitePath"
                    type="primary"
                    size="small"
                    @click="() => handleDownload(clusterData.hadoopConfigs.yarnSitePath, 'yarn-site.xml')"
                  >
                    <template #icon><DownloadOutlined /></template>
                    下载
                  </AButton>
                </div>
                <div class="config-file-preview">
                  <div v-if="clusterData.hadoopConfigs?.yarnSitePath" class="file-exists">
                    <FileTextOutlined />
                    yarn-site.xml
                  </div>
                  <div v-else class="file-not-exists">未上传配置文件</div>
                </div>
              </div>
            </ADescriptionsItem>
          </template>
        </ADescriptions>
      </ASpin>
    </ACard>
  </div>
</template>

<style scoped>
.cluster-detail-container {
  padding: 24px;
}

.max-w-800px {
  max-width: 800px;
}

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.ml-2 {
  margin-left: 8px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mb-2 {
  margin-bottom: 8px;
}

.w-full {
  width: 100%;
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.justify-between {
  justify-content: space-between;
}

.font-medium {
  font-weight: 500;
}

.config-file-preview {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  background-color: #f5f5f5;
}

.file-exists {
  color: #1890ff;
}

.file-not-exists {
  color: #999;
}

.button {
  display: inline-flex;
  align-items: center;
}

.file-download-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  min-height: 32px;
}

.file-download-item span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
</style>
