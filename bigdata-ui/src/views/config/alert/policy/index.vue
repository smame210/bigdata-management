<script setup lang="tsx">
import { Button, Popconfirm, Switch } from 'ant-design-vue';
import { fetchAlertPolicyDelete, fetchAlertPolicyList, fetchAlertPolicyUpdateStatus } from '@/service/api';
import { useTable, useTableOperate, useTableScroll } from '@/hooks/common/table';
import { $t } from '@/locales';
import PolicyOperateDrawer from './modules/policy-operate-drawer.vue';
import PolicySearch from './modules/policy-search.vue';

const { tableWrapperRef, scrollConfig } = useTableScroll();

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
  apiFn: fetchAlertPolicyList,
  apiParams: {
    current: 1,
    size: 10,
    name: '',
    alertName: '',
    taskName: '',
    status: undefined
  },
  columns: () => [
    {
      key: 'index',
      title: $t('common.index'),
      dataIndex: 'index',
      align: 'center',
      width: 64
    },
    {
      key: 'name',
      dataIndex: 'name',
      title: $t('page.config.alert_policy.alertPolicyName'),
      align: 'center',
      minWidth: 120
    },
    {
      key: 'alertName',
      dataIndex: 'alertName',
      title: $t('page.config.alert.alertName'),
      align: 'center',
      minWidth: 120,
      customRender: ({ record }) => <span>{record.alertName}</span>
    },
    {
      key: 'taskName',
      dataIndex: 'taskName',
      title: $t('page.manage.task.taskName'),
      align: 'center',
      minWidth: 120,
      customRender: ({ record }) => <span>{record.taskName}</span>
    },
    {
      key: 'status',
      dataIndex: 'status',
      title: $t('page.config.alert_policy.alertPolicyStatus'),
      align: 'center',
      width: 100,
      customRender: ({ record }) => {
        if (record.status === null) {
          return null;
        }

        return (
          <Switch
            checked={record.status === 1}
            checkedChildren="启用"
            unCheckedChildren="停用"
            onChange={checked => handleStatusChange(record.id, checked ? 1 : 0)}
          />
        );
      }
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: $t('page.config.alert_policy.createTime'),
      align: 'center',
      minWidth: 100
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      minWidth: 150,
      customRender: ({ record }) => {
        return (
          <div class="flex-center gap-8px">
            <Button type="primary" ghost size="small" onClick={() => edit(record.id)}>
              {$t('common.edit')}
            </Button>
            <Popconfirm title={$t('common.confirmDelete')} onConfirm={() => handleDelete(record.id)}>
              <Button danger size="small">
                {$t('common.delete')}
              </Button>
            </Popconfirm>
          </div>
        );
      }
    }
  ]
});

const {
  drawerVisible,
  operateType,
  editingData,
  handleAdd,
  handleEdit,
  checkedRowKeys,
  rowSelection,
  onBatchDeleted,
  onDeleted
} = useTableOperate(data as never, getData);

function handleBatchDelete() {
  // 调用批量删除的API
  onBatchDeleted();
}

function handleDelete(id: number) {
  fetchAlertPolicyDelete(id).then(({ error }) => {
    if (!error) {
      onDeleted();
      getData();
    } else {
      window.$message?.error('删除失败');
    }
  });
}

function edit(id: number) {
  handleEdit(id);
}

async function handleStatusChange(id: number, status: 0 | 1) {
  const { error } = await fetchAlertPolicyUpdateStatus(id, status);
  if (error) {
    window.$message?.error('状态更新失败');
    return;
  }

  // 根据操作的状态显示不同的成功消息
  if (status === 1) {
    window.$message?.success($t('common.enableSuccess'));
  } else {
    window.$message?.success($t('common.disableSuccess'));
  }
  getData();
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <PolicySearch v-model:model="searchParams" @reset="resetSearchParams" @search="getDataByPage" />
    <ACard
      :title="$t('page.config.alert_policy.title')"
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
        class="h-full"
      />

      <PolicyOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData as any"
        @submitted="getDataByPage"
      />
    </ACard>
  </div>
</template>

<style scoped></style>
