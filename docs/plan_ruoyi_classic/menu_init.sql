-- ============================================================
-- 文瀛餐厅管理系统 — 菜单权限初始化
-- 适用: RuoYi-Vue 3.9.2
-- 创建日期: 2026-06-19
-- 描述: 创建餐厅业务菜单、按钮权限、3 角色、测试账号
-- 执行: Get-Content menu_init.sql -Raw | mysql -uroot -proot "ry-vue"
-- ============================================================

-- 使用事务保证原子性
START TRANSACTION;

-- 清理历史（幂等）
DELETE FROM sys_user_role WHERE user_id >= 100;
DELETE FROM sys_user WHERE user_id >= 100;
DELETE FROM sys_role_menu WHERE role_id >= 100;
DELETE FROM sys_role WHERE role_id >= 100;
DELETE FROM sys_menu WHERE menu_id >= 2000;

-- ============================================================
-- 1. 菜单：餐厅管理目录（parent_id=0）
-- ============================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2000, '餐厅管理', 0, 5, 'restaurant', NULL, 1, 0, 'M', '0', '0', '', 'clipboard', 'admin', NOW(), '餐厅业务目录');

-- ============================================================
-- 2. 7 个页面菜单
-- ============================================================
-- 数据看板
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2010, '数据看板', 2000, 1, 'stats', 'restaurant/stats/index', 1, 0, 'C', '0', '0', 'restaurant:stats:list', 'dashboard', 'admin', NOW(), '数据看板');

-- 菜品分类
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2020, '菜品分类', 2000, 2, 'category', 'restaurant/category/index', 1, 0, 'C', '0', '0', 'restaurant:category:list', 'tree', 'admin', NOW(), '菜品分类管理');

-- 菜品管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2030, '菜品管理', 2000, 3, 'dish', 'restaurant/dish/index', 1, 0, 'C', '0', '0', 'restaurant:dish:list', 'list', 'admin', NOW(), '菜品管理');

-- 食材档案
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2040, '食材档案', 2000, 4, 'ingredient', 'restaurant/ingredient/index', 1, 0, 'C', '0', '0', 'restaurant:ingredient:list', 'edit', 'admin', NOW(), '食材档案');

-- 库存管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2050, '库存管理', 2000, 5, 'inventory', 'restaurant/inventory/index', 1, 0, 'C', '0', '0', 'restaurant:inventory:list', 'shopping', 'admin', NOW(), '库存管理');

-- 菜品配方
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2060, '菜品配方', 2000, 6, 'recipe', 'restaurant/recipe/index', 1, 0, 'C', '0', '0', 'restaurant:recipe:list', 'guide', 'admin', NOW(), '菜品配方');

-- 订单管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2070, '订单管理', 2000, 7, 'order', 'restaurant/order/index', 1, 0, 'C', '0', '0', 'restaurant:order:list', 'order', 'admin', NOW(), '订单管理');

-- ============================================================
-- 3. 按钮权限（menu_type='F'）
-- ============================================================
-- 菜品分类按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2021, '分类查询', 2020, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:category:query', '#', 'admin', NOW(), ''),
(2022, '分类新增', 2020, 2, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:category:add',   '#', 'admin', NOW(), ''),
(2023, '分类修改', 2020, 3, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:category:edit',  '#', 'admin', NOW(), ''),
(2024, '分类删除', 2020, 4, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:category:remove','#', 'admin', NOW(), '');

-- 菜品管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2031, '菜品查询',   2030, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:dish:query',  '#', 'admin', NOW(), ''),
(2032, '菜品新增',   2030, 2, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:dish:add',    '#', 'admin', NOW(), ''),
(2033, '菜品修改',   2030, 3, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:dish:edit',   '#', 'admin', NOW(), ''),
(2034, '菜品删除',   2030, 4, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:dish:remove', '#', 'admin', NOW(), ''),
(2035, '菜品上下架', 2030, 5, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:dish:status', '#', 'admin', NOW(), '');

-- 食材档案按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2041, '食材查询', 2040, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:ingredient:query',  '#', 'admin', NOW(), ''),
(2042, '食材新增', 2040, 2, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:ingredient:add',    '#', 'admin', NOW(), ''),
(2043, '食材修改', 2040, 3, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:ingredient:edit',   '#', 'admin', NOW(), ''),
(2044, '食材删除', 2040, 4, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:ingredient:remove', '#', 'admin', NOW(), '');

-- 库存管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2051, '库存查询', 2050, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:inventory:query',   '#', 'admin', NOW(), ''),
(2052, '入库登记', 2050, 2, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:inventory:stockin', '#', 'admin', NOW(), '');

-- 入库管理按钮（TStockInController 生成器默认权限）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2053, '入库查询', 2050, 3, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:stockIn:query',   '#', 'admin', NOW(), ''),
(2054, '入库新增', 2050, 4, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:stockIn:add',     '#', 'admin', NOW(), ''),
(2055, '入库修改', 2050, 5, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:stockIn:edit',    '#', 'admin', NOW(), ''),
(2056, '入库删除', 2050, 6, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:stockIn:remove',  '#', 'admin', NOW(), ''),
(2057, '入库导出', 2050, 7, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:stockIn:export',  '#', 'admin', NOW(), '');

-- 菜品配方按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2061, '配方查询', 2060, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:recipe:query', '#', 'admin', NOW(), ''),
(2062, '配方保存', 2060, 2, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:recipe:save',  '#', 'admin', NOW(), '');

-- 订单管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2071, '订单查询', 2070, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:order:query',    '#', 'admin', NOW(), ''),
(2072, '模拟下单', 2070, 2, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:order:mock',     '#', 'admin', NOW(), ''),
(2073, '查看详情', 2070, 3, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:order:detail',   '#', 'admin', NOW(), ''),
(2074, '完成订单', 2070, 4, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:order:complete', '#', 'admin', NOW(), ''),
(2075, '退单',     2070, 5, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:order:cancel',   '#', 'admin', NOW(), '');

-- 看板按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2011, '看板查询', 2010, 1, '', NULL, 1, 0, 'F', '0', '0', 'restaurant:stats:query', '#', 'admin', NOW(), '');

-- ============================================================
-- 4. 角色：MANAGER（食堂经理）、STOCK_KEEPER（库管员）
-- ============================================================
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) VALUES
(100, '食堂经理', 'MANAGER',      3, '5', 1, 1, '0', '0', 'admin', NOW(), '食堂经理：菜品/分类/配方/订单/库存查看/看板'),
(101, '库管员',   'STOCK_KEEPER', 4, '5', 1, 1, '0', '0', 'admin', NOW(), '库管员：食材/入库/库存查看');

-- ============================================================
-- 5. 角色菜单分配
-- ============================================================
-- MANAGER 角色：除食材档案维护外的所有业务菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(100, 2000),  -- 餐厅管理目录
(100, 2010),  -- 数据看板
(100, 2011),  -- 看板查询
(100, 2020),  -- 菜品分类
(100, 2021), (100, 2022), (100, 2023), (100, 2024),  -- 分类按钮
(100, 2030),  -- 菜品管理
(100, 2031), (100, 2032), (100, 2033), (100, 2034), (100, 2035),  -- 菜品按钮
(100, 2050),  -- 库存管理
(100, 2051),  -- 库存查询（无入库权限）
(100, 2060),  -- 菜品配方
(100, 2061), (100, 2062),  -- 配方按钮
(100, 2070),  -- 订单管理
(100, 2071), (100, 2072), (100, 2073), (100, 2074), (100, 2075);  -- 订单按钮

-- STOCK_KEEPER 角色：食材档案 + 库存管理
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(101, 2000),  -- 餐厅管理目录
(101, 2040),  -- 食材档案
(101, 2041), (101, 2042), (101, 2043), (101, 2044),  -- 食材按钮
(101, 2050),  -- 库存管理
(101, 2051), (101, 2052),  -- 库存按钮（查询 + 入库登记）
(101, 2053), (101, 2054), (101, 2055), (101, 2056), (101, 2057);  -- 入库管理按钮（TStockInController）

-- ============================================================
-- 6. 测试用户
-- ============================================================
-- 测试账号默认密码：admin123
-- (与 RuoYi 内置 admin 账号共用同一个 BCrypt hash)
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, password, status, del_flag, create_by, create_time, remark) VALUES
(100, 100, 'manager',     '食堂经理', '00', 'manager@wenying.com',  '13800138001', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', 'admin', NOW(), '食堂经理测试账号 - 默认密码 admin123'),
(101, 100, 'stockkeeper', '库管员',   '00', 'stock@wenying.com',    '13800138002', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', 'admin', NOW(), '库管员测试账号 - 默认密码 admin123');

-- 用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(100, 100),  -- manager -> MANAGER
(101, 101);  -- stockkeeper -> STOCK_KEEPER

-- admin (user_id=1) 角色扩展为：原有 admin 角色 + MANAGER 角色（保留系统管理权限同时获得业务权限）
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 100);  -- admin 额外拥有 MANAGER 业务权限

-- admin 还需要拥有所有业务菜单（通过 MANAGER 角色继承）
-- 但由于 admin 角色 (role_id=1) 没分配餐厅菜单，所以这里再为 admin 单独分配
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 2000),  -- 餐厅管理目录
(1, 2010), (1, 2011),  -- 数据看板
(1, 2020), (1, 2021), (1, 2022), (1, 2023), (1, 2024),  -- 菜品分类
(1, 2030), (1, 2031), (1, 2032), (1, 2033), (1, 2034), (1, 2035),  -- 菜品
(1, 2040), (1, 2041), (1, 2042), (1, 2043), (1, 2044),  -- 食材
(1, 2050), (1, 2051), (1, 2052), (1, 2053), (1, 2054), (1, 2055), (1, 2056), (1, 2057),  -- 库存
(1, 2060), (1, 2061), (1, 2062),  -- 配方
(1, 2070), (1, 2071), (1, 2072), (1, 2073), (1, 2074), (1, 2075);  -- 订单

COMMIT;

-- 验证
SELECT 'menu_count' AS metric, COUNT(*) AS value FROM sys_menu WHERE menu_id >= 2000
UNION ALL
SELECT 'role_count', COUNT(*) FROM sys_role WHERE role_id >= 100
UNION ALL
SELECT 'user_count', COUNT(*) FROM sys_user WHERE user_id >= 100
UNION ALL
SELECT 'role_menu_count', COUNT(*) FROM sys_role_menu WHERE menu_id >= 2000;
