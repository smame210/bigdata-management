<script setup lang="tsx">
import { computed, onMounted, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { CronExpressionParser } from 'cron-parser';
import dayjs from 'dayjs';
import { fetchScheduleById, updateSchedule } from '@/service/api';
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

// 定义表单数据
const formRef = ref();
const formState = reactive<Partial<Api.ScheduleManage.Schedule>>({
  taskId: 0,
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
const selectedTask = ref<Api.TaskManage.Task>();
const loading = ref(false);
const initialLoading = ref(true);
// cron对应的未来5个时间
const cronPreview = ref<string[]>([]);

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

// 获取调度详情
async function fetchScheduleDetail() {
  initialLoading.value = true;
  try {
    const { data } = await fetchScheduleById(Number(props.id));
    if (data) {
      // 填充表单数据
      Object.assign(formState, data);
      formState.startTime = dayjs(data.startTime, 'YYYY-MM-DD HH:mm:ss') as unknown as string;
      if (formState.endTime) {
        formState.endTime = dayjs(data.endTime, 'YYYY-MM-DD HH:mm:ss') as unknown as string;
      }

      // 如果是自定义频次，预览CRON表达式
      if (formState.scheduleFrequency === 'CUSTOM' && formState.cronExpression) {
        listNext();
      }
    }
  } catch (error) {
    console.error('获取调度详情失败', error);
    message.error('获取调度详情失败');
  } finally {
    initialLoading.value = false;
  }
}

// 根据任务类型计算可用的调度频次
const availableFrequencies = computed(() => {
  const taskMode = selectedTask.value?.taskMode;

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

// 调度频次变更时
function handleFrequencyChange(frequency: any) {
  formState.scheduleFrequency = frequency;

  // 清空自定义CRON表达式
  if (frequency !== 'CUSTOM') {
    cronPreview.value = [];
    formState.cronExpression = '';
  }
}

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
  formRef.value.validate().then(async () => {
    loading.value = true;
    try {
      if (!formState.id) {
        message.error('调度ID无效');
        return;
      }
      const payload = {
        ...formState,
        startTime: formState.startTime ? dayjs(formState.startTime).format('YYYY-MM-DD HH:mm:ss') : '',
        endTime: formState.endTime ? dayjs(formState.endTime).format('YYYY-MM-DD HH:mm:ss') : ''
      };
      const { error } = await updateSchedule(formState.id, payload);
      if (error) {
        message.error('更新调度任务失败');
        return;
      }

      message.success('更新调度任务成功');
      removeActiveTab();
      routerPushByKey('manage_schedule');
    } catch (error) {
      console.error('表单验证失败:', error);
    } finally {
      loading.value = false;
    }
  });
}

// 取消操作
function handleCancel() {
  removeActiveTab();
  routerPushByKey('manage_schedule');
}

// 页面加载时获取任务列表和调度详情
onMounted(() => {
  fetchScheduleDetail();
});
</script>

<template>
  <div class="schedule-add-container">
    <ACard :title="$t('page.manage.schedule.editSchedule')" :bordered="false" class="w-full">
      <AForm
        ref="formRef"
        :model="formState"
        :rules="formRules"
        layout="vertical"
        :loading="initialLoading"
        class="mx-auto max-w-800px"
      >
        <!-- 选择任务 -->
        <AFormItem name="taskId" :label="$t('page.manage.task.taskName')">
          <AInput v-model:value="formState.taskName" :disabled="true" class="disabled-field" />
        </AFormItem>

        <!-- 调度名称 -->
        <AFormItem name="scheduleName" :label="$t('page.manage.schedule.scheduleName')">
          <AInput
            v-model:value="formState.scheduleName"
            :placeholder="$t('page.manage.schedule.form.scheduleName')"
            autocomplete="off"
          />
        </AFormItem>

        <!-- 调度频次 -->
        <AFormItem name="scheduleFrequency" :label="$t('page.manage.schedule.scheduleFrequency')">
          <ASelect
            v-model:value="formState.scheduleFrequency"
            :options="availableFrequencies"
            @change="handleFrequencyChange"
          />
        </AFormItem>

        <!-- 自定义CRON表达式 -->
        <AFormItem v-show="showCronExpressionInput" name="cronExpression" :label="$t('page.manage.schedule.cron')">
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
        <AFormItem name="startTime" :label="$t('page.manage.schedule.startTime')">
          <ADatePicker
            v-model:value="formState.startTime"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            :placeholder="$t('page.manage.schedule.form.startTime')"
            class="width: 100%"
          />
        </AFormItem>

        <!-- 结束时间 -->
        <AFormItem name="endTime" :label="$t('page.manage.schedule.endTime')">
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
