<script setup lang="ts">
import { ref } from 'vue';
import { DownOutlined, UpOutlined } from '@ant-design/icons-vue';
import { $t } from '@/locales';
import { useAntdForm } from '@/hooks/common/form';

defineOptions({
  name: 'TaskInstanceSearch'
});

interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const { formRef, validate, resetFields } = useAntdForm();

const model = defineModel<Api.TaskInstanceManage.TaskInstanceSearchParams>('model', { required: true });

// 添加展开/收起状态控制
const expanded = ref(false);

// 切换展开/收起状态
function toggleExpand() {
  expanded.value = !expanded.value;
}

async function reset() {
  await resetFields();
  emit('reset');
}

async function search() {
  await validate();
  emit('search');
}

// 引擎类型选项 - 根据实际情况添加
const engineTypeOptions = [
  { label: 'Spark', value: 'spark' },
  { label: 'Flink', value: 'flink' }
];

const taskModeOptions = [
  { label: '批处理', value: 'batch' },
  { label: '流处理', value: 'streaming' }
];
</script>

<template>
  <ACard :title="$t('common.search')" :bordered="false" class="card-wrapper">
    <AForm
      ref="formRef"
      :model="model"
      :label-col="{
        span: 5,
        md: 7
      }"
      label-wrap
    >
      <ARow :gutter="[16, 16]" wrap>
        <ACol :span="24" :md="12" :lg="6">
          <AFormItem :label="$t('page.manage.schedule.scheduleName')" name="scheduleName" class="m-0">
            <AInput
              v-model:value="model.scheduleName"
              :placeholder="$t('page.manage.schedule.form.scheduleName')"
              autocomplete="off"
            />
          </AFormItem>
        </ACol>
        <ACol :span="24" :md="12" :lg="6">
          <AFormItem :label="$t('page.manage.task.taskName')" name="taskName" class="m-0">
            <AInput
              v-model:value="model.taskName"
              :placeholder="$t('page.manage.task.form.taskName')"
              autocomplete="off"
            />
          </AFormItem>
        </ACol>
        <ACol :span="24" :md="12" :lg="6">
          <AFormItem :label="$t('page.manage.task.taskMode')" name="taskMode" class="m-0">
            <ASelect
              v-model:value="model.taskMode"
              :placeholder="$t('page.manage.task.form.taskMode')"
              :options="taskModeOptions"
              allow-clear
            />
          </AFormItem>
        </ACol>

        <!-- 可展开的附加查询条件 -->
        <TransitionGroup name="expand-collapse">
          <template v-if="expanded">
            <ACol :span="24" :md="12" :lg="6">
              <AFormItem :label="$t('page.config.cluster.clusterName')" name="clusterName" class="m-0">
                <AInput
                  v-model:value="model.clusterName"
                  :placeholder="$t('page.config.cluster.form.clusterName')"
                  autocomplete="off"
                />
              </AFormItem>
            </ACol>
            <ACol :span="24" :md="12" :lg="6">
              <AFormItem :label="$t('page.manage.task.engineType')" name="engineType" class="m-0">
                <ASelect
                  v-model:value="model.engineType"
                  :placeholder="$t('page.manage.task.form.engineType')"
                  :options="engineTypeOptions"
                  allow-clear
                />
              </AFormItem>
            </ACol>
          </template>
        </TransitionGroup>

        <div class="flex-1">
          <AFormItem class="m-0">
            <div class="w-full flex-y-center justify-end gap-12px">
              <AButton type="link" class="expand-button" @click="toggleExpand">
                <template v-if="expanded">
                  <UpOutlined class="align-middle" />
                  <span class="ml-8px align-middle">{{ $t('common.collapse') }}</span>
                </template>
                <template v-else>
                  <DownOutlined class="align-middle" />
                  <span class="ml-8px align-middle">{{ $t('common.expand') }}</span>
                </template>
              </AButton>
              <AButton @click="reset">
                <template #icon>
                  <icon-ic-round-refresh class="align-sub text-icon" />
                </template>
                <span class="ml-8px">{{ $t('common.reset') }}</span>
              </AButton>
              <AButton type="primary" ghost @click="search">
                <template #icon>
                  <icon-ic-round-search class="align-sub text-icon" />
                </template>
                <span class="ml-8px">{{ $t('common.search') }}</span>
              </AButton>
            </div>
          </AFormItem>
        </div>
      </ARow>
    </AForm>
  </ACard>
</template>

<style scoped>
/* 展开/收起过渡效果 */
.expand-collapse-enter-active,
.expand-collapse-leave-active {
  transition: all 0.3s ease-in-out;
  max-height: 80px;
  opacity: 1;
  overflow: hidden;
}

.expand-collapse-enter-from,
.expand-collapse-leave-to {
  max-height: 0;
  opacity: 0;
  margin-top: 0 !important;
  margin-bottom: 0 !important;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
}

/* 按钮过渡效果 */
.expand-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  line-height: 1;
  transition: all 0.3s;
}

.expand-button :deep(.anticon) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  vertical-align: middle;
  transition: transform 0.3s ease;
}

.expand-button span {
  line-height: 1;
}

/* 添加一个微小的旋转效果 */
.expand-button:hover :deep(.anticon) {
  transform: translateY(2px);
}
</style>
