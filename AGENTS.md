# AGENTS.md

本文件为 Qoder（qoder.com）在此仓库中工作时提供指引。

## 仓库概况

本仓库是 **文瀛餐厅管理系统**（中北大学课程/答辩项目），基于 **RuoYi-Vue 3.9.2 经典版** 二次开发。

## 仓库布局与关键目录

仓库包含两部分：施工文档 + 可运行的 RuoYi-Vue 工程。

- **`G:\restaurant_manager\RuoYi-Vue\`** — 唯一实际运行目录，所有后端/前端业务代码以此为准
- **`docs/plan_ruoyi_classic/`** — 唯一文档根目录（PRD、TRD、系统设计、施工计划、schema、菜单 SQL）
- **`sql/`** 和 **`RuoYi-Vue/sql/`** — 若依基座初始化 SQL

业务代码落位（不新建 Maven 子模块，贴合若依现有结构）：

| 层 | 位置 |
|---|---|
| Controller | `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/restaurant/` |
| Domain / Mapper / Service | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/` |
| Mapper XML | `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/` |
| 前端页面 | `RuoYi-Vue/ruoyi-ui/src/views/restaurant/<模块>/index.vue` |
| 前端 API | `RuoYi-Vue/ruoyi-ui/src/api/restaurant/<模块>.js` |

当前 6 个已生成 CRUD 模块：`category`、`dish`、`ingredient`、`inventory`、`order`、`stockIn`。  
`T_dish_ingredient` 和 `t_order_item` 表已落库但尚无 Domain/Mapper/Service（扣库存链路依赖它们）。

## 构建、运行、验证命令

所有命令从 `G:\restaurant_manager\RuoYi-Vue\` 执行：

| 用途 | 命令 |
|------|------|
| 编译后端 | `mvn -pl ruoyi-admin -am compile` |
| 打包后端（跳过测试） | `mvn -pl ruoyi-admin -am package -DskipTests` |
| 安装前端依赖 | `cd ruoyi-ui && npm install` |
| 启动前端开发服务器 | `cd ruoyi-ui && npm run dev` |
| 前端生产构建 | `cd ruoyi-ui && npm run build:prod` |

本仓库尚无自动化测试套件。验证手段：

1. 后端编译通过：`mvn -pl ruoyi-admin -am compile`
2. 前端生产构建通过：`cd ruoyi-ui && npm run build:prod`
3. 手动验收受影响的 CRUD 页面、菜单、权限按钮

## 前端路由机制

餐厅页面**不依赖** `ruoyi-ui/src/router/index.js` 手工注册。登录成功后，前端通过 `store/modules/permission.js` 调用后端 `getRouters()` 接口，用 `loadView(route.component)` 动态加载页面组件。因此菜单是否正确取决于 `sys_menu` 表中 `component` 字段是否指向 `restaurant/<模块>/index`。

## 数据库与菜单

- 业务 schema：`docs/plan_ruoyi_classic/schema.sql`（8 张业务表）
- 菜单初始化：`docs/plan_ruoyi_classic/menu_init.sql`（31 菜单/按钮 + 2 角色 + 2 测试账号）
- 数据库：`ry-vue`，MySQL 8，端口 3306；Redis 端口 6379
- 修改菜单或表结构时，同步更新 `schema.sql` 和 `menu_init.sql`，再手动执行 SQL

## 编码规范

- **Java**：4 空格缩进，类名 `UpperCamelCase`，字段/方法 `lowerCamelCase`，遵循若依生成器命名风格
- **Vue**：页面放 `src/views/restaurant/<模块>/index.vue`，API 放 `src/api/restaurant/<模块>.js`
- 新增 Controller / Service / Mapper 保持与若依生成器产物一致的结构
- 小步修改，不要顺手重构无关的生成代码
- 业务逻辑涉及库存变动（扣库存、退单回滚等）必须加 `@Transactional`，并用 `UPDATE ... WHERE stock >= deduct` 防并发超扣

## 权限命名

当前策略（已在 D3 校准阶段确认）：

- **CRUD 操作**保持若依生成器默认命名：`restaurant:<模块>:add/edit/remove/export`
- **D6 业务操作**新增语义化权限按钮：`restaurant:order:mock`、`restaurant:order:complete`、`restaurant:order:cancel`、`restaurant:inventory:stockin` 等
- 修改权限命名必须**同步更新**后端 `@PreAuthorize`、前端 `v-hasPermi`、`sys_menu` 表三处

## 提交规范

沿用现有 Conventional Commits 风格：`feat:`、`fix:`、`docs:`、`chore:`，可加中文作用域如 `docs(计划):`、`feat(权限):`。

## 纪律

- 临时/中间文件统一存放至 `G:\restaurant_manager_tmp\`，不污染项目根目录
- 不提交真实密码或密钥；本地 DB/Redis 配置仅存于环境级配置
- 施工计划以 `docs/plan_ruoyi_classic/04_施工计划_当前进度版.md` 为单一事实源
