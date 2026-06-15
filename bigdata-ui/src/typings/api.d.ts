/**
 * Namespace Api
 *
 * All backend api type
 */
declare namespace Api {
  namespace Common {
    /** common params of paginating */
    interface PaginatingCommonParams {
      /** current page number */
      current: number;
      /** page size */
      size: number;
      /** total count */
      total: number;
    }

    /** common params of paginating query list data */
    interface PaginatingQueryRecord<T = any> extends PaginatingCommonParams {
      records: T[];
    }

    /** common search params of table */
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /**
     * enable status
     *
     * - "1": enabled
     * - "2": disabled
     */
    type EnableStatus = '1' | '2';

    /** common record */
    type CommonRecord<T = any> = {
      /** record id */
      id: number;
      /** record creator */
      createBy: string;
      /** record create time */
      createTime: string;
      /** record updater */
      updateBy: string;
      /** record update time */
      updateTime: string;
      /** record status */
      status: EnableStatus;
    } & T;
  }

  /**
   * Namespace Auth
   *
   * Backend api module: "auth"
   */
  namespace Auth {
    interface LoginToken {
      token: string;
      refreshToken: string;
    }

    interface UserInfo {
      userId: string;
      userName: string;
      roles: string[];
      buttons: string[];
    }
  }

  /**
   * Namespace Route
   *
   * Backend api module: "route"
   */
  namespace Route {
    type ElegantConstRoute = import('@elegant-router/types').ElegantConstRoute;

    interface MenuRoute extends ElegantConstRoute {
      id: string;
    }

    interface UserRoute {
      routes: MenuRoute[];
      home: import('@elegant-router/types').LastLevelRouteKey;
    }
  }

  /**
   * namespace SystemManage
   *
   * backend api module: "systemManage"
   */
  namespace SystemManage {
    /** role */
    type Role = Common.CommonRecord<{
      /** role name */
      roleName: string;
      /** role code */
      roleCode: string;
      /** role description */
      roleDesc: string;
    }>;

    /** role search params */
    type RoleSearchParams = Partial<
      Pick<Api.SystemManage.Role, 'roleName' | 'roleCode' | 'status'> & Common.CommonSearchParams
    >;

    /** role list */
    type RoleList = Common.PaginatingQueryRecord<Role>;

    /** all role */
    type AllRole = Pick<Role, 'id' | 'roleName' | 'roleCode'>;

    /**
     * user gender
     *
     * - "1": "male"
     * - "2": "female"
     */
    type UserGender = '1' | '2';

    /** user */
    type User = Common.CommonRecord<{
      /** user name */
      userName: string;
      /** user gender */
      userGender: UserGender;
      /** user nick name */
      nickName: string;
      /** user phone */
      userPhone: string;
      /** user email */
      userEmail: string;
      /** user role code collection */
      userRoles: string[];
    }>;

    /** user search params */
    type UserSearchParams = Partial<
      Pick<Api.SystemManage.User, 'userName' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail' | 'status'> &
        Common.CommonSearchParams
    >;

    /** user list */
    type UserList = Common.PaginatingQueryRecord<User>;

    /** user save params */
    type UserSaveParams = {
      id?: number;
      userName?: string;
      nickName?: string;
      gender?: number;
      phone?: string;
      email?: string;
      status?: number;
    };

    /**
     * menu type
     *
     * - "1": directory
     * - "2": menu
     */
    type MenuType = '1' | '2';

    type MenuButton = {
      /**
       * button code
       *
       * it can be used to control the button permission
       */
      code: string;
      /** button description */
      desc: string;
    };

    /**
     * icon type
     *
     * - "1": iconify icon
     * - "2": local icon
     */
    type IconType = '1' | '2';

    type MenuPropsOfRoute = Pick<
      import('vue-router').RouteMeta,
      | 'i18nKey'
      | 'keepAlive'
      | 'constant'
      | 'order'
      | 'href'
      | 'hideInMenu'
      | 'activeMenu'
      | 'multiTab'
      | 'fixedIndexInTab'
      | 'query'
    >;

    type Menu = Common.CommonRecord<{
      /** parent menu id */
      parentId: number;
      /** menu type */
      menuType: MenuType;
      /** menu name */
      menuName: string;
      /** route name */
      routeName: string;
      /** route path */
      routePath: string;
      /** component */
      component?: string;
      /** iconify icon name or local icon name */
      icon: string;
      /** icon type */
      iconType: IconType;
      /** buttons */
      buttons?: MenuButton[] | null;
      /** children menu */
      children?: Menu[];
    }> &
      MenuPropsOfRoute;

    /** menu list */
    type MenuList = Common.PaginatingQueryRecord<Menu>;

    type MenuTree = {
      id: number;
      label: string;
      pId: number;
      children?: MenuTree[];
    };
  }

  /**
   * namespace TaskManage
   *
   * backend api module: "task-manage"
   */
  namespace TaskManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    type TaskStatus = 0 | 1;

    type EngineType = 'flink' | 'spark';

    type TaskType = 'session' | 'pre-job' | 'application';

    type TaskMode = 'batch' | 'streaming';

    /** task */
    type Task = Common.CommonRecord<{
      taskName: string;
      engineType: EngineType;
      taskMode: TaskMode;
      taskStatus: TaskStatus;
      latestLaunchTime: string;
      clusterId: number;
      clusterName: string;
      config: any;
    }>;

    /** task search params */
    type TaskSearchParams = Partial<
      Pick<Api.TaskManage.Task, 'taskName' | 'engineType' | 'taskMode' | 'taskStatus' | 'clusterId'> &
        CommonSearchParams
    >;

    /** task list */
    type TaskList = Common.PaginatingQueryRecord<Task>;
  }

  namespace ClusterManage {
    type ClusterStatus = 0 | 1;
    type ClusterHealthStatus = 0 | 1 | 2 | 3;

    type ClusterType = 'yarn' | 'standalone';

    type Cluster = Common.CommonRecord<{
      clusterName: string;
      clusterType: ClusterType;
      clusterStatus: ClusterStatus;
      healthStatus?: ClusterHealthStatus;
      healthCheckedAt?: string;
      metadata: string;
      desc: string;
    }>;

    type ClusterSearchParams = Partial<
      Pick<Api.ClusterManage.Cluster, 'clusterName' | 'clusterType' | 'clusterStatus'>
    >;
  }

  namespace AlertManage {
    type AlertStatus = 0 | 1;

    type AlertType = 'dingding';

    type AlertBase = Common.CommonRecord<{
      name: string;
      type: AlertType;
      metadata: any;
      template?: string;
    }>;

    type Alert = Omit<AlertBase, 'status'> & {
      status: AlertStatus;
    };

    type AlertSearchParams = Partial<Pick<Api.AlertManage.Alert, 'name' | 'type' | 'status'>>;
  }

  namespace AlertPolicyManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    type AlertPolicyStatus = 0 | 1;

    type AlertPolicyBase = Common.CommonRecord<{
      name: string;
      alertId: number | undefined;
      alertName: string;
      taskId: number | undefined;
      taskName: string;
      type: Api.AlertManage.AlertType;
      conditions: any;
    }>;

    type AlertPolicy = Omit<AlertPolicyBase, 'status'> & {
      status: AlertPolicyStatus; // 重写status类型
    };

    type AlertPolicySearchParams = Partial<
      Pick<Api.AlertPolicyManage.AlertPolicy, 'name' | 'alertName' | 'taskName' | 'status'> & CommonSearchParams
    >;

    type AlertPolicyList = Common.PaginatingQueryRecord<AlertPolicy>;
  }

  namespace ScheduleManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    type ScheduleStatus = 0 | 1;

    type ScheduleFrequency = 'ONCE' | 'MINUTELY' | 'HOURLY' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'CUSTOM';

    type Schedule = Common.CommonRecord<{
      /** 关联的任务ID */
      taskId: number;
      /** 任务名称 */
      taskName: string;
      /** 引擎类型 */
      engineType: Api.TaskManage.EngineType;
      /** 集群名称 */
      clusterName: string;
      /** 调度名称 */
      scheduleName: string;
      /** 调度状态：0-禁用 1-启用 */
      scheduleStatus: ScheduleStatus;
      /** 调度频率 */
      scheduleFrequency: ScheduleFrequency;
      /** Cron表达式 */
      cronExpression: string;
      /** 调度生效开始时间 */
      startTime: string;
      /** 调度生效结束时间 */
      endTime: string;
      /** 最大重试次数 */
      maxRetryTimes?: number;
      /** 重试间隔（秒） */
      retryInterval?: number;
      /** 任务超时时间（秒） */
      timeoutSeconds?: number;
      /** 依赖的任务IDs */
      dependencyTaskIds?: string;
      /** 任务参数 */
      params?: string;
    }>;

    type ScheduleSearchParams = Partial<
      Pick<Api.ScheduleManage.Schedule, 'taskName' | 'engineType' | 'clusterName' | 'scheduleName' | 'scheduleStatus'> &
        CommonSearchParams
    >;

    type ScheduleList = Common.PaginatingQueryRecord<Schedule>;
  }

  namespace TaskInstanceManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    type TaskInstanceStatus = 0 | 1 | 2 | 3 | 4 | 5 | 6;

    type TaskInstance = Common.CommonRecord<{
      taskName: string;
      engineType: Api.TaskManage.EngineType;
      taskMode: Api.TaskManage.TaskMode;
      taskStatus: TaskInstanceStatus;
      clusterType: Api.ClusterManage.ClusterType;
      clusterId: number;
      clusterName: string;
      scheduleName: string;
      taskInstanceId: string;
      clusterMetadata: string;
    }>;

    type TaskInstanceSearchParams = Partial<
      Pick<
        Api.TaskInstanceManage.TaskInstance,
        'taskName' | 'engineType' | 'clusterName' | 'scheduleName' | 'taskMode'
      > &
        CommonSearchParams
    >;

    type TaskInstanceList = Common.PaginatingQueryRecord<TaskInstance>;
  }

  /**
   * namespace Dashboard
   *
   * backend api module: "dashboard"
   */
  namespace Dashboard {
    interface Statistics {
      totalTasks: number;
      runningInstances: number;
      failedInstances: number;
      totalClusters: number;
    }

    interface TaskTrend {
      date: string;
      total: number;
      finished: number;
      failed: number;
    }

    interface StatusDistribution {
      status: number;
      statusName?: string;
      count: number;
    }
  }
}
