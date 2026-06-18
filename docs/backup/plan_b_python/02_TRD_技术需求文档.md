# 文瀛餐厅管理系统 — 技术需求文档 (TRD)
## 方案 B：FastAPI + Python + Vue3

**版本**：v1.0  **日期**：2026-06-14

---

## 1. 技术选型

| 层 | 技术 | 版本 | 说明 |
|----|------|------|------|
| 后端语言 | Python | 3.11 | 语法简洁，代码量少 |
| 后端框架 | **FastAPI** | 0.111.x | 自动生成 OpenAPI 文档；异步支持；比 Django/Flask 更现代 |
| ORM | **SQLAlchemy** | 2.x | 主流 Python ORM；支持原生 SQL 混用 |
| 数据校验 | **Pydantic v2** | — | FastAPI 内置集成，Schema 即校验 |
| JWT | **python-jose** | — | 生成/解析 JWT token |
| 密码加密 | **passlib[bcrypt]** | — | bcrypt 哈希密码 |
| 数据库驱动 | **PyMySQL** | — | Python 连接 MySQL |
| 数据库 | MySQL | 8.0 | 信创可迁移 openGauss/达梦 |
| 前端框架 | Vue 3 + Vite | — | 与方案 A 前端相同 |
| UI 组件 | Element Plus | — | — |
| 图表 | Apache ECharts | 5.x | — |
| 运行 | `uvicorn main:app --reload` + `npm run dev` | — | 本地演示，无需 Docker |

---

## 2. 系统架构

```
┌─────────────────────────────────────────────┐
│          浏览器 (Vue3 SPA)                   │
│  登录页 / 看板 / 菜品 / 食材 / 配方 / 订单   │
└──────────────────┬──────────────────────────┘
                   │  HTTP/REST + JWT Bearer
┌──────────────────▼──────────────────────────┐
│          FastAPI 后端（单体）                 │
│                                             │
│  routers/          ← 路由层（Controller 等效）│
│  ├── auth.py       ← 登录/JWT               │
│  ├── dish.py       ← 菜品路由               │
│  ├── ingredient.py ← 食材路由               │
│  ├── inventory.py  ← 库存路由               │
│  ├── recipe.py     ← 配方路由               │
│  ├── order.py      ← 订单路由（含核心逻辑）  │
│  └── stats.py      ← 统计路由               │
│                                             │
│  services/         ← 业务逻辑               │
│  models/           ← SQLAlchemy ORM 模型    │
│  schemas/          ← Pydantic 请求/响应模型 │
│  core/             ← JWT/安全/依赖注入       │
│  database.py       ← 数据库连接配置          │
└─────────────────────────────────────────────┘
                   │
              ┌────▼────┐
              │ MySQL 8 │
              └─────────┘
```

---

## 3. 工程目录结构

```
backend/
├── main.py                 # FastAPI 入口，注册路由，配置 CORS
├── database.py             # SQLAlchemy engine + SessionLocal
├── requirements.txt
│
├── core/
│   ├── config.py           # SECRET_KEY / TOKEN_EXPIRE / DB_URL
│   ├── security.py         # create_access_token / verify_token / hash_password
│   └── deps.py             # get_db() / get_current_user() 依赖注入
│
├── models/                 # SQLAlchemy ORM 模型（对应数据库表）
│   ├── user.py             # User, Role
│   ├── dish.py             # Category, Dish
│   ├── ingredient.py       # Ingredient, Inventory, StockIn
│   ├── recipe.py           # DishIngredient
│   └── order.py            # Order, OrderItem
│
├── schemas/                # Pydantic 请求/响应 Schema
│   ├── auth.py             # LoginRequest, TokenResponse
│   ├── dish.py             # DishCreate, DishUpdate, DishOut
│   ├── ingredient.py       # IngredientCreate, InventoryOut
│   ├── recipe.py           # RecipeSaveRequest
│   ├── order.py            # MockOrderRequest, OrderOut
│   └── stats.py            # OverviewResponse
│
├── routers/                # FastAPI 路由（等效 Controller）
│   ├── auth.py
│   ├── dish.py
│   ├── ingredient.py
│   ├── inventory.py
│   ├── recipe.py
│   ├── order.py
│   └── stats.py
│
├── services/               # 业务逻辑（等效 Service）
│   ├── dish_service.py
│   ├── ingredient_service.py
│   ├── inventory_service.py   # ★ 扣库存核心
│   ├── order_service.py       # ★ 下单+完成订单
│   └── stats_service.py
│
└── seed/
    ├── schema.sql             # 建表 SQL
    └── seed_data.py           # 演示数据生成脚本

frontend/                      # 与方案 A 相同（Vue3 + Element Plus）
```

---

## 4. 数据库设计（与方案 A 完全相同的 8 张表）

```sql
CREATE DATABASE IF NOT EXISTS restaurant_b DEFAULT CHARSET utf8mb4;
USE restaurant_b;

CREATE TABLE t_user (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(50)  NOT NULL UNIQUE,
  password  VARCHAR(100) NOT NULL COMMENT 'bcrypt哈希',
  role      VARCHAR(20)  NOT NULL DEFAULT 'manager'
            COMMENT 'admin/manager/stock_keeper',
  is_active TINYINT      DEFAULT 1
);

CREATE TABLE t_category (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  sort INT         DEFAULT 0,
  is_deleted TINYINT DEFAULT 0
);

CREATE TABLE t_dish (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL,
  category_id BIGINT        NOT NULL,
  price       DECIMAL(10,2) NOT NULL,
  description VARCHAR(500),
  status      TINYINT       DEFAULT 1,  -- 1上架 0下架
  is_deleted  TINYINT       DEFAULT 0,
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE t_ingredient (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(100)  NOT NULL,
  unit         VARCHAR(20)   NOT NULL,
  safety_stock DECIMAL(10,2) DEFAULT 0,
  is_deleted   TINYINT       DEFAULT 0,
  create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_dish_ingredient (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  dish_id       BIGINT        NOT NULL,
  ingredient_id BIGINT        NOT NULL,
  quantity      DECIMAL(10,3) NOT NULL,
  UNIQUE KEY uk_di (dish_id, ingredient_id)
);

CREATE TABLE t_inventory (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT        NOT NULL UNIQUE,
  stock         DECIMAL(10,2) NOT NULL DEFAULT 0,
  update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE t_stock_in (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT        NOT NULL,
  quantity      DECIMAL(10,2) NOT NULL,
  unit_price    DECIMAL(10,2),
  remark        VARCHAR(200),
  operator_id   BIGINT,
  in_time       DATETIME      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_order (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no     VARCHAR(50)   NOT NULL UNIQUE,
  total_amount DECIMAL(10,2) NOT NULL,
  status       TINYINT       NOT NULL DEFAULT 1,
  order_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_time   (order_time),
  INDEX idx_status (status)
);

CREATE TABLE t_order_item (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id  BIGINT        NOT NULL,
  dish_id   BIGINT        NOT NULL,
  dish_name VARCHAR(100)  NOT NULL,
  price     DECIMAL(10,2) NOT NULL,
  quantity  INT           NOT NULL,
  INDEX idx_order (order_id)
);
```

---

## 5. 核心代码实现

### 5.1 requirements.txt

```
fastapi==0.111.0
uvicorn[standard]==0.29.0
sqlalchemy==2.0.30
pymysql==1.1.1
pydantic==2.7.1
python-jose[cryptography]==3.3.0
passlib[bcrypt]==1.7.4
python-multipart==0.0.9
```

### 5.2 database.py（数据库连接）

```python
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase

DB_URL = "mysql+pymysql://root:yourpassword@localhost:3306/restaurant_b?charset=utf8mb4"

engine = create_engine(DB_URL, echo=False)
SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=False)

class Base(DeclarativeBase):
    pass
```

### 5.3 core/security.py（JWT + 密码）

```python
from datetime import datetime, timedelta
from jose import jwt, JWTError
from passlib.context import CryptContext

SECRET_KEY = "your-secret-key-change-in-production"
ALGORITHM  = "HS256"
EXPIRE_MIN = 30

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def hash_password(password: str) -> str:
    return pwd_context.hash(password)

def verify_password(plain: str, hashed: str) -> bool:
    return pwd_context.verify(plain, hashed)

def create_token(data: dict) -> str:
    payload = data.copy()
    payload["exp"] = datetime.utcnow() + timedelta(minutes=EXPIRE_MIN)
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

def decode_token(token: str) -> dict:
    try:
        return jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    except JWTError:
        return None
```

### 5.4 core/deps.py（依赖注入）

```python
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session
from database import SessionLocal
from core.security import decode_token
from models.user import User

bearer = HTTPBearer()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def get_current_user(
    cred: HTTPAuthorizationCredentials = Depends(bearer),
    db: Session = Depends(get_db)
) -> User:
    payload = decode_token(cred.credentials)
    if not payload:
        raise HTTPException(status_code=401, detail="Token 无效或已过期")
    user = db.query(User).filter(User.username == payload.get("sub")).first()
    if not user or not user.is_active:
        raise HTTPException(status_code=401, detail="用户不存在")
    return user

# 角色检查工厂函数
def require_role(*roles: str):
    def checker(current_user: User = Depends(get_current_user)):
        if current_user.role not in roles:
            raise HTTPException(status_code=403, detail="权限不足")
        return current_user
    return checker

# 使用示例：
# @router.post("/dish", dependencies=[Depends(require_role("admin","manager"))])
```

### 5.5 routers/auth.py（登录接口）

```python
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from core.deps import get_db
from core.security import verify_password, create_token
from models.user import User

router = APIRouter(prefix="/api/auth", tags=["认证"])

@router.post("/login")
def login(username: str, password: str, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.username == username).first()
    if not user or not verify_password(password, user.password):
        raise HTTPException(status_code=401, detail="用户名或密码错误")
    token = create_token({"sub": user.username, "role": user.role})
    return {
        "access_token": token,
        "token_type": "bearer",
        "role": user.role,
        "username": user.username
    }
```

### 5.6 services/order_service.py（★ 核心业务）

```python
from sqlalchemy.orm import Session
from sqlalchemy import text
from fastapi import HTTPException
from models.order import Order, OrderItem
from models.dish import Dish
from models.recipe import DishIngredient
from models.ingredient import Ingredient
import time

def mock_order(db: Session, items: list[dict]) -> Order:
    """模拟下单"""
    total = 0.0
    order_no = f"M{int(time.time() * 1000)}"

    order = Order(order_no=order_no, total_amount=0, status=1)
    db.add(order)
    db.flush()  # 获取 order.id（不提交事务）

    for item in items:
        dish = db.query(Dish).filter(
            Dish.id == item["dish_id"],
            Dish.status == 1,
            Dish.is_deleted == 0
        ).first()
        if not dish:
            raise HTTPException(400, f"菜品 ID={item['dish_id']} 不存在或已下架")

        oi = OrderItem(
            order_id=order.id,
            dish_id=dish.id,
            dish_name=dish.name,
            price=dish.price,
            quantity=item["quantity"]
        )
        db.add(oi)
        total += float(dish.price) * item["quantity"]

    order.total_amount = round(total, 2)
    db.commit()
    db.refresh(order)
    return order


def complete_order(db: Session, order_id: int) -> None:
    """完成订单 → 按配方扣库存（核心）"""
    order = db.query(Order).filter(
        Order.id == order_id, Order.status == 1
    ).first()
    if not order:
        raise HTTPException(404, "订单不存在或状态不允许完成")

    items = db.query(OrderItem).filter(OrderItem.order_id == order_id).all()

    for item in items:
        recipes = db.query(DishIngredient).filter(
            DishIngredient.dish_id == item.dish_id
        ).all()

        for r in recipes:
            deduct = float(r.quantity) * item.quantity

            # 原子扣减：UPDATE WHERE stock >= deduct
            # rowcount==0 说明库存不足
            result = db.execute(
                text("""
                    UPDATE t_inventory
                    SET stock = stock - :deduct,
                        update_time = NOW()
                    WHERE ingredient_id = :iid
                      AND stock >= :deduct
                """),
                {"deduct": deduct, "iid": r.ingredient_id}
            )

            if result.rowcount == 0:
                db.rollback()  # 显式回滚
                ingr = db.query(Ingredient).filter(
                    Ingredient.id == r.ingredient_id
                ).first()
                raise HTTPException(
                    400,
                    f"食材【{ingr.name if ingr else r.ingredient_id}】库存不足"
                )

    order.status = 2
    db.commit()
```

> **关键点说明**
> 1. `db.flush()` 在同一事务内获取 auto_increment id，不需要提交
> 2. 原子扣减用 `WHERE stock >= :deduct`，让数据库一条语句完成"判断+扣减"
> 3. `rowcount == 0` 时显式 `db.rollback()` 再抛异常，保证数据一致
> 4. SQLAlchemy `text()` 执行原生 SQL，完全避免 ORM 的 SELECT+UPDATE 竞态

### 5.7 统计 SQL（stats_service.py）

```python
from sqlalchemy import text
from sqlalchemy.orm import Session
from datetime import date, timedelta

def get_overview(db: Session) -> dict:
    today = date.today()
    seven_days_ago = today - timedelta(days=6)

    # 今日营收
    revenue = db.execute(text("""
        SELECT IFNULL(SUM(total_amount), 0)
        FROM t_order
        WHERE DATE(order_time) = :today AND status = 2
    """), {"today": today}).scalar()

    # 今日订单数
    order_count = db.execute(text("""
        SELECT COUNT(*) FROM t_order
        WHERE DATE(order_time) = :today AND status = 2
    """), {"today": today}).scalar()

    # 近 7 天营收
    revenue_7 = db.execute(text("""
        SELECT DATE_FORMAT(order_time, '%m-%d') AS date,
               IFNULL(SUM(total_amount), 0) AS amount
        FROM t_order
        WHERE order_time >= :start AND status = 2
        GROUP BY DATE(order_time)
        ORDER BY DATE(order_time)
    """), {"start": seven_days_ago}).mappings().all()

    # 销量 Top10
    top_dishes = db.execute(text("""
        SELECT oi.dish_name AS name, SUM(oi.quantity) AS qty
        FROM t_order_item oi
        JOIN t_order o ON oi.order_id = o.id
        WHERE o.status = 2
        GROUP BY oi.dish_name
        ORDER BY qty DESC
        LIMIT 10
    """)).mappings().all()

    return {
        "todayRevenue":  float(revenue),
        "todayOrders":   int(order_count),
        "revenue7Days":  [dict(r) for r in revenue_7],
        "topDishes":     [dict(r) for r in top_dishes],
    }
```

### 5.8 main.py（入口配置）

```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import auth, dish, ingredient, inventory, recipe, order, stats

app = FastAPI(title="文瀛餐厅管理系统", version="1.0.0")

# CORS（允许前端 Vue dev server 跨域）
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],  # Vite 默认端口
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册路由
for router in [auth, dish, ingredient, inventory, recipe, order, stats]:
    app.include_router(router.router)
```

---

## 6. 接口规范

与方案 A 接口路径完全相同，FastAPI 自动生成 Swagger 文档，访问 `http://localhost:8000/docs` 即可查看和测试所有接口。

### 统一响应格式

FastAPI 默认返回 JSON，无需额外封装。错误时用 `HTTPException` 抛出：
```python
raise HTTPException(status_code=400, detail="食材库存不足")
```
前端收到：`{ "detail": "食材库存不足" }` + HTTP 400

---

## 7. RBAC 权限实现（完整代码，约 100 行）

见上方 `core/deps.py` 中的 `require_role` 工厂函数，使用方式：

```python
# routers/dish.py
from core.deps import require_role, get_db

@router.post("/api/dish")
def create_dish(
    dish: DishCreate,
    db: Session = Depends(get_db),
    _: User = Depends(require_role("admin", "manager"))
):
    ...

# routers/inventory.py
@router.post("/api/stockIn")
def stock_in(
    record: StockInCreate,
    db: Session = Depends(get_db),
    _: User = Depends(require_role("admin", "stock_keeper"))
):
    ...
```

前端菜单可见性：登录时后端返回 `role` 字段，前端 Vue Router 的 `beforeEach` 守卫按角色过滤菜单。

---

## 8. 安全要求

| 项 | 实现方式 |
|----|---------|
| 认证 | python-jose JWT，Bearer token，30 分钟有效期 |
| 密码 | passlib bcrypt 哈希，不存明文 |
| 权限 | `require_role()` 依赖注入，接口级控制 |
| SQL 注入 | SQLAlchemy 参数化查询；text() 中用 `:param` 占位符 |
| CORS | 明确配置 `allow_origins`，不用 `*` 通配 |
| 接口文档 | 生产环境可关闭 `docs_url=None` |

---

## 9. 启动方式

```bash
# 后端
cd backend
pip install -r requirements.txt
# 修改 database.py 中的 DB_URL
uvicorn main:app --reload --port 8000
# 访问 http://localhost:8000/docs 查看接口文档

# 前端（与方案 A 相同的 Vue3 工程）
cd frontend
pnpm install
pnpm run dev
# 访问 http://localhost:5173
```
