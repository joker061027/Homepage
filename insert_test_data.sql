-- 数字餐票管理系统测试数据
-- 数据库: shuzicanpiao1

USE shuzicanpiao1;

-- 清空现有数据（按依赖关系顺序）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM ticket;
DELETE FROM ott;
DELETE FROM `order`;
DELETE FROM tickettype;
DELETE FROM stall;
DELETE FROM canteen;
DELETE FROM user;
DELETE FROM role;
DELETE FROM agencies;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 插入部门数据 (agencies)
INSERT INTO agencies (AgenciesId, AgenciesName, Status, FK_Creator, CreateDatetime, CreateIP) VALUES
('DEPT001', '行政部', 1, 'admin', '20250730100000', '127.0.0.1'),
('DEPT002', '财务部', 1, 'admin', '20250730100000', '127.0.0.1'),
('DEPT003', '人事部', 1, 'admin', '20250730100000', '127.0.0.1'),
('DEPT004', '技术部', 1, 'admin', '20250730100000', '127.0.0.1'),
('DEPT005', '后勤部', 1, 'admin', '20250730100000', '127.0.0.1');

-- 2. 插入角色数据 (role)
INSERT INTO role (RoleId, RoleName, Status, FK_Creator, CreateDateIP) VALUES
('ROLE001', '系统管理员', 1, 'admin', '127.0.0.1'),
('ROLE002', '部门管理员', 1, 'admin', '127.0.0.1'),
('ROLE003', '普通员工', 1, 'admin', '127.0.0.1'),
('ROLE004', '食堂管理员', 1, 'admin', '127.0.0.1'),
('ROLE005', '档口人员', 1, 'admin', '127.0.0.1');

-- 3. 插入用户数据 (user)
INSERT INTO user (UserId, UserName, RoleId, AgenciesId, FullName, Password, Status, FK_Creator, CreateDatetime, CreateIP, phonenumber) VALUES
('USER001', 'admin', 'ROLE001', 'DEPT001', '管理员', '123456', 1, 'system', '20250730100000', '127.0.0.1', '13800138000'),
('USER002', 'zhangsan', 'ROLE002', 'DEPT002', '张三', '123456', 1, 'admin', '20250730100000', '127.0.0.1', '13800138001'),
('USER003', 'lisi', 'ROLE003', 'DEPT003', '李四', '123456', 1, 'admin', '20250730100000', '127.0.0.1', '13800138002'),
('USER004', 'wangwu', 'ROLE003', 'DEPT004', '王五', '123456', 1, 'admin', '20250730100000', '127.0.0.1', '13800138003'),
('USER005', 'canteen_admin', 'ROLE004', 'DEPT005', '食堂管理', '123456', 1, 'admin', '20250730100000', '127.0.0.1', '13800138004'),
('USER006', 'stall_user1', 'ROLE005', 'DEPT005', '档口员工1', '123456', 1, 'admin', '20250730100000', '127.0.0.1', '13800138005'),
('USER007', 'stall_user2', 'ROLE005', 'DEPT005', '档口员工2', '123456', 1, 'admin', '20250730100000', '127.0.0.1', '13800138006');

-- 4. 插入食堂数据 (canteen)
INSERT INTO canteen (CanteenId, CanteenName, Status, FK_Creator, CreateDatetime, CreateIP) VALUES
('CANTEEN001', '第一食堂', 1, 'admin', '20250730100000', '127.0.0.1'),
('CANTEEN002', '第二食堂', 1, 'admin', '20250730100000', '127.0.0.1'),
('CANTEEN003', '第三食堂', 1, 'admin', '20250730100000', '127.0.0.1');

-- 5. 插入档口数据 (stall)
INSERT INTO stall (StallId, StallName, CanteenId, Statue, FK_Creator, CreateDatetime, CreateIP) VALUES
('STALL001', '川菜档口', 'CANTEEN001', 1, 'admin', '20250730100000', '127.0.0.1'),
('STALL002', '粤菜档口', 'CANTEEN001', 1, 'admin', '20250730100000', '127.0.0.1'),
('STALL003', '面食档口', 'CANTEEN002', 1, 'admin', '20250730100000', '127.0.0.1'),
('STALL004', '快餐档口', 'CANTEEN002', 1, 'admin', '20250730100000', '127.0.0.1'),
('STALL005', '素食档口', 'CANTEEN003', 1, 'admin', '20250730100000', '127.0.0.1');

-- 6. 插入餐票类型数据 (tickettype)
INSERT INTO tickettype (TypeID, TypeName, Value, CanteenId, Status, FK_Creator, CreateDatetime, CreateIP, remark) VALUES
('TYPE001', '早餐券', 5, 'CANTEEN001', 1, 'admin', '20250730100000', '127.0.0.1', '适用于早餐时段，面值5元'),
('TYPE002', '午餐券', 15, 'CANTEEN001', 1, 'admin', '20250730100000', '127.0.0.1', '适用于午餐时段，面值15元'),
('TYPE003', '晚餐券', 12, 'CANTEEN001', 1, 'admin', '20250730100000', '127.0.0.1', '适用于晚餐时段，面值12元'),
('TYPE004', '通用券', 10, 'CANTEEN002', 1, 'admin', '20250730100000', '127.0.0.1', '全时段通用，面值10元'),
('TYPE005', '素食券', 8, 'CANTEEN003', 1, 'admin', '20250730100000', '127.0.0.1', '素食专用券，面值8元');

-- 7. 插入订单数据 (order)
INSERT INTO `order` (OrderId, OrderNumber, AgenciesId, PassUserId, Status, FK_Creator, CreateDatetime, CreateIP, Suggestion, ExpirationTime, PassTime, StartUserid) VALUES
('ORDER001', 'ORD20250730001', 'DEPT002', 'USER002', 1, 'USER003', '20250730100000', '127.0.0.1', '部门月度餐票申请', '20250830100000', '20250730110000', 'USER003'),
('ORDER002', 'ORD20250730002', 'DEPT003', 'USER002', 1, 'USER004', '20250730100000', '127.0.0.1', '员工餐票补充申请', '20250830100000', '20250730120000', 'USER004'),
('ORDER003', 'ORD20250730003', 'DEPT004', 'USER002', 0, 'USER005', '20250730100000', '127.0.0.1', '技术部团建餐票', '20250830100000', '', 'USER005');

-- 8. 插入订单项数据 (ott)
INSERT INTO ott (OTTID, OrderId, TypeID, OredrAmount, Status, FK_Creator, CreateDatetime, CreateIP) VALUES
('OTT001', 'ORDER001', 'TYPE002', 50, 1, 'USER003', '20250730100000', '127.0.0.1'),
('OTT002', 'ORDER001', 'TYPE003', 30, 1, 'USER003', '20250730100000', '127.0.0.1'),
('OTT003', 'ORDER002', 'TYPE001', 20, 1, 'USER004', '20250730100000', '127.0.0.1'),
('OTT004', 'ORDER002', 'TYPE004', 25, 1, 'USER004', '20250730100000', '127.0.0.1'),
('OTT005', 'ORDER003', 'TYPE005', 15, 0, 'USER005', '20250730100000', '127.0.0.1');

-- 9. 插入餐票数据 (ticket) - 只为已通过的订单生成餐票
INSERT INTO ticket (TickerId, TypeID, StallId, UsedId, CheckId, UseTime, OrderId, ExpirationTime, CheckTime, Staus, Fk_Creator, CreateDatetime, CreateIP, typename) VALUES
-- ORDER001的午餐券 (50张中的前5张作为示例)
('TICKET001', 'TYPE002', 'STALL001', 'USER006', 'USER005', NULL, 'ORDER001', '20250830', NULL, 0, 'USER002', '20250730110000', '127.0.0.1', '午餐券'),
('TICKET002', 'TYPE002', 'STALL001', 'USER006', 'USER005', NULL, 'ORDER001', '20250830', NULL, 0, 'USER002', '20250730110000', '127.0.0.1', '午餐券'),
('TICKET003', 'TYPE002', 'STALL002', 'USER007', 'USER005', NULL, 'ORDER001', '20250830', NULL, 0, 'USER002', '20250730110000', '127.0.0.1', '午餐券'),
('TICKET004', 'TYPE002', 'STALL002', 'USER007', 'USER005', '20250730130000', 'ORDER001', '20250830', '20250730140000', 1, 'USER002', '20250730110000', '127.0.0.1', '午餐券'),
('TICKET005', 'TYPE002', 'STALL001', 'USER006', 'USER005', '20250730130000', 'ORDER001', '20250830', '20250730140000', 1, 'USER002', '20250730110000', '127.0.0.1', '午餐券'),

-- ORDER001的晚餐券 (30张中的前3张作为示例)
('TICKET006', 'TYPE003', 'STALL001', 'USER006', 'USER005', NULL, 'ORDER001', '20250830', NULL, 0, 'USER002', '20250730110000', '127.0.0.1', '晚餐券'),
('TICKET007', 'TYPE003', 'STALL002', 'USER007', 'USER005', NULL, 'ORDER001', '20250830', NULL, 0, 'USER002', '20250730110000', '127.0.0.1', '晚餐券'),
('TICKET008', 'TYPE003', 'STALL001', 'USER006', 'USER005', '20250730180000', 'ORDER001', '20250830', '20250730190000', 1, 'USER002', '20250730110000', '127.0.0.1', '晚餐券'),

-- ORDER002的早餐券 (20张中的前2张作为示例)
('TICKET009', 'TYPE001', 'STALL003', 'USER006', 'USER005', NULL, 'ORDER002', '20250830', NULL, 0, 'USER002', '20250730120000', '127.0.0.1', '早餐券'),
('TICKET010', 'TYPE001', 'STALL003', 'USER006', 'USER005', '20250731070000', 'ORDER002', '20250830', '20250731080000', 1, 'USER002', '20250730120000', '127.0.0.1', '早餐券');

-- 查询验证数据
SELECT '=== 数据插入完成，验证结果 ===' as message;
SELECT 'agencies' as table_name, COUNT(*) as record_count FROM agencies
UNION ALL
SELECT 'role', COUNT(*) FROM role
UNION ALL
SELECT 'user', COUNT(*) FROM user
UNION ALL
SELECT 'canteen', COUNT(*) FROM canteen
UNION ALL
SELECT 'stall', COUNT(*) FROM stall
UNION ALL
SELECT 'tickettype', COUNT(*) FROM tickettype
UNION ALL
SELECT 'order', COUNT(*) FROM `order`
UNION ALL
SELECT 'ott', COUNT(*) FROM ott
UNION ALL
SELECT 'ticket', COUNT(*) FROM ticket;
