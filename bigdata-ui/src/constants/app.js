import { transformRecordToOption } from '@/utils/common';
export const GLOBAL_HEADER_MENU_ID = '__GLOBAL_HEADER_MENU__';
export const GLOBAL_SIDER_MENU_ID = '__GLOBAL_SIDER_MENU__';
export const themeSchemaRecord = {
  light: 'theme.themeSchema.light',
  dark: 'theme.themeSchema.dark',
  auto: 'theme.themeSchema.auto'
};
export const themeSchemaOptions = transformRecordToOption(themeSchemaRecord);
export const loginModuleRecord = {
  'pwd-login': 'page.login.pwdLogin.title',
  'code-login': 'page.login.codeLogin.title',
  register: 'page.login.register.title',
  'reset-pwd': 'page.login.resetPwd.title',
  'bind-wechat': 'page.login.bindWeChat.title'
};
export const themeLayoutModeRecord = {
  vertical: 'theme.layoutMode.vertical',
  'vertical-mix': 'theme.layoutMode.vertical-mix',
  horizontal: 'theme.layoutMode.horizontal',
  'horizontal-mix': 'theme.layoutMode.horizontal-mix'
};
export const themeLayoutModeOptions = transformRecordToOption(themeLayoutModeRecord);
export const themeScrollModeRecord = {
  wrapper: 'theme.scrollMode.wrapper',
  content: 'theme.scrollMode.content'
};
export const themeScrollModeOptions = transformRecordToOption(themeScrollModeRecord);
export const themeTabModeRecord = {
  chrome: 'theme.tab.mode.chrome',
  button: 'theme.tab.mode.button'
};
export const themeTabModeOptions = transformRecordToOption(themeTabModeRecord);
export const themePageAnimationModeRecord = {
  'fade-slide': 'theme.page.mode.fade-slide',
  fade: 'theme.page.mode.fade',
  'fade-bottom': 'theme.page.mode.fade-bottom',
  'fade-scale': 'theme.page.mode.fade-scale',
  'zoom-fade': 'theme.page.mode.zoom-fade',
  'zoom-out': 'theme.page.mode.zoom-out',
  none: 'theme.page.mode.none'
};
export const themePageAnimationModeOptions = transformRecordToOption(themePageAnimationModeRecord);
export const resetCacheStrategyRecord = {
  close: 'theme.resetCacheStrategy.close',
  refresh: 'theme.resetCacheStrategy.refresh'
};
export const resetCacheStrategyOptions = transformRecordToOption(resetCacheStrategyRecord);
