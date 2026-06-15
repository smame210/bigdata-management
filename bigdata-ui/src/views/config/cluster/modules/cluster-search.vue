<script setup lang="ts">
import { PlusOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons-vue';
import { useRouterPush } from '@/hooks/common/router';
import { $t } from '@/locales';
import { useAntdForm } from '@/hooks/common/form';

defineOptions({
  name: 'ClusterSearch'
});

const { routerPushByKey } = useRouterPush();

// 定义组件发出的事件
interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
  (e: 'add'): void;
}
const emit = defineEmits<Emits>();
const { formRef, validate, resetFields } = useAntdForm();
const model = defineModel<Api.ClusterManage.ClusterSearchParams>('model', { required: true });

// 配置类型选项
const configTypeOptions = [{ label: $t('page.config.cluster.cluster_type.yarn'), value: 'yarn' }];

// 搜索方法
async function handleSearch() {
  await validate();
  emit('search');
}

// 重置方法
async function handleReset() {
  await resetFields();
  emit('reset');
}

async function handleAdd() {
  routerPushByKey('config_cluster-add');
  emit('add');
}
</script>

<template>
  <ACard :title="$t('common.search')" :bordered="false" class="card-wrapper">
    <AForm ref="formRef" layout="inline" :model="model" class="search-form">
      <div class="search-fields">
        <AFormItem :label="$t('page.config.cluster.clusterName')">
          <AInput
            v-model:value="model.clusterName"
            :placeholder="$t('page.config.cluster.form.clusterName')"
            class="search-input"
          />
        </AFormItem>

        <AFormItem :label="$t('page.config.cluster.clusterType')">
          <ASelect
            v-model:value="model.clusterType"
            :placeholder="$t('page.config.cluster.form.clusterType')"
            class="search-select"
            allow-clear
          >
            <ASelectOption v-for="item in configTypeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </ASelectOption>
          </ASelect>
        </AFormItem>

        <AFormItem :label="$t('page.config.cluster.clusterStatus')">
          <ASelect
            v-model:value="model.clusterStatus"
            :placeholder="$t('page.config.cluster.form.clusterStatus')"
            class="search-select-small"
            allow-clear
          >
            <ASelectOption value="1">{{ $t('page.config.common.status.enable') }}</ASelectOption>
            <ASelectOption value="0">{{ $t('page.config.common.status.disable') }}</ASelectOption>
          </ASelect>
        </AFormItem>
      </div>

      <div class="search-buttons">
        <AButton type="primary" class="icon-button" @click="handleSearch">
          <template #icon><SearchOutlined /></template>
          {{ $t('common.search') }}
        </AButton>
        <AButton class="icon-button ml-2" @click="handleReset">
          <template #icon><ReloadOutlined /></template>
          {{ $t('common.reset') }}
        </AButton>
        <AButton type="primary" class="icon-button ml-2" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          {{ $t('common.add') }}
        </AButton>
      </div>
    </AForm>
  </ACard>
</template>

<style scoped>
.cluster-search {
  margin-bottom: 16px;
}

.search-form {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  width: 100%;
}

.search-fields {
  display: flex;
  flex-wrap: wrap;
}

.search-buttons {
  display: flex;
  align-items: flex-start;
  margin-left: auto;
}

.icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.search-input {
  width: 200px;
}

.search-select {
  width: 160px;
}

.search-select-small {
  width: 120px;
}

.ml-2 {
  margin-left: 8px;
}

/* 响应式样式调整 */
@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
  }

  .search-fields {
    width: 100%;
  }

  .search-input,
  .search-select,
  .search-select-small {
    width: 100%;
  }

  .search-buttons {
    width: 100%;
    justify-content: flex-end;
    margin-top: 8px;
  }
}
</style>
