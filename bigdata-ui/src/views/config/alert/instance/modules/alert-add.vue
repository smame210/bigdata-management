<script setup lang="ts">
import { h, reactive, ref } from 'vue';
import type { FormInstance } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import type { CheckboxChangeEvent } from 'ant-design-vue/es/checkbox/interface';
import { PlusOutlined } from '@ant-design/icons-vue';
import { $t } from '@/locales';
import { fetchAlertAdd, fetchAlertTemplateParams } from '@/service/api';
import dingdingImg from '@/assets/imgs/dingding.jpg';

defineOptions({
  name: 'AlertAdd'
});

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['update:open', 'success']);
const step = ref(1);
const formRef = ref<FormInstance>();
// 表单数据
const formState = reactive({
  name: '',
  type: '',
  dingdingConfig: {
    webhook: '',
    secret: '',
    atMobiles: [] as string[],
    atAll: false
  },
  template: ''
});
// 告警类型选项
const alterTypeOptions = [
  {
    label: '钉钉',
    value: 'dingding',
    img: dingdingImg
  }
];
// 加载状态
const loading = ref<boolean>(false);
// 新手机号输入
const newMobile = ref<string>('');
// 表单验证规则
const rules: Record<string, App.Global.FormRule[]> = {
  name: [{ required: true, message: '请输入告警名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择告警类型', trigger: 'change' }]
};
// 模板参数列表
const templateParams = ref<Record<string, string>>({});
// 加载模板参数状态
const loadingParams = ref<boolean>(false);

function selectType(type: string) {
  formState.type = type;
  step.value = 2;
}

function nextStepForm() {
  formRef.value
    ?.validate()
    .then(() => {
      step.value = 3;
      // 当进入第三步时，加载模板参数
      if (formState.type) {
        loadTemplateParams(formState.type);
      }
    })
    .catch(() => {
      message.warning('请完善表单信息');
    });
}

// 加载模板参数
async function loadTemplateParams(type: string) {
  try {
    loadingParams.value = true;
    const { error, data } = await fetchAlertTemplateParams(type);
    if (error) {
      message.error('获取模板参数失败');
      return;
    }
    templateParams.value = data || {};
  } catch (error) {
    console.error('获取模板参数失败:', error);
    message.error('获取模板参数失败');
  } finally {
    loadingParams.value = false;
  }
}

function handleCancel() {
  emit('update:open', false);
  step.value = 1;
  resetForm();
}

function resetForm() {
  formState.name = '';
  formState.type = '';
  formState.dingdingConfig = {
    webhook: '',
    secret: '',
    atMobiles: [],
    atAll: false
  };
  formState.template = '';
}

// 添加手机号
function addMobile() {
  if (!newMobile.value) {
    message.warning('请输入手机号');
    return;
  }

  // 简单的手机号验证
  const mobilePattern = /^1[3-9]\d{9}$/;
  if (!mobilePattern.test(newMobile.value)) {
    message.warning('请输入有效的手机号');
    return;
  }

  // 检查是否已存在
  if (formState.dingdingConfig.atMobiles.includes(newMobile.value)) {
    message.warning('该手机号已添加');
    return;
  }

  formState.dingdingConfig.atMobiles.push(newMobile.value);
  newMobile.value = '';
}

// 删除手机号
function removeMobile(index: number) {
  formState.dingdingConfig.atMobiles.splice(index, 1);
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value?.validate();
    loading.value = true;
    const formData: Partial<Api.AlertManage.Alert> = {
      name: formState.name,
      type: formState.type as Api.AlertManage.AlertType,
      template: formState.template
    };
    if (formState.type === 'dingding') {
      formData.metadata = formState.dingdingConfig;
    }
    const { error } = await fetchAlertAdd(formData as Api.AlertManage.Alert);
    if (error) {
      message.error('添加告警失败');
      return;
    }
    message.success('添加告警成功');
    emit('success');
    emit('update:open', false);

    // 重置表单
    formState.name = '';
    formState.dingdingConfig.webhook = '';
    formState.dingdingConfig.secret = '';
    formState.dingdingConfig.atMobiles = [];
    formState.dingdingConfig.atAll = false;
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    loading.value = false;
  }
}

// 处理@所有人勾选变化
function handleAtAllChange(checked: CheckboxChangeEvent) {
  if (checked.target.checked) {
    // 如果勾选了@所有人，清空手机号列表
    formState.dingdingConfig.atMobiles = [];
    newMobile.value = '';
  }
}
</script>

<template>
  <AModal
    :open="props.open"
    :title="$t('page.config.alert.addAlert')"
    :confirm-loading="loading"
    width="600px"
    :footer="null"
    @cancel="handleCancel"
  >
    <div v-if="step === 1">
      <div class="flex space-x-4">
        <div v-for="item in alterTypeOptions" :key="item.value" class="card mb-2" @click="selectType(item.value)">
          <img :src="item.img" alt="" class="max-h-full max-w-full object-contain" />
          <div>{{ item.label }}</div>
        </div>
      </div>
    </div>

    <div v-else-if="step === 2">
      <AForm ref="formRef" :model="formState" :rules="rules" layout="vertical">
        <AFormItem name="name" label="告警名称">
          <AInput v-model:value="formState.name" placeholder="请输入告警名称" autocomplete="off" />
        </AFormItem>

        <div v-if="formState.type === 'dingding'">
          <AFormItem
            label="Webhook 地址"
            :name="['dingdingConfig', 'webhook']"
            :rules="[
              { required: true, message: '请输入钉钉 Webhook 地址', trigger: 'blur' },
              { type: 'url', message: '请输入有效的 URL 地址', trigger: 'blur' }
            ]"
          >
            <AInput
              v-model:value="formState.dingdingConfig.webhook"
              placeholder="请输入钉钉 Webhook 地址"
              autocomplete="off"
            />
          </AFormItem>

          <AFormItem name="secret" label="安全密钥">
            <AInput v-model:value="formState.dingdingConfig.secret" placeholder="请输入安全密钥" autocomplete="off" />
          </AFormItem>

          <AFormItem v-if="!formState.dingdingConfig.atAll" label="@手机号列表">
            <div class="mb-2 flex">
              <AInput
                v-model:value="newMobile"
                placeholder="请输入手机号"
                class="mr-2 flex-1"
                autocomplete="off"
                @press-enter="addMobile"
              />
              <AButton type="primary" :icon="h(PlusOutlined)" class="no-wrap-btn" @click="addMobile">添加</AButton>
            </div>
            <div>
              <ATag
                v-for="(mobile, index) in formState.dingdingConfig.atMobiles"
                :key="index"
                closable
                class="mb-2 mr-2"
                @close="removeMobile(index)"
              >
                {{ mobile }}
              </ATag>
            </div>
          </AFormItem>

          <AFormItem>
            <ACheckbox v-model:checked="formState.dingdingConfig.atAll" @change="handleAtAllChange">@所有人</ACheckbox>
          </AFormItem>
        </div>
      </AForm>
      <div class="btn-actions mt-4">
        <AButton
          @click="
            () => {
              step = 1;
            }
          "
        >
          上一步
        </AButton>
        <AButton type="primary" @click="nextStepForm">下一步</AButton>
      </div>
    </div>

    <div v-else-if="step === 3">
      <div class="panel-surface">
        <ASpin :spinning="loadingParams">
          可用参数：
          <br />
          <template v-if="Object.keys(templateParams).length > 0">
            <div v-for="(meaning, paramName) in templateParams" :key="paramName">
              <strong>[{{ paramName }}]</strong>
              {{ meaning }}
              <br />
            </div>
          </template>
          <template v-else>
            <div>加载参数中...</div>
          </template>
        </ASpin>
      </div>
      <ATextarea
        v-model:value="formState.template"
        :auto-size="{ minRows: 2, maxRows: 5 }"
        placeholder="请输入告警模版，例如：告警：[name] 在 [time] 触发，内容：[content]"
      />
      <div class="btn-actions mt-4">
        <AButton
          @click="
            () => {
              step = 2;
            }
          "
        >
          上一步
        </AButton>
        <AButton type="primary" @click="handleSubmit">提交</AButton>
      </div>
    </div>
  </AModal>
</template>

<style scoped>
.mb-2 {
  margin-bottom: 8px;
}

.mr-2 {
  margin-right: 8px;
}

.flex {
  display: flex;
}

.flex-1 {
  flex: 1;
}

.text-sm {
  font-size: 14px;
}

.no-wrap-btn {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
}

:deep(.no-wrap-btn .anticon) {
  display: flex;
}

/* 确保图标与文本垂直对齐 */
:deep(.ant-btn .anticon) {
  display: flex;
  align-items: center;
  vertical-align: middle;
  line-height: 1;
}

.card {
  width: clamp(96px, 16vw, 136px);
  height: clamp(96px, 16vw, 136px);
  border: none;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background-color 0.2s ease;
  background-color: rgb(var(--container-bg-color));
}

.card img {
  width: 72%; /* 图标占容器的比例，可根据实际图形留白微调到 68%~75% */
  height: 72%;
  object-fit: contain; /* 保持等比缩放，不裁切 */
  transition: transform 0.2s ease;
}

.card:hover {
  /* 去掉蓝色内描边，仅保留柔和阴影与背景 */
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px) scale(1.02);
  background-color: rgba(var(--primary-color), 0.08);
}

/* 避免点击/键盘焦点出现系统蓝色边线 */
.card:focus,
.card:focus-visible,
.card:active {
  outline: none;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.card:hover img {
  transform: scale(1.03);
}

.space-x-4 > * + * {
  margin-left: 16px;
}

.panel-surface {
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  font-size: 14px;
  background-color: rgb(var(--layout-bg-color));
  border: 1px solid rgba(var(--base-text-color), 0.08);
}

.param-tag {
  background-color: rgba(var(--base-text-color), 0.06);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: monospace;
  color: rgba(var(--base-text-color), 0.78);
}

/* 新增：按钮容器居中并设置间距 */
.btn-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap; /* 窄屏时可换行 */
  row-gap: 12px; /* 换行后的行间距 */
  column-gap: clamp(24px, 8vw, 64px); /* 根据宽度自适应的列间距：24px~64px */
}
</style>
