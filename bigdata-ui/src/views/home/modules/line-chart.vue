<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { fetchDashboardTaskTrend } from '@/service/api';

defineOptions({
  name: 'LineChart'
});

const appStore = useAppStore();
const loading = ref(false);

const { domRef, updateOptions } = useEcharts(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  legend: {
    data: [$t('page.home.visitCount'), $t('page.home.turnover'), $t('page.home.dealCount')]
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [] as string[]
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      color: '#4f46e5',
      name: $t('page.home.visitCount'),
      type: 'line',
      smooth: true,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0.25, color: '#4f46e5' },
            { offset: 1, color: '#fff' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [] as number[]
    },
    {
      color: '#10b981',
      name: $t('page.home.turnover'),
      type: 'line',
      smooth: true,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0.25, color: '#10b981' },
            { offset: 1, color: '#fff' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [] as number[]
    },
    {
      color: '#f59e0b',
      name: $t('page.home.dealCount'),
      type: 'line',
      smooth: true,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0.25, color: '#f59e0b' },
            { offset: 1, color: '#fff' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [] as number[]
    }
  ]
}));

let refreshTimer: ReturnType<typeof setInterval> | null = null;

async function loadTrend() {
  loading.value = true;
  const { data } = await fetchDashboardTaskTrend(7);
  if (data && data.length > 0) {
    updateOptions(opts => {
      opts.xAxis.data = data.map(item => item.date.slice(5)); // MM-DD
      opts.series[0].data = data.map(item => item.total);
      opts.series[1].data = data.map(item => item.finished);
      opts.series[2].data = data.map(item => item.failed);
      return opts;
    });
  }
  loading.value = false;
}

function updateLocale() {
  updateOptions((opts, factory) => {
    const originOpts = factory();
    opts.legend.data = originOpts.legend.data;
    opts.series.forEach((series, index) => {
      series.name = originOpts.series[index].name;
    });
    return opts;
  });
}

onMounted(() => {
  loadTrend();
  refreshTimer = setInterval(loadTrend, 30_000);
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
  <ACard :bordered="false" class="card-wrapper" :title="$t('page.home.taskTrend')">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </ACard>
</template>

<style scoped></style>
