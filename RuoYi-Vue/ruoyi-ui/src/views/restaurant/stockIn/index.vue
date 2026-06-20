<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="食材" prop="ingredientId">
        <el-select v-model="queryParams.ingredientId" placeholder="请选择食材" clearable>
          <el-option
            v-for="item in ingredientList"
            :key="item.ingredientId"
            :label="item.name"
            :value="item.ingredientId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="入库时间" prop="inTime">
        <el-date-picker clearable
          v-model="queryParams.inTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择入库时间">
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
          v-hasPermi="['restaurant:stockIn:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['restaurant:stockIn:edit']"
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
          v-hasPermi="['restaurant:stockIn:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['restaurant:stockIn:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockInList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入库ID" align="center" prop="stockInId" />
      <el-table-column label="食材" align="center" prop="ingredientName" />
      <el-table-column label="入库数量" align="center" prop="quantity" />
      <el-table-column label="采购单价" align="center" prop="unitPrice" />
      <el-table-column label="入库时间" align="center" prop="inTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.inTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['restaurant:stockIn:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['restaurant:stockIn:remove']"
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

    <!-- 添加或修改入库记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="食材" prop="ingredientId">
              <el-select v-model="form.ingredientId" placeholder="请选择食材" style="width: 100%;">
                <el-option
                  v-for="item in ingredientList"
                  :key="item.ingredientId"
                  :label="item.name"
                  :value="item.ingredientId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="入库数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0.01" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="采购单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="入库时间" prop="inTime">
              <el-date-picker clearable
                v-model="form.inTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择入库时间"
                style="width: 100%;">
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
  </div>
</template>

<script>
import { listStockIn, getStockIn, delStockIn, addStockIn, updateStockIn } from "@/api/restaurant/stockIn"
import { listIngredient } from "@/api/restaurant/ingredient"

export default {
  name: "StockIn",
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
      // 入库记录表格数据
      stockInList: [],
      // 食材列表（下拉选择用）
      ingredientList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ingredientId: null,
        inTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ingredientId: [
          { required: true, message: "食材不能为空", trigger: "change" }
        ],
        quantity: [
          { required: true, message: "入库数量不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getIngredientList()
  },
  methods: {
    /** 查询入库记录列表 */
    getList() {
      this.loading = true
      listStockIn(this.queryParams).then(response => {
        this.stockInList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 加载食材列表 */
    getIngredientList() {
      listIngredient({ pageNum: 1, pageSize: 999, status: '0' }).then(response => {
        this.ingredientList = response.rows
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
        stockInId: null,
        ingredientId: null,
        quantity: null,
        unitPrice: null,
        inTime: null,
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
      this.ids = selection.map(item => item.stockInId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加入库记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const stockInId = row.stockInId || this.ids
      getStockIn(stockInId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改入库记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.stockInId != null) {
            updateStockIn(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addStockIn(this.form).then(response => {
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
      const stockInIds = row.stockInId || this.ids
      this.$modal.confirm('是否确认删除入库记录编号为"' + stockInIds + '"的数据项？').then(function() {
        return delStockIn(stockInIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('restaurant/stockIn/export', {
        ...this.queryParams
      }, `stockIn_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
