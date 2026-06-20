<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="dict in dict.type.restaurant_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间" prop="orderTime">
        <el-date-picker clearable
          v-model="queryParams.orderTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择下单时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间" prop="completeTime">
        <el-date-picker clearable
          v-model="queryParams.completeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['restaurant:order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-order"
          size="mini"
          @click="handleMock"
          v-hasPermi="['restaurant:order:mock']"
        >模拟下单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['restaurant:order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['restaurant:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['restaurant:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column label="订单总额" align="center" prop="totalAmount" />
      <el-table-column label="订单状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.restaurant_order_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" align="center" prop="orderTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="completeTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.completeTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['restaurant:order:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['restaurant:order:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.status === 1"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleComplete(scope.row)"
            v-hasPermi="['restaurant:order:complete']"
          >完成</el-button>
          <el-button
            v-if="scope.row.status === 2"
            size="mini"
            type="text"
            icon="el-icon-refresh-left"
            @click="handleCancel(scope.row)"
            v-hasPermi="['restaurant:order:cancel']"
          >退单</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['restaurant:order:remove']"
          >删除</el-button>
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

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="请输入订单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="订单总额" prop="totalAmount">
              <el-input v-model="form.totalAmount" placeholder="请输入订单总额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="订单状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择订单状态">
                <el-option
                  v-for="dict in dict.type.restaurant_order_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="下单时间" prop="orderTime">
              <el-date-picker clearable
                v-model="form.orderTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择下单时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="完成时间" prop="completeTime">
              <el-date-picker clearable
                v-model="form.completeTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择完成时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 模拟下单对话框 -->
    <el-dialog title="模拟下单" :visible.sync="mockOpen" width="700px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="菜品明细">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addMockItem">添加菜品</el-button>
        </el-form-item>
        <el-table :data="mockForm.orderItems" border size="small">
          <el-table-column label="菜品" align="center" width="220">
            <template slot-scope="scope">
              <el-select v-model="scope.row.dishId" placeholder="请选择菜品" size="small" @change="(val) => onDishChange(scope.row, val)">
                <el-option
                  v-for="dish in dishList"
                  :key="dish.dishId"
                  :label="dish.name"
                  :value="dish.dishId"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单价" align="center" width="100">
            <template slot-scope="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column label="份数" align="center" width="120">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" :max="99" size="small" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column label="小计" align="center" width="100">
            <template slot-scope="scope">
              ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="mini" icon="el-icon-delete" @click="removeMockItem(scope.$index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-form-item label="备注" style="margin-top: 15px;">
          <el-input v-model="mockForm.remark" type="textarea" placeholder="订单备注（选填）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMock">下 单</el-button>
        <el-button @click="mockOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-form label-width="100px" class="detail-form">
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单号：">
              <span>{{ detailData.orderNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单状态：">
              <dict-tag :options="dict.type.restaurant_order_status" :value="detailData.status"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单总额：">
              <span class="amount">¥{{ detailData.totalAmount }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下单时间：">
              <span>{{ parseTime(detailData.orderTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="detailData.completeTime">
            <el-form-item label="完成时间：">
              <span>{{ parseTime(detailData.completeTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注：">
              <span>{{ detailData.remark || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <h4 class="detail-title">菜品明细</h4>
      <el-table :data="detailData.orderItems" border size="small">
        <el-table-column label="菜品" align="center" prop="dishName" />
        <el-table-column label="单价" align="center" prop="price" />
        <el-table-column label="份数" align="center" prop="quantity" />
        <el-table-column label="小计" align="center">
          <template slot-scope="scope">
            ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder, mockOrder, completeOrder, cancelOrder } from "@/api/restaurant/order"
import { listDish } from "@/api/restaurant/dish"

export default {
  name: "Order",
  dicts: ['restaurant_order_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 订单表格数据
      orderList: [],
      // 菜品列表（模拟下单用）
      dishList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示模拟下单弹窗
      mockOpen: false,
      // 模拟下单表单
      mockForm: {
        remark: null,
        orderItems: []
      },
      // 是否显示详情弹窗
      detailOpen: false,
      // 订单详情数据
      detailData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        status: null,
        orderTime: null,
        completeTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderNo: [
          { required: true, message: "订单号不能为空", trigger: "blur" }
        ],
        totalAmount: [
          { required: true, message: "订单总额不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "订单状态不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getDishList()
  },
  methods: {
    /** 查询订单列表 */
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        orderId: null,
        orderNo: null,
        totalAmount: null,
        status: null,
        orderTime: null,
        completeTime: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      }
      this.resetForm("form")
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
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加订单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const orderId = row.orderId || this.ids
      getOrder(orderId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改订单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.orderId != null) {
            updateOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addOrder(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const orderIds = row.orderId || this.ids
      this.$modal.confirm('是否确认删除订单编号为"' + orderIds + '"的数据项？').then(function() {
        return delOrder(orderIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('restaurant/order/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`)
    },
    /** 加载菜品列表（模拟下单用） */
    getDishList() {
      listDish({ pageNum: 1, pageSize: 999, status: '0' }).then(response => {
        this.dishList = response.rows
      })
    },
    /** 打开模拟下单弹窗 */
    handleMock() {
      this.mockForm = { remark: null, orderItems: [] }
      this.mockOpen = true
    },
    /** 添加模拟下单菜品行 */
    addMockItem() {
      this.mockForm.orderItems.push({ dishId: null, dishName: null, price: 0, quantity: 1 })
    },
    /** 移除模拟下单菜品行 */
    removeMockItem(index) {
      this.mockForm.orderItems.splice(index, 1)
    },
    /** 菜品选择变更时回填菜品名和单价 */
    onDishChange(row, dishId) {
      const dish = this.dishList.find(d => d.dishId === dishId)
      if (dish) {
        row.dishName = dish.name
        row.price = dish.price
      }
    },
    /** 提交模拟下单 */
    submitMock() {
      if (this.mockForm.orderItems.length === 0) {
        this.$modal.msgError("请至少添加一个菜品")
        return
      }
      const invalid = this.mockForm.orderItems.find(item => !item.dishId || !item.quantity || item.quantity < 1)
      if (invalid) {
        this.$modal.msgError("请选择菜品并填写有效份数")
        return
      }
      mockOrder(this.mockForm).then(response => {
        this.$modal.msgSuccess("下单成功，订单ID：" + response.data)
        this.mockOpen = false
        this.getList()
      })
    },
    /** 完成订单 */
    handleComplete(row) {
      this.$modal.confirm('确认完成订单"' + row.orderNo + '"吗？完成后将按配方扣减库存。').then(() => {
        return completeOrder(row.orderId)
      }).then(() => {
        this.$modal.msgSuccess("订单已完成，库存已扣减")
        this.getList()
      }).catch(err => {
        if (err !== 'cancel' && err?.msg) {
          this.$modal.msgError(err.msg)
        }
      })
    },
    /** 退单 */
    handleCancel(row) {
      this.$modal.confirm('确认退单"' + row.orderNo + '"吗？退单后将回滚库存。').then(() => {
        return cancelOrder(row.orderId)
      }).then(() => {
        this.$modal.msgSuccess("退单成功，库存已回滚")
        this.getList()
      }).catch(err => {
        if (err !== 'cancel' && err?.msg) {
          this.$modal.msgError(err.msg)
        }
      })
    },
    /** 查看详情 */
    handleDetail(row) {
      getOrder(row.orderId).then(response => {
        this.detailData = response.data
        this.detailOpen = true
      })
    }
  }
}
</script>

<style scoped>
.detail-form .el-form-item {
  margin-bottom: 8px;
}
.detail-form .amount {
  color: #f56c6c;
  font-weight: bold;
}
.detail-title {
  margin: 16px 0 12px;
  font-size: 14px;
  color: #606266;
  border-left: 4px solid #409eff;
  padding-left: 8px;
}
</style>
