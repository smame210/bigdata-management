<script setup lang="tsx">
import dayjs from 'dayjs';
import { Button, Popconfirm, Tag, Tooltip, message } from 'ant-design-vue';
import { computed, ref } from 'vue';
import { batchDeleteTask, fetchExecuteTask, fetchGetTaskList } from '@/service/api';
import { useTable, useTableOperate, useTableScroll } from '@/hooks/common/table';
import { $t } from '@/locales';
import { enableTaskStatusRecord } from '@/constants/business';
import { useRouterPush } from '@/hooks/common/router';
import SvgIcon from '@/components/custom/svg-icon.vue';
import TaskSearch from './modules/task-search.vue';

const { routerPushByKey } = useRouterPush();

const executingIds = ref(new Set<number>());

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
  apiFn: fetchGetTaskList,
  apiParams: {
    current: 1,
    size: 10,
    taskName: '',
    engineType: undefined,
    taskStatus: undefined,
    taskMode: undefined,
    clusterId: undefined
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
      key: 'taskMode',
      dataIndex: 'taskMode',
      title: $t('page.manage.task.taskMode'),
      align: 'center',
      width: 46,
      customRender: ({ record }) => {
        const modeMap: Record<string, { label: string; color: string }> = {
          batch: { label: $t('page.manage.common.batch'), color: 'blue' },
          streaming: { label: $t('page.manage.common.streaming'), color: 'cyan' }
        };
        const mode = modeMap[record.taskMode] || { label: record.taskMode, color: 'default' };
        return <Tag color={mode.color}>{mode.label}</Tag>;
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
      key: 'latestLaunchTime',
      dataIndex: 'latestLaunchTime',
      title: $t('page.manage.task.latestLaunchTime'),
      align: 'center',
      width: 60,
      customRender: ({ record }) =>
        record.latestLaunchTime ? dayjs(record.latestLaunchTime).format('YYYY-MM-DD HH:mm:ss') : '-'
    },
    {
      key: 'taskStatus',
      dataIndex: 'taskStatus',
      title: $t('page.manage.task.taskStatus'),
      align: 'center',
      width: 30,
      customRender: ({ record }) => {
        if (record.taskStatus === null) {
          return null;
        }

        const tagMap: Record<Api.TaskManage.TaskStatus, string> = {
          0: 'default',
          1: 'success'
        };

        const label = $t(enableTaskStatusRecord[record.taskStatus]);

        return <Tag color={tagMap[record.taskStatus]}>{label}</Tag>;
      }
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 50,
      fixed: 'right',
      customRender: ({ record }) => (
        <div class="flex-center gap-8px">
          <Button type="primary" size="small" onClick={() => handleView(record.id)}>
            {$t('common.detail')}
          </Button>
          <Button type="primary" ghost size="small" onClick={() => handleEdit(record.id)}>
            {$t('common.edit')}
          </Button>
          <Tooltip title={(record as any).clusterStatus === 0 ? '集群已停用，无法执行任务' : ''}>
            <Button
              type="primary"
              size="small"
              disabled={(record as any).clusterStatus === 0 || executingIds.value.has(record.id)}
              loading={executingIds.value.has(record.id)}
              style={{ backgroundColor: '#52c41a', borderColor: '#52c41a' }}
              onClick={() => handleExecute(record.id)}
            >
              {$t('page.manage.common.launch')}
            </Button>
          </Tooltip>
          <Popconfirm title={$t('common.confirmDelete')} onConfirm={() => handleDelete(record.id)}>
            <Button danger size="small">
              {$t('common.delete')}
            </Button>
          </Popconfirm>
        </div>
      )
    }
  ]
});

const {
  checkedRowKeys,
  rowSelection: originalRowSelection,
  onBatchDeleted,
  onDeleted
} = useTableOperate(data, getData);

// 自定义 rowSelection，设置列宽度
const rowSelection = computed(() => {
  return {
    ...originalRowSelection.value,
    columnWidth: 10,
    fixed: true
  };
});

async function handleView(id: number) {
  routerPushByKey('manage_task-detail', { params: { id: String(id) } });
}

async function handleEdit(id: number) {
  routerPushByKey('manage_task-update', { params: { id: String(id) } });
}

async function handleExecute(id: number) {
  executingIds.value.add(id);
  try {
    const { error, data: result } = await fetchExecuteTask(id);
    if (!error && result) {
      message.success(`${$t('page.manage.common.launch')}成功`);
      getData();
    }
  } finally {
    executingIds.value.delete(id);
  }
}

async function handleBatchDelete() {
  const ids = columnChecks.value.map(item => Number(item.key));

  const { error } = await batchDeleteTask(ids);
  if (error) {
    message.error('删除时发生异常!');
  } else {
    onBatchDeleted();
  }
}

function handleAdd() {
  routerPushByKey('manage_task-add');
}

function handleRest() {
  resetSearchParams();
  getData();
}

async function handleDelete(id: number) {
  const ids = [id];
  const { error } = await batchDeleteTask(ids);
  if (error) {
    message.error('删除时发生异常!');
  } else {
    onDeleted();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <TaskSearch v-model:model="searchParams" @reset="handleRest" @search="getDataByPage" />
    <ACard
      :title="$t('page.manage.task.title')"
      :bordered="false"
      :body-style="{ flex: 1, overflow: 'hidden' }"
      class="flex-col-stretch sm:flex-1-hidden card-wrapper"
    >
      <template #extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          @add="handleAdd"
          @delete="handleBatchDelete"
          @refresh="getData"
        />
      </template>
      <ATable
        ref="tableWrapperRef"
        :columns="columns"
        :data-source="data"
        size="small"
        :row-selection="rowSelection"
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
