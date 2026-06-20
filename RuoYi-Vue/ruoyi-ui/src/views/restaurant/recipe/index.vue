<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="recipe-header">
        <span>配方管理</span>
        <el-tag v-if="selectedDishName" type="info">当前菜品：{{ selectedDishName }}</el-tag>
      </div>

      <el-form size="small" label-width="80px" class="recipe-filter">
        <el-form-item label="选择菜品">
          <el-select
            v-model="selectedDishId"
            filterable
            clearable
            placeholder="请选择菜品"
            style="width: 360px"
            @change="handleDishChange"
          >
            <el-option
              v-for="item in dishOptions"
              :key="item.dishId"
              :label="item.name"
              :value="item.dishId"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <el-empty v-if="!selectedDishId" description="请先选择菜品后再维护配方" />

      <template v-else>
        <el-table :data="recipeList" border style="width: 100%">
          <el-table-column label="食材" prop="ingredientId" min-width="280">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.ingredientId"
                filterable
                clearable
                placeholder="请选择食材"
                style="width: 100%"
              >
                <el-option
                  v-for="item in ingredientOptions"
                  :key="item.ingredientId"
                  :label="item.name"
                  :value="item.ingredientId"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="用量" prop="quantity" width="220">
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="0.01"
                :precision="2"
                :step="0.1"
                controls-position="right"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDeleteRow(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="recipe-actions">
          <el-button type="primary" plain icon="el-icon-plus" @click="handleAddRow">新增食材</el-button>
          <el-button type="success" icon="el-icon-check" :loading="saving" @click="handleSubmit">保存配方</el-button>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script>
import { listDish } from '@/api/restaurant/dish'
import { listInventory } from '@/api/restaurant/inventory'
import { getRecipe, saveRecipe } from '@/api/restaurant/recipe'

export default {
  name: 'Recipe',
  data() {
    return {
      selectedDishId: null,
      selectedDishName: '',
      dishOptions: [],
      ingredientOptions: [],
      recipeList: [],
      saving: false
    }
  },
  created() {
    this.loadDishOptions()
    this.loadIngredients()
  },
  methods: {
    loadDishOptions() {
      listDish({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.dishOptions = response.rows || []
        const routeDishId = this.$route.query.dishId
        const routeDishName = this.$route.query.dishName
        if (routeDishId) {
          this.selectedDishId = Number(routeDishId)
          this.selectedDishName = routeDishName || this.getDishNameById(this.selectedDishId)
          this.loadRecipe()
        }
      })
    },
    loadIngredients() {
      listInventory({ pageNum: 1, pageSize: 1000 }).then(response => {
        const rows = response.rows || []
        const ingredientMap = new Map()
        rows.forEach(item => {
          if (item && item.ingredientId && !ingredientMap.has(item.ingredientId)) {
            ingredientMap.set(item.ingredientId, {
              ingredientId: item.ingredientId,
              name: item.ingredientName
            })
          }
        })
        this.ingredientOptions = Array.from(ingredientMap.values())
      })
    },
    getDishNameById(dishId) {
      const matched = this.dishOptions.find(item => item.dishId === dishId)
      return matched ? matched.name : ''
    },
    handleDishChange(dishId) {
      if (!dishId) {
        this.selectedDishName = ''
        this.recipeList = []
        return
      }
      this.selectedDishName = this.getDishNameById(dishId)
      this.loadRecipe()
    },
    loadRecipe() {
      if (!this.selectedDishId) {
        this.recipeList = []
        return
      }
      getRecipe(this.selectedDishId).then(response => {
        const data = response.data || []
        this.recipeList = data.map(item => ({
          ingredientId: item.ingredientId,
          quantity: Number(item.quantity)
        }))
      })
    },
    handleAddRow() {
      this.recipeList.push({ ingredientId: null, quantity: 0.01 })
    },
    handleDeleteRow(index) {
      this.recipeList.splice(index, 1)
    },
    validateRecipeRows(rows) {
      for (const row of rows) {
        if (!row.ingredientId) {
          this.$modal.msgError('每一行都必须选择食材')
          return false
        }
        if (row.quantity == null || Number(row.quantity) <= 0) {
          this.$modal.msgError('配方用量必须大于0')
          return false
        }
      }
      const ids = rows.map(row => row.ingredientId)
      if (new Set(ids).size !== ids.length) {
        this.$modal.msgError('存在重复的食材，请检查')
        return false
      }
      return true
    },
    handleSubmit() {
      if (!this.selectedDishId) {
        this.$modal.msgError('请先选择菜品')
        return
      }
      if (this.recipeList.length === 0) {
        this.$modal.confirm('当前没有配置任何食材，保存将清空该菜品配方，是否继续？').then(() => {
          this.doSave([])
        }).catch(() => {})
        return
      }
      if (!this.validateRecipeRows(this.recipeList)) {
        return
      }
      this.doSave(this.recipeList)
    },
    doSave(ingredients) {
      this.saving = true
      saveRecipe({
        dishId: this.selectedDishId,
        ingredients
      }).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.loadRecipe()
      }).finally(() => {
        this.saving = false
      })
    }
  }
}
</script>

<style scoped>
.recipe-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.recipe-filter {
  margin-bottom: 20px;
}

.recipe-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
