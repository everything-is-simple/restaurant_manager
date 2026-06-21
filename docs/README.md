# 文瀛餐厅管理系统 — 项目文档

> 所属院系：计算机科学与技术学院（大数据学院）
> 指导老师：王斌　|　实施平台：自主可控创新实验室
> 验收方式：答辩 + 软件　|　参与学生上限：3 人

专为高校食堂打造的后台管理系统，涵盖菜品上架、库存盘点、订单统计、就餐数据分析模块，
联动前端点餐数据，辅助食堂管控食材损耗、优化菜品供给，实现食堂精细化运营。

## 施工方案文档（唯一真相源）

所有施工文档位于 [plan_ruoyi_classic/](plan_ruoyi_classic/) 目录：

| 文档 | 内容 |
|------|------|
| [01_PRD_产品需求文档.md](plan_ruoyi_classic/01_PRD_产品需求文档.md) | 项目背景、角色、功能范围、不做清单、验收标准 |
| [02_TRD_技术方案与安装使用说明.md](plan_ruoyi_classic/02_TRD_技术方案与安装使用说明.md) | 技术选型、安装步骤、接口清单、工程落位 |
| [03_Design_系统设计文档.md](plan_ruoyi_classic/03_Design_系统设计文档.md) | 架构、模块、数据流、权限矩阵 |
| [04_施工计划_当前进度版.md](plan_ruoyi_classic/04_施工计划_当前进度版.md) | D1-D10 逐日排期 + 当前进度标记 + 业务算法清单 |
| [05_Core_Algorithm_核心算法说明.md](plan_ruoyi_classic/05_Core_Algorithm_核心算法说明.md) | 扣库存事务、状态机、看板统计 SQL |
| [06_用户使用说明.md](plan_ruoyi_classic/06_用户使用说明.md) | 三角色使用说明、主链路操作、常见提示 |
| [07_答辩演示脚本.md](plan_ruoyi_classic/07_答辩演示脚本.md) | 3-5 分钟答辩演示流程和讲解词 |
| [08_Menu_Permission_菜单与权限初始化清单.md](plan_ruoyi_classic/08_Menu_Permission_菜单与权限初始化清单.md) | 菜单树、按钮权限、角色分配 |
| [09_答辩PPT大纲.md](plan_ruoyi_classic/09_答辩PPT大纲.md) | 12 页答辩 PPT 素材稿和讲稿 |
| [10_彩排检查记录.md](plan_ruoyi_classic/10_彩排检查记录.md) | 彩排检查项、验证记录、自动化限制说明 |
| [menu_init.sql](plan_ruoyi_classic/menu_init.sql) | 菜单/角色/用户初始化 SQL（36 业务菜单/按钮 + 2 角色 + 2 测试账号） |
| [schema.sql](plan_ruoyi_classic/schema.sql) | 8 张业务表 + 演示数据 |
| [gen_config_update.sql](plan_ruoyi_classic/gen_config_update.sql) | 若依代码生成器配置脚本（包名/模块名/字典） |

## 技术栈速览

- 脚手架：RuoYi-Vue 3.9.2 经典版
- 前端：Vue 2 + Vue CLI + Element UI + ECharts
- 后端：Java 17 + Spring Boot 4.x + MyBatis(XML) + Spring Security(JWT)
- 数据库：MySQL 8.x
- 缓存：Redis
- 构建：Maven + npm
