<script setup lang="tsx">
import dayjs from 'dayjs';
import { Button, Popconfirm, Tag, message } from 'ant-design-vue';
import { useRoute } from 'vue-router';
import { fetchGetTaskInstanceList, fetchKillTaskInstance, fetchTrackingUrl } from '@/service/api';
import { useTable, useTableScroll } from '@/hooks/common/table';
import { $t } from '@/locales';
import { enableTaskInstanceStatusRecord } from '@/constants/business';
import SvgIcon from '@/components/custom/svg-icon.vue';
import TaskInstanceSearch from './modules/taskinstance-search.vue';

const route = useRoute();

const { tableWrapperRef, scrollConfig } = useTableScroll(1900);

const engineIconMap: Record<string, string> = {
  flink: 'simple-icons:apacheflink',
  spark: 'simple-icons:apachespark'
};

const engineColorMap: Record<string, string> = {
  flink: '#e6522c',
  spark: '#e25a1c'
};

const {
  columns,
  columnChecks,
  data,
  getData,
  getDataByPage,
  loading,
  mobilePagination,
  searchParams,
  resetSearchParams
} = useTable({
  apiFn: fetchGetTaskInstanceList,
  apiParams: {
    current: 1,
    size: 10,
    taskName: '',
    taskMode: undefined,
    engineType: undefined,
    clusterName: '',
    scheduleName: ''
  },
  columns: () => [
    {
      key: 'index',
      title: $t('common.index'),
      dataIndex: 'index',
      align: 'center',
      width: 10,
      fixed: true
    },
    {
      key: 'taskName',
      dataIndex: 'taskName',
      title: $t('page.manage.task.taskName'),
      align: 'center',
      width: 64
    },
    {
      key: 'engineType',
      dataIndex: 'engineType',
      title: $t('page.manage.task.engineType'),
      align: 'center',
      width: 46,
      customRender: ({ record }) => {
        const icon = engineIconMap[record.engineType] || 'mdi:help-circle-outline';
        const color = engineColorMap[record.engineType] || '#999';
        return (
          <div class="flex-center gap-4px">
            <SvgIcon icon={icon} style={{ color }} class="text-16px" />
            <span class="uppercase">{record.engineType}</span>
          </div>
        );
      }
    },
    {
      key: 'clusterName',
      dataIndex: 'clusterName',
      title: $t('page.config.cluster.clusterName'),
      align: 'center',
      width: 46
    },
    {
      key: 'scheduleName',
      dataIndex: 'scheduleName',
      title: $t('page.manage.schedule.scheduleName'),
      align: 'center',
      width: 46
    },
    {
      key: 'taskStatus',
      dataIndex: 'taskStatus',
      title: $t('page.manage.task_instance.taskStatus'),
      align: 'center',
      width: 30,
      customRender: ({ record }) => {
        if (record.taskStatus === null) {
          return null;
        }

        const tagMap: Record<Api.TaskInstanceManage.TaskInstanceStatus, string> = {
          0: 'default',
          1: 'default',
          2: 'processing',
          3: 'success',
          4: 'error',
          5: 'warning',
          6: 'warning'
        };

        const label = $t(enableTaskInstanceStatusRecord[record.taskStatus]);

        return <Tag color={tagMap[record.taskStatus]}>{label}</Tag>;
      }
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: $t('page.manage.task.createTime'),
      align: 'center',
      width: 60,
      customRender: ({ record }) => (record.createTime ? dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') : '-')
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 40,
      fixed: 'right',
      customRender: ({ record }) => (
        <div class="flex-center gap-8px">
          <Button type="primary" size="small" onClick={() => handleView(record)}>
            {$t('common.detail')}
          </Button>
          {record.taskStatus === 2 ? (
            <Popconfirm title="确认终止任务实例？" onConfirm={() => handleKill(record.id)}>
              <Button danger size="small">
                终止
              </Button>
            </Popconfirm>
          ) : null}
        </div>
      )
    }
  ]
});

// 从路由查询参数设置初始搜索条件
const queryTaskName = route.query.taskName as string;
if (queryTaskName) {
  searchParams.taskName = queryTaskName;
  getData();
}

async function handleView(record: Api.TaskInstanceManage.TaskInstance) {
  const { error, data: url } = await fetchTrackingUrl(record.id);
  if (!error && url) {
    window.open(url, '_blank');
  }
}

function handleRest() {
  resetSearchParams();
  getData();
}

async function handleKill(id: number) {
  // kill instance
  const { error, data: result, response } = await fetchKillTaskInstance(id);
  if (!error && result === true) {
    message.success('终止任务实例成功');
    return;
  }
  message.error(`终止任务失败！${response?.data?.msg || ''}`);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <TaskInstanceSearch v-model:model="searchParams" @reset="handleRest" @search="getDataByPage" />
    <ACard
      :title="$t('page.manage.task_instance.title')"
      :bordered="false"
      :body-style="{ flex: 1, overflow: 'hidden' }"
      class="flex-col-stretch sm:flex-1-hidden card-wrapper"
    >
      <template #extra>
        <div class="flex flex-wrap justify-end gap-x-12px gap-y-8px lt-sm:(w-200px py-12px)">
          <slot name="prefix"></slot>
          <AButton size="small" @click="getData">
            <template #icon>
              <icon-mdi-refresh class="align-sub text-icon" :class="{ 'animate-spin': loading }" />
            </template>
            <span class="ml-8px">{{ $t('common.refresh') }}</span>
          </AButton>
          <TableColumnSetting v-model:columns="columnChecks" />
          <slot name="suffix"></slot>
        </div>
      </template>
      <ATable
        ref="tableWrapperRef"
        :columns="columns"
        :data-source="data"
        size="small"
        :scroll="scrollConfig"
        :loading="loading"
        row-key="id"
        :pagination="mobilePagination"
        class="table-selection-compact h-full"
      />
    </ACard>
  </div>
</template>

<style scoped></style>
