# 文瀛餐厅管理系统 — 菜单与权限初始化清单

## 1. 文档目的

本文指导在若依后台"菜单管理""角色管理"中初始化餐厅业务菜单、按钮权限和角色可见范围。

## 2. 菜单树

```text
餐厅管理（目录）
├─ 数据看板
├─ 菜品分类
├─ 菜品管理
├─ 食材档案
├─ 库存管理
├─ 菜品配方
└─ 订单管理
```

## 3. 顶级目录菜单

| 菜单名称 | 类型 | 路由地址 | 组件路径 | 权限标识 | 图标 | 可见角色 |
|----------|------|----------|----------|----------|------|----------|
| 餐厅管理 | 目录 | `/restaurant` | — | — | `clipboard` | 全部三角色 |

## 4. 页面菜单清单

| 菜单名称 | 路由地址 | 组件路径 | 权限标识 | 图标 | 可见角色 |
|----------|----------|----------|----------|------|----------|
| 数据看板 | `stats` | `restaurant/stats/index` | `restaurant:stats:query` | `dashboard` | ADMIN、MANAGER |
| 菜品分类 | `category` | `restaurant/category/index` | `restaurant:category:list` | `tree` | ADMIN、MANAGER |
| 菜品管理 | `dish` | `restaurant/dish/index` | `restaurant:dish:list` | `list` | ADMIN、MANAGER |
| 食材档案 | `ingredient` | `restaurant/ingredient/index` | `restaurant:ingredient:list` | `edit` | ADMIN、STOCK_KEEPER |
| 库存管理 | `inventory` | `restaurant/inventory/index` | `restaurant:inventory:list` | `shopping` | ADMIN、MANAGER、STOCK_KEEPER |
| 菜品配方 | `recipe` | `restaurant/recipe/index` | `restaurant:recipe:list` | `guide` | ADMIN、MANAGER |
| 订单管理 | `order` | `restaurant/order/index` | `restaurant:order:list` | `order` | ADMIN、MANAGER |

说明：组件路径对应 `ruoyi-ui/src/views/restaurant/...`

## 5. 按钮权限清单

### 5.1 菜品分类

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 分类查询 | `restaurant:category:query` | ADMIN、MANAGER |
| 分类新增 | `restaurant:category:add` | ADMIN、MANAGER |
| 分类修改 | `restaurant:category:edit` | ADMIN、MANAGER |
| 分类删除 | `restaurant:category:remove` | ADMIN、MANAGER |

### 5.2 菜品管理

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 菜品查询 | `restaurant:dish:query` | ADMIN、MANAGER |
| 菜品新增 | `restaurant:dish:add` | ADMIN、MANAGER |
| 菜品修改 | `restaurant:dish:edit` | ADMIN、MANAGER |
| 菜品删除 | `restaurant:dish:remove` | ADMIN、MANAGER |
| 菜品上下架 | `restaurant:dish:status` | ADMIN、MANAGER |

### 5.3 食材档案

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 食材查询 | `restaurant:ingredient:query` | ADMIN、STOCK_KEEPER |
| 食材新增 | `restaurant:ingredient:add` | ADMIN、STOCK_KEEPER |
| 食材修改 | `restaurant:ingredient:edit` | ADMIN、STOCK_KEEPER |
| 食材删除 | `restaurant:ingredient:remove` | ADMIN、STOCK_KEEPER |

### 5.4 库存管理

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 库存查询 | `restaurant:inventory:query` | ADMIN、MANAGER、STOCK_KEEPER |
| 入库登记 | `restaurant:inventory:stockin` | ADMIN、STOCK_KEEPER |
| 入库查询 | `restaurant:stockIn:query` | ADMIN、STOCK_KEEPER |
| 入库新增 | `restaurant:stockIn:add` | ADMIN、STOCK_KEEPER |
| 入库修改 | `restaurant:stockIn:edit` | ADMIN、STOCK_KEEPER |
| 入库删除 | `restaurant:stockIn:remove` | ADMIN、STOCK_KEEPER |
| 入库导出 | `restaurant:stockIn:export` | ADMIN、STOCK_KEEPER |

说明：不创建"新增库存""删除库存"按钮，库存由"新增食材自动初始化"和"入库"驱动。`restaurant:stockIn:*` 为 TStockInController 生成器默认权限。

### 5.5 菜品配方

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 配方查询 | `restaurant:recipe:query` | ADMIN、MANAGER |
| 配方保存 | `restaurant:recipe:save` | ADMIN、MANAGER |

说明：配方页是"整页编辑 + 覆盖保存"模型，不创建默认 add/edit/remove 按钮。

### 5.6 订单管理

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 订单查询 | `restaurant:order:query` | ADMIN、MANAGER |
| 模拟下单 | `restaurant:order:mock` | ADMIN、MANAGER |
| 查看详情 | `restaurant:order:detail` | ADMIN、MANAGER |
| 完成订单 | `restaurant:order:complete` | ADMIN、MANAGER |
| 退单 | `restaurant:order:cancel` | ADMIN、MANAGER |

说明：不创建默认 add/edit/remove 按钮，订单由业务操作驱动。

### 5.7 数据看板

| 按钮名称 | 权限标识 | 可见角色 |
|----------|----------|----------|
| 看板查询 | `restaurant:stats:query` | ADMIN、MANAGER |

## 6. 角色菜单分配

### 6.1 ADMIN（系统管理员）

- 保留若依原有系统管理、系统监控、系统工具全部菜单
- 额外分配全部 7 个餐厅业务页面 + 全部按钮权限

### 6.2 MANAGER（食堂经理）

- 数据看板、菜品分类、菜品管理、库存管理、菜品配方、订单管理
- 不分配：食材档案维护、入库登记
- 按钮：拥有菜品/分类/配方/订单/看板/库存查看相关全部按钮

### 6.3 STOCK_KEEPER（库管员）

- 食材档案、库存管理
- 按钮：食材增删改查 + 入库登记 + 入库管理 CRUD（TStockInController）
- 不分配：菜品/配方/订单/看板

## 7. 权限矩阵（与 03_Design 一致）

| 功能 | ADMIN | MANAGER | STOCK_KEEPER |
|------|:-----:|:-------:|:------------:|
| 数据看板 | ✅ | ✅ | ❌ |
| 菜品分类 | ✅ | ✅ | ❌ |
| 菜品管理 | ✅ | ✅ | ❌ |
| 食材档案 | ✅ | ❌ | ✅ |
| 库存查看 | ✅ | ✅ | ✅ |
| 入库登记 | ✅ | ❌ | ✅ |
| 菜品配方 | ✅ | ✅ | ❌ |
| 订单管理 | ✅ | ✅ | ❌ |
| 系统管理 | ✅ | ❌ | ❌ |

## 8. 后端 @PreAuthorize 权限前缀

| 模块 | 前缀 |
|------|------|
| 分类 | `restaurant:category` |
| 菜品 | `restaurant:dish` |
| 食材 | `restaurant:ingredient` |
| 库存 | `restaurant:inventory` + `restaurant:stockIn` |
| 配方 | `restaurant:recipe` |
| 订单 | `restaurant:order` |
| 看板 | `restaurant:stats` |

示例：

```java
@PreAuthorize("@ss.hasPermi('restaurant:dish:list')")
@PreAuthorize("@ss.hasPermi('restaurant:inventory:stockin')")
@PreAuthorize("@ss.hasPermi('restaurant:order:complete')")
```

## 9. 若依后台初始化顺序

1. 创建一级目录"餐厅管理"
2. 创建 7 个页面菜单
3. 给每个页面挂接按钮权限
4. 创建角色：ADMIN、MANAGER、STOCK_KEEPER
5. 给角色分配菜单和按钮
6. 创建测试账号：`manager/manager123`、`stockkeeper/stock123`
7. 登录不同角色验证左侧菜单显示
