<template>
  <div class="app-container dashboard-container">
    <!-- 顶部刷新栏 -->
    <el-row :gutter="10" type="flex" align="middle" class="mb8">
      <el-col :span="12">
        <span class="dashboard-title">文瀛餐厅 · 数据看板</span>
        <span class="dashboard-subtitle">实时反映经营与库存情况</span>
      </el-col>
      <el-col :span="12" style="text-align: right;">
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          :loading="loading"
          @click="loadDashboard"
        >刷新数据</el-button>
      </el-col>
    </el-row>

    <!-- 四个数字卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="12" :sm="12" :lg="6" v-for="card in cards" :key="card.key">
        <div class="stat-card" :class="'stat-card--' + card.theme">
          <div class="stat-card__icon">
            <i :class="card.icon"></i>
          </div>
          <div class="stat-card__body">
            <div class="stat-card__label">{{ card.label }}</div>
            <div class="stat-card__value">{{ card.value }}</div>
            <div class="stat-card__suffix" v-if="card.suffix">{{ card.suffix }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 两个图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="chart-card__header">
            <span><i class="el-icon-data-line"></i> 近 7 天营收趋势</span>
            <span class="chart-card__hint">单位：元</span>
          </div>
          <div ref="trendChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="chart-card__header">
            <span><i class="el-icon-trophy"></i> 销量 Top10</span>
            <span class="chart-card__hint">按已完成订单份数统计</span>
          </div>
          <div ref="topChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存预警表格 -->
    <el-card class="warning-card" shadow="hover">
      <div slot="header" class="chart-card__header">
        <span><i class="el-icon-warning"></i> 库存预警</span>
        <span class="chart-card__hint">当前库存量低于安全库存的食材</span>
      </div>
      <el-table
        v-loading="loading"
        :data="warningList"
        border
        stripe
        size="small"
        empty-text="暂无库存预警"
      >
        <el-table-column label="食材ID" align="center" prop="ingredientId" width="80" />
        <el-table-column label="食材名称" align="center" prop="ingredientName" min-width="160" />
        <el-table-column label="当前库存" align="center" prop="stock" width="140">
          <template slot-scope="scope">
            <el-tag type="danger" size="small">{{ scope.row.stock }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="单位" align="center" prop="unit" width="80" />
        <el-table-column label="安全库存" align="center" prop="safetyStock" width="120" />
        <el-table-column label="缺口" align="center" width="120">
          <template slot-scope="scope">
            <span style="color: #F56C6C; font-weight: 600;">
              {{ formatGap(scope.row) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboard } from '@/api/restaurant/stats'

export default {
  name: 'RestaurantStats',
  data() {
    return {
      loading: false,
      todayRevenue: 0,
      todayOrderCount: 0,
      dishCount: 0,
      warningCount: 0,
      revenueTrend: [],
      topDishes: [],
      warningList: [],
      trendChart: null,
      topChart: null
    }
  },
  computed: {
    cards() {
      return [
        {
          key: 'revenue',
          label: '今日营收',
          value: this.formatMoney(this.todayRevenue),
          suffix: '元',
          icon: 'el-icon-money',
          theme: 'money'
        },
        {
          key: 'orders',
          label: '今日订单数',
          value: this.todayOrderCount || 0,
          suffix: '单',
          icon: 'el-icon-s-order',
          theme: 'order'
        },
        {
          key: 'dishes',
          label: '在售菜品数',
          value: this.dishCount || 0,
          suffix: '道',
          icon: 'el-icon-dish',
          theme: 'dish'
        },
        {
          key: 'warning',
          label: '库存预警数',
          value: this.warningCount || 0,
          suffix: '项',
          icon: 'el-icon-warning-outline',
          theme: 'warning'
        }
      ]
    }
  },
  mounted() {
    this.loadDashboard()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeChart('trendChart')
    this.disposeChart('topChart')
  },
  methods: {
    loadDashboard() {
      this.loading = true
      getDashboard({ trendDays: 7, topLimit: 10 })
        .then((res) => {
          const data = res.data || {}
          this.todayRevenue = data.todayRevenue || 0
          this.todayOrderCount = data.todayOrderCount || 0
          this.dishCount = data.dishCount || 0
          this.warningCount = data.warningCount || 0
          this.revenueTrend = data.revenueTrend || []
          this.topDishes = data.topDishes || []
          this.warningList = data.warningList || []
          this.$nextTick(() => {
            this.renderTrendChart()
            this.renderTopChart()
          })
        })
        .catch((err) => {
          console.error('[Stats] 加载看板数据失败', err)
          this.$message.error('看板数据加载失败，请稍后重试')
        })
        .finally(() => {
          this.loading = false
        })
    },
    renderTrendChart() {
      this.disposeChart('trendChart')
      const chart = echarts.init(this.$refs.trendChart)
      const dates = this.revenueTrend.map((item) => item.date)
      const amounts = this.revenueTrend.map((item) => Number(item.amount || 0))
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            const p = params[0]
            return `${p.axisValue}<br/>营收：${this.formatMoney(p.value)} 元`
          }
        },
        grid: {
          left: 40,
          right: 20,
          top: 30,
          bottom: 40,
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: (v) => this.formatMoney(v)
          }
        },
        series: [
          {
            name: '营收',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 3, color: '#409EFF' },
            itemStyle: { color: '#409EFF' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64,158,255,0.4)' },
                { offset: 1, color: 'rgba(64,158,255,0.05)' }
              ])
            },
            data: amounts
          }
        ]
      })
      this.trendChart = chart
    },
    renderTopChart() {
      this.disposeChart('topChart')
      const chart = echarts.init(this.$refs.topChart)
      const list = [...(this.topDishes || [])].reverse()
      const names = list.map((d) => d.dishName)
      const quantities = list.map((d) => Number(d.totalQuantity || 0))
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: (params) => {
            const p = params[0]
            return `${p.name}<br/>销量：${p.value} 份`
          }
        },
        grid: {
          left: 80,
          right: 30,
          top: 20,
          bottom: 30,
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'category',
          data: names,
          axisLabel: { color: '#606266' }
        },
        series: [
          {
            name: '销量',
            type: 'bar',
            barWidth: 18,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#67C23A' },
                { offset: 1, color: '#409EFF' }
              ]),
              borderRadius: [0, 4, 4, 0]
            },
            label: {
              show: true,
              position: 'right',
              color: '#606266',
              formatter: '{c} 份'
            },
            data: quantities
          }
        ]
      })
      this.topChart = chart
    },
    disposeChart(key) {
      if (this[key]) {
        this[key].dispose()
        this[key] = null
      }
    },
    handleResize() {
      this.trendChart && this.trendChart.resize()
      this.topChart && this.topChart.resize()
    },
    formatMoney(v) {
      const n = Number(v || 0)
      if (Number.isNaN(n)) return '0.00'
      return n.toFixed(2)
    },
    formatGap(row) {
      const stock = Number(row.stock || 0)
      const safety = Number(row.safetyStock || 0)
      const gap = (safety - stock).toFixed(2)
      return gap > 0 ? `-${gap}` : '0.00'
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 16px;
}

.dashboard-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-right: 12px;
}
.dashboard-subtitle {
  font-size: 12px;
  color: #909399;
}

.stat-cards {
  margin-top: 4px;
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  height: 108px;
  padding: 0 20px;
  margin-bottom: 16px;
  border-radius: 6px;
  color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease;
  &:hover {
    transform: translateY(-2px);
  }
  &__icon {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    i {
      font-size: 32px;
      color: #fff;
    }
  }
  &__body {
    flex: 1;
  }
  &__label {
    font-size: 13px;
    opacity: 0.9;
  }
  &__value {
    font-size: 26px;
    font-weight: 600;
    margin-top: 4px;
  }
  &__suffix {
    font-size: 12px;
    opacity: 0.85;
  }
  &--money {
    background: linear-gradient(135deg, #f4516c, #f78989);
  }
  &--order {
    background: linear-gradient(135deg, #409eff, #66b1ff);
  }
  &--dish {
    background: linear-gradient(135deg, #67c23a, #85ce61);
  }
  &--warning {
    background: linear-gradient(135deg, #e6a23c, #f0b75e);
  }
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  margin-bottom: 16px;
  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 600;
    i {
      margin-right: 6px;
      color: #409eff;
    }
  }
  &__hint {
    font-weight: 400;
    font-size: 12px;
    color: #909399;
  }
}

.chart {
  width: 100%;
  height: 340px;
}

.warning-card {
  margin-bottom: 16px;
}
</style>
