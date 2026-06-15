<script setup lang="tsx">
import { ref } from 'vue';
import { Button, Popconfirm, Switch, message } from 'ant-design-vue';
import { fetchDeleteUser, fetchGetUserList, fetchResetPassword, fetchUpdateUserStatus } from '@/service/api';
import { useTable, useTableOperate, useTableScroll } from '@/hooks/common/table';
import { $t } from '@/locales';
import { useAuthStore } from '@/store/modules/auth';
import UserOperateDrawer from './modules/user-operate-drawer.vue';
import UserSearch from './modules/user-search.vue';

const authStore = useAuthStore();
const currentUserId = Number(authStore.userInfo.userId);

const { tableWrapperRef, scrollConfig } = useTableScroll(1200);

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
  apiFn: fetchGetUserList,
  apiParams: {
    current: 1,
    size: 10,
    status: undefined,
    userName: undefined,
    userGender: undefined,
    userPhone: undefined,
    userEmail: undefined
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
      key: 'userName',
      dataIndex: 'userName',
      title: $t('page.manage.user.userName'),
      align: 'center',
      minWidth: 100,
      ellipsis: true
    },
    {
      key: 'userPhone',
      dataIndex: 'userPhone',
      title: $t('page.manage.user.userPhone'),
      align: 'center',
      width: 120
    },
    {
      key: 'userEmail',
      dataIndex: 'userEmail',
      title: $t('page.manage.user.userEmail'),
      align: 'center',
      minWidth: 180,
      ellipsis: true
    },
    {
      key: 'status',
      dataIndex: 'status',
      title: $t('page.manage.user.userStatus'),
      align: 'center',
      width: 100,
      customRender: ({ record }) => {
        if (record.status === null) {
          return null;
        }

        return (
          <Switch
            checked={Number(record.status) === 1}
            checkedChildren="启用"
            unCheckedChildren="停用"
            disabled={record.id === currentUserId}
            onChange={checked => handleStatusChange(record.id, checked ? 1 : 2)}
          />
        );
      }
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 200,
      customRender: ({ record }) => (
        <div class="flex-center gap-8px">
          <Button type="primary" ghost size="small" onClick={() => edit(record.id)}>
            {$t('common.edit')}
          </Button>
          <Button size="small" onClick={() => openResetPassword(record.id)}>
            修改密码
          </Button>
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
  drawerVisible,
  operateType,
  editingData,
  handleAdd,
  handleEdit,
  checkedRowKeys,
  rowSelection,
  onBatchDeleted,
  onDeleted
} = useTableOperate(data, getData);

// 修改密码弹窗
const resetPasswordVisible = ref(false);
const resetPasswordUserId = ref<number | null>(null);
const resetPasswordOld = ref('');
const resetPasswordNew = ref('');
const resetPasswordConfirm = ref('');

function openResetPassword(id: number) {
  resetPasswordUserId.value = id;
  resetPasswordOld.value = '';
  resetPasswordNew.value = '';
  resetPasswordConfirm.value = '';
  resetPasswordVisible.value = true;
}

async function handleResetPassword() {
  if (!resetPasswordUserId.value) return;
  if (!resetPasswordOld.value) {
    message.warning('请输入原始密码');
    return;
  }
  if (!resetPasswordNew.value) {
    message.warning('请输入新密码');
    return;
  }
  if (resetPasswordNew.value !== resetPasswordConfirm.value) {
    message.warning('两次输入的密码不一致');
    return;
  }
  await fetchResetPassword(resetPasswordUserId.value, resetPasswordOld.value, resetPasswordNew.value);
  message.success('密码修改成功');
  resetPasswordVisible.value = false;
}

async function handleBatchDelete() {
  onBatchDeleted();
}

async function handleDelete(id: number) {
  await fetchDeleteUser(id);
  onDeleted();
}

async function handleStatusChange(id: number, status: number) {
  const { error } = await fetchUpdateUserStatus(id, status);
  if (!error) {
    message.success(status === 1 ? '启用成功' : '停用成功');
    getData();
  }
}

function edit(id: number) {
  handleEdit(id);
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <UserSearch v-model:model="searchParams" @reset="resetSearchParams" @search="getDataByPage" />
    <ACard
      :title="$t('page.manage.user.title')"
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
      <div ref="tableWrapperRef" class="h-full">
        <ATable
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
      </div>

      <UserOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getDataByPage"
      />

      <AModal
        v-model:open="resetPasswordVisible"
        title="修改密码"
        :width="400"
        :ok-text="$t('common.confirm')"
        :cancel-text="$t('common.cancel')"
        @ok="handleResetPassword"
      >
        <AForm layout="vertical">
          <AFormItem label="原始密码">
            <AInputPassword v-model:value="resetPasswordOld" placeholder="请输入原始密码" autocomplete="off" />
          </AFormItem>
          <AFormItem label="新密码">
            <AInputPassword v-model:value="resetPasswordNew" placeholder="请输入新密码" autocomplete="off" />
          </AFormItem>
          <AFormItem label="确认新密码">
            <AInputPassword v-model:value="resetPasswordConfirm" placeholder="请再次输入新密码" autocomplete="off" />
          </AFormItem>
        </AForm>
      </AModal>
    </ACard>
  </div>
</template>

<style scoped></style>
