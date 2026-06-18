# 文瀛餐厅管理系统 — 系统设计文档 (Design)
## 方案 A：若依 RuoYi-Vue + Java

**版本**：v1.0  **日期**：2026-06-14

---

## 1. 整体架构

```
┌────────────────────────────────────────────────────────────────┐
│                    浏览器（PC 后台管理端）                        │
│                                                                │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ ┌──────┐  │
│  │ 数据看板  │ │ 菜品管理  │ │ 食材库存  │ │ 配方   │ │ 订单 │  │
│  │ ECharts  │ │ 列表+弹窗 │ │ 预警列表  │ │ 动态行 │ │ 模拟 │  │
│  └──────────┘ └──────────┘ └──────────┘ └────────┘ └──────┘  │
│                    Vue3 + Element Plus (若依前端)               │
└─────────────────────────┬──────────────────────────────────────┘
                          │ HTTP REST + JWT Bearer Token
┌─────────────────────────▼──────────────────────────────────────┐
│                    Spring Boot 后端                              │
│                                                                 │
│  Controller（参数校验/响应封装）                                  │
│       ↓                                                         │
│  Service（业务逻辑/事务）                                         │
│       ↓                                                         │
│  Mapper（MyBatis-Plus/XML）                                     │
│       ↓                                                         │
│  MySQL 8.0                                                      │
│                                                                 │
│  横切关注点（若依已有）：                                          │
│    SecurityConfig(JWT) / GlobalExceptionHandler /               │
│    @Log(操作日志) / @DataScope(数据权限) / XssFilter             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 模块职责划分

| 模块 | 包路径 | 核心类 | 职责 |
|------|--------|--------|------|
| 菜品 | `.dish` | DishController, DishService | CRUD + 上下架 |
| 分类 | `.dish` | CategoryController | 简单字典管理 |
| 食材 | `.ingredient` | IngredientService | 档案 + 新增时初始化库存 |
| 配方 | `.recipe` | RecipeService | 覆盖写菜品配方 |
| 库存 | `.inventory` | InventoryService | 入库累加、扣减、预警查询 |
| 订单 | `.order` | OrderService ★ | 模拟下单、完成（扣库存）、退单 |
| 统计 | `.stats` | StatsService | 4 条聚合 SQL，返回看板数据 |

---

## 3. 数据流设计

### 3.1 模拟下单流程

```
前端 POST /api/order/mock
  └─ OrderController.mockOrder(req)
       └─ OrderService.mockOrder(items)
            ├─ 1. 校验菜品是否上架
            ├─ 2. 计算 total_amount
            ├─ 3. INSERT t_order (status=1)
            ├─ 4. 批量 INSERT t_order_item
            └─ 5. 返回 orderId + orderNo
```

### 3.2 完成订单 → 自动扣库存（核心流程）

```
前端 PUT /api/order/complete/{id}
  └─ OrderController.complete(id)
       └─ OrderService.completeOrder(id)  @Transactional
            ├─ 1. SELECT t_order WHERE id=? AND status=1
            ├─ 2. SELECT t_order_item WHERE order_id=?
            ├─ 3. for each order_item:
            │       SELECT t_dish_ingredient WHERE dish_id=?
            │       for each recipe:
            │         deduct = recipe.quantity × item.quantity
            │         UPDATE t_inventory
            │           SET stock = stock - deduct
            │           WHERE ingredient_id=? AND stock >= deduct
            │         if rows==0 → throw ServiceException → 事务回滚
            └─ 4. UPDATE t_order SET status=2
```

### 3.3 入库流程

```
前端 POST /api/stockIn
  └─ InventoryService.stockIn(record)  @Transactional
       ├─ 1. INSERT t_stock_in
       └─ 2. UPDATE t_inventory SET stock = stock + qty
              WHERE ingredient_id=?
```

---

## 4. 前端路由设计

若依前端路由从后端动态加载（菜单表驱动），
以下为需要在若依后台「系统管理→菜单管理」中录入的菜单：

| 菜单名 | 路由路径 | 组件路径 | 角色可见 |
|--------|---------|---------|---------|
| 数据看板 | /dashboard/stats | dashboard/stats/index | MANAGER |
| 菜品管理 | /dish/list | dish/index | MANAGER |
| 食材档案 | /ingredient/list | ingredient/index | STOCK_KEEPER |
| 库存管理 | /inventory/list | inventory/index | STOCK_KEEPER |
| 菜品配方 | /recipe/list | recipe/index | MANAGER |
| 订单管理 | /order/list | order/index | MANAGER |
| 系统管理 | （若依自带，无需配置） | — | ADMIN |

---

## 5. 前端组件结构

```
src/
├── api/
│   ├── dish.js          # GET/POST/PUT/DELETE /api/dish/*
│   ├── ingredient.js    # 食材相关
│   ├── inventory.js     # 库存相关
│   ├── recipe.js        # 配方相关
│   ├── order.js         # 订单相关
│   └── stats.js         # 统计看板
│
├── views/
│   ├── dashboard/
│   │   └── stats/
│   │       └── index.vue    # 看板：4 张 ECharts 图 + 2 个数字卡片
│   ├── dish/
│   │   └── index.vue        # 菜品列表+新增编辑弹窗+上下架
│   ├── ingredient/
│   │   └── index.vue        # 食材列表+新增编辑弹窗
│   ├── inventory/
│   │   └── index.vue        # 库存列表(预警标红)+入库弹窗
│   ├── recipe/
│   │   └── index.vue        # 配方维护(左选菜品+右动态行)
│   └── order/
│       └── index.vue        # 订单列表+详情弹窗+模拟下单+完成按钮
```

---

## 6. 权限矩阵

| 功能 | ADMIN | MANAGER | STOCK_KEEPER |
|------|:-----:|:-------:|:------------:|
| 用户/角色/菜单管理 | ✅ | ❌ | ❌ |
| 数据看板 | ✅ | ✅ | ❌ |
| 菜品管理（增删改查） | ✅ | ✅ | ❌ |
| 菜品配方维护 | ✅ | ✅ | ❌ |
| 食材档案（增删改查） | ✅ | ❌ | ✅ |
| 入库登记 | ✅ | ❌ | ✅ |
| 库存查看 | ✅ | ✅ | ✅ |
| 订单查看/模拟下单/完成 | ✅ | ✅ | ❌ |

权限代码按若依约定录入 sys_menu 的 `perms` 字段，例如：
- `dish:list`、`dish:add`、`dish:edit`、`dish:remove`
- `inventory:list`、`stockIn:add`
- `order:list`、`order:mock`、`order:complete`

后端接口用 `@PreAuthorize("@ss.hasPermi('order:mock')")` 注解保护。

---

## 7. 错误处理设计

| 场景 | HTTP 状态 | code | msg |
|------|---------|------|-----|
| 未登录 / token 过期 | 401 | 401 | "未授权，请重新登录" |
| 无权限 | 403 | 403 | "没有操作权限" |
| 业务错误（库存不足等） | 200 | 500 | "食材【XX】库存不足" |
| 参数校验失败 | 200 | 500 | "菜品名称不能为空" |
| 系统异常 | 200 | 500 | "系统错误，请联系管理员" |

若依内置 `GlobalExceptionHandler` 统一处理，业务层抛 `ServiceException` 即可。

---

## 8. 演示数据方案

在 `schema.sql` 中提供初始数据，另需在 D9 执行演示数据脚本（模拟近 7 天订单）：

```sql
-- 演示数据生成思路（D9 执行）
-- 在 t_order + t_order_item 中插入近 7 天的历史数据
-- 每天 25-40 单，总金额 800-1500 元，保证看板图表好看

-- 示例：插入昨天的一笔订单
INSERT INTO t_order(order_no, total_amount, status, order_time)
VALUES('DEMO001', 45.00, 2, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO t_order_item(order_id, dish_id, dish_name, price, quantity)
VALUES(LAST_INSERT_ID(), 1, '红烧肉', 15.00, 2),
      (LAST_INSERT_ID(), 7, '米饭',   1.50,  2);
-- 按此格式批量插入 7 天数据...
```

> 建议写一个 Python 小脚本（或 Java main）随机生成 200 条订单数据，
> 分布在近 7 天，让折线图有起伏，Top10 有差异，看板才好看。

---

## 9. 关键配置

### application-druid.yml（数据库）
```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
        username: root
        password: yourpassword
```

### application.yml（JWT & token 有效期）
```yaml
# 若依 token 配置
token:
  header: Authorization
  secret: abcdefghijklmnopqrstuvwxyz  # 生产环境换强密钥
  expireTime: 30  # 分钟
```

### 前端 .env.development（API 代理）
```
VITE_APP_BASE_API = '/dev-api'
```

### vue.config.js / vite.config.js（代理）
```javascript
proxy: {
  '/dev-api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: path => path.replace(/^\/dev-api/, '')
  }
}
```
