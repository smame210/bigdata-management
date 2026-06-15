import { $t } from '@/locales';
import { useSvgIcon } from '@/hooks/common/icon';
/**
 * Filter auth routes by roles
 *
 * @param routes Auth routes
 * @param roles Roles
 */
export function filterAuthRoutesByRoles(routes, roles) {
  return routes.flatMap(route => filterAuthRouteByRoles(route, roles));
}
/**
 * Filter auth route by roles
 *
 * @param route Auth route
 * @param roles Roles
 */
function filterAuthRouteByRoles(route, roles) {
  const routeRoles = (route.meta && route.meta.roles) || [];
  // if the route's "roles" is empty, then it is allowed to access
  const isEmptyRoles = !routeRoles.length;
  // if the user's role is included in the route's "roles", then it is allowed to access
  const hasPermission = routeRoles.some(role => roles.includes(role));
  const filterRoute = { ...route };
  if (filterRoute.children?.length) {
    filterRoute.children = filterRoute.children.flatMap(item => filterAuthRouteByRoles(item, roles));
  }
  // Exclude the route if it has no children after filtering
  if (filterRoute.children?.length === 0) {
    return [];
  }
  return hasPermission || isEmptyRoles ? [filterRoute] : [];
}
/**
 * sort route by order
 *
 * @param route route
 */
function sortRouteByOrder(route) {
  if (route.children?.length) {
    route.children.sort((next, prev) => (Number(next.meta?.order) || 0) - (Number(prev.meta?.order) || 0));
    route.children.forEach(sortRouteByOrder);
  }
  return route;
}
/**
 * sort routes by order
 *
 * @param routes routes
 */
export function sortRoutesByOrder(routes) {
  routes.sort((next, prev) => (Number(next.meta?.order) || 0) - (Number(prev.meta?.order) || 0));
  routes.forEach(sortRouteByOrder);
  return routes;
}
/**
 * Get global menus by auth routes
 *
 * @param routes Auth routes
 */
export function getGlobalMenusByAuthRoutes(routes) {
  const menus = [];
  routes.forEach(route => {
    if (!route.meta?.hideInMenu) {
      const menu = getGlobalMenuByBaseRoute(route);
      if (route.children?.some(child => !child.meta?.hideInMenu)) {
        menu.children = getGlobalMenusByAuthRoutes(route.children);
      }
      menus.push(menu);
    }
  });
  return menus;
}
/**
 * Update locale of global menus
 *
 * @param menus
 */
export function updateLocaleOfGlobalMenus(menus) {
  const result = [];
  menus.forEach(menu => {
    const { i18nKey, label, children } = menu;
    const newLabel = i18nKey ? $t(i18nKey) : label;
    const newMenu = {
      ...menu,
      label: newLabel,
      title: newLabel
    };
    if (children?.length) {
      newMenu.children = updateLocaleOfGlobalMenus(children);
    }
    result.push(newMenu);
  });
  return result;
}
/**
 * Get global menu by route
 *
 * @param route
 */
function getGlobalMenuByBaseRoute(route) {
  const { SvgIconVNode } = useSvgIcon();
  const { name, path } = route;
  const { title, i18nKey, icon = import.meta.env.VITE_MENU_ICON, localIcon, iconFontSize } = route.meta ?? {};
  const label = i18nKey ? $t(i18nKey) : title;
  const menu = {
    key: name,
    label,
    i18nKey,
    routeKey: name,
    routePath: path,
    icon: SvgIconVNode({ icon, localIcon, fontSize: iconFontSize || 20 }),
    title: label
  };
  return menu;
}
/**
 * Get cache route names
 *
 * @param routes Vue routes (two levels)
 */
export function getCacheRouteNames(routes) {
  const cacheNames = [];
  routes.forEach(route => {
    // only get last two level route, which has component
    route.children?.forEach(child => {
      if (child.component && child.meta?.keepAlive) {
        cacheNames.push(child.name);
      }
    });
  });
  return cacheNames;
}
/**
 * Is route exist by route name
 *
 * @param routeName
 * @param routes
 */
export function isRouteExistByRouteName(routeName, routes) {
  return routes.some(route => recursiveGetIsRouteExistByRouteName(route, routeName));
}
/**
 * Recursive get is route exist by route name
 *
 * @param route
 * @param routeName
 */
function recursiveGetIsRouteExistByRouteName(route, routeName) {
  let isExist = route.name === routeName;
  if (isExist) {
    return true;
  }
  if (route.children && route.children.length) {
    isExist = route.children.some(item => recursiveGetIsRouteExistByRouteName(item, routeName));
  }
  return isExist;
}
/**
 * Get selected menu key path
 *
 * @param selectedKey
 * @param menus
 */
export function getSelectedMenuKeyPathByKey(selectedKey, menus) {
  const keyPath = [];
  menus.some(menu => {
    const path = findMenuPath(selectedKey, menu);
    const find = Boolean(path?.length);
    if (find) {
      keyPath.push(...path);
    }
    return find;
  });
  return keyPath;
}
/**
 * Find menu path
 *
 * @param targetKey Target menu key
 * @param menu Menu
 */
function findMenuPath(targetKey, menu) {
  const path = [];
  function dfs(item) {
    path.push(item.key);
    if (item.key === targetKey) {
      return true;
    }
    if (item.children) {
      for (const child of item.children) {
        if (dfs(child)) {
          return true;
        }
      }
    }
    path.pop();
    return false;
  }
  if (dfs(menu)) {
    return path;
  }
  return null;
}
/**
 * Get breadcrumbs by route
 *
 * @param route
 * @param menus
 */
export function getBreadcrumbsByRoute(route, menus) {
  const key = route.name;
  const activeKey = route.meta?.activeMenu;
  const menuKey = activeKey || key;
  for (const menu of menus) {
    if (menu.key === menuKey) {
      return [menu];
    }
    if (menu.key === activeKey) {
      const ROUTE_DEGREE_SPLITTER = '_';
      const parentKey = key.split(ROUTE_DEGREE_SPLITTER).slice(0, -1).join(ROUTE_DEGREE_SPLITTER);
      const breadcrumbMenu = getGlobalMenuByBaseRoute(route);
      if (parentKey !== activeKey) {
        return [breadcrumbMenu];
      }
      return [menu, breadcrumbMenu];
    }
    if (menu.children?.length) {
      const result = getBreadcrumbsByRoute(route, menu.children);
      if (result.length > 0) {
        return [menu, ...result];
      }
    }
  }
  return [];
}
/**
 * Transform menu to searchMenus
 *
 * @param menus - menus
 * @param treeMap
 */
export function transformMenuToSearchMenus(menus, treeMap = []) {
  if (menus && menus.length === 0) return [];
  return menus.reduce((acc, cur) => {
    if (!cur.children) {
      acc.push(cur);
    }
    if (cur.children && cur.children.length > 0) {
      transformMenuToSearchMenus(cur.children, treeMap);
    }
    return acc;
  }, treeMap);
}
