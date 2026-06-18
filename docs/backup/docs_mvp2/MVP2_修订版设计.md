# 文瀛餐厅管理系统 — 修订版设计（简单而不简陋）

> 10 天，2-3 人，2 年编程基础。
> 原则：**核心业务闭环完整，用成熟脚手架省重复劳动，不做超出验收要求的东西。**

---

## 0. 核心策略：站在巨人肩膀上

**不从零搭框架，用若依（RuoYi-Vue）做底座。**

若依是国内最流行的开源后台脚手架（GitHub 3 万+ Star），内置：
- 登录 / JWT / Spring Security / 多角色 RBAC
- 用户管理 / 菜单权限 / 操作日志
- **代码生成器**（根据数据库表一键生成 CRUD 代码，前后端都生成）

你只需要：建好 8 张业务表 → 用代码生成器出基础 CRUD → 在生成的代码上写业务逻辑。
估计节省 **40% 的工作量**。

**官方仓库（Vue3 版）**：https://github.com/yangzongzhuan/RuoYi-Vue

---

## 1. 用户角色（保留 3 个，够用）

| 角色 | 职责 | 备注 |
|------|------|------|
| **系统管理员** | 用户管理、基础配置 | 若依内置，几乎不用自己写 |
| **食堂经理** | 菜品管理、订单查看、数据看板 | 主要演示角色 |
| **库管员** | 食材入库、盘点、库存查看 | 体现"多角色"，答辩有说头 |

> 三个角色登录后看到不同菜单，体现 RBAC。若依直接支持，配置就行，不用写代码。

---

## 2. 功能模块（保留核心，砍掉边角）

| 模块 | 功能 | 是否保留 | 说明 |
|------|------|---------|------|
| 菜品管理 | 增删改查、上下架、分类 | ✅ 保留 | 代码生成器出基础，加上下架逻辑 |
| 食材管理 | 食材档案（名称/单位/安全库存） | ✅ 保留 | 这是库存联动的基础 |
| 菜品配方 | 一道菜消耗哪些食材多少量 | ✅ 保留 | **核心**，联动的关键 |
| 库存管理 | 当前库存量 + 低库存预警 | ✅ 保留 | 一张表，简单 |
| 入库记录 | 采购入库（食材+数量+日期） | ✅ 保留 | 一张表，代码生成器出 |
| 订单管理 | 接收模拟订单、查看列表详情 | ✅ 保留 | 模拟下单按钮替代前端点餐 |
| **订单→扣库存** | 完成订单时按配方自动扣减 | ✅ 保留 | **答辩核心演示点** |
| 数据看板 | 今日营收、订单数、销量Top10、7天趋势 | ✅ 保留 | ECharts，答辩第一眼 |
| 食材-配方损耗分析 | 理论消耗 vs 实际出库对比 | ⚠️ 可选 | 时间够就做，时间不够砍 |
| 数据导出 | Excel | ❌ 砍 | 答辩用不上 |
| 图片上传 | 菜品图片 | ❌ 砍 | 存 URL 字符串，图片直接用在线链接 |
| 供应商管理 | 供应商档案 | ❌ 砍 | 入库备注里写供应商名字就够 |

---

## 3. 数据库设计（8 张表）

若依本身自带 sys_* 系列表（用户/角色/权限），你只需要写业务表。

```sql
-- =========================================
-- 若依已有（不用你建）
-- sys_user, sys_role, sys_menu, sys_user_role, sys_role_menu ...
-- =========================================

-- =========================================
-- 业务表（你来建，共 8 张）
-- =========================================

-- 1. 菜品分类
CREATE TABLE t_category (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(50) NOT NULL,  -- 主食/热菜/凉菜/汤品/饮品
  sort      INT DEFAULT 0,
  del_flag  CHAR(1) DEFAULT '0'
);

-- 2. 菜品
CREATE TABLE t_dish (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(100)  NOT NULL,
  category_id  BIGINT        NOT NULL,
  price        DECIMAL(10,2) NOT NULL,
  description  VARCHAR(500),
  status       TINYINT       DEFAULT 1,  -- 1上架 0下架
  create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag     CHAR(1)       DEFAULT '0'
);

-- 3. 食材
CREATE TABLE t_ingredient (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100)  NOT NULL,
  unit          VARCHAR(20)   NOT NULL,  -- kg / 个 / 瓶
  safety_stock  DECIMAL(10,2) DEFAULT 0, -- 低于此值预警
  create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  del_flag      CHAR(1)       DEFAULT '0'
);

-- 4. 菜品配方（核心：菜品-食材多对多）
CREATE TABLE t_dish_ingredient (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  dish_id        BIGINT        NOT NULL,
  ingredient_id  BIGINT        NOT NULL,
  quantity       DECIMAL(10,3) NOT NULL  -- 单份消耗量
);

-- 5. 库存（当前库存量，每种食材一行）
CREATE TABLE t_inventory (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id  BIGINT        NOT NULL UNIQUE,
  stock          DECIMAL(10,2) NOT NULL DEFAULT 0,
  update_time    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. 入库记录
CREATE TABLE t_stock_in (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id  BIGINT        NOT NULL,
  quantity       DECIMAL(10,2) NOT NULL,
  unit_price     DECIMAL(10,2),
  remark         VARCHAR(200),
  operator       BIGINT,         -- sys_user.id
  in_time        DATETIME        DEFAULT CURRENT_TIMESTAMP
);

-- 7. 订单
CREATE TABLE t_order (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no      VARCHAR(50)   NOT NULL UNIQUE,
  total_amount  DECIMAL(10,2) NOT NULL,
  status        TINYINT       NOT NULL DEFAULT 1,  -- 1已下单 2完成 3退单
  order_time    DATETIME      DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_time (order_time)
);

-- 8. 订单明细
CREATE TABLE t_order_item (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id  BIGINT        NOT NULL,
  dish_id   BIGINT        NOT NULL,
  dish_name VARCHAR(100)  NOT NULL,   -- 快照，菜品改名历史不乱
  price     DECIMAL(10,2) NOT NULL,
  quantity  INT           NOT NULL,
  INDEX idx_order (order_id)
);
```

**ER 关系一句话**：菜品 → 配方 → 食材 → 库存；订单 → 明细 → 菜品

---

## 4. 核心业务逻辑（最重要的一段代码）

### 订单完成 → 按配方扣库存（事务）

```java
@Transactional
public void completeOrder(Long orderId) {
    TOrder order = orderMapper.selectById(orderId);
    List<TOrderItem> items = orderItemMapper.selectByOrderId(orderId);

    for (TOrderItem item : items) {
        // 查这道菜的配方
        List<TDishIngredient> recipes =
            dishIngredientMapper.selectByDishId(item.getDishId());

        for (TDishIngredient recipe : recipes) {
            // 该食材需扣减量 = 配方量 × 点餐份数
            BigDecimal deduct = recipe.getQuantity()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

            // 扣库存（用 UPDATE 带条件防并发超扣）
            int rows = inventoryMapper.deductStock(
                recipe.getIngredientId(), deduct);
            if (rows == 0) {
                throw new ServiceException("食材库存不足，无法完成订单");
            }
        }
    }

    order.setStatus(2);  // 已完成
    orderMapper.updateById(order);
}
```

```sql
-- inventoryMapper 对应的 SQL（在 XML 里写）
UPDATE t_inventory
SET stock = stock - #{deduct}
WHERE ingredient_id = #{ingredientId}
  AND stock >= #{deduct}   -- 这个条件防止扣成负数
```

> **这两段是答辩必考点**：事务保证原子性；UPDATE 带 `stock >= deduct` 条件防超扣。能逐行讲清楚，技术分直接拿到。

---

## 5. 接口设计（只列业务接口，若依系统接口不用你写）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/dish/list | 菜品分页列表（支持分类/关键字筛选） |
| POST | /api/dish | 新增菜品 |
| PUT  | /api/dish | 修改菜品 |
| PUT  | /api/dish/status | 上下架 |
| GET  | /api/ingredient/list | 食材列表 |
| POST | /api/stockIn | 入库登记 |
| GET  | /api/inventory/list | 库存列表（含预警标记） |
| GET  | /api/order/list | 订单列表 |
| POST | /api/order/mock | 模拟下单（选菜品+数量） |
| PUT  | /api/order/complete/{id} | 完成订单（触发扣库存） |
| GET  | /api/stats/overview | 看板数据（一次返回 4 组数据） |

> 若依代码生成器会生成大部分 GET/POST/PUT/DELETE 基础接口，你只需要额外写 `/mock`、`/complete`、`/stats/overview` 这三个有业务逻辑的接口。

---

## 6. 数据看板（4 张图，ECharts）

```
┌──────────────┬──────────────┐
│  今日营收     │  今日订单数   │  ← 数字卡片（大字，好看）
│  ¥ 2,340    │  87 单       │
├──────────────┴──────────────┤
│  近 7 天营收趋势（折线图）    │  ← 体现时间维度
├─────────────────────────────┤
│  菜品销量 Top10（横向柱状图） │  ← 体现哪个菜卖得好
└─────────────────────────────┘
```

`/api/stats/overview` 一个接口返回：
```json
{
  "todayRevenue": 2340.00,
  "todayOrders": 87,
  "revenue7Days": [{"date":"06-08","amount":1800}, ...],
  "topDishes": [{"name":"红烧肉","qty":120}, ...]
}
```

对应 SQL（后端 Service 里写 4 条聚合查询，不复杂）：
```sql
-- 今日营收
SELECT IFNULL(SUM(total_amount),0) FROM t_order
WHERE DATE(order_time)=CURDATE() AND status=2;

-- 销量 Top10
SELECT dish_name, SUM(quantity) as qty
FROM t_order_item oi
JOIN t_order o ON oi.order_id=o.id
WHERE o.status=2
GROUP BY dish_name ORDER BY qty DESC LIMIT 10;
```

---

## 7. 前端页面结构（7 个页面）

```
登录页（若依自带，稍微改下 logo 和配色）
└── 主框架（若依自带侧边栏 + 顶栏）
    ├── 数据看板       ← 登录后第一页，ECharts 4 图
    ├── 菜品管理       ← 列表 + 弹窗增删改 + 上下架开关（代码生成器出）
    ├── 食材管理       ← 列表 + 弹窗（代码生成器出）
    ├── 菜品配方       ← 列表 + 配方维护（选食材+数量，需手写）
    ├── 库存管理       ← 列表（含预警标记红色高亮）+ 入库弹窗
    ├── 订单管理       ← 列表 + 详情 + 【模拟下单】+ 【完成订单】按钮
    └── （系统管理：用户/角色 若依自带，直接用）
```

> **菜品配方页**是唯一需要手写的非标准页面：左边选菜品，右边动态添加"食材+用量"行，类似 BOM 编辑。可以用 Element Plus 的动态表格行实现。

---

## 8. 技术方案对比（三选一）

### 方案 A：若依 + Java（推荐）

| 项 | 内容 |
|----|------|
| 底座 | RuoYi-Vue（Vue3版） |
| 后端 | Spring Boot 3 + MyBatis-Plus |
| 前端 | Vue3 + Element Plus + ECharts |
| 优势 | RBAC/JWT/代码生成器免费得；中文文档完整；毕设同类项目最多 |
| 适合 | 学过 Spring Boot，哪怕只学了一学期 |
| 参考 | https://github.com/yangzongzhuan/RuoYi-Vue |

### 方案 B：FastAPI + Python（备选）

| 项 | 内容 |
|----|------|
| 后端 | Python 3.11 + FastAPI + SQLAlchemy + python-jose |
| 前端 | 同方案 A（Vue3 + Element Plus + ECharts） |
| 优势 | 代码量比 Java 少约 30%；没有若依但 FastAPI 本身很快 |
| 劣势 | RBAC 需要自己写（约 150 行，不复杂但要花半天）；无代码生成器 |
| 适合 | Python 比 Java 更熟的同学 |

### 方案 C：Django（极限简化版）

| 项 | 内容 |
|----|------|
| 后端 | Python + Django 4.x |
| 前端 | Django Template + ECharts（不分离） |
| 优势 | Django Admin 内置增删改查；整个项目一个工程；最少代码量 |
| 劣势 | 界面略丑；配方页 / 看板页定制较麻烦 |
| 适合 | 只有 7 天、或前端完全没基础 |

---

## 9. 10 天实施计划（修订版）

| 天 | 任务 | 关键产出 |
|----|------|---------|
| **D1** | 克隆若依-Vue3 跑通 + MySQL 建 8 张业务表 + 团队分工 Git 分支 | 框架跑通，表建好 |
| **D2** | 用若依代码生成器生成：菜品、食材、入库、库存、订单的基础 CRUD | 后端接口 80% 自动生成 |
| **D3** | 前端：菜品管理页联调 + 上下架逻辑；前端：食材管理页联调 | 菜品食材模块可用 |
| **D4** | 手写菜品配方页（前端动态行 + 后端保存接口）；配方数据入库测试 | 配方维护可用 |
| **D5** | 后端：模拟下单接口 + **完成订单→扣库存事务**；后端测试 | 核心闭环后端通 |
| **D6** | 前端：订单页（列表+详情+模拟下单表单+完成按钮）；联调 | 业务主闭环可演示 |
| **D7** | 后端：`/stats/overview` 接口（4 条聚合 SQL）；库存预警标记 | 统计接口完成 |
| **D8** | 前端：数据看板页（ECharts 4 图）；整体样式微调 | **系统核心完成** |
| **D9** | 全链路联调修 bug；灌入演示数据（30 菜品、7 天内 200 单分布合理） | 系统稳定可演示 |
| **D10** | 写文档（各 1-2 页）+ 答辩 PPT + 彩排 2 次 | 验收材料齐 |

---

## 10. 答辩演示脚本（3 分钟版）

1. **登录三个账号各看一眼**（30秒）：管理员/食堂经理/库管员，展示不同菜单权限，体现 RBAC
2. **新建菜品 + 配置配方**（40秒）：新增"番茄炒蛋"，配方设置消耗番茄 200g、鸡蛋 2 个
3. **库管员入库**（20秒）：番茄入库 10kg，库存从 0 变成 10000g
4. **模拟下单**（30秒）：下 3 份番茄炒蛋，点"完成订单"
5. **看库存变化**（20秒）：番茄库存自动从 10000g 变成 9400g（扣了 600g），说明联动
6. **切到数据看板**（40秒）：今日营收变了、订单数变了、Top10 里出现番茄炒蛋

> **关键话术**：步骤 5 讲完后说——"这就是系统的核心价值：联动前端点餐数据，管控食材损耗，每一笔订单完成都会按配方精准扣减食材，食堂经理可以随时看到实际库存消耗，减少人工盘点误差。"

---

## 11. 答辩高频问题 & 标准答

| 问题 | 答 |
|------|----|
| 为什么选若依？ | "若依是国内最流行的开源权限管理脚手架，内置 RBAC、JWT、代码生成，让我们把精力集中在食堂业务逻辑上，而不是重复造轮子。" |
| 库存扣减怎么保证准确？ | "用 Spring 事务 + UPDATE 语句带 `stock >= 扣减量` 条件，原子更新，防止并发超扣，扣失败整个事务回滚。" |
| 为什么不接真实点餐前端？ | "系统预留了标准订单录入接口 `/api/order/mock`，未来前端点餐小程序只需调用此接口即可接入；MVP 阶段用按钮模拟验证闭环。" |
| 配方联动有什么意义？ | "用配方把菜品和食材关联，每完成一笔订单系统自动推算食材消耗，食堂经理可对比入库量与理论消耗量，识别损耗异常。" |
| 自主可控体现在哪？ | "全栈开源：若依/Spring Boot/Vue/MySQL，MySQL 可迁移到 openGauss 或达梦，无任何闭源商业依赖。" |
