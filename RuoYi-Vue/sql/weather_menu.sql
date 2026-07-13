-- ----------------------------
-- 天气管理模块 - 菜单SQL
-- 执行前请确保已经存在 sys_menu 表，并且数据库中已有基础菜单数据
-- 如果之前已执行过，可以先删除再执行：
-- delete from sys_menu where menu_id >= 2000 and menu_id <= 2050;
-- ----------------------------

-- 一级菜单：天气管理（目录）
insert into sys_menu values('2000', '天气管理', '0', '5', 'weather', null, '', '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', sysdate(), '', null, '天气管理目录');

-- 二级菜单：监测城市
insert into sys_menu values('2001', '监测城市', '2000', '1', 'city', 'city/city/index', '', '', 1, 0, 'C', '0', '0', 'city:city:list', 'build', 'admin', sysdate(), '', null, '监测城市菜单');

-- 监测城市按钮权限
insert into sys_menu values('2002', '城市查询', '2001', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'city:city:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2003', '城市新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'city:city:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2004', '城市修改', '2001', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'city:city:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2005', '城市删除', '2001', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'city:city:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2006', '城市导出', '2001', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'city:city:export', '#', 'admin', sysdate(), '', null, '');

-- 二级菜单：天气记录
insert into sys_menu values('2010', '天气记录', '2000', '2', 'record', 'record/record/index', '', '', 1, 0, 'C', '0', '0', 'record:record:list', 'list', 'admin', sysdate(), '', null, '天气记录菜单');

-- 天气记录按钮权限
insert into sys_menu values('2011', '记录查询', '2010', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'record:record:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2012', '记录新增', '2010', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'record:record:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2013', '记录修改', '2010', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'record:record:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2014', '记录删除', '2010', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'record:record:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2015', '记录导出', '2010', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'record:record:export', '#', 'admin', sysdate(), '', null, '');

-- 二级菜单：告警规则
insert into sys_menu values('2020', '告警规则', '2000', '3', 'alert', 'alert/alert/index', '', '', 1, 0, 'C', '0', '0', 'alert:alert:list', 'warning', 'admin', sysdate(), '', null, '告警规则菜单');

-- 告警规则按钮权限
insert into sys_menu values('2021', '规则查询', '2020', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'alert:alert:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2022', '规则新增', '2020', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'alert:alert:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2023', '规则修改', '2020', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'alert:alert:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2024', '规则删除', '2020', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'alert:alert:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2025', '规则导出', '2020', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'alert:alert:export', '#', 'admin', sysdate(), '', null, '');
