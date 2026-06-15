<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { useAntdForm, useFormRules } from '@/hooks/common/form';
import {
  fetchAlertList,
  fetchAlertPolicyAdd,
  fetchAlertPolicyDetail,
  fetchAlertPolicyUpdate,
  fetchGetAllTask
} from '@/service/api';
import { enableTaskInstanceStatusRecord } from '@/constants/business';
import { $t } from '@/locales';

defineOptions({
  name: 'PolicyOperateDrawer'
});

interface Props {
  /** the type of operation */
  operateType: AntDesign.TableOperateType;
  /** the edit row data */
  rowData?: Api.AlertPolicyManage.AlertPolicy | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { formRef, validate } = useAntdForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<AntDesign.TableOperateType, string> = {
    add: $t('page.config.alert_policy.addAlertPolicy'),
    edit: $t('page.config.alert_policy.editAlertPolicy')
  };
  return titles[props.operateType];
});
// 定义单个条件的类型
interface Condition {
  key: string; // 条件键（如"任务状态"）
  operator: string; // 运算符（"="或"!="）
  value: string | number; // 条件值
}
type Model = Pick<
  Api.AlertPolicyManage.AlertPolicy,
  'name' | 'alertId' | 'alertName' | 'taskId' | 'taskName' | 'type'
> & {
  conditions: Condition[];
};

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    name: '',
    alertId: undefined,
    alertName: '',
    taskId: undefined,
    taskName: '',
    type: 'dingding',
    conditions: []
  };
}

type RuleKey = Extract<keyof Model, 'name' | 'alertId' | 'taskId' | 'conditions'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  alertId: defaultRequiredRule,
  taskId: defaultRequiredRule,
  conditions: defaultRequiredRule
};

const alertOptions = ref<CommonType.Option<number>[]>([]);
const taskOptions = ref<CommonType.Option<number>[]>([]);
const alertTypeMap: Record<Api.AlertManage.AlertType, string> = {
  dingding: '钉钉'
};

async function getAlertOptions() {
  const { error, data } = await fetchAlertList({ status: 1 as any });

  if (!error) {
    const options = (data || []).map(item => ({
      label: `${item.name}(${alertTypeMap[item.type] || item.type})`,
      value: item.id
    }));

    alertOptions.value = [...options];
  }
}

async function getTaskOptions() {
  const { error, data } = await fetchGetAllTask();

  if (!error) {
    const options = (data || []).map(item => ({
      label: item.taskName,
      value: item.id
    }));

    taskOptions.value = [...options];
  }
}

// 添加条件键选项
const conditionKeyOptions = ref([{ label: '任务状态', value: 'taskStatus' }]);
const operatorOptions = ref([
  { label: '等于', value: '=' },
  { label: '不等于', value: '!=' }
]);
const taskStatusOptions = ref<CommonType.Option<string | number>[]>([
  { label: $t(enableTaskInstanceStatusRecord[0]), value: 0 }, // 未启动
  { label: $t(enableTaskInstanceStatusRecord[1]), value: 1 }, // 进行中
  { label: $t(enableTaskInstanceStatusRecord[2]), value: 2 }, // 已完成
  { label: $t(enableTaskInstanceStatusRecord[3]), value: 3 }, // 失败
  { label: $t(enableTaskInstanceStatusRecord[4]), value: 4 } // 已停止
]);

// 添加新条件的方法
function addCondition() {
  model.value.conditions.push({
    key: 'taskStatus', // 默认选择"任务状态"
    operator: '=', // 默认选择"等于"
    value: '' // 默认空值
  });
}

// 删除条件的方法
function removeCondition(index: number) {
  model.value.conditions.splice(index, 1);
}

async function handleInitModel() {
  model.value = createDefaultModel();

  if (props.operateType === 'edit' && props.rowData) {
    const { error, data } = await fetchAlertPolicyDetail(props.rowData.id);
    if (!error && data) {
      model.value.name = data.name;
      model.value.alertId = data.alertId;
      model.value.alertName = data.alertName;
      model.value.taskId = data.taskId;
      model.value.taskName = data.taskName;
      model.value.type = data.type;
      model.value.conditions = (data.conditions || []).map((condition: Condition) => ({
        key: condition.key,
        operator: condition.operator,
        value: condition.value
      }));
    } else {
      window.$message?.error('获取策略详情失败');
    }
  }
}

function closeDrawer() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();

  // 将条件数组转换为后端需要的格式（如果需要）
  const formattedConditions = model.value.conditions.map((condition: Condition) => ({
    key: condition.key,
    operator: condition.operator,
    value: condition.value
  }));

  // 构建提交数据
  const submitData: Partial<Api.AlertPolicyManage.AlertPolicy> = {
    name: model.value.name,
    alertId: model.value.alertId,
    taskId: model.value.taskId,
    conditions: formattedConditions
  };

  if (props.operateType === 'add') {
    const { error } = await fetchAlertPolicyAdd(submitData);
    if (!error) {
      window.$message?.success($t('common.addSuccess'));
      closeDrawer();
      emit('submitted');
    } else {
      window.$message?.error('新增策略失败');
    }
  } else if (props.operateType === 'edit' && props.rowData) {
    const { error } = await fetchAlertPolicyUpdate(props.rowData.id, submitData);
    if (!error) {
      window.$message?.success($t('common.updateSuccess'));
      closeDrawer();
      emit('submitted');
    } else {
      window.$message?.error('更新策略失败');
    }
  }
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    // resetFields();
    formRef.value?.clearValidate?.();
    getAlertOptions();
    getTaskOptions();

    // 如果是新增模式，默认添加一个空条件
    if (props.operateType === 'add' && model.value.conditions.length === 0) {
      addCondition();
    }
  }
});
</script>

<template>
  <ADrawer v-model:open="visible" :title="title" :width="460">
    <AForm ref="formRef" layout="vertical" :model="model" :rules="rules">
      <AFormItem :label="$t('page.config.alert_policy.alertPolicyName')" name="name">
        <AInput v-model:value="model.name" :placeholder="$t('page.config.alert_policy.form.name')" autocomplete="off" />
      </AFormItem>
      <AFormItem :label="$t('page.config.alert.alertName')" name="alertId">
        <ASelect
          v-model:value="model.alertId"
          :options="alertOptions"
          :placeholder="$t('page.config.alert.form.name')"
        />
      </AFormItem>
      <AFormItem :label="$t('page.manage.task.taskName')" name="taskId">
        <ASelect
          v-model:value="model.taskId"
          :options="taskOptions"
          :placeholder="$t('page.manage.task.form.taskName')"
        />
      </AFormItem>
      <AFormItem :label="$t('page.config.alert_policy.conditions')" name="conditions">
        <div v-for="(condition, index) in model.conditions" :key="index" class="condition-item">
          <div class="condition-row">
            <AFormItem :name="['conditions', index, 'key']" :rules="[defaultRequiredRule]" no-style>
              <ASelect
                v-model:value="condition.key"
                :options="conditionKeyOptions"
                class="w-1/3"
                :placeholder="$t('page.config.alert_policy.form.conditionKey')"
              />
            </AFormItem>

            <!-- operator -->
            <AFormItem :name="['conditions', index, 'operator']" :rules="[defaultRequiredRule]" no-style>
              <ASelect
                v-model:value="condition.operator"
                :options="operatorOptions"
                class="w-1/3"
                :placeholder="$t('page.config.alert_policy.form.operator')"
              />
            </AFormItem>

            <!-- value -->
            <AFormItem :name="['conditions', index, 'value']" :rules="[defaultRequiredRule]" no-style>
              <ASelect
                v-model:value="condition.value"
                :options="taskStatusOptions"
                class="w-1/3"
                :placeholder="$t('page.config.alert_policy.form.conditionValue')"
              />
            </AFormItem>

            <AButton v-if="index > 0" type="text" danger @click="removeCondition(index)">
              <template #icon><DeleteOutlined /></template>
            </AButton>
          </div>
        </div>

        <div class="add-condition">
          <AButton type="dashed" block @click="addCondition">
            <template #icon><PlusOutlined /></template>
            添加条件
          </AButton>
        </div>
      </AFormItem>
    </AForm>
    <template #footer>
      <ASpace :size="16">
        <AButton @click="closeDrawer">{{ $t('common.cancel') }}</AButton>
        <AButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</AButton>
      </ASpace>
    </template>
  </ADrawer>
</template>

<style scoped>
.condition-item {
  margin-bottom: 8px;
}

.condition-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.add-condition {
  margin-top: 8px;
}
</style>
