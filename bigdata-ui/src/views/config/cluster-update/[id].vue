<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { Spin as ASpin, Upload as AUpload, message } from 'ant-design-vue';
import { DownloadOutlined, InboxOutlined } from '@ant-design/icons-vue';
import { $t } from '@/locales';
import { useRouterPush } from '@/hooks/common/router';
import { useTabStore } from '@/store/modules/tab';
import { downloadFile, fetchGetClusterById, updateCluster, uploadFile } from '@/service/api';

const { routerPushByKey } = useRouterPush();
const { removeActiveTab } = useTabStore();

// 路由相关
interface Props {
  id: string;
}

const props = defineProps<Props>();

// 表单引用
const formRef = ref();
const loading = ref(true);
// 上传加载状态
const uploading = ref(false);
type FileType = 'coreSitePath' | 'hdfsSitePath' | 'yarnSitePath' | 'yarnResourceManagerUrl';

// 表单数据
const formData = reactive<{
  id: number;
  clusterName: string;
  clusterType: Api.ClusterManage.ClusterType;
  clusterStatus: Api.ClusterManage.ClusterStatus;
  hadoopConfigs: Record<FileType, string>;
}>({
  id: 0,
  clusterName: '',
  clusterType: 'yarn',
  clusterStatus: 1,
  hadoopConfigs: {
    coreSitePath: '',
    hdfsSitePath: '',
    yarnSitePath: '',
    yarnResourceManagerUrl: ''
  }
});

// 文件列表
const fileList = reactive<Record<FileType, any[]>>({
  coreSitePath: [],
  hdfsSitePath: [],
  yarnSitePath: [],
  yarnResourceManagerUrl: []
});

// 表单校验规则
const formRules = computed<Record<string, App.Global.FormRule[]>>(() => {
  const rules: Record<string, App.Global.FormRule[]> = {
    clusterName: [{ required: true, message: $t('page.config.cluster.form.clusterName'), trigger: 'blur' }],
    clusterType: [{ required: true, message: $t('page.config.cluster.form.clusterType'), trigger: 'change' }],
    clusterStatus: [
      { required: true, type: 'number', message: $t('page.config.cluster.form.clusterStatus'), trigger: 'change' }
    ]
  };

  return rules;
});

// 获取集群详情
async function fetchClusterDetail() {
  try {
    loading.value = true;
    const { error, data } = await fetchGetClusterById(Number(props.id));
    if (!error) {
      formData.id = data.id;
      formData.clusterName = data.clusterName;
      formData.clusterType = data.clusterType || 'yarn';
      formData.clusterStatus = data.clusterStatus ?? 1;

      if (data.clusterType === 'yarn') {
        let hadoopConfigsJson: Partial<Record<FileType, string>> = {};
        if (data.metadata) {
          hadoopConfigsJson = JSON.parse(data.metadata);
        }
        formData.hadoopConfigs = {
          coreSitePath: hadoopConfigsJson.coreSitePath || '',
          hdfsSitePath: hadoopConfigsJson.hdfsSitePath || '',
          yarnSitePath: hadoopConfigsJson.yarnSitePath || '',
          yarnResourceManagerUrl: hadoopConfigsJson.yarnResourceManagerUrl || ''
        };

        if (formData.hadoopConfigs.coreSitePath && formData.hadoopConfigs.coreSitePath !== '-') {
          fileList.coreSitePath = [
            {
              uid: '1',
              name: 'core-site.xml',
              status: 'done',
              data: formData.hadoopConfigs.coreSitePath
            }
          ];
        }

        if (formData.hadoopConfigs.hdfsSitePath && formData.hadoopConfigs.hdfsSitePath !== '-') {
          fileList.hdfsSitePath = [
            {
              uid: '2',
              name: 'hdfs-site.xml',
              status: 'done',
              data: formData.hadoopConfigs.hdfsSitePath
            }
          ];
        }

        if (formData.hadoopConfigs.yarnSitePath && formData.hadoopConfigs.yarnSitePath !== '-') {
          fileList.yarnSitePath = [
            {
              uid: '3',
              name: 'yarn-site.xml',
              status: 'done',
              data: formData.hadoopConfigs.yarnSitePath
            }
          ];
        }
      }
    } else {
      message.error('获取集群详情失败');
    }
  } catch (error) {
    console.error(error);
    message.error('获取集群详情失败');
  } finally {
    loading.value = false;
  }
}

// 文件上传前验证
async function beforeUpload(file: File, fileType: FileType) {
  const isXML = file.type === 'text/xml' || file.name.endsWith('.xml');
  if (!isXML) {
    message.error('只能上传XML文件!');
    return false;
  }

  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('文件必须小于2MB!');
    return false;
  }

  const uid = Date.now().toString();
  fileList[fileType] = [
    {
      uid,
      name: file.name,
      status: 'uploading',
      percent: 0
    }
  ];
  uploading.value = true;
  try {
    const { data } = await uploadFile(file);
    fileList[fileType] = [
      {
        uid,
        name: file.name,
        status: 'done',
        data
      }
    ];
    formData.hadoopConfigs[fileType] = data || '';
    message.success(`${file.name} 上传成功`);
  } catch (error) {
    fileList[fileType] = [
      {
        uid,
        name: file.name,
        status: 'error',
        error
      }
    ];
    message.error(`上传失败`);
  } finally {
    uploading.value = false;
  }
  return false;
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

// 移除文件
function handleRemove(fileType: FileType) {
  fileList[fileType] = [];
  formData.hadoopConfigs[fileType] = '';
}

// 提交表单
function handleSubmit() {
  formRef.value.validate().then(async () => {
    // 构建提交数据
    let submitData: Partial<Api.ClusterManage.Cluster> & Record<string, unknown> = {
      clusterName: formData.clusterName,
      clusterType: formData.clusterType,
      clusterStatus: formData.clusterStatus,
      metaData: ''
    };

    // 根据集群类型处理特有数据
    if (formData.clusterType === 'yarn') {
      submitData = {
        ...submitData,
        ...formData.hadoopConfigs
      };
    }

    try {
      const { error } = await updateCluster(formData.id, submitData);
      if (!error) {
        message.success('更新集群成功');
        removeActiveTab();
        routerPushByKey('config_cluster');
      } else {
        message.error('更新集群失败');
      }
    } catch (error) {
      console.error('更新集群失败', error);
      message.error('更新集群失败');
    } finally {
      loading.value = false;
    }
  });
}

// 取消
const handleCancel = () => {
  removeActiveTab();
  routerPushByKey('config_cluster');
};

// 组件挂载时获取详情
onMounted(() => {
  fetchClusterDetail();
});
</script>

<template>
  <div class="cluster-form-container">
    <ACard :title="$t('page.config.cluster.editCluster')" :bordered="false" class="mx-auto max-w-800px w-full">
      <ASpin :spinning="loading">
        <AForm
          ref="formRef"
          :model="formData"
          :rules="formRules"
          layout="vertical"
          :label-col="{ span: 24 }"
          :wrapper-col="{ span: 24 }"
        >
          <!-- 公共字段 -->
          <AFormItem :label="$t('page.config.cluster.clusterName')" name="clusterName">
            <AInput
              v-model:value="formData.clusterName"
              :placeholder="$t('page.config.cluster.form.clusterName')"
              allow-clear
            />
          </AFormItem>

          <AFormItem :label="$t('page.config.cluster.clusterType')" name="clusterType">
            <ASelect
              v-model:value="formData.clusterType"
              :placeholder="$t('page.config.cluster.form.clusterType')"
              disabled
            >
              <ASelectOption value="YARN">Yarn</ASelectOption>
            </ASelect>
          </AFormItem>

          <!-- Yarn 特有字段 -->
          <div v-if="formData.clusterType === 'yarn'">
            <ADivider orientation="left">Hadoop 配置文件</ADivider>

            <AFormItem
              label="core-site.xml"
              :name="['hadoopConfigs', 'coreSitePath']"
              :rules="[{ required: true, message: $t('page.config.cluster.form.coreSite'), trigger: 'change' }]"
            >
              <AUpload
                v-model:file-list="fileList.coreSitePath"
                :before-upload="file => beforeUpload(file, 'coreSitePath')"
                :max-count="1"
                accept=".xml"
                type="drag"
                :show-upload-list="{ showDownloadIcon: true, showRemoveIcon: true }"
                @remove="() => handleRemove('coreSitePath')"
              >
                <template v-if="!fileList.coreSitePath.length">
                  <p class="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                  <p class="ant-upload-hint">支持 .xml 格式，文件小于 2MB</p>
                </template>
                <template #downloadIcon>
                  <DownloadOutlined
                    @click="handleDownload(formData.hadoopConfigs.coreSitePath, 'core-site.xml')"
                  ></DownloadOutlined>
                </template>
              </AUpload>
            </AFormItem>

            <AFormItem
              label="hdfs-site.xml"
              :name="['hadoopConfigs', 'hdfsSitePath']"
              :rules="[{ required: true, message: $t('page.config.cluster.form.hdfsSite'), trigger: 'change' }]"
            >
              <AUpload
                v-model:file-list="fileList.hdfsSitePath"
                :before-upload="file => beforeUpload(file, 'hdfsSitePath')"
                :max-count="1"
                accept=".xml"
                type="drag"
                :show-upload-list="{ showDownloadIcon: true, showRemoveIcon: true }"
                @remove="() => handleRemove('hdfsSitePath')"
              >
                <template v-if="!fileList.hdfsSitePath.length">
                  <p class="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                  <p class="ant-upload-hint">支持 .xml 格式，文件小于 2MB</p>
                </template>
                <template #downloadIcon>
                  <DownloadOutlined
                    @click="handleDownload(formData.hadoopConfigs.hdfsSitePath, 'hdfs-site.xml')"
                  ></DownloadOutlined>
                </template>
              </AUpload>
            </AFormItem>

            <AFormItem
              label="yarn-site.xml"
              :name="['hadoopConfigs', 'yarnSitePath']"
              :rules="[{ required: true, message: $t('page.config.cluster.form.yarnSite'), trigger: 'change' }]"
            >
              <AUpload
                v-model:file-list="fileList.yarnSitePath"
                :before-upload="file => beforeUpload(file, 'yarnSitePath')"
                :max-count="1"
                accept=".xml"
                type="drag"
                :show-upload-list="{ showDownloadIcon: true, showRemoveIcon: true }"
                @remove="() => handleRemove('yarnSitePath')"
              >
                <template v-if="!fileList.yarnSitePath.length">
                  <p class="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                  <p class="ant-upload-hint">支持 .xml 格式，文件小于 2MB</p>
                </template>
                <template #downloadIcon>
                  <DownloadOutlined
                    @click="handleDownload(formData.hadoopConfigs.yarnSitePath, 'yarn-site.xml')"
                  ></DownloadOutlined>
                </template>
              </AUpload>
            </AFormItem>

            <ADivider orientation="left">YARN 集群配置</ADivider>

            <AFormItem
              label="YARN ResourceManager 地址"
              :name="['hadoopConfigs', 'yarnResourceManagerUrl']"
              :rules="[{ required: true, message: '请填写YARN ResourceManager地址', trigger: 'blur' }]"
            >
              <AInput
                v-model:value="formData.hadoopConfigs.yarnResourceManagerUrl"
                placeholder="例如: http://ty-bigdata-01:8088"
                allow-clear
                autocomplete="off"
              />
            </AFormItem>
          </div>

          <AFormItem class="form-buttons">
            <div class="button-wrapper">
              <AButton type="primary" class="action-button" @click="handleSubmit">
                {{ $t('common.confirm') }}
              </AButton>
              <AButton class="action-button" @click="handleCancel">
                {{ $t('common.cancel') }}
              </AButton>
            </div>
          </AFormItem>
        </AForm>
      </ASpin>
    </ACard>
  </div>
</template>

<style scoped>
.cluster-form-container {
  padding: 24px;
}

.form-buttons {
  margin-top: 24px;
}

.button-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
}

.action-button {
  min-width: 100px;
}

.action-button + .action-button {
  margin-left: 24px; /* 增加按钮之间的间距 */
}

.ml-2 {
  margin-left: 8px;
}

.max-w-800px {
  max-width: 800px;
}

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.button {
  display: inline-flex;
  align-items: center;
}

.disabled-select {
  opacity: 0.8;
  cursor: not-allowed;
}
</style>

<style>
/* 全局样式：拖拽上传区域美化 */
.cluster-form-container .ant-upload-drag {
  background: #ffffff;
  border: 2px dashed #d9d9d9;
  border-radius: 10px;
  cursor: pointer;
  transition: border-color 0.3s;
  overflow: hidden;
}

.cluster-form-container .ant-upload-drag .ant-upload {
  padding: 16px 0;
}

.cluster-form-container .ant-upload-drag .ant-upload-btn {
  padding: 0;
}

.cluster-form-container .ant-upload-drag .ant-upload-drag-container {
  min-height: 148px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.cluster-form-container .ant-upload-drag.ant-upload-drag-hover:not(.ant-upload-disabled) {
  border: 2px dashed #1890ff;
  background: rgba(24, 144, 255, 0.06);
}

.cluster-form-container .ant-upload-drag:hover:not(.ant-upload-disabled) {
  border-color: #40a9ff;
}

/* 已上传文件列表样式 */
.cluster-form-container .ant-upload-list-item {
  border-radius: 2px;
  margin-top: 8px;
}
</style>
