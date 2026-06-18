# 文瀛餐厅管理系统 — 技术需求文档 (TRD)
## 方案 A：若依 RuoYi-Vue + Java

**版本**：v1.0  **日期**：2026-06-14

---

## 1. 技术选型

| 层 | 技术 | 版本 | 说明 |
|----|------|------|------|
| 脚手架 | **RuoYi-Vue** | 3.8.x | 底座；内置 RBAC/JWT/代码生成 |
| 后端语言 | Java | 17 LTS | 信创 JDK（毕昇/鲲鹏）兼容 |
| 后端框架 | Spring Boot | 3.x | 若依已集成 |
| 持久层 | MyBatis-Plus | 3.5.x | 若依已集成 |
| 安全 | Spring Security + JWT | — | 若依已集成 |
| 接口文档 | Knife4j (Swagger 3) | — | 若依已集成 |
| 前端框架 | Vue 3 + Vite | — | 若依 Vue3 版本已集成 |
| UI 组件 | Element Plus | — | 若依已集成 |
| 图表 | Apache ECharts | 5.x | 业务层自行引入 |
| 数据库 | MySQL | 8.0 | 信创可换 openGauss/达梦 |
| 构建 | Maven（后端）/ pnpm（前端）| — | — |
| 运行 | `java -jar` + `npm run dev` | — | 演示环境，无需 Docker |

---

## 2. 系统架构

```
┌─────────────────────────────────────────────┐
│          浏览器 (Vue3 SPA)                   │
│  登录页 / 看板 / 菜品 / 食材 / 配方 / 订单   │
└──────────────────┬──────────────────────────┘
                   │  HTTP/REST + JWT Bearer
┌──────────────────▼──────────────────────────┐
│         Spring Boot 后端（单体）              │
│                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ 若依内置  │  │ 业务模块  │  │ 公共组件 │  │
│  │ sys_*    │  │ dish/    │  │ 全局异常  │  │
│  │ RBAC/JWT │  │ ingredient│ │ 统一响应  │  │
│  │ 操作日志  │  │ order/   │  │ 数据权限  │  │
│  │ 代码生成  │  │ stats    │  └──────────┘  │
│  └──────────┘  └──────────┘                │
│                                             │
│  Controller → Service → Mapper → MySQL      │
└─────────────────────────────────────────────┘
                   │
              ┌────▼────┐
              │ MySQL 8 │
              └─────────┘
```

---

## 3. 数据库设计

### 3.1 若依自带表（不需要建，拿来用）

| 表名 | 用途 |
|------|------|
| sys_user | 用户 |
| sys_role | 角色 |
| sys_menu | 菜单/权限 |
| sys_user_role | 用户-角色关联 |
| sys_role_menu | 角色-菜单关联 |
| sys_oper_log | 操作日志 |
| sys_config | 系统配置 |
| sys_dict_type / sys_dict_data | 字典 |

### 3.2 业务表（需要建，共 8 张）

```sql
-- 建库（在若依 ry-vue 库中执行）
USE `ry-vue`;

-- 1. 菜品分类
CREATE TABLE t_category (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(50)  NOT NULL COMMENT '分类名称',
  sort     INT          DEFAULT 0,
  del_flag CHAR(1)      DEFAULT '0',     -- 若依约定：0正常 2删除
  create_by   VARCHAR(64),
  create_time DATETIME  DEFAULT CURRENT_TIMESTAMP,
  update_by   VARCHAR(64),
  update_time DATETIME  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='菜品分类';

-- 2. 菜品
CREATE TABLE t_dish (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL    COMMENT '菜品名称',
  category_id BIGINT        NOT NULL    COMMENT '分类ID',
  price       DECIMAL(10,2) NOT NULL    COMMENT '售价(元)',
  description VARCHAR(500)              COMMENT '描述',
  status      TINYINT       DEFAULT 1   COMMENT '1上架 0下架',
  del_flag    CHAR(1)       DEFAULT '0',
  create_by   VARCHAR(64),
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
  update_by   VARCHAR(64),
  update_time DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='菜品';

-- 3. 食材
CREATE TABLE t_ingredient (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(100)  NOT NULL   COMMENT '食材名称',
  unit         VARCHAR(20)   NOT NULL   COMMENT '单位(kg/g/个/瓶)',
  safety_stock DECIMAL(10,2) DEFAULT 0  COMMENT '安全库存阈值',
  del_flag     CHAR(1)       DEFAULT '0',
  create_by    VARCHAR(64),
  create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  update_by    VARCHAR(64),
  update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='食材档案';

-- 4. 菜品配方（多对多核心关联）
CREATE TABLE t_dish_ingredient (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  dish_id       BIGINT        NOT NULL  COMMENT '菜品ID',
  ingredient_id BIGINT        NOT NULL  COMMENT '食材ID',
  quantity      DECIMAL(10,3) NOT NULL  COMMENT '单份消耗量',
  UNIQUE KEY uk_dish_ingr (dish_id, ingredient_id)
) COMMENT='菜品配方';

-- 5. 库存（每种食材当前库存量）
CREATE TABLE t_inventory (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT        NOT NULL UNIQUE COMMENT '食材ID',
  stock         DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '当前库存',
  update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='食材库存';

-- 6. 入库记录
CREATE TABLE t_stock_in (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT        NOT NULL  COMMENT '食材ID',
  quantity      DECIMAL(10,2) NOT NULL  COMMENT '入库数量',
  unit_price    DECIMAL(10,2)           COMMENT '采购单价',
  remark        VARCHAR(200)            COMMENT '备注(供应商等)',
  create_by     VARCHAR(64),
  create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP
) COMMENT='入库记录';

-- 7. 订单
CREATE TABLE t_order (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no     VARCHAR(50)   NOT NULL UNIQUE COMMENT '订单号',
  total_amount DECIMAL(10,2) NOT NULL        COMMENT '总金额',
  status       TINYINT       NOT NULL DEFAULT 1
                             COMMENT '1已下单 2已完成 3已退单',
  create_by    VARCHAR(64),
  order_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_time   (order_time),
  INDEX idx_status (status)
) COMMENT='订单';

-- 8. 订单明细
CREATE TABLE t_order_item (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id  BIGINT        NOT NULL  COMMENT '订单ID',
  dish_id   BIGINT        NOT NULL  COMMENT '菜品ID',
  dish_name VARCHAR(100)  NOT NULL  COMMENT '菜品名快照',
  price     DECIMAL(10,2) NOT NULL  COMMENT '成交单价快照',
  quantity  INT           NOT NULL  COMMENT '份数',
  INDEX idx_order (order_id),
  INDEX idx_dish  (dish_id)
) COMMENT='订单明细';
```

### 3.3 ER 关系

```
t_category 1──* t_dish
t_dish     *──* t_ingredient  (通过 t_dish_ingredient)
t_ingredient 1──1 t_inventory
t_ingredient 1──* t_stock_in
t_order    1──* t_order_item *──1 t_dish
```

---

## 4. 后端工程结构

```
ruoyi-admin/                   ← 若依主模块（入口）
ruoyi-system/                  ← 若依系统模块（RBAC/用户/菜单）
ruoyi-common/                  ← 若依公共模块（JWT/异常/工具）
ruoyi-framework/               ← 若依框架模块（Security/MyBatis配置）
restaurant/                    ← 业务模块（新建）
  └── src/main/java/com/wuying/restaurant/
      ├── controller/
      │   ├── DishController.java
      │   ├── CategoryController.java
      │   ├── IngredientController.java
      │   ├── InventoryController.java
      │   ├── StockInController.java
      │   ├── OrderController.java
      │   └── StatsController.java
      ├── service/
      │   ├── DishService.java
      │   ├── IngredientService.java
      │   ├── InventoryService.java   ← 核心：扣库存逻辑
      │   ├── OrderService.java       ← 核心：下单+完成订单
      │   └── StatsService.java
      ├── mapper/
      │   ├── DishMapper.java / DishMapper.xml
      │   ├── IngredientMapper.java
      │   ├── InventoryMapper.java
      │   ├── OrderMapper.java
      │   └── StatsMapper.java        ← 聚合统计 SQL
      └── domain/                     ← 实体类（对应数据库表）
          ├── TDish.java
          ├── TIngredient.java
          ├── TInventory.java
          ├── TOrder.java
          └── TOrderItem.java
```

> **若依代码生成器**可自动生成 domain/mapper/service/controller 四层，
> 只需手写 OrderService 中的扣库存逻辑和 StatsService 中的聚合 SQL。

---

## 5. 核心接口规范

### 统一规范
- 前缀：`/api`（若依默认配置）
- 响应格式：`{ "code": 200, "msg": "操作成功", "data": {} }`
- 鉴权：请求头 `Authorization: Bearer <token>`
- 分页参数：`pageNum`（默认1）、`pageSize`（默认10）

### 5.1 认证

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /login | 登录，返回 token | 否 |
| POST | /logout | 登出 | 是 |
| GET  | /getInfo | 获取当前用户信息+权限 | 是 |

### 5.2 菜品

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/dish/list | 分页列表，参数：`name`、`categoryId`、`status` |
| GET  | /api/dish/{id} | 详情 |
| POST | /api/dish | 新增 |
| PUT  | /api/dish | 修改 |
| PUT  | /api/dish/changeStatus | 上下架，body: `{id, status}` |
| DELETE | /api/dish/{ids} | 批量删除（逻辑） |
| GET  | /api/category/list | 分类列表 |
| POST | /api/category | 新增分类 |
| PUT  | /api/category | 修改分类 |
| DELETE | /api/category/{id} | 删除分类 |

### 5.3 食材 & 库存

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/ingredient/list | 食材分页列表 |
| POST | /api/ingredient | 新增食材（同时初始化 t_inventory stock=0） |
| PUT  | /api/ingredient | 修改 |
| DELETE | /api/ingredient/{ids} | 删除 |
| GET  | /api/inventory/list | 库存列表（含预警标记 `warning: true/false`） |
| POST | /api/stockIn | 入库，body: `{ingredientId, quantity, unitPrice, remark}` |

### 5.4 配方

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/recipe/list | 配方列表，参数：`dishId` |
| POST | /api/recipe/save | 保存配方（覆盖写，传菜品ID+食材列表） |

**POST /api/recipe/save 请求体：**
```json
{
  "dishId": 1,
  "ingredients": [
    { "ingredientId": 2, "quantity": 0.200 },
    { "ingredientId": 3, "quantity": 2.000 }
  ]
}
```

### 5.5 订单

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/order/list | 订单分页，参数：`status`、`startTime`、`endTime` |
| GET  | /api/order/{id} | 订单详情（含明细） |
| POST | /api/order/mock | 模拟下单 |
| PUT  | /api/order/complete/{id} | **完成订单（触发库存扣减）** |
| PUT  | /api/order/cancel/{id} | 退单 |

**POST /api/order/mock 请求体：**
```json
{
  "items": [
    { "dishId": 2, "quantity": 3 },
    { "dishId": 7, "quantity": 3 }
  ]
}
```

**POST /api/order/mock 返回：**
```json
{
  "code": 200,
  "msg": "下单成功",
  "data": { "orderId": 101, "orderNo": "M1718340000001", "totalAmount": 28.50 }
}
```

### 5.6 数据统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/stats/overview | 看板全量数据（一次请求，4组） |

**GET /api/stats/overview 返回：**
```json
{
  "code": 200,
  "data": {
    "todayRevenue":   2340.00,
    "todayOrders":    87,
    "revenue7Days":   [{"date":"06-08","amount":1800.0}, ...],
    "topDishes":      [{"name":"红烧肉","qty":120}, ...]
  }
}
```

---

## 6. 核心业务逻辑实现

### 6.1 OrderService.completeOrder（最重要）

```java
// OrderService.java
@Transactional(rollbackFor = Exception.class)
public void completeOrder(Long orderId) {
    TOrder order = orderMapper.selectById(orderId);
    if (order == null || order.getStatus() != 1) {
        throw new ServiceException("订单不存在或状态不允许完成");
    }

    // 查订单明细
    List<TOrderItem> items = orderItemMapper.selectByOrderId(orderId);

    for (TOrderItem item : items) {
        // 查该菜品的配方
        List<TDishIngredient> recipes =
            dishIngredientMapper.selectByDishId(item.getDishId());

        for (TDishIngredient r : recipes) {
            // 扣减量 = 配方单份量 × 份数
            BigDecimal deduct =
                r.getQuantity().multiply(BigDecimal.valueOf(item.getQuantity()));

            // UPDATE 带条件：stock - deduct WHERE stock >= deduct
            // 返回 0 说明库存不足（防并发超扣）
            int rows = inventoryMapper.deductStock(r.getIngredientId(), deduct);
            if (rows == 0) {
                // 抛出后 @Transactional 自动回滚
                Ingredient ingr = ingredientMapper.selectById(r.getIngredientId());
                throw new ServiceException(
                    "食材【" + ingr.getName() + "】库存不足，无法完成订单");
            }
        }
    }

    // 更新订单状态
    order.setStatus(2);
    orderMapper.updateById(order);
}
```

### 6.2 InventoryMapper.xml（扣库存 SQL）

```xml
<!-- InventoryMapper.xml -->
<update id="deductStock">
    UPDATE t_inventory
    SET stock = stock - #{deduct},
        update_time = NOW()
    WHERE ingredient_id = #{ingredientId}
      AND stock &gt;= #{deduct}
</update>
```

> **关键点**：`AND stock >= #{deduct}` 是防止库存扣成负数的原子操作，
> 这条 WHERE 条件让 MySQL 在单条 UPDATE 内完成"检查+扣减"，
> 不需要先 SELECT 再 UPDATE，天然避免并发超扣。

### 6.3 入库逻辑

```java
// InventoryService.java
@Transactional
public void stockIn(TStockIn record) {
    stockInMapper.insert(record);  // 插入入库记录

    // 库存累加（用 UPDATE 避免并发问题）
    inventoryMapper.addStock(record.getIngredientId(), record.getQuantity());
}
```

```xml
<update id="addStock">
    UPDATE t_inventory
    SET stock = stock + #{quantity}, update_time = NOW()
    WHERE ingredient_id = #{ingredientId}
</update>
```

### 6.4 新增食材时初始化库存

```java
// IngredientService.java
@Transactional
public void addIngredient(TIngredient ingredient) {
    ingredientMapper.insert(ingredient);

    // 为新食材创建库存记录，初始为 0
    TInventory inventory = new TInventory();
    inventory.setIngredientId(ingredient.getId());
    inventory.setStock(BigDecimal.ZERO);
    inventoryMapper.insert(inventory);
}
```

### 6.5 StatsMapper.xml（4 条统计 SQL）

```xml
<!-- 今日营收 -->
<select id="getTodayRevenue" resultType="java.math.BigDecimal">
    SELECT IFNULL(SUM(total_amount), 0)
    FROM t_order
    WHERE DATE(order_time) = CURDATE() AND status = 2
</select>

<!-- 今日订单数 -->
<select id="getTodayOrderCount" resultType="java.lang.Integer">
    SELECT COUNT(*)
    FROM t_order
    WHERE DATE(order_time) = CURDATE() AND status = 2
</select>

<!-- 近 7 天营收（按日） -->
<select id="getRevenue7Days" resultType="map">
    SELECT DATE_FORMAT(order_time, '%m-%d') AS date,
           IFNULL(SUM(total_amount), 0)     AS amount
    FROM t_order
    WHERE order_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
      AND status = 2
    GROUP BY DATE(order_time)
    ORDER BY DATE(order_time)
</select>

<!-- 销量 Top10 -->
<select id="getTopDishes" resultType="map">
    SELECT oi.dish_name AS name, SUM(oi.quantity) AS qty
    FROM t_order_item oi
    JOIN t_order o ON oi.order_id = o.id
    WHERE o.status = 2
    GROUP BY oi.dish_name
    ORDER BY qty DESC
    LIMIT 10
</select>
```

---

## 7. 前端关键实现

### 7.1 ECharts 看板（核心代码）

```javascript
// views/stats/index.vue
import * as echarts from 'echarts'
import { getOverview } from '@/api/stats'

onMounted(async () => {
  const { data } = await getOverview()

  // 折线图：近7天营收
  const lineChart = echarts.init(document.getElementById('lineChart'))
  lineChart.setOption({
    xAxis: { type: 'category', data: data.revenue7Days.map(d => d.date) },
    yAxis: { type: 'value', name: '营收(元)' },
    series: [{
      type: 'line', smooth: true,
      data: data.revenue7Days.map(d => d.amount),
      areaStyle: {}     // 带阴影的面积图，好看
    }]
  })

  // 柱状图：销量 Top10（横向）
  const barChart = echarts.init(document.getElementById('barChart'))
  barChart.setOption({
    yAxis: { type: 'category', data: data.topDishes.map(d => d.name).reverse() },
    xAxis: { type: 'value', name: '销量(份)' },
    series: [{ type: 'bar', data: data.topDishes.map(d => d.qty).reverse() }]
  })
})
```

### 7.2 配方维护页（动态行）

```vue
<!-- views/recipe/index.vue 核心片段 -->
<el-table :data="recipeForm.ingredients">
  <el-table-column label="食材">
    <template #default="{ row }">
      <el-select v-model="row.ingredientId" placeholder="选择食材">
        <el-option v-for="i in ingredientList"
          :key="i.id" :label="i.name" :value="i.id" />
      </el-select>
    </template>
  </el-table-column>
  <el-table-column label="单份用量">
    <template #default="{ row }">
      <el-input v-model="row.quantity" type="number" />
    </template>
  </el-table-column>
  <el-table-column label="操作">
    <template #default="{ $index }">
      <el-button type="danger" @click="removeRow($index)">删除</el-button>
    </template>
  </el-table-column>
</el-table>
<el-button @click="addRow">+ 添加食材</el-button>
```

### 7.3 库存预警标红

```vue
<!-- views/inventory/index.vue -->
<el-table-column label="当前库存">
  <template #default="{ row }">
    <span :style="{ color: row.stock <= row.safetyStock ? 'red' : 'inherit' }">
      {{ row.stock }} {{ row.unit }}
      <el-tag v-if="row.stock <= row.safetyStock" type="danger" size="small">
        库存不足
      </el-tag>
    </span>
  </template>
</el-table-column>
```

---

## 8. 若依快速上手步骤

```bash
# 1. 克隆若依 Vue3 版本
git clone https://github.com/yangzongzhuan/RuoYi-Vue.git
cd RuoYi-Vue

# 2. 导入数据库
# 执行 sql/ry_20xxxxxx.sql（若依初始化）
# 执行业务表 schema.sql

# 3. 修改后端配置
# ruoyi-admin/src/main/resources/application-druid.yml
# 改 url / username / password

# 4. 启动后端
cd ruoyi-admin && mvn spring-boot:run

# 5. 启动前端
cd ruoyi-ui
pnpm install
pnpm run dev
# 访问 http://localhost:80

# 6. 用代码生成器生成业务模块
# 浏览器登录 → 系统工具 → 代码生成 → 导入表（t_dish/t_ingredient等）→ 生成代码
# 下载 zip → 解压合并到工程
```

---

## 9. 安全要求

| 项 | 实现方式 |
|----|---------|
| 认证 | 若依内置 JWT，token 有效期 30min，可配置 |
| 权限 | `@PreAuthorize("@ss.hasPermi('dish:edit')")` 接口级注解 |
| SQL 注入 | MyBatis 参数化查询（`#{}` 占位符），绝对不用 `${}` 拼 SQL |
| XSS | 若依内置 XssFilter 过滤请求参数 |
| 密码 | `BCryptPasswordEncoder`，若依已集成 |
| 操作日志 | 若依内置 `@Log` 注解，自动记录操作人/IP/时间/请求参数 |
