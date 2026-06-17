# 文瀛餐厅管理系统 — 核心算法说明

## 1. 文档目的

本文说明文瀛餐厅管理系统中最能体现业务价值的核心算法与核心业务规则，重点覆盖配方联动、库存扣减、库存预警和看板统计。

系统核心不是复杂数学模型，而是围绕高校食堂运营建立一条可验证的数据闭环：

`菜品 -> 配方 -> 食材 -> 库存 -> 订单 -> 统计看板`

其中“完成订单后按菜品配方自动扣减食材库存”是最关键的业务算法，也是答辩时最应重点说明的部分。

## 2. 核心数据模型

核心算法依赖以下业务表：

| 表名 | 作用 | 核心字段 |
|------|------|----------|
| `t_dish` | 菜品信息 | `id`、`name`、`price`、`status` |
| `t_ingredient` | 食材档案 | `id`、`name`、`unit`、`safety_stock` |
| `t_dish_ingredient` | 菜品配方 | `dish_id`、`ingredient_id`、`quantity` |
| `t_inventory` | 食材库存 | `ingredient_id`、`stock` |
| `t_stock_in` | 入库记录 | `ingredient_id`、`quantity`、`unit_price`、`in_time` |
| `t_order` | 订单主表 | `id`、`order_no`、`total_amount`、`status`、`order_time` |
| `t_order_item` | 订单明细 | `order_id`、`dish_id`、`price`、`quantity` |

关键关系：

- 一个菜品可以消耗多种食材。
- 一种食材可以被多个菜品复用。
- `t_dish_ingredient.quantity` 表示制作 1 份菜品需要消耗的食材数量。
- 订单完成时，系统按订单明细和菜品配方计算理论食材消耗量。

## 3. 算法一：完成订单按配方扣库存

### 3.1 算法目标

当一笔订单从“已下单”变为“已完成”时，系统自动根据订单明细和菜品配方扣减食材库存。

该算法要保证：

1. 只有未完成订单可以执行完成操作。
2. 每种食材扣减量计算准确。
3. 任意食材库存不足时，整个订单完成操作失败。
4. 失败时订单状态和库存都不能发生部分变更。
5. 并发场景下不能把库存扣成负数。

### 3.2 输入与输出

输入：

- `orderId`：订单 ID。

依赖数据：

- `t_order`：订单状态。
- `t_order_item`：订单包含哪些菜品、每道菜几份。
- `t_dish_ingredient`：每道菜的食材配方。
- `t_inventory`：食材当前库存。

输出：

- 成功：订单状态更新为 `2`，表示已完成；对应食材库存被扣减。
- 失败：抛出业务异常，订单状态不变，库存不变。

### 3.3 状态规则

`t_order.status` 约定：

| 状态值 | 含义 | 是否可完成 |
|--------|------|------------|
| `1` | 已下单 | 是 |
| `2` | 已完成 | 否 |
| `3` | 已退单 | 否 |

### 3.4 扣减公式

对订单中每一条明细：

```text
食材扣减量 = 菜品单份食材用量 × 点单份数
```

例如：

- 西红柿鸡蛋每份消耗西红柿 `0.200 kg`、鸡蛋 `2 个`。
- 某订单点了西红柿鸡蛋 `3` 份。
- 则西红柿扣减 `0.200 × 3 = 0.600 kg`，鸡蛋扣减 `2 × 3 = 6 个`。

### 3.5 处理步骤

1. 根据 `orderId` 查询订单。
2. 校验订单存在且状态为 `1`。
3. 查询该订单的全部 `t_order_item` 明细。
4. 遍历订单明细。
5. 根据每条明细的 `dish_id` 查询菜品配方。
6. 根据公式计算每种食材扣减量。
7. 执行带条件的库存扣减 SQL。
8. 若任意扣减 SQL 影响行数为 `0`，说明库存不足，抛出业务异常。
9. 所有食材扣减成功后，将订单状态更新为 `2`。
10. 提交事务。

### 3.6 伪代码

```java
@Transactional(rollbackFor = Exception.class)
public void completeOrder(Long orderId) {
    Order order = orderMapper.selectById(orderId);
    if (order == null || order.getStatus() != 1) {
        throw new ServiceException("订单不存在或状态不允许完成");
    }

    List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);

    for (OrderItem item : items) {
        List<DishIngredient> recipes = recipeMapper.selectByDishId(item.getDishId());

        for (DishIngredient recipe : recipes) {
            BigDecimal deduct = recipe.getQuantity()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            int rows = inventoryMapper.deductStock(recipe.getIngredientId(), deduct);
            if (rows == 0) {
                throw new ServiceException("食材库存不足，无法完成订单");
            }
        }
    }

    order.setStatus(2);
    orderMapper.updateById(order);
}
```

### 3.7 关键 SQL

库存扣减必须使用条件更新：

```sql
UPDATE t_inventory
SET stock = stock - #{deduct},
    update_time = NOW()
WHERE ingredient_id = #{ingredientId}
  AND stock >= #{deduct};
```

这条 SQL 的关键点是 `stock >= #{deduct}`。

它把“检查库存是否足够”和“扣减库存”放在同一条 `UPDATE` 中完成。如果库存不足，数据库不会更新该行，返回影响行数 `0`。服务层据此抛出异常，让事务回滚。

### 3.8 异常与事务

必须使用事务控制完整流程：

- 订单状态更新和库存扣减属于同一个事务。
- 任意食材库存不足时抛 `ServiceException`。
- 抛出异常后，前面已扣减的库存也要回滚。
- 不能出现“部分食材已扣、订单未完成”或“订单完成但库存未扣”的中间状态。

### 3.9 并发安全说明

并发风险：两个用户同时完成不同订单，可能同时扣减同一种食材。

解决方式：

- 不采用“先 SELECT 库存，再 UPDATE 库存”的两步扣减方式。
- 使用 `UPDATE ... WHERE stock >= deduct` 原子条件更新。
- 以数据库行更新保证同一食材库存不会被并发扣成负数。

### 3.10 复杂度说明

设：

- `n` 为订单明细数量。
- `m` 为单个菜品平均配方食材数量。

该算法主要循环次数约为 `n × m`。

时间复杂度：`O(n × m)`。

空间复杂度：`O(1)` 到 `O(n + m)`，取决于 Mapper 查询结果是否一次性加载。

在食堂后台演示规模下，订单明细和配方数量都较小，该复杂度完全可接受。

## 4. 算法二：模拟下单与订单金额计算

### 4.1 算法目标

后台模拟真实点餐端提交订单，生成订单主表和订单明细，为库存扣减和看板统计提供数据来源。

### 4.2 输入与输出

输入：

- 菜品 ID 列表。
- 每道菜的点单份数。

输出：

- 一条 `t_order` 记录。
- 多条 `t_order_item` 记录。
- 订单状态为 `1`，表示已下单。

### 4.3 处理步骤

1. 接收前端提交的菜品与数量。
2. 校验每个菜品存在且 `status = 1`。
3. 读取菜品当前价格。
4. 计算每条明细金额：`菜品单价 × 份数`。
5. 汇总得到订单总金额。
6. 生成唯一订单号。
7. 写入 `t_order`。
8. 写入 `t_order_item`，保存菜品名和成交单价快照。

### 4.4 金额公式

```text
明细金额 = 菜品成交单价 × 点单份数
订单总金额 = 所有明细金额之和
```

订单明细中保存 `dish_name` 和 `price` 快照，是为了避免后续菜品改名或调价影响历史订单统计。

### 4.5 异常与事务

- 菜品不存在时抛出业务异常。
- 菜品已下架时不允许下单。
- 份数必须大于 `0`。
- 订单主表和订单明细必须在同一事务中写入。

### 4.6 复杂度说明

设 `n` 为本次下单包含的菜品种类数。

时间复杂度：`O(n)`。

空间复杂度：`O(n)`，主要用于保存订单明细。

## 5. 算法三：入库累加库存

### 5.1 算法目标

库管员录入食材入库后，系统保存入库记录，并同步增加对应食材库存。

### 5.2 输入与输出

输入：

- `ingredientId`：食材 ID。
- `quantity`：入库数量。
- `unitPrice`：采购单价，可为空。
- `remark`：备注，可为空。

输出：

- 一条 `t_stock_in` 入库记录。
- `t_inventory.stock` 增加对应数量。

### 5.3 处理步骤

1. 校验食材存在。
2. 校验入库数量大于 `0`。
3. 写入 `t_stock_in`。
4. 执行库存累加 SQL。
5. 提交事务。

### 5.4 关键 SQL

```sql
UPDATE t_inventory
SET stock = stock + #{quantity},
    update_time = NOW()
WHERE ingredient_id = #{ingredientId};
```

### 5.5 异常与事务

- 入库记录写入和库存累加必须处于同一事务。
- 如果库存记录不存在，应提示数据异常，或在新增食材时保证提前初始化库存记录。
- 入库数量不能为 `0` 或负数。

### 5.6 复杂度说明

单次入库只影响一种食材。

时间复杂度：`O(1)`。

空间复杂度：`O(1)`。

## 6. 算法四：新增食材初始化库存

### 6.1 算法目标

新增食材档案时，系统自动创建对应库存记录，避免后续入库或扣库存时找不到库存行。

### 6.2 输入与输出

输入：

- 食材名称。
- 单位。
- 安全库存值。

输出：

- 一条 `t_ingredient` 记录。
- 一条 `t_inventory` 记录，初始 `stock = 0`。

### 6.3 处理步骤

1. 校验食材名称、单位、安全库存值。
2. 插入 `t_ingredient`。
3. 获取新食材 ID。
4. 插入 `t_inventory`，库存初始值为 `0`。
5. 提交事务。

### 6.4 伪代码

```java
@Transactional(rollbackFor = Exception.class)
public void addIngredient(Ingredient ingredient) {
    ingredientMapper.insert(ingredient);

    Inventory inventory = new Inventory();
    inventory.setIngredientId(ingredient.getId());
    inventory.setStock(BigDecimal.ZERO);
    inventoryMapper.insert(inventory);
}
```

### 6.5 异常与事务

- 食材插入成功但库存初始化失败时，必须整体回滚。
- `t_inventory.ingredient_id` 应保持唯一约束，防止一个食材出现多条库存记录。

### 6.6 复杂度说明

时间复杂度：`O(1)`。

空间复杂度：`O(1)`。

## 7. 算法五：配方覆盖保存

### 7.1 算法目标

维护某道菜的食材配方。用户保存时，以前端提交的最新配方为准，覆盖该菜品原有配方。

### 7.2 输入与输出

输入：

- `dishId`：菜品 ID。
- 配方明细列表：每行包含 `ingredientId` 和 `quantity`。

输出：

- `t_dish_ingredient` 中该菜品的配方被更新为最新列表。

### 7.3 处理步骤

1. 校验菜品存在。
2. 校验配方列表不为空。
3. 校验每条配方的食材存在。
4. 校验每条配方的单份用量大于 `0`。
5. 校验同一道菜中不能重复选择同一种食材。
6. 删除该菜品原有配方。
7. 批量插入新配方。
8. 提交事务。

### 7.4 关键规则

- `t_dish_ingredient` 有唯一键 `uk_dish_ingredient (dish_id, ingredient_id)`。
- 覆盖保存比逐条增删改更适合当前项目，因为配方维护页是“整体编辑后保存”。
- 删除旧配方和插入新配方必须在同一事务中执行。

### 7.5 伪代码

```java
@Transactional(rollbackFor = Exception.class)
public void saveRecipe(Long dishId, List<RecipeItem> items) {
    checkDishExists(dishId);
    checkRecipeItems(items);

    recipeMapper.deleteByDishId(dishId);

    for (RecipeItem item : items) {
        recipeMapper.insert(dishId, item.getIngredientId(), item.getQuantity());
    }
}
```

### 7.6 异常与事务

- 如果新配方插入失败，应恢复旧配方，即整个事务回滚。
- 如果出现重复食材，前端和后端都应校验，但以后端校验为准。
- 如果菜品已删除或食材已删除，不允许保存配方。

### 7.7 复杂度说明

设 `m` 为某道菜配方中的食材数量。

时间复杂度：`O(m)`。

空间复杂度：`O(m)`。

## 8. 算法六：库存预警

### 8.1 算法目标

找出当前库存低于或等于安全库存的食材，用于库存页面高亮和数据看板预警列表。

### 8.2 输入与输出

输入：

- 无必填输入，可选按食材名称筛选。

输出：

- 低库存食材列表。
- 每条数据包含食材名称、单位、当前库存、安全库存。

### 8.3 判定公式

```text
当前库存 <= 安全库存 => 触发库存预警
```

### 8.4 关键 SQL

```sql
SELECT i.id,
       i.name,
       i.unit,
       i.safety_stock,
       inv.stock
FROM t_ingredient i
JOIN t_inventory inv ON inv.ingredient_id = i.id
WHERE i.del_flag = '0'
  AND inv.stock <= i.safety_stock
ORDER BY inv.stock ASC, i.id ASC;
```

### 8.5 异常与边界

- 安全库存为 `0` 时，只有库存也为 `0` 或负数才预警。
- 正常业务不应出现负库存；若出现负库存，应优先排查绕过扣减算法的直接改库行为。
- 被逻辑删除的食材不参与预警。

### 8.6 复杂度说明

设 `k` 为食材数量。

时间复杂度：`O(k)`，实际由数据库索引和扫描范围决定。

空间复杂度：`O(w)`，`w` 为预警结果数量。

## 9. 算法七：数据看板统计

### 9.1 算法目标

为数据看板一次性返回核心经营指标，展示系统的经营分析能力。

看板建议一次返回以下数据：

- 今日营收。
- 今日订单数。
- 近 7 天营收趋势。
- 菜品销量 Top10。
- 库存预警列表。

### 9.2 统计口径

为保证数据一致，所有营收和销量统计只计算已完成订单：

```text
t_order.status = 2
```

已下单但未完成的订单不计入营收；已退单订单不计入营收和销量。

### 9.3 今日营收

```sql
SELECT IFNULL(SUM(total_amount), 0) AS todayRevenue
FROM t_order
WHERE DATE(order_time) = CURDATE()
  AND status = 2;
```

### 9.4 今日订单数

```sql
SELECT COUNT(*) AS todayOrderCount
FROM t_order
WHERE DATE(order_time) = CURDATE()
  AND status = 2;
```

### 9.5 近 7 天营收趋势

```sql
SELECT DATE_FORMAT(order_time, '%m-%d') AS statDate,
       IFNULL(SUM(total_amount), 0) AS amount
FROM t_order
WHERE order_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
  AND status = 2
GROUP BY DATE(order_time), DATE_FORMAT(order_time, '%m-%d')
ORDER BY DATE(order_time);
```

说明：如果某天没有订单，SQL 不会自动返回 `0`。服务层或前端应补齐近 7 天日期，缺失日期金额填 `0`，这样折线图横轴稳定。

### 9.6 菜品销量 Top10

```sql
SELECT oi.dish_id,
       oi.dish_name,
       SUM(oi.quantity) AS saleCount
FROM t_order_item oi
JOIN t_order o ON o.id = oi.order_id
WHERE o.status = 2
GROUP BY oi.dish_id, oi.dish_name
ORDER BY saleCount DESC
LIMIT 10;
```

说明：使用 `t_order_item.dish_name` 快照展示菜品名，避免菜品改名后历史销量名称变化。

### 9.7 库存预警列表

复用第 8 节库存预警算法，返回当前低库存食材。

### 9.8 输出结构建议

```json
{
  "todayRevenue": 1280.50,
  "todayOrderCount": 86,
  "revenueTrend": [
    { "date": "06-11", "amount": 980.00 },
    { "date": "06-12", "amount": 1120.00 }
  ],
  "dishTop10": [
    { "dishName": "西红柿鸡蛋", "saleCount": 32 }
  ],
  "inventoryWarnings": [
    { "ingredientName": "鸡蛋", "stock": 20, "safetyStock": 50, "unit": "个" }
  ]
}
```

### 9.9 异常与性能

- 看板接口只读，不修改业务数据。
- 看板接口响应目标小于 `2` 秒。
- 演示数据规模较小时，直接聚合 SQL 足够。
- 若后续数据量增大，可增加按日统计中间表或定时任务缓存统计结果。

### 9.10 复杂度说明

看板统计主要由数据库聚合完成。

- 今日营收和今日订单数：与当天订单数量相关。
- 近 7 天趋势：与近 7 天已完成订单数量相关。
- Top10：与已完成订单明细数量相关。
- 库存预警：与食材数量相关。

当前项目面向课程/答辩演示，数据规模较小，直接实时聚合即可满足性能要求。

## 10. 退单规则

### 10.1 规则目标

退单用于将订单标记为已退单，当前版本不做复杂退款和库存返还。

### 10.2 处理规则

- 只有状态为 `1` 的已下单订单可以退单。
- 状态为 `2` 的已完成订单不建议退单，因为库存已经扣减。
- 状态为 `3` 的已退单订单不能重复退单。
- 退单只更新 `t_order.status = 3`，不扣库存。

### 10.3 说明

当前项目目标是支撑后台业务闭环和答辩演示，不引入真实支付、退款、库存返还等复杂场景。若后续扩展真实交易系统，可以新增“售后退单”和“库存返还”流程。

## 11. 答辩讲解建议

答辩时建议用以下顺序讲核心算法：

1. 系统不是简单记录菜品和订单，而是建立了“菜品 -> 配方 -> 食材 -> 库存”的关联模型。
2. 每道菜维护一份配方，记录制作 1 份菜需要消耗哪些食材和多少用量。
3. 后台模拟下单后，订单先处于已下单状态，此时不扣库存。
4. 食堂确认完成订单时，系统按“配方单份用量 × 点单份数”计算食材消耗。
5. 扣库存过程使用事务控制，任意食材库存不足都会整体回滚。
6. 库存扣减 SQL 使用 `stock >= deduct` 条件，避免并发场景下库存被扣成负数。
7. 看板基于已完成订单统计营收、订单数、销量 Top10，并展示库存预警，辅助食堂优化供给。

可概括为：

> 本系统的核心算法是基于菜品配方的食材库存自动扣减算法。它把订单数据和食材库存联动起来，使系统能够从“卖出一道菜”推导出“消耗了哪些食材、消耗了多少”，再通过库存预警和经营看板辅助食堂管理。

## 12. 验证清单

开发和联调时至少验证以下场景：

- 新增食材后自动生成库存记录，初始库存为 `0`。
- 入库后库存正确增加。
- 保存配方后，再次打开能看到最新配方。
- 模拟下单后生成订单主表和明细。
- 完成订单后，库存按配方正确扣减。
- 库存不足时，完成订单失败，库存和订单状态都不变化。
- 已完成订单不能重复完成。
- 已退单订单不能完成。
- 今日营收和今日订单数只统计已完成订单。
- 库存低于或等于安全库存时出现在预警列表。
