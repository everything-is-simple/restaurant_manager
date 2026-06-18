-- 文瀛餐厅管理系统 MVP — 数据库初始化脚本
-- 直接复制到 Navicat / MySQL Workbench 执行即可

CREATE DATABASE IF NOT EXISTS restaurant_mvp DEFAULT CHARSET utf8mb4;
USE restaurant_mvp;

DROP TABLE IF EXISTS t_order_item;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_dish;
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50)  NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL
);

CREATE TABLE t_dish (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL,
  category    VARCHAR(50),
  price       DECIMAL(10,2) NOT NULL,
  stock       INT           NOT NULL DEFAULT 0,
  image       VARCHAR(255),
  status      TINYINT       NOT NULL DEFAULT 1,
  create_time DATETIME      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_order (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no     VARCHAR(50)   NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status       TINYINT       NOT NULL DEFAULT 1,
  order_time   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_time (order_time)
);

CREATE TABLE t_order_item (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id  BIGINT        NOT NULL,
  dish_id   BIGINT        NOT NULL,
  dish_name VARCHAR(100)  NOT NULL,
  price     DECIMAL(10,2) NOT NULL,
  quantity  INT           NOT NULL,
  INDEX idx_order (order_id),
  INDEX idx_dish  (dish_id)
);

-- 演示数据
INSERT INTO t_user(username, password) VALUES ('admin', '123456');

INSERT INTO t_dish(name, category, price, stock, status) VALUES
('红烧肉',      '热菜', 15.00, 100, 1),
('西红柿鸡蛋',  '热菜',  8.00, 200, 1),
('麻婆豆腐',    '热菜', 10.00, 150, 1),
('鱼香肉丝',    '热菜', 12.00, 120, 1),
('宫保鸡丁',    '热菜', 13.00, 110, 1),
('清炒时蔬',    '热菜',  6.00, 180, 1),
('米饭',        '主食',  1.50, 500, 1),
('馒头',        '主食',  1.00, 300, 1),
('紫菜蛋花汤',  '汤品',  3.00, 200, 1),
('冬瓜排骨汤',  '汤品',  6.00, 100, 1),
('凉拌黄瓜',    '凉菜',  5.00, 150, 1),
('豆浆',        '饮品',  2.00, 250, 1);

-- 可以再写一段循环 / 脚本灌入近 7 天的订单数据，看板才好看
-- 留给开发同学自己写一个小工具即可（D9 任务）
