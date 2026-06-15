<script setup lang="ts">
import { h, reactive, ref, watch } from 'vue';
import type { FormInstance } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import type { CheckboxChangeEvent } from 'ant-design-vue/es/checkbox/interface';
import { PlusOutlined } from '@ant-design/icons-vue';
import { $t } from '@/locales';
import { fetchAlertDetail, fetchAlertTemplateParams, fetchAlertUpdate } from '@/service/api';

defineOptions({
  name: 'AlertEdit'
});

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  id: {
    type: Number,
    required: true
  }
});
const emit = defineEmits(['update:open', 'success']);

const formRef = ref<FormInstance>();
const loading = ref(false);
const loadingParams = ref(false);
const step = ref(2);
const newMobile = ref('');

const formState = reactive({
  id: 0,
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

const rules: Record<string, App.Global.FormRule[]> = {
  name: [{ required: true, message: '请输入告警名称', trigger: 'blur' }]
};

watch(
  () => props.id,
  id => {
    if (id && props.open) {
      loadAlertDetail(id);
    }
  },
  { immediate: true }
);

async function loadAlertDetail(id: number) {
  try {
    loading.value = true;
    const { error, data } = await fetchAlertDetail(id);
    if (error) {
      message.error('加载告警详情失败');
      return;
    }
    if (!data) {
      message.error('告警不存在');
      return;
    }
    step.value = 2;
    initForm(data);
    loadTemplateParams(data.type);
  } catch (err) {
    console.error('加载告警详情失败:', err);
    message.error('加载告警详情失败');
  } finally {
    loading.value = false;
  }
}

// 初始化表单
function initForm(data: Api.AlertManage.Alert) {
  formState.id = data.id || 0;
  formState.name = data.name || '';
  formState.type = data.type || '';
  formState.template = data.template || '';
  if (data.type === 'dingding' && data.metadata) {
    const metadata = typeof data.metadata === 'string' ? JSON.parse(data.metadata) : data.metadata;
    formState.dingdingConfig = {
      webhook: metadata.webhook || '',
      secret: metadata.secret || '',
      atMobiles: metadata.atMobiles || [],
      atAll: metadata.atAll || false
    };
  }
}

const templateParams = ref<Record<string, string>>({});

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
  } catch (err) {
    console.error('获取模板参数失败:', err);
    message.error('获取模板参数失败');
  } finally {
    loadingParams.value = false;
  }
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value?.validate();
    loading.value = true;
    const formData: Partial<Api.AlertManage.Alert> = {
      id: formState.id,
      name: formState.name,
      type: formState.type as Api.AlertManage.AlertType,
      template: formState.template
    };
    if (formState.type === 'dingding') {
      formData.metadata = formState.dingdingConfig;
    }
    const { error } = await fetchAlertUpdate(props.id, formData as Api.AlertManage.Alert);
    if (error) {
      message.error('更新告警失败');
      return;
    }
    message.success('更新告警成功');
    emit('success');
    emit('update:open', false);
  } catch (err) {
    console.error('表单验证失败:', err);
  } finally {
    loading.value = false;
  }
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

function handleCancel() {
  emit('update:open', false);
  step.value = 2;
}

function addMobile() {
  if (!newMobile.value) {
    message.warning('请输入手机号');
    return;
  }
  const mobilePattern = /^1[3-9]\d{9}$/;
  if (!mobilePattern.test(newMobile.value)) {
    message.warning('请输入有效的手机号');
    return;
  }
  if (formState.dingdingConfig.atMobiles.includes(newMobile.value)) {
    message.warning('该手机号已添加');
    return;
  }
  formState.dingdingConfig.atMobiles.push(newMobile.value);
  newMobile.value = '';
}

function removeMobile(index: number) {
  formState.dingdingConfig.atMobiles.splice(index, 1);
}

function handleAtAllChange(checked: CheckboxChangeEvent) {
  if (checked.target.checked) {
    formState.dingdingConfig.atMobiles = [];
    newMobile.value = '';
  }
}
</script>

<template>
  <AModal
    :open="props.open"
    :title="$t('page.config.alert.editAlert')"
    :confirm-loading="loading"
    width="600px"
    :footer="null"
    @cancel="handleCancel"
  >
    <div v-if="step === 2">
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
      <div class="mt-4 text-right">
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
      <div class="footer-actions mt-4">
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
:deep(.ant-btn .anticon) {
  display: flex;
  align-items: center;
  vertical-align: middle;
  line-height: 1;
}
.bg-gray-100 {
  background-color: rgb(var(--layout-bg-color));
  border: 1px solid rgba(var(--base-text-color), 0.08);
}
.p-2 {
  padding: 8px;
}
.rounded {
  border-radius: 4px;
}

.panel-surface {
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  font-size: 14px;
  background-color: rgb(var(--layout-bg-color));
  border: 1px solid rgba(var(--base-text-color), 0.08);
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
