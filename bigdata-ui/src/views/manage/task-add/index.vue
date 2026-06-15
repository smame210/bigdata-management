<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { Upload as AUpload, message } from 'ant-design-vue';
import { DeleteOutlined, InboxOutlined, PlusOutlined } from '@ant-design/icons-vue';
import type { DefaultOptionType, SelectValue } from 'ant-design-vue/es/select';
import { $t } from '@/locales';
import { fetchCreateTask, fetchGetClusterList, fetchTaskTypeOptions, uploadFile } from '@/service/api';
import { useRouterPush } from '@/hooks/common/router';
import { useTabStore } from '@/store/modules/tab';

const { routerPushByKey } = useRouterPush();
const { removeTabByRouteName } = useTabStore();

type ClusterOption = {
  label: string;
  value: number;
  clusterType: Api.ClusterManage.ClusterType;
};

type TaskTypeOption = {
  label: string;
  value: Api.TaskManage.TaskType;
};

const taskTypeLabelMap: Record<Api.TaskManage.TaskType, string> = {
  session: 'Session',
  'pre-job': 'Per-Job',
  application: 'Application'
};

// 表单引用
const formRef = ref();

// 表单数据 - 基本信息
type TaskFormData = {
  taskName: string;
  engineType: Api.TaskManage.EngineType | undefined;
  taskMode: Api.TaskManage.TaskMode | undefined;
  clusterId: number | undefined;
  flinkConfig: {
    version: string;
    taskType: string;
    jobManagerUrl: string;
    jarPath: string;
    libPath: string;
    flinkConfPath: string;
    logConfPath: string;
    mainClass: string;
    args: Record<string, string>;
    parallelism: number;
    jobManagerMemory: number;
    taskManagerMemory: number;
    taskManagerSlots: number;
    taskManagerMemoryManagedFraction: number;
  };
};

const formData = reactive<TaskFormData>({
  taskName: '',
  engineType: undefined, // flink、spark
  taskMode: undefined, // 任务模式
  clusterId: undefined, // 集群选择
  // Flink 配置
  flinkConfig: {
    version: '1.17.2',
    taskType: 'application', // session/pre-job/application
    jobManagerUrl: '',
    jarPath: '',
    libPath: '',
    flinkConfPath: '',
    logConfPath: '',
    mainClass: '',
    args: {} as Record<string, string>,
    parallelism: 1,
    jobManagerMemory: 1024,
    taskManagerMemory: 1024,
    taskManagerSlots: 1,
    taskManagerMemoryManagedFraction: 0.4
  }
});

// 引擎类型选项
const engineTypeOptions = [
  { label: 'Flink', value: 'flink' },
  { label: 'Spark', value: 'spark' }
];

// 任务模式选项
const taskModeOptions = [
  { label: $t('page.manage.common.streaming'), value: 'streaming' },
  { label: $t('page.manage.common.batch'), value: 'batch' }
];

type FileType = 'flinkConf' | 'logConf';

// 集群选项
const clusterOptions = ref<ClusterOption[]>([]);
const flinkTaskTypeOptions = ref<TaskTypeOption[]>([]);

// 加载中状态
const loading = ref(false);
const uploading = ref(false);

// 文件上传相关
const fileList = reactive<Record<FileType, any[]>>({
  flinkConf: [],
  logConf: []
});

// 获取集群列表
async function fetchClusterOptions() {
  const params = reactive<Api.ClusterManage.ClusterSearchParams>({
    clusterStatus: 1
  });
  const { error, response, data } = await fetchGetClusterList(params);
  if (!error) {
    clusterOptions.value = data.map(item => ({
      label: item.clusterName,
      value: item.id,
      clusterType: item.clusterType
    }));
  } else {
    message.error(response?.data?.msg || '获取集群列表失败');
  }
}

// 初始化获取集群列表
fetchClusterOptions();

function getSelectedClusterType() {
  return clusterOptions.value.find(item => item.value === Number(formData.clusterId))?.clusterType;
}

const selectedClusterType = computed(() => getSelectedClusterType());

async function syncTaskTypeOptions() {
  const clusterType = selectedClusterType.value;
  if (!clusterType || formData.engineType !== 'flink') {
    flinkTaskTypeOptions.value = [];
    formData.flinkConfig.taskType = 'application';
    return;
  }

  const { error, response, data } = await fetchTaskTypeOptions({
    clusterType,
    engineType: formData.engineType
  });
  if (error) {
    flinkTaskTypeOptions.value = [];
    message.error(response?.data?.msg || '获取任务类型失败');
    return;
  }

  flinkTaskTypeOptions.value = data.map(value => ({
    value,
    label: taskTypeLabelMap[value] ?? value
  }));

  if (!flinkTaskTypeOptions.value.some(item => item.value === formData.flinkConfig.taskType)) {
    formData.flinkConfig.taskType = flinkTaskTypeOptions.value[0]?.value ?? 'application';
  }
}

watch(
  () => [formData.engineType, formData.clusterId],
  async () => {
    await syncTaskTypeOptions();
  },
  { immediate: true }
);

// 表单校验规则
const formRules = computed<Record<string, App.Global.FormRule[]>>(() => {
  const rules: Record<string, App.Global.FormRule[]> = {
    taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
    engineType: [{ required: true, message: '请选择引擎类型', trigger: 'change' }],
    taskMode: [{ required: true, message: '请选择任务模式', trigger: 'change' }],
    clusterId: [{ required: true, message: '请选择集群', trigger: 'change' }]
  };
  return rules;
});

// 引擎类型变更处理
function handleEngineTypeChange(value: SelectValue, _option: DefaultOptionType | DefaultOptionType[]) {
  // 清空引擎特定的配置
  if (value === 'flink') {
    formData.flinkConfig = {
      version: '1.17.2',
      taskType: 'application',
      jobManagerUrl: '',
      jarPath: '',
      libPath: '',
      flinkConfPath: '',
      logConfPath: '',
      mainClass: '',
      args: {} as Record<string, string>,
      parallelism: 1,
      jobManagerMemory: 1024,
      taskManagerMemory: 1024,
      taskManagerSlots: 1,
      taskManagerMemoryManagedFraction: 0.4
    };
  }
}

// 文件上传前验证
async function beforeUpload(file: File, fileType: FileType) {
  const isAllowed = file.name.endsWith('.xml') || file.name.endsWith('.properties') || file.name.endsWith('.yaml');
  if (!isAllowed) {
    message.error('XML、Properties或YAML文件!');
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

    // 更新对应的文件路径
    if (fileType === 'flinkConf') {
      formData.flinkConfig.flinkConfPath = data || '';
    } else if (fileType === 'logConf') {
      formData.flinkConfig.logConfPath = data || '';
    }

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

  if (fileType === 'flinkConf') {
    formData.flinkConfig.flinkConfPath = '';
  } else if (fileType === 'logConf') {
    formData.flinkConfig.logConfPath = '';
  }
}

// 添加参数
function handleAddArg() {
  const tempKey = `__new_${Date.now()}_${Math.random().toString(36).substr(2, 5)}`;
  formData.flinkConfig.args[tempKey] = '';
}

// 删除参数
function handleRemoveArg(key: string) {
  const { [key]: _, ...rest } = formData.flinkConfig.args;
  formData.flinkConfig.args = rest;
}

// 修改参数名
function handleArgKeyChange(oldKey: string, newKey: string) {
  if (oldKey === newKey) return;
  const value = formData.flinkConfig.args[oldKey];
  const { [oldKey]: _, ...rest } = formData.flinkConfig.args;
  formData.flinkConfig.args = rest;
  if (newKey.trim()) {
    formData.flinkConfig.args[newKey.trim()] = value;
  } else {
    const tempKey = `__new_${Date.now()}_${Math.random().toString(36).substr(2, 5)}`;
    formData.flinkConfig.args[tempKey] = value;
  }
}

// 提交表单
function handleSubmit() {
  formRef.value.validate().then(async () => {
    loading.value = true;

    // 构建提交数据
    const submitData: Partial<Api.TaskManage.Task> = {
      taskName: formData.taskName,
      engineType: formData.engineType,
      taskMode: formData.taskMode,
      clusterId: Number(formData.clusterId)
    };

    // 根据引擎类型添加特定配置
    if (formData.engineType === 'flink') {
      // 过滤掉未填写参数名的条目
      const filteredArgs: Record<string, string> = {};
      Object.entries(formData.flinkConfig.args).forEach(([key, value]) => {
        if (!key.startsWith('__new_')) {
          filteredArgs[key] = value;
        }
      });
      formData.flinkConfig.args = filteredArgs;
      submitData.config = formData.flinkConfig;
    }

    const { error, response } = await fetchCreateTask(submitData);
    if (!error) {
      message.success('添加任务成功');
      loading.value = false;
      removeTabByRouteName('manage_task-add');
      routerPushByKey('manage_task');
    } else {
      loading.value = false;
      message.error(response?.data?.msg || '添加任务失败');
    }
  });
}

// 取消
function handleCancel() {
  removeTabByRouteName('manage_task-add');
  routerPushByKey('manage_task');
}
</script>

<template>
  <div class="task-form-container">
    <ACard :title="$t('page.manage.task.addTask')" :bordered="false" class="mx-auto max-w-800px w-full">
      <AForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
        :label-col="{ span: 24 }"
        :wrapper-col="{ span: 24 }"
      >
        <!-- 基本信息部分 -->
        <ADivider orientation="left">基本信息</ADivider>

        <AFormItem label="任务名称" name="taskName">
          <AInput v-model:value="formData.taskName" placeholder="请输入任务名称" allow-clear autocomplete="off" />
        </AFormItem>

        <AFormItem label="引擎类型" name="engineType">
          <ASelect
            v-model:value="formData.engineType"
            placeholder="请选择引擎类型"
            allow-clear
            @change="handleEngineTypeChange"
          >
            <ASelectOption v-for="item in engineTypeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </ASelectOption>
          </ASelect>
        </AFormItem>

        <AFormItem label="任务模式" name="taskMode">
          <ASelect v-model:value="formData.taskMode" placeholder="请选择任务模式" allow-clear>
            <ASelectOption v-for="item in taskModeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </ASelectOption>
          </ASelect>
        </AFormItem>

        <AFormItem label="选择集群" name="clusterId">
          <ASelect v-model:value="formData.clusterId" placeholder="请选择集群" allow-clear>
            <ASelectOption v-for="item in clusterOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </ASelectOption>
          </ASelect>
        </AFormItem>

        <!-- Flink 配置部分 -->
        <template v-if="formData.engineType === 'flink'">
          <ADivider orientation="left">Flink 配置</ADivider>

          <AFormItem label="Flink 版本">
            <AInput
              v-model:value="formData.flinkConfig.version"
              placeholder="请输入 Flink 版本，默认: 1.17.2"
              allow-clear
              autocomplete="off"
            />
          </AFormItem>

          <AFormItem
            label="任务类型"
            :name="['flinkConfig', 'taskType']"
            :rules="[{ required: true, message: '请选择任务类型', trigger: 'change' }]"
          >
            <ASelect v-model:value="formData.flinkConfig.taskType" placeholder="请选择任务类型" allow-clear>
              <ASelectOption v-for="item in flinkTaskTypeOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </ASelectOption>
            </ASelect>
          </AFormItem>

          <AFormItem
            label="JAR 文件路径"
            :name="['flinkConfig', 'jarPath']"
            :rules="[{ required: true, message: '请输入JAR文件地址', trigger: 'blur' }]"
          >
            <AInput
              v-model:value="formData.flinkConfig.jarPath"
              placeholder="请输入 JAR 文件路径，例如: hdfs://localhost:9000/flink/demo.jar"
              allow-clear
              autocomplete="off"
            />
          </AFormItem>

          <AFormItem
            label="依赖地址"
            :name="['flinkConfig', 'libPath']"
            :rules="[{ required: true, message: '请输入依赖地址', trigger: 'blur' }]"
          >
            <AInput
              v-model:value="formData.flinkConfig.libPath"
              placeholder="请输入依赖地址，例如: hdfs://localhost:9000/flink/lib"
              allow-clear
              autocomplete="off"
            />
          </AFormItem>

          <AFormItem label="Flink 配置文件">
            <AUpload
              v-model:file-list="fileList.flinkConf"
              :before-upload="file => beforeUpload(file, 'flinkConf')"
              :max-count="1"
              accept=".yaml"
              type="drag"
              :show-upload-list="{ showRemoveIcon: true }"
              @remove="() => handleRemove('flinkConf')"
            >
              <template v-if="!fileList.flinkConf.length">
                <p class="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">支持 .yaml 格式，文件小于 2MB</p>
              </template>
            </AUpload>
          </AFormItem>

          <AFormItem label="Log4j 配置文件">
            <AUpload
              v-model:file-list="fileList.logConf"
              :before-upload="file => beforeUpload(file, 'logConf')"
              :max-count="1"
              accept=".xml,.properties"
              type="drag"
              :show-upload-list="{ showRemoveIcon: true }"
              @remove="() => handleRemove('logConf')"
            >
              <template v-if="!fileList.logConf.length">
                <p class="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p class="ant-upload-text">点击或拖拽文件到此处上传</p>
                <p class="ant-upload-hint">支持 .xml、.properties 格式，文件小于 2MB</p>
              </template>
            </AUpload>
          </AFormItem>

          <AFormItem
            label="主类"
            :name="['flinkConfig', 'mainClass']"
            :rules="[{ required: true, message: '请输入主类', trigger: 'blur' }]"
          >
            <AInput
              v-model:value="formData.flinkConfig.mainClass"
              placeholder="请输入主类名称，例如: org.example.MainClass"
              allow-clear
            />
          </AFormItem>

          <AFormItem label="程序参数">
            <div class="flex flex-col gap-8px">
              <div v-for="(_, key) in formData.flinkConfig.args" :key="key" class="flex items-center gap-8px">
                <AInput
                  :value="key.startsWith('__new_') ? '' : key"
                  placeholder="参数名"
                  class="w-160px"
                  @update:value="(newKey: string) => handleArgKeyChange(key, newKey)"
                />
                <AInput v-model:value="formData.flinkConfig.args[key]" placeholder="参数值" class="flex-1" />
                <AButton type="text" danger @click="handleRemoveArg(key)">
                  <template #icon>
                    <DeleteOutlined />
                  </template>
                </AButton>
              </div>
              <AButton type="dashed" class="w-full" @click="handleAddArg">
                <template #icon>
                  <PlusOutlined />
                </template>
                添加参数
              </AButton>
            </div>
          </AFormItem>

          <ARow :gutter="16">
            <ACol :span="12">
              <AFormItem label="并行度">
                <AInputNumber v-model:value="formData.flinkConfig.parallelism" :min="1" class="100% width:" />
              </AFormItem>
            </ACol>
            <ACol :span="12">
              <AFormItem label="TaskManager Slots">
                <AInputNumber v-model:value="formData.flinkConfig.taskManagerSlots" :min="1" class="width: 100%" />
              </AFormItem>
            </ACol>
          </ARow>

          <ARow :gutter="16">
            <ACol :span="12">
              <AFormItem label="JobManager 内存 (MB)">
                <AInputNumber
                  v-model:value="formData.flinkConfig.jobManagerMemory"
                  :min="512"
                  :step="512"
                  class="width: 100%"
                />
              </AFormItem>
            </ACol>
            <ACol :span="12">
              <AFormItem label="TaskManager 内存 (MB)">
                <AInputNumber
                  v-model:value="formData.flinkConfig.taskManagerMemory"
                  :min="512"
                  :step="512"
                  class="width: 100%"
                />
              </AFormItem>
            </ACol>
          </ARow>

          <ARow :gutter="16">
            <ACol :span="12">
              <AFormItem label="TaskManager Managed Memory Fraction">
                <AInputNumber
                  v-model:value="formData.flinkConfig.taskManagerMemoryManagedFraction"
                  :min="0"
                  :max="1"
                  :step="0.1"
                  class="width: 100%"
                />
              </AFormItem>
            </ACol>
          </ARow>
        </template>

        <!-- Spark 配置部分（暂时为空） -->
        <template v-else-if="formData.engineType === 'spark'">
          <ADivider orientation="left">Spark 配置</ADivider>
          <AAlert message="Spark 配置暂未实现" type="info" />
        </template>

        <!-- 提交按钮 -->
        <AFormItem class="form-buttons flex justify-center">
          <AButton type="primary" class="mr-4" :loading="loading" @click="handleSubmit">
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
</style>

<style>
/* 全局样式：拖拽上传区域美化 */
.task-form-container .ant-upload-drag {
  background: #ffffff;
  border: 2px dashed #d9d9d9;
  border-radius: 10px;
  cursor: pointer;
  transition: border-color 0.3s;
  overflow: hidden;
}

.task-form-container .ant-upload-drag .ant-upload {
  padding: 16px 0;
}

.task-form-container .ant-upload-drag .ant-upload-btn {
  padding: 0;
}

.task-form-container .ant-upload-drag .ant-upload-drag-container {
  min-height: 148px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.task-form-container .ant-upload-drag.ant-upload-drag-hover:not(.ant-upload-disabled) {
  border: 2px dashed #1890ff;
  background: rgba(24, 144, 255, 0.06);
}

.task-form-container .ant-upload-drag:hover:not(.ant-upload-disabled) {
  border-color: #40a9ff;
}

/* 已上传文件列表样式 */
.task-form-container .ant-upload-list-item {
  border-radius: 2px;
  margin-top: 8px;
}
</style>
