<script setup lang="ts">
import { $t } from '@/locales';
import { useAntdForm } from '@/hooks/common/form';

defineOptions({
  name: 'PolicySearch'
});

interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const { formRef, validate, resetFields } = useAntdForm();

const model = defineModel<Api.AlertPolicyManage.AlertPolicySearchParams>('model', { required: true });

async function reset() {
  await resetFields();
  emit('reset');
}

async function search() {
  await validate();
  emit('search');
}
</script>

<template>
  <ACard :title="$t('common.search')" :bordered="false" class="card-wrapper">
    <AForm ref="formRef" :model="model" class="compact-form">
      <ARow :gutter="[8, 8]" wrap>
        <ACol :span="12" :xl="6">
          <AFormItem :label="$t('page.config.alert_policy.alertPolicyName')" name="name" class="m-0">
            <AInput
              v-model:value="model.name"
              :placeholder="$t('page.config.alert_policy.form.name')"
              autocomplete="off"
            />
          </AFormItem>
        </ACol>
        <ACol :span="12" :xl="6">
          <AFormItem :label="$t('page.config.alert.alertName')" name="alertName" class="m-0">
            <AInput
              v-model:value="model.alertName"
              :placeholder="$t('page.config.alert.form.name')"
              autocomplete="off"
            />
          </AFormItem>
        </ACol>
        <ACol :span="12" :xl="6">
          <AFormItem :label="$t('page.manage.task.taskName')" name="taskName" class="m-0">
            <AInput
              v-model:value="model.taskName"
              :placeholder="$t('page.manage.task.form.taskName')"
              autocomplete="off"
            />
          </AFormItem>
        </ACol>
        <ACol :span="12" :xl="6">
          <AFormItem :label="$t('page.config.alert_policy.alertPolicyStatus')" class="m-0">
            <ASelect v-model:value="model.status" placeholder="请选择状态" class="compact-select" allow-clear>
              <ASelectOption value="1">{{ $t('page.config.common.status.enable') }}</ASelectOption>
              <ASelectOption value="0">{{ $t('page.config.common.status.disable') }}</ASelectOption>
            </ASelect>
          </AFormItem>
        </ACol>
        <ACol :span="24">
          <AFormItem class="m-0">
            <div class="form-actions">
              <AButton @click="reset">
                <template #icon>
                  <icon-ic-round-refresh class="align-sub text-icon" />
                </template>
                <span>{{ $t('common.reset') }}</span>
              </AButton>
              <AButton type="primary" ghost @click="search">
                <template #icon>
                  <icon-ic-round-search class="align-sub text-icon" />
                </template>
                <span>{{ $t('common.search') }}</span>
              </AButton>
            </div>
          </AFormItem>
        </ACol>
      </ARow>
    </AForm>
  </ACard>
</template>

<style scoped>
/* 调整表单整体样式以实现紧凑布局 */
.compact-form {
  margin-bottom: 0;
}

/* 控制按钮间距及布局 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.compact-select {
  width: 100%;
}
</style>
