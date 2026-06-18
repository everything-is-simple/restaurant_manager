# 文瀛餐厅管理系统 — MVP 版本设计（10 天可交付）

> 适用对象：1 名（或 2-3 名）有 2 年编程基础的本科生
> 总工期：10 天
> 目标：**能跑、能演示、答辩讲得清** —— 不追求工程完备

---

## 0. 核心原则：能用够用

> 凡是写下来"看起来很专业但你 10 天做不完"的东西，全部砍掉。

**砍掉的东西（与上一版相比）**

| 砍掉 | 原因 | 替代 |
|------|------|------|
| RBAC 多角色权限 | 太复杂 | **单管理员**，一个账号登录就够 |
| JWT + Spring Security | 配置坑多 | Session（HttpSession）或最简 token |
| 微服务 / Redis / MinIO | 用不到 | 全部不要 |
| Docker / Nginx 部署 | 学习成本高 | `java -jar` 直接跑 |
| 食材配方多对多+理论消耗分析 | 数据建模复杂 | **菜品直接绑定库存数量**（卖一份扣 1） |
| 操作日志、临期预警、批次号 | 锦上添花 | 全部砍 |
| TypeScript、Pinia、动态路由 | 慢 | 纯 JS / Vue3，路由写死 |
| 数据导出 Excel | 答辩用不上 | 砍 |
| 同环比、损耗分析 | SQL 复杂 | 只做"每日营收""菜品销量 Top10" |
| 前端点餐系统对接 | 没人配合 | **自己写一个"模拟点餐"按钮**塞订单进去 |

**保留下来的（核心闭环）**

```
登录 → 上架菜品 → 模拟下单 → 自动扣库存 → 看见看板数据变化
```

这一条线能走通，答辩就稳了。

---

## 1. 简化后的功能清单

| 模块 | 功能 | 说明 |
|------|------|------|
| 登录 | 单账号 admin/123456 | 写死，演示够用 |
| 菜品管理 | 列表 / 新增 / 编辑 / 删除 / 上下架 | 一个表搞定 |
| 库存管理 | **菜品本身就是库存**：每个菜品有 `stock` 字段 | 不做食材层级 |
| 入库 | 在菜品上点"补货"，输入数量加上去 | 一个弹窗 |
| 订单 | 列表 / 详情 / **"模拟下单"按钮** | 用按钮代替前端点餐系统 |
| 自动扣库存 | 下单时菜品 stock - 数量 | 一段事务代码 |
| 数据看板 | ① 今日营收 ② 今日订单数 ③ 近 7 天营收折线 ④ 销量 Top10 柱状 | ECharts 4 张图 |

> **关键简化**：用"菜品自带库存"替代"食材+配方"。
> 答辩可以这样讲："MVP 阶段以菜品为最小库存单元，后续可扩展到食材级配方联动" —— 完全合理。

---

## 2. 技术栈（最小集）

挑你最熟的一套，**不要混着学新东西**。下面给两条路线，二选一。

### 路线 A：Java（推荐，如果你学过 Spring Boot）

| 层 | 技术 | 说明 |
|----|------|------|
| 后端 | **Spring Boot 3 + MyBatis-Plus** | CRUD 一把梭，省去手写 SQL |
| 数据库 | **MySQL 8** | 一个 DB，3-4 张表 |
| 前端 | **Vue 3 + Element Plus + ECharts**（CDN 引入也行） | 用 vue-cli/vite 起项目；不会 TS 就用纯 JS |
| 登录 | HttpSession 或最简 token | 不上 Spring Security |
| 部署 | 本地 `java -jar` + `npm run dev` 即可演示 | 不要 Docker |

### 路线 B：Python（如果你 Java 不熟）

| 层 | 技术 |
|----|------|
| 后端 | **FastAPI** + SQLAlchemy |
| 数据库 | **SQLite**（一个文件，零配置）或 MySQL |
| 前端 | 同路线 A |

> 一旦选定就别换。10 天没有试错预算。

### 更激进的简化（如果你前端完全没基础）

放弃前后端分离，用 **Spring Boot + Thymeleaf + Bootstrap**，整个项目一个工程。
丑一点没关系，能跑。**ECharts 还是用，看板必须有，答辩出彩全靠它**。

---

## 3. 数据库设计（4 张表，够了）

```sql
-- 用户（其实只放一个 admin）
CREATE TABLE t_user (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(50)  NOT NULL UNIQUE,
  password  VARCHAR(100) NOT NULL  -- 简单点直接明文也行，答辩别说就行；想稳就 BCrypt
);

-- 菜品（自带库存，自带分类名）
CREATE TABLE t_dish (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL,
  category    VARCHAR(50),                 -- 直接存字符串，省一张表
  price       DECIMAL(10,2) NOT NULL,
  stock       INT           NOT NULL DEFAULT 0,
  image       VARCHAR(255),                -- 可选，存 URL 即可
  status      TINYINT       NOT NULL DEFAULT 1, -- 1上架 0下架
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP
);

-- 订单
CREATE TABLE t_order (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no     VARCHAR(50)   NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status       TINYINT       NOT NULL DEFAULT 1, -- 1已下单 2已完成 3已退单
  order_time   DATETIME      DEFAULT CURRENT_TIMESTAMP
);

-- 订单明细
CREATE TABLE t_order_item (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id   BIGINT        NOT NULL,
  dish_id    BIGINT        NOT NULL,
  dish_name  VARCHAR(100)  NOT NULL,   -- 冗余，避免菜品改名后历史订单乱
  price      DECIMAL(10,2) NOT NULL,
  quantity   INT           NOT NULL
);
```

就这 4 张表。**不要再加表了**，加一张你就多一天工作量。

---

## 4. 核心接口（10 个左右就够）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/login | 登录 |
| GET  | /api/dishes | 菜品列表（支持分类筛选、关键字搜索） |
| POST | /api/dishes | 新增菜品 |
| PUT  | /api/dishes/{id} | 修改菜品 |
| PUT  | /api/dishes/{id}/status | 上下架 |
| POST | /api/dishes/{id}/restock | 补货（stock += n） |
| DELETE | /api/dishes/{id} | 删除 |
| GET  | /api/orders | 订单列表 |
| POST | /api/orders/mock | **模拟下单**（核心：扣库存） |
| GET  | /api/stats/overview | 看板数据（一次返回 4 张图所需数据） |

**`/api/stats/overview` 返回示例**：
```json
{
  "todayRevenue": 1234.50,
  "todayOrderCount": 56,
  "revenue7Days": [
    {"date":"06-08","amount":800},
    {"date":"06-09","amount":1200}
  ],
  "topDishes": [
    {"name":"红烧肉","qty":120},
    {"name":"西红柿鸡蛋","qty":98}
  ]
}
```
**一个接口喂饱整个看板**，前端简单，演示快。

---

## 5. 核心代码逻辑（模拟下单 + 扣库存）

伪代码，最关键的一段：

```java
@Transactional
public Long mockCreateOrder(List<OrderItemReq> items) {
    BigDecimal total = BigDecimal.ZERO;
    Order order = new Order();
    order.setOrderNo("M" + System.currentTimeMillis());
    order.setStatus(2);  // MVP 直接置"已完成"
    orderMapper.insert(order);

    for (OrderItemReq it : items) {
        Dish d = dishMapper.selectById(it.getDishId());
        if (d.getStock() < it.getQuantity()) {
            throw new BizException(d.getName() + " 库存不足");
        }
        d.setStock(d.getStock() - it.getQuantity());
        dishMapper.updateById(d);   // 扣库存

        OrderItem oi = new OrderItem();
        oi.setOrderId(order.getId());
        oi.setDishId(d.getId());
        oi.setDishName(d.getName());
        oi.setPrice(d.getPrice());
        oi.setQuantity(it.getQuantity());
        orderItemMapper.insert(oi);

        total = total.add(d.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())));
    }
    order.setTotalAmount(total);
    orderMapper.updateById(order);
    return order.getId();
}
```

> **这段代码答辩老师一定会问，背熟它**。事务、库存校验、明细冗余字段，三个点说出来就有分。

---

## 6. 前端页面（5 个就够）

```
登录页
└── 主框架（左侧 5 个菜单）
    ├── 数据看板（4 张 ECharts 图，进系统第一眼看到的就是这里）
    ├── 菜品管理（表格 + 新增/编辑弹窗 + 补货弹窗 + 上下架开关）
    ├── 订单管理（表格 + 详情弹窗）
    ├── 模拟下单（一个"随机生成一笔订单"按钮 + 一个"自选菜品下单"表单）
    └── 退出登录
```

> **看板放第一页**，老师点进系统就看到酷炫图表，第一印象拉满。

---

## 7. 10 天实施计划

> 假设每天 6-8 小时投入。如果有 2-3 人，可以前后端并行，时间还能再压。

| 天 | 任务 | 产出 |
|----|------|------|
| **D1** | 环境搭建：Spring Boot 工程 + MySQL 建库建表 + Vue 工程 + Element Plus | 前后端各自能跑空白页 |
| **D2** | 后端：登录接口；前端：登录页 + 主框架 + 路由 + Axios 封装 | 能登录进入空白主页 |
| **D3** | 后端：菜品 CRUD + 上下架 + 补货 接口；插入演示数据 | Postman / Knife4j 测通 |
| **D4** | 前端：菜品管理页（表格、搜索、新增/编辑弹窗、补货弹窗、上下架开关） | 菜品模块完整可用 |
| **D5** | 后端：模拟下单接口（核心：扣库存 + 事务） + 订单列表/详情接口 | 后端核心闭环跑通 |
| **D6** | 前端：订单页 + "模拟下单"页面（随机下单按钮 + 自选菜品表单） | 业务闭环可演示 |
| **D7** | 后端：`/api/stats/overview` 一个接口出全部看板数据（写 4 个 SQL 聚合） | 数据接口好了 |
| **D8** | 前端：数据看板页 4 张 ECharts 图 + 整体样式微调 | **核心系统完成** |
| **D9** | 整体联调、改 bug、灌入更丰富的演示数据（30 个菜品、100 单订单分布在 7 天里） | 系统稳定 |
| **D10** | 写文档（需求/设计/部署/用户手册各 1-2 页）+ 答辩 PPT + 彩排 2 次 | 验收材料齐全 |

**进度卡点（自检表）**

- D4 结束没做完菜品模块 → 砍掉图片上传、砍掉分类，赶进度
- D6 结束没做完订单闭环 → **直接砍订单详情页**，列表能看就行
- D8 结束没做完看板 → 砍到 2 张图（今日营收数字 + 销量 Top10）
- D10 之前必须完整彩排 1 次

> **原则**：宁可少 2 个功能，也要留 1 天彩排。

---

## 8. 答辩防忽悠话术

老师可能问的问题 & 怎么答：

| 问题 | 答 |
|------|----|
| "为什么没做食材级库存？" | "MVP 阶段以菜品为最小库存单元，简化数据模型快速验证闭环。下一步迭代会引入食材-配方多对多关系，实现食材级损耗分析。" |
| "怎么和前端点餐系统对接？" | "我预留了 `/api/orders/mock` 这种统一订单录入接口，未来前端点餐 App 只需调用这个接口即可接入，MVP 用按钮模拟。" |
| "权限呢？" | "MVP 只有食堂管理员一个角色，复杂 RBAC 在生产版本扩展。核心功能闭环验证优先。" |
| "数据安全？" | "登录鉴权、参数校验、MyBatis 预编译防注入。生产环境会加 HTTPS、密码 BCrypt、操作日志。" |
| "技术栈为什么这样选？" | "全开源、社区成熟、符合自主可控创新实验室要求；MySQL 可平滑迁移到 openGauss / 达梦。" |

> 所有"没做的"都用"MVP 阶段，下一步迭代"挡回去。**这是行业标准话术**，老师听了不会扣分。

---

## 9. 必须做对的 3 件事

1. **看板第一眼好看** —— ECharts 调色 + 演示数据丰富，老师视觉先入为主。
2. **模拟下单流程演示流畅** —— 下单 → 库存数字立刻变 → 看板数字立刻变，三秒讲完闭环。
3. **代码至少能解释** —— 那段扣库存的事务方法，逐行能讲清楚。

做到这 3 件，10 天 MVP 答辩稳过。
