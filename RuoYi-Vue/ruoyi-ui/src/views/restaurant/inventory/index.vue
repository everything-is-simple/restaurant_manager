<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="食材" prop="ingredientName">
        <el-input
          v-model="queryParams.ingredientName"
          placeholder="请输入食材名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="inventoryList">
      <el-table-column label="库存ID" align="center" prop="inventoryId" />
      <el-table-column label="食材" align="center" prop="ingredientName" />
      <el-table-column label="当前库存量" align="center" prop="stock">
        <template slot-scope="scope">
          <el-tag :type="isWarning(scope.row) ? 'danger' : 'success'" size="small">
            {{ scope.row.stock }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="安全库存" align="center" prop="safetyStock" />
      <el-table-column label="预警状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="isWarning(scope.row)" type="danger" size="small">库存不足</el-tag>
          <el-tag v-else type="success" size="small">充足</el-tag>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

  </div>
</template>

<script>
import { listInventory } from "@/api/restaurant/inventory"

export default {
  name: "Inventory",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 食材库存表格数据
      inventoryList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ingredientName: null,
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询食材库存列表 */
    getList() {
      this.loading = true
      listInventory(this.queryParams).then(response => {
        this.inventoryList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 判断是否库存预警 */
    isWarning(row) {
      return row.safetyStock != null && row.stock != null && Number(row.stock) < Number(row.safetyStock)
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    }
  }
}
</script>
