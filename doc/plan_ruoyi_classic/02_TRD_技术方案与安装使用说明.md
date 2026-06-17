# 文瀛餐厅管理系统 — 技术方案与安装使用说明

## 1. 最终技术选型

本项目以本地现有 `RuoYi-Vue` 经典版为唯一底座，不采用其他历史备选路线。

| 层次 | 技术 |
|------|------|
| 脚手架 | RuoYi-Vue 3.9.2 经典版 |
| 后端 | Java 17 + Spring Boot 4.x |
| 安全 | Spring Security + JWT |
| 缓存 | Redis 6/7 |
| 持久层 | MyBatis + XML |
| 前端 | Vue 2 + Vue CLI + Element UI + Vuex |
| 图表 | ECharts 5.x |
| 数据库 | MySQL 8.x Community |
| 构建 | Maven + npm |
| 接口文档 | Springdoc / Swagger UI |

## 2. 真实仓库结构

当前施工代码底座位于 `F:\restaurant_manager\RuoYi-Vue`。

业务代码默认落位：

- Controller：`ruoyi-admin`
- Domain / Mapper / Service：`ruoyi-system`
- Mapper XML：`ruoyi-system/src/main/resources/mapper`
- 前端页面：`ruoyi-ui/src/views/restaurant`
- 前端 API：`ruoyi-ui/src/api/restaurant`

不新建独立 Maven 子模块，优先贴合若依现有结构以减少改造成本。

## 3. 必装软件

- JDK 17
- Maven 3.9 或更高版本
- MySQL 8.x Community，优先 8.4 LTS
- Redis 6 或 7
- Node.js 16 LTS
- npm

## 4. 本机当前已知风险

按当前环境检查结果，存在以下问题：

- `java` 命令不可用
- `mvn` 命令不可用
- Node 当前为 `v26.1.0`
- 当前环境中没有额外前端包管理器，本方案只使用 `npm`

说明：

- 若依经典版前端使用 Vue CLI 4，Node 16 LTS 更稳，Node 26 存在兼容风险
- 后端启动前必须先正确安装并配置 JDK 17 与 Maven 环境变量

## 5. 安装步骤

### 5.1 安装 JDK 17

1. 下载并安装 JDK 17
2. 配置 `JAVA_HOME`
3. 将 `%JAVA_HOME%\\bin` 加入 `Path`
4. 终端执行 `java -version` 验证

### 5.2 安装 Maven 3.9+

1. 下载 Maven 3.9+
2. 配置 `MAVEN_HOME`
3. 将 `%MAVEN_HOME%\\bin` 加入 `Path`
4. 终端执行 `mvn -version` 验证

### 5.3 安装 MySQL 8.x Community

1. 安装 MySQL Community Server
2. 记录 root 密码
3. 确保本地能连接 `localhost:3306`

建议：

- Windows 直接安装 MSI 版本
- 优先 MySQL 8.4 LTS

### 5.4 安装 Redis

1. 安装 Redis
2. 启动本地实例
3. 保持默认地址 `localhost:6379`

### 5.5 安装 Node.js 16 LTS

1. 安装 Node.js 16 LTS
2. 执行 `node -v` 和 `npm -v`
3. 确保不是 Node 26 这类过新版本

## 6. 数据库初始化

### 6.1 创建数据库

```sql
CREATE DATABASE `ry-vue` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 6.2 导入若依初始化 SQL

执行：

- `F:\restaurant_manager\RuoYi-Vue\sql\ry_20260417.sql`

### 6.3 导入业务表 SQL

执行：

- `F:\restaurant_manager\docs\plan_ruoyi_classic\schema.sql`

导入完成后，应同时拥有若依自带 `sys_*` 表和业务 `t_*` 表。

## 7. 配置修改

### 7.1 数据库配置

文件：

- `F:\restaurant_manager\RuoYi-Vue\ruoyi-admin\src\main\resources\application-druid.yml`

需要修改：

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 你的MySQL密码
```

### 7.2 Redis 配置

文件：

- `F:\restaurant_manager\RuoYi-Vue\ruoyi-admin\src\main\resources\application.yml`

默认即可：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## 8. 启动方式

### 8.1 启动后端

在 `F:\restaurant_manager\RuoYi-Vue` 下执行：

```bash
mvn clean install
mvn --projects ruoyi-admin spring-boot:run
```

默认端口：

- 后端 `http://localhost:8080`

### 8.2 启动前端

在 `F:\restaurant_manager\RuoYi-Vue\ruoyi-ui` 下执行：

```bash
npm install
npm run dev
```

默认前端开发端口：

- `http://localhost`

说明：

- 当前若依前端基于 Vue CLI 和 `vue.config.js` 代理配置
- API 代理由 `VUE_APP_BASE_API=/dev-api` 和 `vue.config.js` 提供

## 9. 首次使用步骤

1. 浏览器打开前端登录页
2. 使用默认账号 `admin / admin123` 登录
3. 在“系统工具 -> 代码生成”中导入业务表
4. 生成基础 CRUD 代码
5. 合并生成代码到若依项目
6. 在“系统管理 -> 菜单管理”中配置餐厅业务菜单
7. 创建角色：
   - ADMIN
   - MANAGER
   - STOCK_KEEPER
8. 创建测试账号：
   - `manager / manager123`
   - `stockkeeper / stock123`

## 10. 演示链路

建议答辩演示顺序：

1. 使用不同角色登录，展示权限隔离
2. 维护一条菜品配方
3. 录入一笔食材入库
4. 后台模拟下单
5. 完成订单
6. 查看库存自动扣减
7. 查看看板数据变化

## 11. 核心接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dish/list` | 菜品列表 |
| GET | `/api/category/list` | 分类列表 |
| GET | `/api/ingredient/list` | 食材列表 |
| GET | `/api/inventory/list` | 库存列表 |
| POST | `/api/stockIn` | 入库登记 |
| GET | `/api/recipe/list` | 配方查询 |
| POST | `/api/recipe/save` | 配方保存 |
| GET | `/api/order/list` | 订单列表 |
| POST | `/api/order/mock` | 模拟下单 |
| PUT | `/api/order/complete/{id}` | 完成订单并扣库存 |
| PUT | `/api/order/cancel/{id}` | 退单 |
| GET | `/api/stats/overview` | 看板数据 |

## 12. 关键技术说明

- 配方联动是系统核心价值点
- 完成订单时按“菜品配方 × 份数”扣减库存
- 扣减 SQL 使用 `stock >= deduct` 条件避免超扣
- 业务异常使用若依统一异常处理返回提示
