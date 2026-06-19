<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>配方管理</span>
        <el-tag v-if="dishName" style="margin-left: 10px;" type="info">当前菜品：{{ dishName }}</el-tag>
      </div>
      <div style="text-align: center; padding: 60px 0; color: #909399;">
        <i class="el-icon-s-grid" style="font-size: 48px;"></i>
        <p style="font-size: 16px; margin-top: 20px;">配方管理功能开发中</p>
        <p style="font-size: 14px;">此页面将在 D5 阶段实现，用于维护菜品与食材的配方关系</p>
        <p v-if="dishId" style="font-size: 13px; color: #C0C4CC; margin-top: 10px;">
          菜品ID：{{ dishId }}
        </p>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Recipe',
  data() {
    return {
      dishId: null,
      dishName: null
    }
  },
  created() {
    this.dishId = this.$route.query.dishId
    this.dishName = this.$route.query.dishName
  }
}
</script>
<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>配方管理</span>
      </div>
      <div style="text-align: center; padding: 60px 0; color: #909399;">
        <i class="el-icon-s-grid" style="font-size: 48px;"></i>
        <p style="font-size: 16px; margin-top: 20px;">配方管理功能开发中</p>
        <p style="font-size: 14px;">此页面将在 D5 阶段实现，用于维护菜品与食材的配方关系</p>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Recipe',
  data() {
    return {}
  }
}
</script>
