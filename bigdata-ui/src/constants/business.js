import { transformRecordToOption } from '@/utils/common';
export const enableTaskStatusRecord = {
  0: 'page.manage.common.status.disable',
  1: 'page.manage.common.status.enable'
};
export const enableTaskStatusOptions = transformRecordToOption(enableTaskStatusRecord);
export const enableClusterStatusRecord = {
  1: 'page.config.common.status.enable',
  0: 'page.config.common.status.disable'
};
export const enableClusterStatusOptions = transformRecordToOption(enableClusterStatusRecord);
export const enableScheduleStatusRecord = {
  1: 'page.config.common.status.enable',
  0: 'page.config.common.status.disable'
};
export const enableScheduleStatusOptions = transformRecordToOption(enableScheduleStatusRecord);
export const enableTaskInstanceStatusRecord = {
  0: 'page.manage.common.status.accept',
  1: 'page.manage.common.status.accept',
  2: 'page.manage.common.status.running',
  3: 'page.manage.common.status.finished',
  4: 'page.manage.common.status.failed',
  5: 'page.manage.common.status.killed',
  6: 'page.manage.common.status.killed'
};
export const enableTaskInstanceStatusOptions = transformRecordToOption(enableTaskInstanceStatusRecord);
export const enableAlterStatusRecord = {
  1: 'page.config.common.status.enable',
  0: 'page.config.common.status.disable'
};
export const enableAlterStatusOptions = transformRecordToOption(enableAlterStatusRecord);
export const enableAlterPolicyStatusRecord = {
  1: 'page.config.common.status.enable',
  0: 'page.config.common.status.disable'
};
export const enableAlterPolicyStatusOptions = transformRecordToOption(enableAlterPolicyStatusRecord);
export const enableStatusRecord = {
  1: 'page.manage.common.status.enable',
  2: 'page.manage.common.status.disable'
};
export const enableStatusOptions = transformRecordToOption(enableStatusRecord);
export const userGenderRecord = {
  1: 'page.manage.user.gender.male',
  2: 'page.manage.user.gender.female'
};
export const userGenderOptions = transformRecordToOption(userGenderRecord);
export const menuTypeRecord = {
  1: 'page.manage.menu.type.directory',
  2: 'page.manage.menu.type.menu'
};
export const menuTypeOptions = transformRecordToOption(menuTypeRecord);
export const menuIconTypeRecord = {
  1: 'page.manage.menu.iconType.iconify',
  2: 'page.manage.menu.iconType.local'
};
export const menuIconTypeOptions = transformRecordToOption(menuIconTypeRecord);
