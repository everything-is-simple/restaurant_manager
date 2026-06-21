# 文瀛餐厅管理系统 — 技术方案与安装使用说明

## 1. 项目说明

文瀛餐厅管理系统是中北大学课程/答辩项目，基于 RuoYi-Vue 3.9.2 经典版二次开发。系统聚焦高校食堂后台管理，不开发真实点餐小程序和在线支付，使用后台“模拟下单”完成演示闭环。

核心演示链路：`配方维护 -> 食材入库 -> 模拟下单 -> 完成订单 -> 按配方扣库存 -> 看板统计`。

实际运行工程根目录固定为：

```powershell
G:\restaurant_manager\RuoYi-Vue
```

施工文档根目录固定为：

```powershell
G:\restaurant_manager\docs\plan_ruoyi_classic
```

## 2. 技术选型

| 层次 | 技术 |
|------|------|
| 脚手架 | RuoYi-Vue 3.9.2 经典版 |
| 后端 | Java 17 + Spring Boot + Spring Security + JWT |
| 持久层 | MyBatis + XML |
| 数据库 | MySQL 8.x，库名 `ry-vue` |
| 缓存 | Redis 6/7 |
| 前端 | Vue 2 + Vue CLI + Element UI + Vuex |
| 图表 | ECharts 5.x |
| 构建 | Maven + npm |

业务代码不新建独立 Maven 子模块，沿用 RuoYi-Vue 现有聚合结构：

| 层 | 位置 |
|----|------|
| Controller | `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/restaurant/` |
| Domain / Mapper / Service | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/` |
| Mapper XML | `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/restaurant/` |
| 前端页面 | `RuoYi-Vue/ruoyi-ui/src/views/restaurant/` |
| 前端 API | `RuoYi-Vue/ruoyi-ui/src/api/restaurant/` |

## 3. 环境要求

- JDK 17
- Maven 3.9 或更高版本
- MySQL 8.x Community
- Redis 6 或 7
- Node.js 16 LTS
- npm

说明：RuoYi-Vue 经典版前端基于 Vue CLI 4，Node 16 LTS 兼容性更稳，不建议使用过新的 Node 版本作为答辩环境。

## 4. 数据库初始化

### 4.1 创建数据库

```sql
CREATE DATABASE `ry-vue` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 4.2 导入基础表和业务表

按顺序执行：

```powershell
G:\restaurant_manager\RuoYi-Vue\sql\ry_20260417.sql
G:\restaurant_manager\docs\plan_ruoyi_classic\schema.sql
G:\restaurant_manager\docs\plan_ruoyi_classic\menu_init.sql
```

导入完成后，应同时拥有 RuoYi 基础 `sys_*` 表、餐厅业务 `t_*` 表、餐厅业务菜单、MANAGER/STOCK_KEEPER 角色和两个测试账号。

## 5. 配置说明

### 5.1 数据库配置

文件：

```powershell
G:\restaurant_manager\RuoYi-Vue\ruoyi-admin\src\main\resources\application-druid.yml
```

关键配置：

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 你的MySQL密码
```

### 5.2 Redis 配置

文件：

```powershell
G:\restaurant_manager\RuoYi-Vue\ruoyi-admin\src\main\resources\application.yml
```

默认本地配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## 6. 构建与启动

### 6.1 后端编译

在 `G:\restaurant_manager\RuoYi-Vue` 执行：

```powershell
mvn -pl ruoyi-admin -am compile
```

### 6.2 后端打包

```powershell
mvn -pl ruoyi-admin -am package -DskipTests
```

打包产物位于：

```powershell
G:\restaurant_manager\RuoYi-Vue\ruoyi-admin\target\ruoyi-admin.jar
```

### 6.3 后端运行

```powershell
java -jar G:\restaurant_manager\RuoYi-Vue\ruoyi-admin\target\ruoyi-admin.jar
```

默认后端地址：`http://127.0.0.1:8080`。

### 6.4 前端依赖与开发服务

在 `G:\restaurant_manager\RuoYi-Vue\ruoyi-ui` 执行：

```powershell
npm install
npm run dev
```

默认前端地址：`http://127.0.0.1`。

### 6.5 前端生产构建

```powershell
npm run build:prod
```

生产构建产物位于：

```powershell
G:\restaurant_manager\RuoYi-Vue\ruoyi-ui\dist
```

## 7. 登录账号

| 账号 | 密码 | 角色 | 答辩用途 |
|------|------|------|----------|
| `admin` | `admin123` | 超级管理员 | 查看全部系统与餐厅功能 |
| `manager` | `admin123` | 食堂经理 | 菜品、配方、订单、库存查看、数据看板 |
| `stockkeeper` | `admin123` | 库管员 | 食材、入库、库存管理 |

权限隔离口径：MANAGER 不负责食材档案维护和入库管理；STOCK_KEEPER 不负责订单操作和数据看板。

## 8. 核心接口

所有业务接口均走 RuoYi 前端代理前缀，前端开发环境请求形态为 `/dev-api/restaurant/...`，后端 Controller 实际路径为 `/restaurant/...`。

| 方法 | 后端路径 | 说明 |
|------|----------|------|
| GET | `/restaurant/category/list` | 菜品分类列表 |
| GET | `/restaurant/dish/list` | 菜品列表 |
| GET | `/restaurant/ingredient/list` | 食材列表 |
| GET | `/restaurant/inventory/list` | 库存列表 |
| POST | `/restaurant/stockIn` | 新增入库并联动库存 |
| GET | `/restaurant/recipe/list/{dishId}` | 查询菜品配方 |
| PUT | `/restaurant/recipe/save` | 覆盖保存菜品配方 |
| GET | `/restaurant/order/list` | 订单列表 |
| POST | `/restaurant/order/mock` | 模拟下单 |
| PUT | `/restaurant/order/complete/{orderId}` | 完成订单并按配方扣库存 |
| PUT | `/restaurant/order/cancel/{orderId}` | 退单并回滚库存 |
| GET | `/restaurant/stats/dashboard` | 看板聚合数据 |
| GET | `/restaurant/stats/warning` | 库存预警列表 |

## 9. 演示链路

答辩推荐演示顺序：

1. 打开登录页，展示系统标题和品牌为“文瀛餐厅管理系统”。
2. 使用 `manager/admin123` 登录，展示首页、菜品、配方、订单、看板。
3. 在菜品或配方页面说明菜品与食材用量关系。
4. 展示库存和入库记录，说明入库会增加库存。
5. 在订单页使用“模拟下单”生成订单。
6. 点击“完成订单”，说明系统按 `菜品配方 × 份数` 扣减库存。
7. 回到看板，展示今日营收、今日订单数、销量 Top10 和库存预警变化。
8. 使用 `stockkeeper/admin123` 登录，展示库管员只能看到食材、入库、库存相关菜单。

## 10. 截图清单

答辩材料截图统一保存到：

```powershell
G:\restaurant_manager\docs\plan_ruoyi_classic\assets\d10
```

建议截图：

| 文件名 | 内容 |
|--------|------|
| `01-login.png` | 登录页品牌 |
| `02-dashboard.png` | 首页总览 |
| `03-recipe.png` | 菜品配方页 |
| `04-stock-in.png` | 入库管理页 |
| `05-order.png` | 订单管理页 |
| `06-stats.png` | 数据看板页 |
| `07-role-manager.png` | 食堂经理菜单 |
| `08-role-stockkeeper.png` | 库管员菜单 |

## 11. 核心技术说明

- 新增食材时自动初始化库存，避免食材存在但库存缺行。
- 入库新增、修改、删除均在事务中按差额联动库存。
- 配方保存采用按 `dish_id` 覆盖保存，保证菜品扣减口径唯一。
- 模拟下单不信任前端传入菜名和价格，后端按 `dishId` 读取真实菜品信息。
- 完成订单使用事务，按配方扣库存，并通过 `UPDATE ... WHERE stock >= deduct` 避免并发超扣。
- 退单会按原订单明细回滚库存。
- 看板统计以已完成订单和 `complete_time` 为经营数据口径。
