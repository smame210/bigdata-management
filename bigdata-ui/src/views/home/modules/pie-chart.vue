<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { fetchDashboardStatusDistribution } from '@/service/api';

defineOptions({
  name: 'PieChart'
});

const appStore = useAppStore();

const distributionData = ref<Api.Dashboard.StatusDistribution[]>([]);

function getStatusLabel(status: number) {
  const statusLabels: Record<number, string> = {
    0: $t('page.home.study'),
    1: $t('page.home.work'),
    2: $t('page.home.work'),
    3: $t('page.home.rest'),
    4: $t('page.home.entertainment'),
    5: $t('page.home.entertainment'),
    6: $t('page.home.study')
  };

  return statusLabels[status] || $t('page.home.study');
}

function getChartData() {
  return distributionData.value.map(item => ({
    name: getStatusLabel(item.status),
    value: item.count
  }));
}

const { domRef, updateOptions } = useEcharts(() => ({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    bottom: '1%',
    left: 'center',
    itemStyle: {
      borderWidth: 0
    }
  },
  series: [
    {
      color: ['#94a3b8', '#3b82f6', '#10b981', '#ef4444', '#f59e0b'],
      name: $t('page.home.schedule'),
      type: 'pie',
      radius: ['45%', '75%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 1
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '12'
        }
      },
      labelLine: {
        show: false
      },
      data: [] as { name: string; value: number }[]
    }
  ]
}));

let refreshTimer: ReturnType<typeof setInterval> | null = null;

async function loadDistribution() {
  const { data } = await fetchDashboardStatusDistribution();
  if (data && data.length > 0) {
    distributionData.value = data;
    updateOptions(opts => {
      opts.series[0].data = getChartData();
      return opts;
    });
  }
}

function updateLocale() {
  updateOptions((opts, factory) => {
    const originOpts = factory();
    opts.series[0].name = originOpts.series[0].name;
    opts.series[0].data = getChartData();
    return opts;
  });
}

onMounted(() => {
  loadDistribution();
  refreshTimer = setInterval(loadDistribution, 30_000);
});

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
});

watch(
  () => appStore.locale,
  () => updateLocale()
);
</script>

<template>
  <ACard :bordered="false" class="card-wrapper" :title="$t('page.home.taskStatusPie')">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </ACard>
</template>

<style scoped></style>
