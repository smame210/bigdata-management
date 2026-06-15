<script setup lang="tsx">
import dayjs from 'dayjs';
import { computed, onMounted, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { CronExpressionParser } from 'cron-parser';
import { createSchedule, fetchGetTaskList } from '@/service/api';
import { $t } from '@/locales';
import { useRouterPush } from '@/hooks/common/router';
import { useTabStore } from '@/store/modules/tab';

const { routerPushByKey } = useRouterPush();
const { removeActiveTab } = useTabStore();

// 定义表单数据
const formRef = ref();
const formState = reactive<Partial<Api.ScheduleManage.Schedule>>({
  taskId: undefined,
  scheduleName: '',
  scheduleStatus: 1,
  scheduleFrequency: 'ONCE',
  cronExpression: '',
  startTime: '',
  endTime: '',
  maxRetryTimes: 0,
  retryInterval: 0,
  timeoutSeconds: 0,
  dependencyTaskIds: ''
});

// 获取任务列表
const taskList = ref<Api.TaskManage.Task[]>([]);
const selectedTask = ref<Partial<Api.TaskManage.Task>>({});
const loading = ref(false);

// 调度频次选项
const frequencyOptions = [
  { label: '单次', value: 'ONCE' },
  { label: '每分钟', value: 'MINUTELY' },
  { label: '每小时', value: 'HOURLY' },
  { label: '每天', value: 'DAILY' },
  { label: '每周', value: 'WEEKLY' },
  { label: '每月', value: 'MONTHLY' },
  { label: '自定义', value: 'CUSTOM' }
];

// 根据任务类型计算可用的调度频次
const availableFrequencies = computed(() => {
  const taskMode = selectedTask.value.taskMode;

  // 如果是流处理任务，只能选择单次执行
  if (taskMode === 'streaming') {
    return frequencyOptions.filter(option => option.value === 'ONCE');
  }

  // 批处理任务可以选择所有频次
  return frequencyOptions;
});

// 是否显示CRON表达式输入框
const showCronExpressionInput = computed(() => {
  return formState.scheduleFrequency === 'CUSTOM';
});

// 表单校验规则
const cronValidator = (_: App.Global.FormRule, value: string) => {
  try {
    CronExpressionParser.parse(value.trim());
    return Promise.resolve();
  } catch (e: any) {
    return Promise.reject(e.message);
  }
};

const formRules = computed<Record<string, App.Global.FormRule[]>>(() => {
  const rules: Record<string, App.Global.FormRule[]> = {
    taskId: [{ required: true, message: $t('page.manage.schedule.form.taskName'), trigger: 'change' }],
    scheduleName: [{ required: true, message: $t('page.manage.schedule.form.scheduleName'), trigger: 'blur' }],
    scheduleFrequency: [
      { required: true, message: $t('page.manage.schedule.form.scheduleFrequency'), trigger: 'change' }
    ],
    cronExpression: [
      {
        required: formState.scheduleFrequency === 'CUSTOM',
        validator: cronValidator,
        message: $t('page.manage.schedule.form.cron'),
        trigger: 'change'
      }
    ],
    startTime: [{ required: true, message: $t('page.manage.schedule.form.startTime'), trigger: 'change' }]
  };
  return rules;
});

// 获取任务列表
async function fetchTasks() {
  loading.value = true;
  try {
    const { data } = await fetchGetTaskList({
      current: 1,
      size: -1
    });

    if (data && data.records) {
      taskList.value = data.records;
    }
  } catch {
    message.error('获取任务列表失败');
  } finally {
    loading.value = false;
  }
}

// 任务选择变更时
function handleTaskChange(value: unknown) {
  const taskId = typeof value === 'number' ? value : undefined;
  formState.taskId = taskId;

  // 查找并设置当前选中的任务
  const task = taskList.value.find(item => item.id === taskId);
  selectedTask.value = task || {};

  // 如果是流处理任务，自动设置为单次调度
  if (task && task.taskMode === 'streaming') {
    formState.scheduleFrequency = 'ONCE';
  }
}

// 调度频次变更时
function handleFrequencyChange(frequency: any) {
  formState.scheduleFrequency = frequency;

  // 清空自定义CRON表达式
  if (frequency !== 'CUSTOM') {
    formState.cronExpression = '';
  }
}

// cron对应的未来5个时间
const cronPreview = ref<string[]>([]);

function listNext() {
  cronPreview.value = [];
  const expr = (formState.cronExpression || '').trim();
  if (!expr) return;

  try {
    const interval = CronExpressionParser.parse(expr);
    const nextFiveDates = interval.take(5);
    const times: string[] = [];
    nextFiveDates.map(date => times.push(date.toString()));
    cronPreview.value = times;
  } catch (e: any) {
    console.error(e);
  }
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value.validate();

    loading.value = true;
    const payload = {
      ...formState,
      startTime: formState.startTime ? dayjs(formState.startTime).format('YYYY-MM-DD HH:mm:ss') : '',
      endTime: formState.endTime ? dayjs(formState.endTime).format('YYYY-MM-DD HH:mm:ss') : ''
    };
    const { error } = await createSchedule(payload);

    if (error) {
      message.error('创建调度任务失败');
      return;
    }

    message.success('创建调度任务成功');
    removeActiveTab();
    routerPushByKey('manage_schedule');
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    loading.value = false;
  }
}

// 取消操作
function handleCancel() {
  removeActiveTab();
  routerPushByKey('manage_schedule');
}

// 页面加载时获取任务列表
onMounted(() => {
  fetchTasks();
});
</script>

<template>
  <div class="schedule-add-container">
    <ACard :title="$t('page.manage.schedule.addSchedule')" :bordered="false" class="w-full">
      <AForm ref="formRef" :model="formState" :rules="formRules" layout="vertical" class="mx-auto max-w-800px">
        <!-- 调度名称 -->
        <AFormItem name="scheduleName" :label="$t('page.manage.schedule.scheduleName')">
          <AInput
            v-model:value="formState.scheduleName"
            :placeholder="$t('page.manage.schedule.form.scheduleName')"
            autocomplete="off"
          />
        </AFormItem>

        <!-- 选择任务 -->
        <AFormItem name="taskId" :label="$t('page.manage.task.taskName')">
          <ASelect
            v-model:value="formState.taskId"
            :loading="loading"
            :placeholder="$t('page.manage.schedule.form.taskName')"
            allow-clear
            @change="handleTaskChange"
          >
            <ASelectOption v-for="task in taskList" :key="task.id" :value="task.id">
              {{ task.taskName }} ({{ task.taskMode === 'batch' ? '批处理' : '流处理' }})
            </ASelectOption>
          </ASelect>
        </AFormItem>

        <!-- 调度频次 -->
        <AFormItem
          name="scheduleFrequency"
          :label="$t('page.manage.schedule.scheduleFrequency')"
          tooltip="流任务只支持单次调度"
        >
          <ASelect
            v-model:value="formState.scheduleFrequency"
            :options="availableFrequencies"
            @change="handleFrequencyChange"
          />
        </AFormItem>

        <!-- 自定义CRON表达式 -->
        <AFormItem v-if="showCronExpressionInput" name="cronExpression" :label="$t('page.manage.schedule.cron')">
          <AInput
            v-model:value="formState.cronExpression"
            :placeholder="$t('page.manage.schedule.form.cron')"
            autocomplete="off"
            @blur="listNext"
          />
          <AAlert v-if="cronPreview.length" message="未来执行时间" type="info" show-icon>
            <template #description>
              <div class="cron-preview-text">{{ cronPreview.join('\n') }}</div>
            </template>
          </AAlert>
        </AFormItem>

        <!-- 开始时间 -->
        <AFormItem
          name="startTime"
          :label="$t('page.manage.schedule.startTime')"
          tooltip="如果开始时间小于当前时间，启用时将立即调度一次"
        >
          <ADatePicker
            v-model:value="formState.startTime"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            :placeholder="$t('page.manage.schedule.form.startTime')"
            class="100% width:"
          />
        </AFormItem>

        <!-- 结束时间 -->
        <AFormItem
          name="endTime"
          :label="$t('page.manage.schedule.endTime')"
          tooltip="到达结束时间只会终止调度，不会终止进行中的任务"
        >
          <ADatePicker
            v-model:value="formState.endTime"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            :placeholder="$t('page.manage.schedule.form.endTime')"
            class="width: 100%"
          />
        </AFormItem>

        <!-- 最大重试次数 -->
        <AFormItem name="maxRetryTimes" :label="$t('page.manage.schedule.maxRetryTimes')">
          <AInputNumber v-model:value="formState.maxRetryTimes" :min="0" class="width: 100%" />
        </AFormItem>

        <!-- 重试间隔 -->
        <AFormItem name="retryInterval" :label="$t('page.manage.schedule.retryInterval')">
          <AInputNumber v-model:value="formState.retryInterval" :min="0" addon-after="秒" class="width: 100%" />
        </AFormItem>

        <!-- 任务超时时间 -->
        <AFormItem name="timeoutSeconds" :label="$t('page.manage.schedule.timeout')">
          <AInputNumber v-model:value="formState.timeoutSeconds" :min="0" addon-after="秒" class="width: 100%" />
        </AFormItem>

        <!-- 依赖任务 -->
        <!--        <AFormItem name="dependencyTaskIds" :label="$t('page.manage.schedule.dependencyTaskIds')">-->
        <!--          <AInput-->
        <!--            v-model:value="formState.dependencyTaskIds"-->
        <!--            :placeholder="$t('page.manage.schedule.dependencyTaskIdsPlaceholder')"-->
        <!--          />-->
        <!--        </AFormItem>-->

        <!-- 表单操作按钮 -->
        <AFormItem>
          <div class="flex justify-center gap-16px">
            <AButton @click="handleCancel">
              {{ $t('common.cancel') }}
            </AButton>
            <AButton type="primary" :loading="loading" @click="handleSubmit">
              {{ $t('common.confirm') }}
            </AButton>
          </div>
        </AFormItem>
      </AForm>
    </ACard>
  </div>
</template>

<style scoped>
.schedule-add-container {
  padding: 24px;
}

.cron-preview-text {
  white-space: pre-line;
}
</style>
