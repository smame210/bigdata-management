<script setup lang="tsx">
import { Button, Dropdown, Menu, MenuItem, Modal, Tag, message } from 'ant-design-vue';
import { DownOutlined } from '@ant-design/icons-vue';
import { deleteSchedule, fetchScheduleList, updateScheduleStatus } from '@/service/api';
import { useTable, useTableOperate, useTableScroll } from '@/hooks/common/table';
import { $t } from '@/locales';
import { enableScheduleStatusRecord } from '@/constants/business';
import { useRouterPush } from '@/hooks/common/router';
import SvgIcon from '@/components/custom/svg-icon.vue';
import ScheduleSearch from './modules/schedule-search.vue';

const { routerPushByKey } = useRouterPush();

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
  apiFn: fetchScheduleList,
  apiParams: {
    current: 1,
    size: 10,
    taskName: '',
    engineType: undefined,
    clusterName: '',
    scheduleName: '',
    scheduleStatus: undefined
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
      key: 'scheduleStatus',
      dataIndex: 'scheduleStatus',
      title: $t('page.manage.schedule.scheduleStatus'),
      align: 'center',
      width: 30,
      customRender: ({ record }) => {
        if (record.scheduleStatus === null) {
          return null;
        }

        const tagMap: Record<Api.ScheduleManage.ScheduleStatus, string> = {
          0: 'default',
          1: 'success'
        };

        const label = $t(enableScheduleStatusRecord[record.scheduleStatus]);

        return <Tag color={tagMap[record.scheduleStatus]}>{label}</Tag>;
      }
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: $t('page.manage.task.createTime'),
      align: 'center',
      width: 60
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 40,
      fixed: 'right',
      customRender: ({ record }) => (
        <div class="flex-center gap-8px">
          <Button type="primary" size="small" onClick={() => handleView(record.id)}>
            {$t('common.detail')}
          </Button>
          <Button type="primary" ghost size="small" onClick={() => handleEdit(record.id)}>
            {$t('common.edit')}
          </Button>
          <Dropdown
            overlay={
              <Menu
                onClick={({ key }) => {
                  if (key === 'delete') {
                    Modal.confirm({
                      title: $t('common.confirmDelete'),
                      onOk: () => handleDelete(record.id)
                    });
                  } else if (key === 'enable') {
                    handleStatus(record.id, 1);
                  } else if (key === 'disable') {
                    handleStatus(record.id, 0);
                  }
                }}
              >
                {record.scheduleStatus === 0 ? (
                  <MenuItem key="enable">{$t('page.manage.common.status.enable')}</MenuItem>
                ) : (
                  <MenuItem key="disable">{$t('page.manage.common.status.disable')}</MenuItem>
                )}
                <MenuItem key="delete">{$t('common.delete')}</MenuItem>
              </Menu>
            }
          >
            <Button size="small">
              {$t('common.action')}
              <DownOutlined />
            </Button>
          </Dropdown>
        </div>
      )
    }
  ]
});

const { onDeleted } = useTableOperate(data, getData);

async function handleView(id: number) {
  routerPushByKey('manage_schedule-detail', { params: { id: String(id) } });
}

async function handleEdit(id: number) {
  routerPushByKey('manage_schedule-update', { params: { id: String(id) } });
}

function handleAdd() {
  routerPushByKey('manage_schedule-add');
}

function handleRest() {
  resetSearchParams();
  getData();
}

async function handleStatus(id: number, status: number) {
  const { error } = await updateScheduleStatus(id, status);
  if (error) {
    message.error('更新状态时发生异常!');
  } else {
    onDeleted();
  }
}

async function handleDelete(id: number) {
  const { error } = await deleteSchedule(id);
  if (error) {
    message.error('删除时发生异常!');
  } else {
    onDeleted();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ScheduleSearch v-model:model="searchParams" @reset="handleRest" @search="getDataByPage" />
    <ACard
      :title="$t('page.manage.schedule.title')"
      :bordered="false"
      :body-style="{ flex: 1, overflow: 'hidden' }"
      class="flex-col-stretch sm:flex-1-hidden card-wrapper"
    >
      <template #extra>
        <div class="flex flex-wrap justify-end gap-x-12px gap-y-8px lt-sm:(w-200px py-12px)">
          <slot name="prefix"></slot>
          <slot name="default">
            <AButton size="small" ghost type="primary" @click="handleAdd">
              <template #icon>
                <icon-ic-round-plus class="align-sub text-icon" />
              </template>
              <span class="ml-8px">{{ $t('common.add') }}</span>
            </AButton>
          </slot>
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
