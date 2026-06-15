import { transformRecordToOption } from '@/utils/common';

export const enableTaskStatusRecord: Record<Api.TaskManage.TaskStatus, App.I18n.I18nKey> = {
  '0': 'page.manage.common.status.disable',
  '1': 'page.manage.common.status.enable'
};

export const enableTaskStatusOptions = transformRecordToOption(enableTaskStatusRecord);

export const enableClusterStatusRecord: Record<Api.ClusterManage.ClusterStatus, App.I18n.I18nKey> = {
  '1': 'page.config.common.status.enable',
  '0': 'page.config.common.status.disable'
};

export const enableClusterStatusOptions = transformRecordToOption(enableClusterStatusRecord);

export const enableScheduleStatusRecord: Record<Api.ScheduleManage.ScheduleStatus, App.I18n.I18nKey> = {
  '1': 'page.config.common.status.enable',
  '0': 'page.config.common.status.disable'
};

export const enableScheduleStatusOptions = transformRecordToOption(enableScheduleStatusRecord);

export const enableTaskInstanceStatusRecord: Record<Api.TaskInstanceManage.TaskInstanceStatus, App.I18n.I18nKey> = {
  '0': 'page.manage.common.status.accept',
  '1': 'page.manage.common.status.accept',
  '2': 'page.manage.common.status.running',
  '3': 'page.manage.common.status.finished',
  '4': 'page.manage.common.status.failed',
  '5': 'page.manage.common.status.killed',
  '6': 'page.manage.common.status.killed'
};

export const enableTaskInstanceStatusOptions = transformRecordToOption(enableTaskInstanceStatusRecord);

export const enableAlterStatusRecord: Record<Api.AlertManage.AlertStatus, App.I18n.I18nKey> = {
  '1': 'page.config.common.status.enable',
  '0': 'page.config.common.status.disable'
};

export const enableAlterStatusOptions = transformRecordToOption(enableAlterStatusRecord);

export const enableAlterPolicyStatusRecord: Record<Api.AlertPolicyManage.AlertPolicyStatus, App.I18n.I18nKey> = {
  '1': 'page.config.common.status.enable',
  '0': 'page.config.common.status.disable'
};

export const enableAlterPolicyStatusOptions = transformRecordToOption(enableAlterPolicyStatusRecord);

export const enableStatusRecord: Record<Api.Common.EnableStatus, App.I18n.I18nKey> = {
  '1': 'page.manage.common.status.enable',
  '2': 'page.manage.common.status.disable'
};

export const enableStatusOptions = transformRecordToOption(enableStatusRecord);

export const userGenderRecord: Record<Api.SystemManage.UserGender, App.I18n.I18nKey> = {
  '1': 'page.manage.user.gender.male',
  '2': 'page.manage.user.gender.female'
};

export const userGenderOptions = transformRecordToOption(userGenderRecord);

export const menuTypeRecord: Record<Api.SystemManage.MenuType, App.I18n.I18nKey> = {
  '1': 'page.manage.menu.type.directory',
  '2': 'page.manage.menu.type.menu'
};

export const menuTypeOptions = transformRecordToOption(menuTypeRecord);

export const menuIconTypeRecord: Record<Api.SystemManage.IconType, App.I18n.I18nKey> = {
  '1': 'page.manage.menu.iconType.iconify',
  '2': 'page.manage.menu.iconType.local'
};

export const menuIconTypeOptions = transformRecordToOption(menuIconTypeRecord);
