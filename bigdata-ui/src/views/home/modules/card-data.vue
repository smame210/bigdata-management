<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { useRouterPush } from '@/hooks/common/router';
import { $t } from '@/locales';
import { fetchDashboardStatistics } from '@/service/api';

defineOptions({
  name: 'CardData'
});

const { routerPushByKey } = useRouterPush();

interface CardData {
  key: string;
  title: string;
  value: number;
  unit: string;
  color: {
    start: string;
    end: string;
  };
  icon: string;
  route: App.Global.RouteKey;
}

const cardData = ref<CardData[]>([
  {
    key: 'taskCount',
    title: $t('page.home.projectCount'),
    value: 0,
    unit: '',
    color: { start: '#4f46e5', end: '#7c3aed' },
    icon: 'ant-design:database-outlined',
    route: 'manage_task'
  },
  {
    key: 'runningTask',
    title: $t('page.home.runningTask'),
    value: 0,
    unit: '',
    color: { start: '#059669', end: '#10b981' },
    icon: 'ant-design:play-circle-outlined',
    route: 'manage_taskinstance'
  },
  {
    key: 'failedTask',
    title: $t('page.home.failedTask'),
    value: 0,
    unit: '',
    color: { start: '#dc2626', end: '#f87171' },
    icon: 'ant-design:close-circle-outlined',
    route: 'manage_taskinstance'
  },
  {
    key: 'clusterCount',
    title: $t('page.home.clusterCount'),
    value: 0,
    unit: '',
    color: { start: '#2563eb', end: '#60a5fa' },
    icon: 'ant-design:cluster-outlined',
    route: 'config_cluster'
  }
]);

let refreshTimer: ReturnType<typeof setInterval> | null = null;

async function loadStatistics() {
  const { data } = await fetchDashboardStatistics();
  if (data) {
    cardData.value = [
      {
        key: 'taskCount',
        title: $t('page.home.projectCount'),
        value: data.totalTasks,
        unit: '',
        color: { start: '#4f46e5', end: '#7c3aed' },
        icon: 'ant-design:database-outlined',
        route: 'manage_task'
      },
      {
        key: 'runningTask',
        title: $t('page.home.runningTask'),
        value: data.runningInstances,
        unit: '',
        color: { start: '#059669', end: '#10b981' },
        icon: 'ant-design:play-circle-outlined',
        route: 'manage_taskinstance'
      },
      {
        key: 'failedTask',
        title: $t('page.home.failedTask'),
        value: data.failedInstances,
        unit: '',
        color: { start: '#dc2626', end: '#f87171' },
        icon: 'ant-design:close-circle-outlined',
        route: 'manage_taskinstance'
      },
      {
        key: 'clusterCount',
        title: $t('page.home.clusterCount'),
        value: data.totalClusters,
        unit: '',
        color: { start: '#2563eb', end: '#60a5fa' },
        icon: 'ant-design:cluster-outlined',
        route: 'config_cluster'
      }
    ];
  }
}

onMounted(() => {
  loadStatistics();
  refreshTimer = setInterval(loadStatistics, 30_000);
});

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
});

function handleCardClick(route: App.Global.RouteKey) {
  routerPushByKey(route);
}

function getGradientColor(color: CardData['color']) {
  return `linear-gradient(to bottom right, ${color.start}, ${color.end})`;
}
</script>

<template>
  <ACard :bordered="false" size="small" class="card-wrapper">
    <ARow :gutter="[16, 16]">
      <ACol v-for="item in cardData" :key="item.key" :span="24" :md="12" :lg="6">
        <div
          class="flex-1 cursor-pointer rd-8px px-16px pb-4px pt-8px text-white transition-all hover:scale-[1.02]"
          :style="{ backgroundImage: getGradientColor(item.color) }"
          @click="handleCardClick(item.route)"
        >
          <h3 class="text-16px opacity-90">{{ item.title }}</h3>
          <div class="flex justify-between pt-12px">
            <SvgIcon :icon="item.icon" class="text-32px opacity-80" />
            <CountTo
              :prefix="item.unit"
              :start-value="1"
              :end-value="item.value"
              class="text-30px text-white dark:text-dark"
            />
          </div>
        </div>
      </ACol>
    </ARow>
  </ACard>
</template>

<style scoped></style>
