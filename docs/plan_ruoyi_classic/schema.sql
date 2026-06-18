-- 文瀛餐厅管理系统业务表
-- 在若依数据库 ry-vue 中执行
-- 适配若依代码生成器：主键 xxx_id、审计字段 create_by/create_time/update_by/update_time/remark、逻辑删除 del_flag

USE `ry-vue`;

DROP TABLE IF EXISTS t_order_item;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_stock_in;
DROP TABLE IF EXISTS t_inventory;
DROP TABLE IF EXISTS t_dish_ingredient;
DROP TABLE IF EXISTS t_ingredient;
DROP TABLE IF EXISTS t_dish;
DROP TABLE IF EXISTS t_category;

-- ----------------------------
-- 菜品分类
-- ----------------------------
CREATE TABLE t_category (
  category_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
  name        VARCHAR(50)  NOT NULL              COMMENT '分类名称',
  sort        INT          NOT NULL DEFAULT 0    COMMENT '显示顺序',
  status      CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '状态（0正常 1停用）',
  del_flag    CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '删除标志（0正常 2删除）',
  create_by   VARCHAR(64)  DEFAULT ''            COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL          COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''            COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL          COMMENT '更新时间',
  remark      VARCHAR(500) DEFAULT NULL          COMMENT '备注'
) ENGINE=InnoDB COMMENT='菜品分类';

-- ----------------------------
-- 菜品
-- ----------------------------
CREATE TABLE t_dish (
  dish_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜品ID',
  name        VARCHAR(100) NOT NULL              COMMENT '菜品名称',
  category_id BIGINT       NOT NULL              COMMENT '分类ID',
  price       DECIMAL(10,2) NOT NULL             COMMENT '售价',
  description VARCHAR(500) DEFAULT NULL          COMMENT '描述',
  status      CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '状态（0上架 1下架）',
  del_flag    CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '删除标志（0正常 2删除）',
  create_by   VARCHAR(64)  DEFAULT ''            COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL          COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''            COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL          COMMENT '更新时间',
  remark      VARCHAR(500) DEFAULT NULL          COMMENT '备注'
) ENGINE=InnoDB COMMENT='菜品';

-- ----------------------------
-- 食材档案
-- ----------------------------
CREATE TABLE t_ingredient (
  ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '食材ID',
  name          VARCHAR(100) NOT NULL              COMMENT '食材名称',
  unit          VARCHAR(20)  NOT NULL              COMMENT '单位',
  safety_stock  DECIMAL(10,2) NOT NULL DEFAULT 0   COMMENT '安全库存量',
  status        CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '状态（0正常 1停用）',
  del_flag      CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '删除标志（0正常 2删除）',
  create_by     VARCHAR(64)  DEFAULT ''            COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL          COMMENT '创建时间',
  update_by     VARCHAR(64)  DEFAULT ''            COMMENT '更新者',
  update_time   DATETIME     DEFAULT NULL          COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL          COMMENT '备注'
) ENGINE=InnoDB COMMENT='食材档案';

-- ----------------------------
-- 菜品配方（菜品与食材多对多）
-- ----------------------------
CREATE TABLE t_dish_ingredient (
  dish_ingredient_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配方ID',
  dish_id            BIGINT        NOT NULL             COMMENT '菜品ID',
  ingredient_id      BIGINT        NOT NULL             COMMENT '食材ID',
  quantity           DECIMAL(10,3) NOT NULL             COMMENT '单份消耗量',
  UNIQUE KEY uk_dish_ingredient (dish_id, ingredient_id),
  create_by   VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL                 COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL                 COMMENT '更新时间',
  remark      VARCHAR(500) DEFAULT NULL                 COMMENT '备注'
) ENGINE=InnoDB COMMENT='菜品配方';

-- ----------------------------
-- 食材库存（与食材一对一）
-- ----------------------------
CREATE TABLE t_inventory (
  inventory_id  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '库存ID',
  ingredient_id BIGINT       NOT NULL UNIQUE       COMMENT '食材ID',
  stock         DECIMAL(10,2) NOT NULL DEFAULT 0   COMMENT '当前库存量',
  del_flag      CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '删除标志（0正常 2删除）',
  create_by     VARCHAR(64)  DEFAULT ''            COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL          COMMENT '创建时间',
  update_by     VARCHAR(64)  DEFAULT ''            COMMENT '更新者',
  update_time   DATETIME     DEFAULT NULL          COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL          COMMENT '备注'
) ENGINE=InnoDB COMMENT='食材库存';

-- ----------------------------
-- 入库记录
-- ----------------------------
CREATE TABLE t_stock_in (
  stock_in_id   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '入库ID',
  ingredient_id BIGINT        NOT NULL             COMMENT '食材ID',
  quantity      DECIMAL(10,2) NOT NULL             COMMENT '入库数量',
  unit_price    DECIMAL(10,2) DEFAULT NULL         COMMENT '采购单价',
  in_time       DATETIME      DEFAULT NULL         COMMENT '入库时间',
  del_flag      CHAR(1)       NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
  create_by     VARCHAR(64)   DEFAULT ''           COMMENT '创建者',
  create_time   DATETIME      DEFAULT NULL         COMMENT '创建时间',
  update_by     VARCHAR(64)   DEFAULT ''           COMMENT '更新者',
  update_time   DATETIME      DEFAULT NULL         COMMENT '更新时间',
  remark        VARCHAR(500)  DEFAULT NULL         COMMENT '备注'
) ENGINE=InnoDB COMMENT='入库记录';

-- ----------------------------
-- 订单主表
-- ----------------------------
CREATE TABLE t_order (
  order_id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
  order_no      VARCHAR(50)  NOT NULL UNIQUE       COMMENT '订单号',
  total_amount  DECIMAL(10,2) NOT NULL             COMMENT '订单总额',
  status        TINYINT      NOT NULL DEFAULT 1    COMMENT '订单状态（1已下单 2已完成 3已退单）',
  order_time    DATETIME     DEFAULT NULL          COMMENT '下单时间',
  complete_time DATETIME     DEFAULT NULL          COMMENT '完成时间',
  del_flag      CHAR(1)      NOT NULL DEFAULT '0'  COMMENT '删除标志（0正常 2删除）',
  create_by     VARCHAR(64)  DEFAULT ''            COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL          COMMENT '创建时间',
  update_by     VARCHAR(64)  DEFAULT ''            COMMENT '更新者',
  update_time   DATETIME     DEFAULT NULL          COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL          COMMENT '备注',
  INDEX idx_time (order_time),
  INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='订单';

-- ----------------------------
-- 订单明细
-- ----------------------------
CREATE TABLE t_order_item (
  order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
  order_id      BIGINT        NOT NULL             COMMENT '订单ID',
  dish_id       BIGINT        NOT NULL             COMMENT '菜品ID',
  dish_name     VARCHAR(100)  NOT NULL             COMMENT '菜品名快照',
  price         DECIMAL(10,2) NOT NULL             COMMENT '成交单价快照',
  quantity      INT           NOT NULL             COMMENT '份数',
  create_by     VARCHAR(64)   DEFAULT ''           COMMENT '创建者',
  create_time   DATETIME      DEFAULT NULL         COMMENT '创建时间',
  update_by     VARCHAR(64)   DEFAULT ''           COMMENT '更新者',
  update_time   DATETIME      DEFAULT NULL         COMMENT '更新时间',
  remark        VARCHAR(500)  DEFAULT NULL         COMMENT '备注',
  INDEX idx_order (order_id),
  INDEX idx_dish (dish_id)
) ENGINE=InnoDB COMMENT='订单明细';

-- ============================
-- 演示数据
-- ============================

INSERT INTO t_category(name, sort) VALUES
('热菜', 1), ('主食', 2), ('汤品', 3), ('凉菜', 4), ('饮品', 5);

INSERT INTO t_dish(name, category_id, price, status) VALUES
('红烧肉', 1, 15.00, '0'),
('西红柿鸡蛋', 1, 8.00, '0'),
('麻婆豆腐', 1, 10.00, '0'),
('鱼香肉丝', 1, 12.00, '0'),
('宫保鸡丁', 1, 13.00, '0'),
('清炒时蔬', 1, 6.00, '0'),
('米饭', 2, 1.50, '0'),
('馒头', 2, 1.00, '0'),
('紫菜蛋花汤', 3, 3.00, '0'),
('冬瓜排骨汤', 3, 6.00, '0'),
('凉拌黄瓜', 4, 5.00, '0'),
('豆浆', 5, 2.00, '0');

INSERT INTO t_ingredient(name, unit, safety_stock) VALUES
('猪五花肉', 'kg', 5),
('西红柿', 'kg', 3),
('鸡蛋', '个', 50),
('豆腐', 'kg', 4),
('猪肉丝', 'kg', 5),
('鸡丁', 'kg', 4),
('青菜', 'kg', 5),
('大米', 'kg', 20),
('面粉', 'kg', 10),
('紫菜', 'g', 200),
('排骨', 'kg', 4),
('黄瓜', 'kg', 3),
('黄豆', 'kg', 5);

INSERT INTO t_inventory(ingredient_id, stock)
SELECT ingredient_id, 10 FROM t_ingredient;

INSERT INTO t_dish_ingredient(dish_id, ingredient_id, quantity) VALUES
(2, 2, 0.200),
(2, 3, 2.000),
(1, 1, 0.300),
(3, 4, 0.250),
(7, 8, 0.100);
-- 文瀛餐厅管理系统业务表
-- 在若依数据库 ry-vue 中执行

USE `ry-vue`;

DROP TABLE IF EXISTS t_order_item;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_stock_in;
DROP TABLE IF EXISTS t_inventory;
DROP TABLE IF EXISTS t_dish_ingredient;
DROP TABLE IF EXISTS t_ingredient;
DROP TABLE IF EXISTS t_dish;
DROP TABLE IF EXISTS t_category;

CREATE TABLE t_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT NOT NULL DEFAULT 0,
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '0正常 2删除'
) COMMENT='菜品分类';

CREATE TABLE t_dish (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT '菜品名称',
  category_id BIGINT NOT NULL COMMENT '分类ID',
  price DECIMAL(10,2) NOT NULL COMMENT '售价',
  description VARCHAR(500) COMMENT '描述',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag CHAR(1) NOT NULL DEFAULT '0'
) COMMENT='菜品';

CREATE TABLE t_ingredient (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT '食材名称',
  unit VARCHAR(20) NOT NULL COMMENT '单位',
  safety_stock DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '安全库存量',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  del_flag CHAR(1) NOT NULL DEFAULT '0'
) COMMENT='食材档案';

CREATE TABLE t_dish_ingredient (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dish_id BIGINT NOT NULL COMMENT '菜品ID',
  ingredient_id BIGINT NOT NULL COMMENT '食材ID',
  quantity DECIMAL(10,3) NOT NULL COMMENT '单份消耗量',
  UNIQUE KEY uk_dish_ingredient (dish_id, ingredient_id)
) COMMENT='菜品配方';

CREATE TABLE t_inventory (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT NOT NULL UNIQUE COMMENT '食材ID',
  stock DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '当前库存量',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='食材库存';

CREATE TABLE t_stock_in (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  ingredient_id BIGINT NOT NULL COMMENT '食材ID',
  quantity DECIMAL(10,2) NOT NULL COMMENT '入库数量',
  unit_price DECIMAL(10,2) COMMENT '采购单价',
  remark VARCHAR(200) COMMENT '备注',
  operator BIGINT COMMENT '操作人 sys_user.id',
  in_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间'
) COMMENT='入库记录';

CREATE TABLE t_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1已下单 2已完成 3已退单',
  order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_time (order_time),
  INDEX idx_status (status)
) COMMENT='订单';

CREATE TABLE t_order_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  dish_id BIGINT NOT NULL COMMENT '菜品ID',
  dish_name VARCHAR(100) NOT NULL COMMENT '菜品名快照',
  price DECIMAL(10,2) NOT NULL COMMENT '成交单价快照',
  quantity INT NOT NULL COMMENT '份数',
  INDEX idx_order (order_id),
  INDEX idx_dish (dish_id)
) COMMENT='订单明细';

INSERT INTO t_category(name, sort) VALUES
('热菜', 1), ('主食', 2), ('汤品', 3), ('凉菜', 4), ('饮品', 5);

INSERT INTO t_dish(name, category_id, price, status) VALUES
('红烧肉', 1, 15.00, 1),
('西红柿鸡蛋', 1, 8.00, 1),
('麻婆豆腐', 1, 10.00, 1),
('鱼香肉丝', 1, 12.00, 1),
('宫保鸡丁', 1, 13.00, 1),
('清炒时蔬', 1, 6.00, 1),
('米饭', 2, 1.50, 1),
('馒头', 2, 1.00, 1),
('紫菜蛋花汤', 3, 3.00, 1),
('冬瓜排骨汤', 3, 6.00, 1),
('凉拌黄瓜', 4, 5.00, 1),
('豆浆', 5, 2.00, 1);

INSERT INTO t_ingredient(name, unit, safety_stock) VALUES
('猪五花肉', 'kg', 5),
('西红柿', 'kg', 3),
('鸡蛋', '个', 50),
('豆腐', 'kg', 4),
('猪肉丝', 'kg', 5),
('鸡丁', 'kg', 4),
('青菜', 'kg', 5),
('大米', 'kg', 20),
('面粉', 'kg', 10),
('紫菜', 'g', 200),
('排骨', 'kg', 4),
('黄瓜', 'kg', 3),
('黄豆', 'kg', 5);

INSERT INTO t_inventory(ingredient_id, stock)
SELECT id, 10 FROM t_ingredient;

INSERT INTO t_dish_ingredient(dish_id, ingredient_id, quantity) VALUES
(2, 2, 0.200),
(2, 3, 2.000),
(1, 1, 0.300),
(3, 4, 0.250),
(7, 8, 0.100);
