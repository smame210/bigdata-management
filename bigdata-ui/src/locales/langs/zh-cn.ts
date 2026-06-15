const local: App.I18n.Schema = {
  system: {
    title: '大数据管理系统',
    updateTitle: '系统版本更新通知',
    updateContent: '检测到系统有新版本发布，是否立即刷新页面？',
    updateConfirm: '立即刷新',
    updateCancel: '稍后再说'
  },
  common: {
    action: '操作',
    detail: '查看',
    add: '新增',
    addSuccess: '添加成功',
    backToHome: '返回首页',
    back: '返回',
    batchDelete: '批量删除',
    cancel: '取消',
    close: '关闭',
    check: '勾选',
    selected: '已选择',
    deselect: '取消选择',
    columnSetting: '列设置',
    config: '配置',
    confirm: '确认',
    delete: '删除',
    deleteSuccess: '删除成功',
    confirmDelete: '确认删除吗？',
    edit: '编辑',
    error: '错误',
    index: '序号',
    keywordSearch: '请输入关键词搜索',
    logout: '退出登录',
    logoutConfirm: '确认退出登录吗？',
    lookForward: '敬请期待',
    modify: '修改',
    modifySuccess: '修改成功',
    noData: '无数据',
    operate: '操作',
    pleaseCheckValue: '请检查输入的值是否合法',
    refresh: '刷新',
    reset: '重置',
    search: '搜索',
    switch: '切换',
    tip: '提示',
    trigger: '触发',
    update: '更新',
    updateSuccess: '更新成功',
    userCenter: '个人中心',
    yesOrNo: {
      yes: '是',
      no: '否'
    },
    collapse: '折叠',
    expand: '展开',
    enableSuccess: '启用成功',
    disableSuccess: '停用成功'
  },
  request: {
    logout: '请求失败后登出用户',
    logoutMsg: '用户状态失效，请重新登录',
    logoutWithModal: '请求失败后弹出模态框再登出用户',
    logoutWithModalMsg: '用户状态失效，请重新登录',
    refreshToken: '请求的token已过期，刷新token',
    tokenExpired: 'token已过期'
  },
  theme: {
    themeSchema: {
      title: '主题模式',
      light: '亮色模式',
      dark: '暗黑模式',
      auto: '跟随系统'
    },
    grayscale: '灰色模式',
    colourWeakness: '色弱模式',
    layoutMode: {
      title: '布局模式',
      vertical: '左侧菜单模式',
      'vertical-mix': '左侧菜单混合模式',
      horizontal: '顶部菜单模式',
      'horizontal-mix': '顶部菜单混合模式',
      reverseHorizontalMix: '一级菜单与子级菜单位置反转'
    },
    recommendColor: '应用推荐算法的颜色',
    recommendColorDesc: '推荐颜色的算法参照',
    themeColor: {
      title: '主题颜色',
      primary: '主色',
      info: '信息色',
      success: '成功色',
      warning: '警告色',
      error: '错误色',
      followPrimary: '跟随主色'
    },
    scrollMode: {
      title: '滚动模式',
      wrapper: '外层滚动',
      content: '主体滚动'
    },
    page: {
      animate: '页面切换动画',
      mode: {
        title: '页面切换动画类型',
        'fade-slide': '滑动',
        fade: '淡入淡出',
        'fade-bottom': '底部消退',
        'fade-scale': '缩放消退',
        'zoom-fade': '渐变',
        'zoom-out': '闪现',
        none: '无'
      }
    },
    fixedHeaderAndTab: '固定头部和标签栏',
    header: {
      height: '头部高度',
      breadcrumb: {
        visible: '显示面包屑',
        showIcon: '显示面包屑图标'
      }
    },
    tab: {
      visible: '显示标签栏',
      cache: '标签栏信息缓存',
      height: '标签栏高度',
      mode: {
        title: '标签栏风格',
        chrome: '谷歌风格',
        button: '按钮风格'
      }
    },
    sider: {
      inverted: '深色侧边栏',
      width: '侧边栏宽度',
      collapsedWidth: '侧边栏折叠宽度',
      mixWidth: '混合布局侧边栏宽度',
      mixCollapsedWidth: '混合布局侧边栏折叠宽度',
      mixChildMenuWidth: '混合布局子菜单宽度'
    },
    footer: {
      visible: '显示底部',
      fixed: '固定底部',
      height: '底部高度',
      right: '底部局右'
    },
    watermark: {
      visible: '显示全屏水印',
      text: '水印文本'
    },
    themeDrawerTitle: '主题配置',
    pageFunTitle: '页面功能',
    resetCacheStrategy: {
      title: '重置缓存策略',
      close: '关闭页面',
      refresh: '刷新页面'
    },
    configOperation: {
      copyConfig: '复制配置',
      copySuccessMsg: '复制成功，请替换 src/theme/settings.ts 中的变量 themeSettings',
      resetConfig: '重置配置',
      resetSuccessMsg: '重置成功'
    }
  },
  route: {
    login: '登录',
    403: '无权限',
    404: '页面不存在',
    500: '服务器错误',
    'iframe-page': '外链页面',
    home: '首页',
    document: '文档',
    document_project: '项目文档',
    'document_project-link': '项目文档(外链)',
    document_vue: 'Vue文档',
    document_vite: 'Vite文档',
    document_unocss: 'UnoCSS文档',
    document_naive: 'Naive UI文档',
    document_antd: 'Ant Design Vue文档',
    'user-center': '个人中心',
    function: '系统功能',
    function_tab: '标签页',
    'function_multi-tab': '多标签页',
    'function_hide-child': '隐藏子菜单',
    'function_hide-child_one': '隐藏子菜单',
    'function_hide-child_two': '菜单二',
    'function_hide-child_three': '菜单三',
    function_request: '请求',
    'function_toggle-auth': '切换权限',
    'function_super-page': '超级管理员可见',
    manage: '运维中心',
    manage_task: '任务管理',
    'manage_task-add': '新增任务',
    'manage_task-update': '编辑任务',
    'manage_task-detail': '查看任务',
    manage_role: '角色管理',
    manage_menu: '菜单管理',
    config: '配置中心',
    config_cluster: '集群配置',
    'config_cluster-add': '新增集群配置',
    'config_cluster-detail': '集群配置详情',
    'config_cluster-update': '编辑集群配置',
    config_alert: '告警配置',
    config_alert_instance: '告警实例配置',
    config_alert_policy: '告警策略配置',
    manage_schedule: '任务调度',
    'manage_schedule-add': '新增任务调度',
    'manage_schedule-update': '编辑任务调度',
    'manage_schedule-detail': '查看调度任务',
    manage_taskinstance: '任务实例',
    system: '系统管理',
    system_user: '用户管理',
    'system_user-detail': '用户详情',
    exception: '异常页',
    exception_403: '403',
    exception_404: '404',
    exception_500: '500'
  },
  page: {
    login: {
      common: {
        loginOrRegister: '登录 / 注册',
        userNamePlaceholder: '请输入用户名',
        phonePlaceholder: '请输入手机号',
        codePlaceholder: '请输入验证码',
        passwordPlaceholder: '请输入密码',
        confirmPasswordPlaceholder: '请再次输入密码',
        codeLogin: '验证码登录',
        confirm: '确定',
        back: '返回',
        validateSuccess: '验证成功',
        loginSuccess: '登录成功',
        welcomeBack: '欢迎回来，{userName} ！'
      },
      pwdLogin: {
        title: '密码登录',
        rememberMe: '记住我',
        forgetPassword: '忘记密码？',
        register: '注册账号',
        otherAccountLogin: '其他账号登录',
        otherLoginMode: '其他登录方式',
        superAdmin: '超级管理员',
        admin: '管理员',
        user: '普通用户'
      },
      codeLogin: {
        title: '验证码登录',
        getCode: '获取验证码',
        reGetCode: '{time}秒后重新获取',
        sendCodeSuccess: '验证码发送成功',
        imageCodePlaceholder: '请输入图片验证码'
      },
      register: {
        title: '注册账号',
        agreement: '我已经仔细阅读并接受',
        protocol: '《用户协议》',
        policy: '《隐私权政策》'
      },
      resetPwd: {
        title: '重置密码'
      },
      bindWeChat: {
        title: '绑定微信'
      }
    },
    about: {
      title: '关于',
      introduction: `SoybeanAdmin 是一个优雅且功能强大的后台管理模板，基于最新的前端技术栈，包括 Vue3, Vite5, TypeScript, Pinia 和 UnoCSS。它内置了丰富的主题配置和组件，代码规范严谨，实现了自动化的文件路由系统。此外，它还采用了基于 ApiFox 的在线Mock数据方案。SoybeanAdmin 为您提供了一站式的后台管理解决方案，无需额外配置，开箱即用。同样是一个快速学习前沿技术的最佳实践。`,
      projectInfo: {
        title: '项目信息',
        version: '版本',
        latestBuildTime: '最新构建时间',
        githubLink: 'Github 地址',
        previewLink: '预览地址'
      },
      prdDep: '生产依赖',
      devDep: '开发依赖'
    },
    home: {
      greeting: '欢迎回来，{userName}，大数据平台运行正常!',
      weatherDesc: '实时监控大数据任务与集群状态',
      projectCount: '任务总数',
      todo: '运行中',
      message: '集群数',
      downloadCount: '今日调度',
      registerCount: '失败任务',
      schedule: '任务状态分布',
      study: '未启动',
      work: '运行中',
      rest: '已完成',
      entertainment: '失败',
      visitCount: '任务总数',
      turnover: '运行中任务',
      dealCount: '失败任务',
      projectNews: {
        title: '平台动态',
        moreNews: '更多动态',
        desc1: '大数据平台 v2.0 发布，支持 Flink Standalone 模式！',
        desc2: '新增任务状态同步功能，实时追踪任务运行状态。',
        desc3: '告警系统集成钉钉通知，支持状态与超时告警。',
        desc4: 'Spring Boot 升级至 3.5.14，Java 21 运行时。',
        desc5: '前端页面重设计，新增数据可视化仪表盘。'
      },
      creativity: '创意',
      clusterCount: '集群总数',
      runningTask: '运行中任务',
      failedTask: '失败任务',
      todaySchedule: '今日调度',
      taskTrend: '近7天任务趋势',
      taskStatusPie: '任务状态分布'
    },
    function: {
      tab: {
        tabOperate: {
          title: '标签页操作',
          addTab: '添加标签页',
          addTabDesc: '跳转到关于页面',
          closeTab: '关闭标签页',
          closeCurrentTab: '关闭当前标签页',
          closeAboutTab: '关闭"关于"标签页',
          addMultiTab: '添加多标签页',
          addMultiTabDesc1: '跳转到多标签页页面',
          addMultiTabDesc2: '跳转到多标签页页面(带有查询参数)'
        },
        tabTitle: {
          title: '标签页标题',
          changeTitle: '修改标题',
          change: '修改',
          resetTitle: '重置标题',
          reset: '重置'
        }
      },
      multiTab: {
        routeParam: '路由参数',
        backTab: '返回 function_tab'
      },
      toggleAuth: {
        toggleAccount: '切换账号',
        authHook: '权限钩子函数 `hasAuth`',
        superAdminVisible: '超级管理员可见',
        adminVisible: '管理员可见',
        adminOrUserVisible: '管理员和用户可见'
      },
      request: {
        repeatedErrorOccurOnce: '重复请求错误只出现一次',
        repeatedError: '重复请求错误',
        repeatedErrorMsg1: '自定义请求错误 1',
        repeatedErrorMsg2: '自定义请求错误 2'
      }
    },
    manage: {
      common: {
        status: {
          enable: '启用',
          disable: '禁用',
          accept: '未启动',
          running: '进行中',
          failed: '失败',
          finished: '已完成',
          killed: '已停止'
        },
        launch: '执行',
        stop: '停止',
        confirmLaunch: '确认执行吗？',
        confirmStop: '确认停止吗？',
        streaming: '流处理',
        batch: '批处理'
      },
      task: {
        title: '任务列表',
        taskName: '任务名称',
        engineType: '引擎类型',
        createTime: '创建时间',
        latestLaunchTime: '最近启动时间',
        updateTime: '更新时间',
        taskStatus: '任务状态',
        taskType: '任务类型',
        taskMode: '任务模式',
        form: {
          taskName: '请输入任务名称',
          engineType: '请选择引擎类型',
          taskType: '请输入任务类型',
          taskStatus: '请选择任务状态',
          taskMode: '请选择任务模式'
        },
        detail: '查看任务',
        addTask: '新增任务',
        editTask: '编辑任务'
      },
      schedule: {
        common: {
          cron: 'cron表达式'
        },
        title: '调度列表',
        scheduleName: '调度名称',
        scheduleStatus: '调度状态',
        scheduleFrequency: '调度频率',
        cron: 'cron表达式',
        startTime: '开始调度时间',
        endTime: '结束调度时间',
        maxRetryTimes: '最大重试次数',
        retryInterval: '重试时间间隔',
        timeout: '超时时间',
        createTime: '创建时间',
        updateTime: '更新时间',
        form: {
          scheduleName: '请输入调度名称',
          scheduleStatus: '请选择调度状态',
          taskName: '请选择调度的任务',
          cron: '请输入cron正确的表达式',
          startTime: '请选择开始调度时间',
          endTime: '请选择结束调度时间',
          scheduleFrequency: '请选择调度频率'
        },
        addSchedule: '新增任务调度',
        editSchedule: '编辑任务调度'
      },
      task_instance: {
        title: '任务实例列表',
        taskStatus: '实例状态',
        createTime: '创建时间'
      },
      role: {
        title: '角色列表',
        roleName: '角色名称',
        roleCode: '角色编码',
        roleStatus: '角色状态',
        roleDesc: '角色描述',
        menuAuth: '菜单权限',
        buttonAuth: '按钮权限',
        form: {
          roleName: '请输入角色名称',
          roleCode: '请输入角色编码',
          roleStatus: '请选择角色状态',
          roleDesc: '请输入角色描述'
        },
        addRole: '新增角色',
        editRole: '编辑角色'
      },
      user: {
        title: '用户列表',
        userName: '用户名',
        userGender: '性别',
        nickName: '昵称',
        userPhone: '手机号',
        userEmail: '邮箱',
        userStatus: '用户状态',
        userRole: '用户角色',
        form: {
          userName: '请输入用户名',
          userGender: '请选择性别',
          nickName: '请输入昵称',
          userPhone: '请输入手机号',
          userEmail: '请输入邮箱',
          userStatus: '请选择用户状态',
          userRole: '请选择用户角色'
        },
        addUser: '新增用户',
        editUser: '编辑用户',
        gender: {
          male: '男',
          female: '女'
        }
      },
      menu: {
        home: '首页',
        title: '菜单列表',
        id: 'ID',
        parentId: '父级菜单ID',
        menuType: '菜单类型',
        menuName: '菜单名称',
        routeName: '路由名称',
        query: '路由参数',
        routePath: '路由路径',
        pathParam: '路径参数',
        layout: '布局',
        page: '页面组件',
        i18nKey: '国际化key',
        icon: '图标',
        localIcon: '本地图标',
        iconTypeTitle: '图标类型',
        order: '排序',
        keepAlive: '缓存路由',
        href: '外链',
        hideInMenu: '隐藏菜单',
        activeMenu: '高亮的菜单',
        multiTab: '支持多页签',
        fixedIndexInTab: '固定在页签中的序号',
        button: '按钮',
        buttonCode: '按钮编码',
        buttonDesc: '按钮描述',
        menuStatus: '菜单状态',
        constant: '常量路由',
        form: {
          home: '请选择首页',
          menuType: '请选择菜单类型',
          menuName: '请输入菜单名称',
          routeName: '请输入路由名称',
          routePath: '请输入路由路径',
          pathParam: '请输入路径参数',
          page: '请选择页面组件',
          layout: '请选择布局组件',
          i18nKey: '请输入国际化key',
          icon: '请输入图标',
          queryKey: '请输入路由参数Key',
          queryValue: '请输入路由参数Value',
          localIcon: '请选择本地图标',
          order: '请输入排序',
          keepAlive: '请选择是否缓存路由',
          href: '请输入外链',
          hideInMenu: '请选择是否隐藏菜单',
          activeMenu: '请输入高亮的菜单的路由名称',
          multiTab: '请选择是否支持多标签',
          fixedInTab: '请选择是否固定在页签中',
          fixedIndexInTab: '请输入固定在页签中的序号',
          button: '请选择是否按钮',
          buttonCode: '请输入按钮编码',
          buttonDesc: '请输入按钮描述',
          menuStatus: '请选择菜单状态'
        },
        addMenu: '新增菜单',
        editMenu: '编辑菜单',
        addChildMenu: '新增子菜单',
        type: {
          directory: '目录',
          menu: '菜单'
        },
        iconType: {
          iconify: 'iconify图标',
          local: '本地图标'
        }
      }
    },
    config: {
      common: {
        status: {
          enable: '启用',
          disable: '停用'
        }
      },
      cluster: {
        title: '集群配置',
        clusterName: '集群名称',
        clusterType: '集群类型',
        clusterStatus: '集群状态',
        createTime: '创建时间',
        updateTime: '更新时间',
        form: {
          clusterName: '请输入集群名称',
          clusterType: '请输入集群类型',
          clusterStatus: '请输入集群状态',
          coreSite: '请上传core-site.xml文件',
          hdfsSite: '请上传hdfs-site.xml文件',
          yarnSite: '请上传yarn-site.xml文件'
        },
        cluster_type: {
          yarn: 'Yarn',
          k8s: 'Kubernetes',
          flink_standalone: 'flink实例'
        },
        addCluster: '添加集群',
        editCluster: '编辑集群'
      },
      cluster_detail: {
        title: '集群配置详情'
      },
      alert: {
        title: '告警配置',
        alertName: '告警实例名称',
        alertType: '告警实例类型',
        alertStatus: '告警实例状态',
        createTime: '创建时间',
        alert_type: {
          dingding: '钉钉'
        },
        form: {
          name: '请输入告警实例名称',
          type: '请选择告警实例类型',
          status: '请选择告警实例状态'
        },
        addAlert: '新增告警实例',
        editAlert: '修改告警配置'
      },
      alert_policy: {
        title: '告警策略列表',
        alertPolicyName: '名称',
        alertPolicyStatus: '状态',
        conditions: '条件',
        createTime: '创建时间',
        form: {
          name: '请输入告警策略名称',
          status: '请选择告警策略状态',
          conditionKey: '请选择触发条件',
          operator: '请选择判断条件',
          conditionValue: '请选择条件值'
        },
        addAlertPolicy: '新增告警策略',
        editAlertPolicy: '编辑告警策略'
      }
    }
  },
  form: {
    required: '不能为空',
    userName: {
      required: '请输入用户名',
      invalid: '用户名格式不正确'
    },
    phone: {
      required: '请输入手机号',
      invalid: '手机号格式不正确'
    },
    pwd: {
      required: '请输入密码',
      invalid: '密码格式不正确，6-18位字符，支持字母、数字、下划线和@#$%&+=!'
    },
    confirmPwd: {
      required: '请输入确认密码',
      invalid: '两次输入密码不一致'
    },
    code: {
      required: '请输入验证码',
      invalid: '验证码格式不正确'
    },
    email: {
      required: '请输入邮箱',
      invalid: '邮箱格式不正确'
    }
  },
  dropdown: {
    closeCurrent: '关闭',
    closeOther: '关闭其它',
    closeLeft: '关闭左侧',
    closeRight: '关闭右侧',
    closeAll: '关闭所有'
  },
  icon: {
    themeConfig: '主题配置',
    themeSchema: '主题模式',
    lang: '切换语言',
    fullscreen: '全屏',
    fullscreenExit: '退出全屏',
    reload: '刷新页面',
    collapse: '折叠菜单',
    expand: '展开菜单',
    pin: '固定',
    unpin: '取消固定'
  }
};

export default local;
