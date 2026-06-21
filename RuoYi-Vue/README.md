# 文瀛餐厅管理系统

文瀛餐厅管理系统是中北大学课程/答辩项目，基于 RuoYi-Vue 3.9.2 经典版二次开发。

## 项目定位

- 面向餐厅后台管理场景，覆盖分类、菜品、食材、库存、入库、配方、订单和经营看板。
- 核心演示链路为：配方 -> 入库 -> 模拟下单 -> 完成订单扣库存 -> 退单回滚 -> 看板统计。
- 后端采用 Spring Boot、Spring Security、MyBatis、MySQL、Redis。
- 前端采用 Vue 2、Element UI、ECharts。

## 运行目录

本目录是实际运行工程根目录：

```bash
G:\restaurant_manager\RuoYi-Vue
```

## 常用命令

```bash
# 后端编译
mvn -pl ruoyi-admin -am compile

# 前端安装依赖
cd ruoyi-ui
npm install

# 前端开发服务
npm run dev

# 前端生产构建
npm run build:prod
```

## 文档

施工计划、数据库 schema、菜单初始化和演示数据脚本位于：

```bash
G:\restaurant_manager\docs\plan_ruoyi_classic
```
