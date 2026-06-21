<template>
  <div class="app-container home-page">
    <el-row :gutter="20" class="hero-row">
      <el-col :xs="24" :lg="16">
        <div class="hero-panel">
          <div class="hero-panel__eyebrow">文瀛餐厅管理系统</div>
          <h1 class="hero-panel__title">餐厅日常运营一屏统览</h1>
          <p class="hero-panel__desc">
            面向食堂经理与库管员，围绕菜品、配方、库存、入库、订单与看板建立完整业务闭环，支持答辩演示与日常教学实训。
          </p>
          <div class="hero-panel__actions">
            <el-button type="primary" icon="el-icon-data-analysis" @click="goRoute('/restaurant/stats')">查看数据看板</el-button>
            <el-button plain icon="el-icon-s-order" @click="goRoute('/restaurant/order')">进入订单管理</el-button>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="8">
        <div class="summary-panel">
          <div class="summary-panel__header">演示主链</div>
          <div class="summary-panel__item" v-for="item in flowItems" :key="item.title">
            <div class="summary-panel__step">{{ item.step }}</div>
            <div class="summary-panel__content">
              <div class="summary-panel__title">{{ item.title }}</div>
              <div class="summary-panel__desc">{{ item.desc }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="section-row">
      <el-col :xs="24" :md="12" :lg="8" v-for="card in moduleCards" :key="card.title">
        <div class="module-card">
          <div class="module-card__icon">
            <i :class="card.icon"></i>
          </div>
          <div class="module-card__title">{{ card.title }}</div>
          <div class="module-card__desc">{{ card.desc }}</div>
          <el-button type="text" @click="goRoute(card.path)">进入模块</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      flowItems: [
        { step: '01', title: '维护配方', desc: '从菜品页或配方页维护菜品食材组成与用量。' },
        { step: '02', title: '执行入库', desc: '食材新增自动建库存，入库记录按差额联动库存。' },
        { step: '03', title: '模拟下单', desc: '生成订单与明细，校验菜品状态和基础数据完整性。' },
        { step: '04', title: '完成与回滚', desc: '完成订单扣库存，退单自动回滚，异常时事务整体回退。' }
      ],
      moduleCards: [
        { title: '数据看板', desc: '查看今日营收、订单数、近 7 天趋势、销量 Top10 与库存预警。', path: '/restaurant/stats', icon: 'el-icon-data-line' },
        { title: '菜品与配方', desc: '维护分类、菜品上下架与配方，保证订单扣减口径正确。', path: '/restaurant/dish', icon: 'el-icon-dish' },
        { title: '库存与入库', desc: '维护食材档案、库存与入库记录，支撑扣减与预警展示。', path: '/restaurant/inventory', icon: 'el-icon-box' }
      ]
    }
  },
  methods: {
    goRoute(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped lang="scss">
.home-page {
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.hero-row,
.section-row {
  margin-bottom: 20px;
}

.hero-panel,
.summary-panel,
.module-card {
  height: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.hero-panel {
  min-height: 260px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hero-panel__eyebrow {
  color: #409eff;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
}

.hero-panel__title {
  margin: 0 0 16px;
  font-size: 34px;
  line-height: 1.2;
  color: #1f2d3d;
}

.hero-panel__desc,
.summary-panel__desc,
.module-card__desc {
  color: #5b6b7f;
  line-height: 1.7;
}

.hero-panel__desc {
  max-width: 680px;
  margin-bottom: 24px;
}

.hero-panel__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.summary-panel__header,
.module-card__title {
  color: #1f2d3d;
  font-weight: 600;
}

.summary-panel__header {
  margin-bottom: 18px;
  font-size: 18px;
}

.summary-panel__item {
  display: flex;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid #eef2f7;
}

.summary-panel__item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}

.summary-panel__step {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.summary-panel__title {
  color: #1f2d3d;
  font-weight: 600;
  margin-bottom: 6px;
}

.module-card {
  min-height: 220px;
  display: flex;
  flex-direction: column;
}

.module-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: #f0f7ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  margin-bottom: 16px;
}

.module-card__desc {
  margin: 12px 0 18px;
  flex: 1;
}

@media (max-width: 991px) {
  .hero-panel__title {
    font-size: 28px;
  }
}
</style>
