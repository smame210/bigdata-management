<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { Upload as AUpload, message } from 'ant-design-vue';
import { InboxOutlined } from '@ant-design/icons-vue';
import { $t } from '@/locales';
import { addCluster, uploadFile } from '@/service/api';
import { useRouterPush } from '@/hooks/common/router';
import { useTabStore } from '@/store/modules/tab';

const { routerPushByKey } = useRouterPush();
const { removeTabByRouteName } = useTabStore();

// 表单引用
const formRef = ref();

// 表单数据
const formData = reactive<{
  clusterName: string;
  clusterType: Api.ClusterManage.ClusterType | undefined;
  clusterStatus: Api.ClusterManage.ClusterStatus;
  hadoopConfigs: Record<FileType, string>;
}>({
  clusterName: '',
  clusterType: undefined,
  clusterStatus: 1, // 默认启用
  hadoopConfigs: {
    coreSitePath: '',
    hdfsSitePath: '',
    yarnSitePath: '',
    yarnResourceManagerUrl: ''
  }
});

// 定义一个类型
type FileType = 'coreSitePath' | 'hdfsSitePath' | 'yarnSitePath' | 'yarnResourceManagerUrl';

// 声明文件列表
const fileList = reactive<Record<FileType, any[]>>({
  coreSitePath: [],
  hdfsSitePath: [],
  yarnSitePath: [],
  yarnResourceManagerUrl: []
});

// 上传加载状态
const uploading = ref(false);

// 配置类型选项
const configTypeOptions = [{ label: $t('page.config.cluster.cluster_type.yarn'), value: 'yarn' }];

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

// 集群类型变更处理
function handleClusterTypeChange(value: unknown) {
  if (value !== 'yarn') {
    // 清空YARN相关字段
    fileList.coreSitePath = [];
    fileList.hdfsSitePath = [];
    fileList.yarnSitePath = [];
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

    const { error } = await addCluster(submitData);
    if (error) {
      message.error('添加集群配置失败');
    } else {
      message.success('添加集群配置成功');
      removeTabByRouteName('config_cluster-add');
      routerPushByKey('config_cluster');
    }
  });
}

// 取消
function handleCancel() {
  removeTabByRouteName('config_cluster-add');
  routerPushByKey('config_cluster');
}
</script>

<template>
  <div class="cluster-form-container">
    <ACard :title="$t('page.config.cluster.addCluster')" :bordered="false" class="mx-auto max-w-800px w-full">
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
            autocomplete="off"
          />
        </AFormItem>

        <AFormItem :label="$t('page.config.cluster.clusterType')" name="clusterType">
          <ASelect
            v-model:value="formData.clusterType"
            :placeholder="$t('page.config.cluster.form.clusterType')"
            allow-clear
            @change="handleClusterTypeChange"
          >
            <ASelectOption v-for="item in configTypeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </ASelectOption>
          </ASelect>
        </AFormItem>

        <AFormItem :label="$t('page.config.cluster.clusterStatus')" name="clusterStatus">
          <ASelect
            v-model:value="formData.clusterStatus"
            :placeholder="$t('page.config.cluster.form.clusterStatus')"
            allow-clear
          >
            <ASelectOption :value="1">{{ $t('page.config.common.status.enable') }}</ASelectOption>
            <ASelectOption :value="0">{{ $t('page.config.common.status.disable') }}</ASelectOption>
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
              :show-upload-list="{ showRemoveIcon: true }"
              @remove="() => handleRemove('coreSitePath')"
            >
              <template v-if="!fileList.coreSitePath.length">
                <p class="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">支持 .xml 格式，文件小于 2MB</p>
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
              :show-upload-list="{ showRemoveIcon: true }"
              @remove="() => handleRemove('hdfsSitePath')"
            >
              <template v-if="!fileList.hdfsSitePath.length">
                <p class="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">支持 .xml 格式，文件小于 2MB</p>
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
              :show-upload-list="{ showRemoveIcon: true }"
              @remove="() => handleRemove('yarnSitePath')"
            >
              <template v-if="!fileList.yarnSitePath.length">
                <p class="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">支持 .xml 格式，文件小于 2MB</p>
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

        <AFormItem class="form-buttons flex justify-center">
          <AButton type="primary" class="mr-4" @click="handleSubmit">
            {{ $t('common.confirm') }}
          </AButton>
          <AButton @click="handleCancel">
            {{ $t('common.cancel') }}
          </AButton>
        </AFormItem>
      </AForm>
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
</style>

<style>
/* 全局样式：拖拽上传区域美化 */
.cluster-form-container .ant-upload-drag {
  background: rgb(var(--container-bg-color));
  border: 2px dashed rgba(var(--base-text-color), 0.18);
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
  border: 2px dashed rgb(var(--primary-color));
  background: rgba(var(--primary-color), 0.08);
}

.cluster-form-container .ant-upload-drag:hover:not(.ant-upload-disabled) {
  border-color: rgb(var(--primary-color));
}

/* 已上传文件列表样式 */
.cluster-form-container .ant-upload-list-item {
  border-radius: 2px;
  margin-top: 8px;
}
</style>
