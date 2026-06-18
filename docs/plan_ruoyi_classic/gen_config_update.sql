USE `ry-vue`;

-- ============================================
-- gen_table: 基本信息配置
-- ============================================
UPDATE gen_table SET package_name='com.ruoyi.system', module_name='restaurant',
  business_name='category', function_name='菜品分类' WHERE table_name='t_category';

UPDATE gen_table SET package_name='com.ruoyi.system', module_name='restaurant',
  business_name='dish', function_name='菜品' WHERE table_name='t_dish';

UPDATE gen_table SET package_name='com.ruoyi.system', module_name='restaurant',
  business_name='ingredient', function_name='食材档案' WHERE table_name='t_ingredient';

UPDATE gen_table SET package_name='com.ruoyi.system', module_name='restaurant',
  business_name='inventory', function_name='食材库存' WHERE table_name='t_inventory';

UPDATE gen_table SET package_name='com.ruoyi.system', module_name='restaurant',
  business_name='order', function_name='订单' WHERE table_name='t_order';

UPDATE gen_table SET package_name='com.ruoyi.system', module_name='restaurant',
  business_name='stockIn', function_name='入库记录' WHERE table_name='t_stock_in';

-- ============================================
-- gen_table_column: 修正 sort 类型 (INT → Integer)
-- ============================================
UPDATE gen_table_column SET java_type='Integer'
  WHERE column_name='sort' AND table_id IN (SELECT table_id FROM gen_table WHERE table_name='t_category');

-- t_dish 没有 sort 列，跳过

-- ============================================
-- gen_table_column: status 字段配置为 radio + sys_normal_disable
-- ============================================
-- t_category, t_dish, t_ingredient 的 status 都是 CHAR(1) 状态
UPDATE gen_table_column c
  JOIN gen_table t ON c.table_id = t.table_id
  SET c.html_type='radio', c.dict_type='sys_normal_disable', c.query_type='EQ'
  WHERE c.column_name='status'
    AND t.table_name IN ('t_category','t_dish','t_ingredient');

-- ============================================
-- gen_table_column: remark 从列表中移除
-- ============================================
UPDATE gen_table_column SET is_list='0' WHERE column_name='remark';

-- ============================================
-- gen_table_column: del_flag 不应在列表中
-- ============================================
UPDATE gen_table_column SET is_list=NULL, is_query=NULL WHERE column_name='del_flag';

-- ============================================
-- gen_table_column: sort (显示顺序) 不应是查询条件
-- ============================================
UPDATE gen_table_column SET is_query=NULL
  WHERE column_name='sort' AND table_id IN (SELECT table_id FROM gen_table WHERE table_name='t_category');

-- ============================================
-- gen_table_column: t_order.status 用自定义字典 (TINYINT 1/2/3)
-- 这里先配为 select，dict_type 稍后手工建
-- ============================================
UPDATE gen_table_column c
  JOIN gen_table t ON c.table_id = t.table_id
  SET c.html_type='select', c.dict_type='restaurant_order_status', c.query_type='EQ'
  WHERE c.column_name='status' AND t.table_name='t_order';
