# 文瀛餐厅管理系统 — 系统设计文档 (Design)
## 方案 B：FastAPI + Python + Vue3

**版本**：v1.0  **日期**：2026-06-14

---

## 1. 整体架构

```
┌───────────────────────────────────────────────────────────────┐
│                    浏览器（PC 后台管理端）                       │
│                                                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ ┌──────┐ │
│  │ 数据看板  │ │ 菜品管理  │ │ 食材库存  │ │ 配方   │ │ 订单 │ │
│  │ ECharts  │ │ 列表+弹窗 │ │ 预警列表  │ │ 动态行 │ │ 模拟 │ │
│  └──────────┘ └──────────┘ └──────────┘ └────────┘ └──────┘ │
│                    Vue3 + Element Plus + Axios                │
└──────────────────────┬────────────────────────────────────────┘
                       │  HTTP REST + JWT Bearer Token
┌──────────────────────▼────────────────────────────────────────┐
│                    FastAPI 后端                                 │
│                                                               │
│  Depends(get_current_user) → 每个请求自动验证 JWT              │
│  Depends(require_role(...)) → 接口级角色控制                   │
│                                                               │
│  Router（路由）→ Service（业务）→ SQLAlchemy（ORM）             │
│                                                               │
│  自动文档：http://localhost:8000/docs（Swagger UI）            │
└────────────────────────────┬──────────────────────────────────┘
                             │
                      ┌──────▼──────┐
                      │  MySQL 8.0  │
                      └─────────────┘
```

---

## 2. 模块职责划分

| 模块 | 文件 | 职责 |
|------|------|------|
| 认证 | routers/auth.py + core/security.py | 登录、JWT 生成/验证 |
| 权限 | core/deps.py | get_current_user、require_role |
| 菜品 | routers/dish.py + services/dish_service.py | CRUD + 上下架 |
| 食材 | routers/ingredient.py + services/ingredient_service.py | 档案 + 初始化库存 |
| 配方 | routers/recipe.py | 覆盖写菜品配方 |
| 库存 | routers/inventory.py + services/inventory_service.py | 入库、查询、预警 |
| 订单 | routers/order.py + services/order_service.py ★ | 下单、完成（扣库存）、退单 |
| 统计 | routers/stats.py + services/stats_service.py | 4 条聚合 SQL，返回看板 |

---

## 3. 数据流设计

### 3.1 请求处理流程（每个接口通用）

```
浏览器 HTTP 请求
  → FastAPI Router 接收
      → Pydantic Schema 自动校验请求体
          → Depends(get_current_user) 验证 JWT，注入 user 对象
              → Depends(require_role(...)) 检查角色
                  → 调用 Service 函数（传入 db Session）
                      → Service 调用 SQLAlchemy/text SQL
                          → 返回 Pydantic Schema 响应
```

### 3.2 完成订单 → 扣库存（核心流程）

```
PUT /api/order/complete/{id}
  → require_role("admin","manager") 通过
  → order_service.complete_order(db, order_id)
       ├─ 查 t_order WHERE id=? AND status=1
       ├─ 查 t_order_item WHERE order_id=?
       ├─ for each item:
       │     查 t_dish_ingredient WHERE dish_id=?
       │     for each recipe:
       │         deduct = recipe.quantity × item.quantity
       │         执行原生 SQL：
       │           UPDATE t_inventory
       │           SET stock = stock - :deduct
       │           WHERE ingredient_id=:iid AND stock >= :deduct
       │         if rowcount == 0:
       │             db.rollback()
       │             raise HTTPException(400, "食材XXX库存不足")
       └─ order.status = 2
          db.commit()
```

### 3.3 SQLAlchemy Session 与事务

```python
# database.py 配置
SessionLocal = sessionmaker(
    bind=engine,
    autocommit=False,  # 不自动提交
    autoflush=False    # 不自动 flush
)

# 在 Service 中：
# - db.flush()     → 发送 SQL 到数据库但不提交（用于获取自增 ID）
# - db.commit()    → 提交事务
# - db.rollback()  → 回滚事务（库存不足时调用）
# - db.refresh(obj)→ 重新从数据库加载对象（获取最新数据）
```

---

## 4. SQLAlchemy 模型定义

```python
# models/dish.py
from sqlalchemy import Column, BigInteger, String, Numeric, SmallInteger, DateTime, text
from database import Base

class Category(Base):
    __tablename__ = "t_category"
    id         = Column(BigInteger, primary_key=True, autoincrement=True)
    name       = Column(String(50),  nullable=False)
    sort       = Column(BigInteger,  default=0)
    is_deleted = Column(SmallInteger, default=0)

class Dish(Base):
    __tablename__ = "t_dish"
    id          = Column(BigInteger, primary_key=True, autoincrement=True)
    name        = Column(String(100), nullable=False)
    category_id = Column(BigInteger,  nullable=False)
    price       = Column(Numeric(10,2), nullable=False)
    description = Column(String(500))
    status      = Column(SmallInteger, default=1)   # 1上架 0下架
    is_deleted  = Column(SmallInteger, default=0)
    create_time = Column(DateTime, server_default=text("CURRENT_TIMESTAMP"))
    update_time = Column(DateTime, server_default=text("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"))


# models/ingredient.py
from sqlalchemy import Column, BigInteger, String, Numeric, SmallInteger, DateTime, text
from database import Base

class Ingredient(Base):
    __tablename__ = "t_ingredient"
    id           = Column(BigInteger, primary_key=True, autoincrement=True)
    name         = Column(String(100), nullable=False)
    unit         = Column(String(20),  nullable=False)
    safety_stock = Column(Numeric(10,2), default=0)
    is_deleted   = Column(SmallInteger, default=0)

class Inventory(Base):
    __tablename__ = "t_inventory"
    id            = Column(BigInteger, primary_key=True, autoincrement=True)
    ingredient_id = Column(BigInteger, nullable=False, unique=True)
    stock         = Column(Numeric(10,2), default=0)

class StockIn(Base):
    __tablename__ = "t_stock_in"
    id            = Column(BigInteger, primary_key=True, autoincrement=True)
    ingredient_id = Column(BigInteger, nullable=False)
    quantity      = Column(Numeric(10,2), nullable=False)
    unit_price    = Column(Numeric(10,2))
    remark        = Column(String(200))
    operator_id   = Column(BigInteger)
    in_time       = Column(DateTime, server_default=text("CURRENT_TIMESTAMP"))


# models/recipe.py
from sqlalchemy import Column, BigInteger, Numeric, UniqueConstraint
from database import Base

class DishIngredient(Base):
    __tablename__ = "t_dish_ingredient"
    __table_args__ = (UniqueConstraint("dish_id", "ingredient_id"),)
    id            = Column(BigInteger, primary_key=True, autoincrement=True)
    dish_id       = Column(BigInteger, nullable=False)
    ingredient_id = Column(BigInteger, nullable=False)
    quantity      = Column(Numeric(10,3), nullable=False)


# models/order.py
from sqlalchemy import Column, BigInteger, String, Numeric, SmallInteger, DateTime, text
from database import Base

class Order(Base):
    __tablename__ = "t_order"
    id           = Column(BigInteger, primary_key=True, autoincrement=True)
    order_no     = Column(String(50), nullable=False, unique=True)
    total_amount = Column(Numeric(10,2), nullable=False)
    status       = Column(SmallInteger, nullable=False, default=1)
    order_time   = Column(DateTime, server_default=text("CURRENT_TIMESTAMP"))

class OrderItem(Base):
    __tablename__ = "t_order_item"
    id        = Column(BigInteger, primary_key=True, autoincrement=True)
    order_id  = Column(BigInteger, nullable=False)
    dish_id   = Column(BigInteger, nullable=False)
    dish_name = Column(String(100), nullable=False)
    price     = Column(Numeric(10,2), nullable=False)
    quantity  = Column(BigInteger, nullable=False)
```

---

## 5. Pydantic Schema 示例

```python
# schemas/order.py
from pydantic import BaseModel
from decimal import Decimal
from datetime import datetime

class OrderItemIn(BaseModel):
    dish_id: int
    quantity: int

class MockOrderRequest(BaseModel):
    items: list[OrderItemIn]

class OrderItemOut(BaseModel):
    dish_name: str
    price: Decimal
    quantity: int

    class Config:
        from_attributes = True   # 允许从 ORM 对象构造

class OrderOut(BaseModel):
    id: int
    order_no: str
    total_amount: Decimal
    status: int
    order_time: datetime
    items: list[OrderItemOut] = []

    class Config:
        from_attributes = True
```

---

## 6. 前端设计（与方案 A 完全相同）

前端 Vue3 + Element Plus + ECharts 工程与方案 A 共用，区别只在于：

| 差异点 | 方案 A | 方案 B |
|--------|--------|--------|
| 后端 API 端口 | 8080 | **8000** |
| 错误响应格式 | `{ code, msg, data }` | `{ detail: "..." }` + HTTP 状态码 |
| 前端错误处理 | 统一判断 `res.code != 200` | 判断 HTTP status !== 2xx |
| API 文档 | Knife4j http://localhost:8080/doc.html | Swagger http://localhost:8000/docs |

**前端 Axios 错误处理适配（方案 B）**：

```javascript
// utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8000',
  timeout: 10000
})

// 请求拦截：加 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截：统一错误提示
request.interceptors.response.use(
  res => res.data,
  err => {
    const msg = err.response?.data?.detail || '请求失败'
    ElMessage.error(msg)
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default request
```

---

## 7. 权限矩阵（与方案 A 相同逻辑）

| 功能 | admin | manager | stock_keeper |
|------|:-----:|:-------:|:------------:|
| 用户管理 | ✅ | ❌ | ❌ |
| 数据看板 | ✅ | ✅ | ❌ |
| 菜品管理 | ✅ | ✅ | ❌ |
| 菜品配方 | ✅ | ✅ | ❌ |
| 食材档案 | ✅ | ❌ | ✅ |
| 入库登记 | ✅ | ❌ | ✅ |
| 库存查看 | ✅ | ✅ | ✅ |
| 订单管理 | ✅ | ✅ | ❌ |

后端实现：`Depends(require_role("admin","manager"))` 装饰各路由函数。
前端实现：Vuex/Pinia 存储 role，router.beforeEach 过滤可见菜单。

---

## 8. 错误处理设计

| 场景 | HTTP 状态 | detail |
|------|---------|--------|
| 未登录 / token 过期 | 401 | "Token 无效或已过期" |
| 无权限 | 403 | "权限不足" |
| 库存不足 | 400 | "食材【西红柿】库存不足" |
| 参数错误 | 422 | Pydantic 自动生成详细错误信息 |
| 系统异常 | 500 | FastAPI 默认处理 |

---

## 9. 演示数据脚本（seed_data.py）

```python
# seed/seed_data.py
# 运行：python seed_data.py
# 生成近 7 天、每天 25-40 单的随机订单，让看板图表好看

import random
from datetime import datetime, timedelta
from database import SessionLocal
from models.order import Order, OrderItem

DISHES = [
    (1, "红烧肉", 15.00),
    (2, "西红柿鸡蛋", 8.00),
    (3, "麻婆豆腐", 10.00),
    (4, "鱼香肉丝", 12.00),
    (7, "米饭", 1.50),
]

def seed():
    db = SessionLocal()
    today = datetime.now()
    counter = 1

    for day_offset in range(6, -1, -1):  # 从 6 天前到今天
        day = today - timedelta(days=day_offset)
        n_orders = random.randint(25, 40)

        for _ in range(n_orders):
            # 随机时段（7:00-19:00）
            hour   = random.choice([7,8,11,12,13,17,18,19])
            minute = random.randint(0, 59)
            order_time = day.replace(hour=hour, minute=minute, second=0)

            items = random.sample(DISHES, k=random.randint(1, 3))
            total = sum(price * random.randint(1, 3) for _, _, price in items)

            order = Order(
                order_no=f"SEED{counter:06d}",
                total_amount=round(total, 2),
                status=2,    # 全部标为已完成
                order_time=order_time
            )
            db.add(order)
            db.flush()

            for dish_id, dish_name, price in items:
                qty = random.randint(1, 3)
                db.add(OrderItem(
                    order_id=order.id,
                    dish_id=dish_id,
                    dish_name=dish_name,
                    price=price,
                    quantity=qty
                ))
            counter += 1

    db.commit()
    db.close()
    print(f"演示数据生成完成，共 {counter-1} 笔订单")

if __name__ == "__main__":
    seed()
```
