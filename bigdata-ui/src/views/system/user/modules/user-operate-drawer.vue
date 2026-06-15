<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useAntdForm, useFormRules } from '@/hooks/common/form';
import { fetchAddUser, fetchUpdateUser } from '@/service/api';
import { $t } from '@/locales';

defineOptions({
  name: 'UserOperateDrawer'
});

interface Props {
  /** the type of operation */
  operateType: AntDesign.TableOperateType;
  /** the edit row data */
  rowData?: Api.SystemManage.User | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { formRef, validate, resetFields } = useAntdForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<AntDesign.TableOperateType, string> = {
    add: $t('page.manage.user.addUser'),
    edit: $t('page.manage.user.editUser')
  };
  return titles[props.operateType];
});

type Model = Pick<Api.SystemManage.User, 'userName' | 'userPhone' | 'userEmail'>;

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    userName: '',
    userPhone: '',
    userEmail: ''
  };
}

type RuleKey = Extract<keyof Model, 'userName'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  userName: defaultRequiredRule
};

function handleInitModel() {
  model.value = createDefaultModel();

  if (props.operateType === 'edit' && props.rowData) {
    Object.assign(model.value, props.rowData);
  }
}

function closeDrawer() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();

  const params: Api.SystemManage.UserSaveParams = {
    userName: model.value.userName,
    phone: model.value.userPhone,
    email: model.value.userEmail
  };

  if (props.operateType === 'edit' && props.rowData) {
    params.id = props.rowData.id;
    await fetchUpdateUser(params);
  } else {
    await fetchAddUser(params);
  }

  window.$message?.success($t('common.updateSuccess'));
  closeDrawer();
  emit('submitted');
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    resetFields();
  }
});
</script>

<template>
  <ADrawer v-model:open="visible" :title="title" :width="360">
    <AForm ref="formRef" layout="vertical" :model="model" :rules="rules">
      <AFormItem :label="$t('page.manage.user.userName')" name="userName">
        <AInput v-model:value="model.userName" :placeholder="$t('page.manage.user.form.userName')" />
      </AFormItem>
      <AFormItem :label="$t('page.manage.user.userPhone')" name="userPhone">
        <AInput v-model:value="model.userPhone" :placeholder="$t('page.manage.user.form.userPhone')" />
      </AFormItem>
      <AFormItem :label="$t('page.manage.user.userEmail')" name="userEmail">
        <AInput v-model:value="model.userEmail" :placeholder="$t('page.manage.user.form.userEmail')" />
      </AFormItem>
    </AForm>
    <template #footer>
      <ASpace :size="16">
        <AButton @click="closeDrawer">{{ $t('common.cancel') }}</AButton>
        <AButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</AButton>
      </ASpace>
    </template>
  </ADrawer>
</template>

<style scoped></style>
