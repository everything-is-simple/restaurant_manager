# 文瀛餐厅管理系统 — 开发任务清单 (Task)
## 方案 B：FastAPI + Python + Vue3

**版本**：v1.0  **日期**：2026-06-14  **总工期**：10 天

---

## 团队分工

| 成员 | 主责 | 备注 |
|------|------|------|
| 成员 A（后端/基础） | 工程搭建、数据库、认证/RBAC、菜品+食材+配方+库存 后端 | 兼 D1 环境 |
| 成员 B（后端/业务） | 订单后端（含核心扣库存）、统计接口、演示数据脚本 | 兼测试 |
| 成员 C（前端） | 所有前端页面、ECharts 看板、联调 | 兼文档+PPT |

> 如果只有 1 人独立开发，D2 后端搭建比方案 A 花时间（没有代码生成器），但 Python 代码更简短，整体工作量相当。

---

## 每日任务（按天）

### D1 — 环境搭建

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 1.1 | 安装 Python 3.11、创建虚拟环境 `python -m venv .venv` | A | venv 激活成功 |
| 1.2 | 创建 `backend/` 工程目录，新建 `requirements.txt`，`pip install -r requirements.txt` | A | 无报错安装完成 |
| 1.3 | 创建 MySQL 数据库 `restaurant_b`，执行 `schema.sql`（8 张表 + 演示基础数据） | A | `SHOW TABLES` 可见 8 张表 |
| 1.4 | 写 `database.py`（SQLAlchemy engine + SessionLocal） + `main.py`（空 FastAPI）| A | `uvicorn main:app --reload` 启动，访问 `/docs` 有 Swagger 页面 |
| 1.5 | 创建 `frontend/` 工程：`npm create vue@latest`，选 Vue3+Vite+JS | C | `pnpm dev` 访问 localhost:5173 |
| 1.6 | 前端安装依赖：Element Plus、ECharts、Axios、Vue Router、Pinia | C | 编译无报错 |
| 1.7 | Git 仓库初始化，`dev` 分支，`.gitignore` 排除 `__pycache__/`、`.venv/`、`node_modules/` | A | 推送到远程 |

**当天产出**：前后端各自能启动，Swagger 文档页面可访问。

---

### D2 — 认证 + RBAC 后端

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 2.1 | 写 `models/user.py`（User 模型） | A | 导入无报错 |
| 2.2 | 写 `core/security.py`（hash_password / verify_password / create_token / decode_token） | A | 单独测试：hash + verify 正确 |
| 2.3 | 写 `core/deps.py`（get_db / get_current_user / require_role） | A | — |
| 2.4 | 写 `routers/auth.py`（POST /api/auth/login） | A | Postman 测通，返回 token |
| 2.5 | 初始化三个账号（admin/manager/stockkeeper）写入数据库，密码 bcrypt 加密 | A | 三账号可登录 |
| 2.6 | 前端：登录页（用户名+密码表单，调 /api/auth/login，token 存 localStorage） | C | 登录成功跳转主页 |
| 2.7 | 前端：Axios request 封装（baseURL=localhost:8000，自动加 Authorization 头，401 跳登录） | C | 封装完成 |
| 2.8 | 前端：主布局框架（左侧菜单 + 顶部用户名/退出，Vue Router，Pinia 存 role） | C | 主框架可显示空白内容区 |

**当天产出**：登录功能完整，token 鉴权可用，前端主框架搭好。

---

### D3 — 菜品后端 + 前端

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 3.1 | 写 `models/dish.py`（Category + Dish ORM 模型） | A | — |
| 3.2 | 写 `schemas/dish.py`（CategoryCreate/Out、DishCreate/Update/Out） | A | — |
| 3.3 | 写 `services/dish_service.py`（CRUD + changeStatus） | A | — |
| 3.4 | 写 `routers/dish.py`（GET /list / POST / PUT / PUT changeStatus / DELETE） | A | Swagger 测通全部接口 |
| 3.5 | 前端：`api/dish.js`（封装菜品相关接口） | C | — |
| 3.6 | 前端：菜品管理页 `views/dish/index.vue` | C | — |
| — | &nbsp;&nbsp;表格列表（分类下拉筛选、关键字搜索） | C | 筛选生效 |
| — | &nbsp;&nbsp;新增/编辑弹窗（分类下拉选）| C | 保存成功 |
| — | &nbsp;&nbsp;上下架（el-switch，调 changeStatus） | C | 开关生效 |
| — | &nbsp;&nbsp;删除（二次确认） | C | 删后刷新 |
| 3.7 | 联调菜品页 | A+C | CRUD 完整跑通 |

**当天产出**：菜品管理模块完整可用。

---

### D4 — 食材 + 配方后端与前端

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 4.1 | 写 `models/ingredient.py`（Ingredient / Inventory / StockIn） | A | — |
| 4.2 | 写 `schemas/ingredient.py` | A | — |
| 4.3 | `services/ingredient_service.py`：新增食材时同步初始化 t_inventory | A | 新增食材后库存表出现对应行 stock=0 |
| 4.4 | `routers/ingredient.py`（食材 CRUD） | A | Swagger 测通 |
| 4.5 | **配方后端**（`models/recipe.py` + `routers/recipe.py`） | A | — |
| — | &nbsp;&nbsp;`GET /api/recipe?dish_id=x`：查某菜品配方 | A | — |
| — | &nbsp;&nbsp;`POST /api/recipe/save`：覆盖写（先 DELETE WHERE dish_id=? 再批量 INSERT） | A | 测通 |
| 4.6 | 前端：食材管理页（CRUD，较简单，参考菜品页写） | C | CRUD 跑通 |
| 4.7 | **前端：配方维护页**（重点手写页面） | C | — |
| — | &nbsp;&nbsp;左侧：菜品下拉，切换时加载该菜品配方 | C | 联动正常 |
| — | &nbsp;&nbsp;右侧：动态行（食材下拉 + 用量输入 + 删行） | C | 可增删行 |
| — | &nbsp;&nbsp;保存按钮调 POST /api/recipe/save | C | 保存后重新加载配方 |
| 4.8 | 联调配方页 | A+C | 配方保存+加载正常 |

**当天产出**：食材档案 + 配方维护完整可用。

---

### D5 — 订单后端（核心）

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 5.1 | 写 `models/order.py`（Order + OrderItem） | B | — |
| 5.2 | 写 `schemas/order.py`（MockOrderRequest / OrderOut / OrderItemOut） | B | — |
| 5.3 | **`services/order_service.mock_order()`** | B | — |
| — | &nbsp;&nbsp;校验菜品上架 + 计算 total | B | — |
| — | &nbsp;&nbsp;db.add(order) + db.flush() 获取 id | B | — |
| — | &nbsp;&nbsp;批量 db.add(OrderItem) + db.commit() | B | Swagger 测通，数据库有记录 |
| 5.4 | **`services/order_service.complete_order()`（★最重要）** | B | — |
| — | &nbsp;&nbsp;查 order + items | B | — |
| — | &nbsp;&nbsp;for 每个 item → 查配方 → text() 原子 UPDATE | B | — |
| — | &nbsp;&nbsp;rowcount==0 时 db.rollback() + raise HTTPException | B | 库存不足时回滚成功 |
| — | &nbsp;&nbsp;全部通过后 order.status=2 + db.commit() | B | 完成后库存正确扣减 |
| 5.5 | `services/order_service.cancel_order()`（status → 3，不扣库存） | B | 测通 |
| 5.6 | 写 `routers/order.py`（list / detail / mock / complete / cancel） | B | 全接口 Swagger 测通 |
| 5.7 | **手动测试扣库存逻辑**（用 Swagger 跑：配方→入库→下单→完成→查库存） | B | 库存数字正确减少 |

**当天产出**：订单核心闭环后端完成，扣库存测试通过。

---

### D6 — 订单前端 + 库存前端

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 6.1 | 前端：库存管理页 `views/inventory/index.vue` | C | — |
| — | &nbsp;&nbsp;列表：stock <= safety_stock 时行标红 | C | 预警高亮 |
| — | &nbsp;&nbsp;入库弹窗：食材下拉 + 数量 + 单价 + 备注 | C | 入库后库存数字变化 |
| 6.2 | 前端：订单管理页 `views/order/index.vue` | C | — |
| — | &nbsp;&nbsp;列表：时间/状态筛选，el-tag 显示状态颜色 | C | 筛选生效 |
| — | &nbsp;&nbsp;详情弹窗：订单基本信息 + 明细表格 | C | 弹窗正常 |
| — | &nbsp;&nbsp;**"模拟下单"弹窗**：勾选菜品+输份数 | C | 下单后列表刷新 |
| — | &nbsp;&nbsp;**"完成订单"按钮**（仅 status=1 显示） | C | 点击后库存变化 |
| 6.3 | 联调：完成订单后刷新库存页，验证库存扣减 | B+C | 数字正确 |
| 6.4 | 前端菜单权限：manager 无食材/入库菜单，stock_keeper 无订单/看板菜单 | C | 权限隔离正确 |

**当天产出**：业务主闭环可完整演示。

---

### D7 — 统计接口 + 看板后端

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 7.1 | `services/stats_service.get_overview()`：4 条 text() SQL | B | 先在 Navicat 验证 SQL 结果正确 |
| 7.2 | `schemas/stats.py`（OverviewResponse Pydantic 模型） | B | — |
| 7.3 | `routers/stats.py`（GET /api/stats/overview，require_role(manager,admin)） | B | Swagger 返回 4 组数据 |
| 7.4 | 库存预警接口 `GET /api/inventory/warning`（stock <= safety_stock） | A | 返回预警列表 |

**当天产出**：统计接口完成，数据结构对齐前端需要。

---

### D8 — 数据看板前端

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 8.1 | 看板页：2 个数字卡片（今日营收、今日订单数，大字+彩色背景） | C | 数字正确 |
| 8.2 | 近 7 天营收折线图（ECharts，带渐变面积，x 轴为日期） | C | 图表渲染正常 |
| 8.3 | 销量 Top10 横向柱状图 | C | 图表渲染正常 |
| 8.4 | 库存预警列表（调 /api/inventory/warning，嵌入看板底部） | C | 预警食材显示 |
| 8.5 | window resize 时 chart.resize() 自适应 | C | 缩放不变形 |
| 8.6 | 全站标题改为"文瀛餐厅管理系统"，登录页背景/颜色微调 | C | 外观到位 |

**当天产出**：系统视觉完整，看板有 ECharts 动效。

---

### D9 — 联调测试 + 演示数据

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 9.1 | 全链路演示走一遍，记录 bug | 全员 | Bug 清单整理 |
| 9.2 | 修复 P0 bug | A+B | 主流程无阻断 |
| 9.3 | 运行 `python seed/seed_data.py` 生成 7 天内 200 笔订单 | B | 折线图有起伏，Top10 有层次 |
| 9.4 | 入库补货至足量（避免演示扣库存失败） | B | 库存充足 |
| 9.5 | 验证三角色权限：分别登录，确认菜单隔离正确 | A | 权限正确 |
| 9.6 | FastAPI 文档验证（/docs 所有接口均有 JWT 锁按钮，输入 token 可测试） | B | 文档可用 |
| 9.7 | 前端样式 review | C | 无明显视觉问题 |

**当天产出**：系统稳定，演示数据丰富。

---

### D10 — 文档 + PPT + 彩排

| # | 任务 | 负责 | 完成标志 |
|---|------|------|---------|
| 10.1 | 需求文档（主要功能截图+说明，1-2页） | C | 完成 |
| 10.2 | 系统设计文档（架构图+核心表+关键代码，1-2页） | A | 完成 |
| 10.3 | 部署说明（5 步：克隆→建库→改 DB_URL→pip install→启动，1页） | A | 完成 |
| 10.4 | 用户手册（三角色操作说明，截图，1页） | C | 完成 |
| 10.5 | 答辩 PPT（背景→功能演示→架构→核心亮点→自主可控说明→总结） | C | PPT 完成 |
| 10.6 | **彩排第一次**（限时 8 分钟，记录卡壳点） | 全员 | 找出问题 |
| 10.7 | 修复彩排发现的演示 bug | A+B | 修复完 |
| 10.8 | **彩排第二次**（全程流畅，控制在 8 分钟） | 全员 | 流畅无卡顿 |

**当天产出**：验收材料齐全，答辩准备完毕。

---

## 方案 B 特有注意事项

### Python 常见踩坑

| 坑 | 现象 | 解决 |
|----|------|------|
| SQLAlchemy lazy load 问题 | 查 order 后访问 items 报 `DetachedInstanceError` | 在 query 时加 `options(selectinload(...))` 或在 session 关闭前取数据 |
| `db.flush()` vs `db.commit()` | flush 后改变丢失 | 确认下单最后调用 `db.commit()` |
| Pydantic v2 from_attributes | `orm_mode` 改成 `model_config = ConfigDict(from_attributes=True)` | Pydantic v2 语法 |
| CORS 导致前端报错 | 前端 axios 报 Network Error | 确认 main.py 里 `allow_origins` 包含 `http://localhost:5173` |
| `text()` SQL 参数 | 用 `$name` 写法报错 | FastAPI 用 `:name` 占位符（不是 `$`） |

### FastAPI 与若依的主要差异（给前端同学）

| 项 | 若依（方案 A） | FastAPI（方案 B） |
|----|--------------|-----------------|
| 错误格式 | `{ code:500, msg:"..." }` | HTTP 4xx/5xx + `{ detail:"..." }` |
| 分页响应 | `{ rows:[...], total:N }` | 自定义，建议同样格式 |
| 接口文档 | http://localhost:8080/doc.html | http://localhost:8000/docs |
| 登录返回 | `{ token: "..." }` | `{ access_token:"...", token_type:"bearer" }` |

---

## 关键风险预案

| 风险 | 触发时间 | 应对 |
|------|---------|------|
| SQLAlchemy 事务不熟悉 | D5 | 先用 text() + db.execute() 写原生 SQL，最安全 |
| Pydantic v2 语法变化 | D2-3 | 遇错查 FastAPI 0.111 官方文档，不要用旧版示例 |
| CORS 跨域报错 | D2 | 检查 main.py allow_origins 是否含 localhost:5173 |
| 配方页前端太复杂 | D4 | 简化为普通表单（下拉+输入框×行），不用动态表格 |
| 演示数据 seed 跑报错 | D9 | 检查 dish_id 是否和数据库一致；先手动测一两笔 |

---

## 验收清单

- [ ] 三角色分别登录，菜单权限互相隔离
- [ ] 菜品增删改查 + 上下架
- [ ] 食材档案增删改查
- [ ] 配方维护（至少 3 道菜有完整配方）
- [ ] 食材入库，库存增加
- [ ] 模拟下单成功，生成订单
- [ ] 完成订单，库存按配方精准扣减
- [ ] 库存不足时，完成订单返回 400 错误（前端显示错误提示）
- [ ] 数据看板 4 图正常显示，数字准确
- [ ] 库存预警标红
- [ ] FastAPI Swagger 文档可通过 JWT token 测试所有接口
- [ ] 源码可通过 README 5 步骤成功运行
