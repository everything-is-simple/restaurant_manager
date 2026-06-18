-- 文瀛餐厅管理系统 MVP2 — 业务表建表脚本
-- 注意：若依自带 sys_* 系列表，只需建以下 8 张业务表
-- 在若依数据库（默认 ry-vue）中执行即可

USE `ry-vue`;

DROP TABLE IF EXISTS t_order_item;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_stock_in;
DROP TABLE IF EXISTS t_inventory;
DROP TABLE IF EXISTS t_dish_ingredient;
DROP TABLE IF EXISTS t_ingredient;
DROP TABLE IF EXISTS t_dish;
DROP TABLE IF EXISTS t_category;

-- 1. 菜品分类
CREATE TABLE t_category (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(50)  NOT NULL COMMENT '分类名称',
  sort      INT          NOT NULL DEFAULT 0,
  del_flag  CHAR(1)      NOT NULL DEFAULT '0' COMMENT '0正常 2删除'
) COMMENT='菜品分类';

-- 2. 菜品
CREATE TABLE t_dish (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(100)  NOT NULL COMMENT '菜品名称',
  category_id  BIGINT        NOT NULL COMMENT '分类ID',
  price        DECIMAL(10,2) NOT NULL COMMENT '售价',
  description  VARCHAR(500)  COMMENT '描述',
  status       TINYINT       NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag     CHAR(1)       NOT NULL DEFAULT '0'
) COMMENT='菜品';

-- 3. 食材
CREATE TABLE t_ingredient (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100)  NOT NULL COMMENT '食材名称',
  unit          VARCHAR(20)   NOT NULL COMMENT '单位(kg/g/个/瓶)',
  safety_stock  DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '安全库存量，低于此值预警',
  create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  del_flag      CHAR(1)       NOT NULL DEFAULT '0'
) COMMENT='食材档案';

-- 4. 菜品配方（核心关联表）
CREATE TABLE t_dish_ingredient (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  dish_id        BIGINT        NOT NULL COMMENT '菜品ID',
  ingredient_id  BIGINT        NOT NULL COMMENT '食材ID',
  quantity       DECIMAL(10,3) NOT NULL COMMENT '单份消耗量',
  UNIQUE KEY uk_dish_ingredient (dish_id, ingredient_id)
) COMMENT='菜品配方';

-- 5. 库存（每种食材当前库存）
CREATE TABLE t_inventory (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id  BIGINT        NOT NULL UNIQUE COMMENT '食材ID',
  stock          DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '当前库存量',
  update_time    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='食材库存';

-- 6. 入库记录
CREATE TABLE t_stock_in (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id  BIGINT        NOT NULL COMMENT '食材ID',
  quantity       DECIMAL(10,2) NOT NULL COMMENT '入库数量',
  unit_price     DECIMAL(10,2)           COMMENT '采购单价',
  remark         VARCHAR(200)            COMMENT '备注（供应商等）',
  operator       BIGINT                  COMMENT '操作人 sys_user.id',
  in_time        DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间'
) COMMENT='入库记录';

-- 7. 订单
CREATE TABLE t_order (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no      VARCHAR(50)   NOT NULL UNIQUE COMMENT '订单号',
  total_amount  DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1已下单 2已完成 3已退单',
  order_time    DATETIME      DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_time (order_time),
  INDEX idx_status (status)
) COMMENT='订单';

-- 8. 订单明细
CREATE TABLE t_order_item (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id   BIGINT        NOT NULL COMMENT '订单ID',
  dish_id    BIGINT        NOT NULL COMMENT '菜品ID',
  dish_name  VARCHAR(100)  NOT NULL COMMENT '菜品名快照',
  price      DECIMAL(10,2) NOT NULL COMMENT '成交单价快照',
  quantity   INT           NOT NULL COMMENT '份数',
  INDEX idx_order (order_id),
  INDEX idx_dish  (dish_id)
) COMMENT='订单明细';

-- =========================================
-- 演示数据
-- =========================================

INSERT INTO t_category(name, sort) VALUES
('热菜', 1), ('主食', 2), ('汤品', 3), ('凉菜', 4), ('饮品', 5);

INSERT INTO t_dish(name, category_id, price, status) VALUES
('红烧肉',      1, 15.00, 1),
('西红柿鸡蛋',  1,  8.00, 1),
('麻婆豆腐',    1, 10.00, 1),
('鱼香肉丝',    1, 12.00, 1),
('宫保鸡丁',    1, 13.00, 1),
('清炒时蔬',    1,  6.00, 1),
('米饭',        2,  1.50, 1),
('馒头',        2,  1.00, 1),
('紫菜蛋花汤',  3,  3.00, 1),
('冬瓜排骨汤',  3,  6.00, 1),
('凉拌黄瓜',    4,  5.00, 1),
('豆浆',        5,  2.00, 1);

INSERT INTO t_ingredient(name, unit, safety_stock) VALUES
('猪五花肉', 'kg', 5),
('西红柿',   'kg', 3),
('鸡蛋',     '个', 50),
('豆腐',     'kg', 4),
('猪肉丝',   'kg', 5),
('鸡丁',     'kg', 4),
('青菜',     'kg', 5),
('大米',     'kg', 20),
('面粉',     'kg', 10),
('紫菜',     'g',  200),
('排骨',     'kg', 4),
('黄瓜',     'kg', 3),
('黄豆',     'kg', 5);

-- 初始化库存（每种食材初始 10 个单位）
INSERT INTO t_inventory(ingredient_id, stock)
SELECT id, 10 FROM t_ingredient;

-- 配方示例（西红柿鸡蛋：消耗西红柿 200g=0.2kg，鸡蛋 2 个）
INSERT INTO t_dish_ingredient(dish_id, ingredient_id, quantity) VALUES
(2, 2, 0.200),   -- 西红柿鸡蛋 → 西红柿 0.2kg
(2, 3, 2.000),   -- 西红柿鸡蛋 → 鸡蛋 2个
(1, 1, 0.300),   -- 红烧肉 → 五花肉 0.3kg
(3, 4, 0.250),   -- 麻婆豆腐 → 豆腐 0.25kg
(7, 8, 0.100);   -- 米饭 → 大米 0.1kg
