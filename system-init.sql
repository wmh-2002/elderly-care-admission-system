/* =========================================================
  养老院入住管理系统 —— 带详细中文注释的统一建库脚本
  MySQL 8.x   字符集：utf8mb4
  执行顺序：①建库 → ②字典/基础 → ③业务表 → ④索引 → ⑤初始数据
==========================================================*/

-- 1️⃣ 创建数据库并设定统一字符集（支持 emoji 生僻字）
CREATE DATABASE IF NOT EXISTS nms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE nms;

-- 2️⃣ 通用字典表：保存性别、支付方式、民族等下拉选项
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  dict_type   VARCHAR(50)  NOT NULL COMMENT '字典类型，例如：gender、pay_method',
  dict_key    VARCHAR(50)  NOT NULL COMMENT '存储值，如 M/F',
  dict_value  VARCHAR(200) NOT NULL COMMENT '显示值，如 男/女',
  sort_no     INT DEFAULT 0 COMMENT '排序号，升序排列',
  UNIQUE KEY uk_dict_type_key (dict_type, dict_key) COMMENT '同一类型下键不能重复'
) COMMENT='系统统一字典表';

-- 3️⃣ 角色表：系统管理员、院长、护理主管、护理员、财务、前台
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色主键',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  remark    VARCHAR(200) COMMENT '角色描述'
) COMMENT='角色表';

-- 4️⃣ 用户表：所有可登录系统的账号（员工）
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户主键',
  username    VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
  pwd         VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 密文）',
  real_name   VARCHAR(50) COMMENT '真实姓名',
  role_id     BIGINT NOT NULL COMMENT '外键→sys_role',
  status      TINYINT DEFAULT 1 COMMENT '状态：1 正常  0 禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) COMMENT='系统用户表';

-- 5️⃣ 房间表：维护楼层、房间号、类型、最大床位数
DROP TABLE IF EXISTS room;
CREATE TABLE room (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房间主键',
  room_no  VARCHAR(20) NOT NULL UNIQUE COMMENT '房间编号，如 101、202',
  room_type VARCHAR(20) NOT NULL COMMENT '单人间/双人间/多人间',
  floor    INT COMMENT '楼层',
  max_bed  INT NOT NULL COMMENT '最大可放床数'
) COMMENT='房间档案';

-- 6️⃣ 床位表：每个房间下的具体床位及状态
DROP TABLE IF EXISTS bed;
CREATE TABLE bed (
  id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '床位主键',
  room_id BIGINT NOT NULL COMMENT '外键→room',
  bed_no  VARCHAR(20) NOT NULL COMMENT '床位编号，如 101-1',
  status  TINYINT DEFAULT 0 COMMENT '0 空闲  1 已入住  2 维修',
  UNIQUE KEY uk_room_bed (room_id, bed_no) COMMENT '同一房间下床号唯一',
  CONSTRAINT fk_bed_room FOREIGN KEY (room_id) REFERENCES room(id)
) COMMENT='床位档案';

-- 7️⃣ 护理等级表：三级护理、二级护理、一级护理及日单价
DROP TABLE IF EXISTS care_level;
CREATE TABLE care_level (
  level_code  VARCHAR(20) PRIMARY KEY COMMENT '等级代码，如 L1/L2/L3',
  level_name  VARCHAR(50) NOT NULL COMMENT '等级名称',
  description VARCHAR(500) COMMENT '描述',
  daily_price DECIMAL(10,2) NOT NULL COMMENT '日单价（元）'
) COMMENT='护理等级字典';

-- 8️⃣ 老人档案表：入住老人所有基本信息、健康信息、联系人、费用标准
DROP TABLE IF EXISTS elder;
CREATE TABLE elder (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '老人主键',
  elder_no        VARCHAR(30) NOT NULL UNIQUE COMMENT '老人编号，可自动生成',
  name            VARCHAR(50) NOT NULL COMMENT '姓名',
  gender          CHAR(1) NOT NULL COMMENT '性别：M 男  F 女',
  birth_date      DATE COMMENT '出生日期',
  id_card         VARCHAR(18) UNIQUE COMMENT '身份证号',
  nation          VARCHAR(20) COMMENT '民族',
  native_place    VARCHAR(100) COMMENT '籍贯',
  phone           VARCHAR(20) COMMENT '本人电话',
  address         VARCHAR(200) COMMENT '家庭地址',
  blood_type      VARCHAR(10) COMMENT '血型',
  allergy         VARCHAR(500) COMMENT '过敏史',
  medical_history VARCHAR(1000) COMMENT '既往病史',
  health_status   VARCHAR(500) COMMENT '目前健康状况',
  contact_name    VARCHAR(50) COMMENT '紧急联系人姓名',
  contact_phone   VARCHAR(20) COMMENT '紧急联系人电话',
  contact_relation VARCHAR(50) COMMENT '与老人关系',
  photo           VARCHAR(200) COMMENT '照片 URL',
  checkin_date    DATE COMMENT '入住日期',
  bed_id          BIGINT UNIQUE COMMENT '外键→bed，唯一约束保证一人一床',
  care_level      VARCHAR(20) NOT NULL COMMENT '护理等级→care_level.level_code',
  fee_standard    DECIMAL(10,2) COMMENT '月收费标准（元）',
  pay_type        VARCHAR(20) COMMENT '缴费方式→字典',
  status          TINYINT DEFAULT 1 COMMENT '状态：1 在院  0 退住',
  create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建档时间',
  CONSTRAINT fk_elder_bed FOREIGN KEY (bed_id) REFERENCES bed(id)
) COMMENT='老人档案';

-- 9️⃣ 护理计划表：针对每个老人制定个性化护理方案
DROP TABLE IF EXISTS care_plan;
CREATE TABLE care_plan (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '计划主键',
  elder_id     BIGINT NOT NULL COMMENT '老人→elder',
  plan_content TEXT NOT NULL COMMENT '计划内容（JSON 或长文本）',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改',
  CONSTRAINT fk_plan_elder FOREIGN KEY (elder_id) REFERENCES elder(id)
) COMMENT='护理计划';

-- 🔟 护理记录表：每日生命体征、饮食、排泄、睡眠、用药、特殊情况
DROP TABLE IF EXISTS care_record;
CREATE TABLE care_record (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录主键',
  elder_id      BIGINT NOT NULL COMMENT '老人→elder',
  record_date   DATE NOT NULL COMMENT '记录日期',
  temperature   DECIMAL(4,1) COMMENT '体温（℃）',
  pulse         INT COMMENT '脉搏（次/分）',
  breath        INT COMMENT '呼吸（次/分）',
  blood_pressure VARCHAR(20) COMMENT '血压，如 120/80',
  diet          VARCHAR(500) COMMENT '饮食情况',
  excrete       VARCHAR(500) COMMENT '排泄情况',
  sleep         VARCHAR(500) COMMENT '睡眠情况',
  medicine      VARCHAR(500) COMMENT '用药记录',
  special       VARCHAR(500) COMMENT '特殊情况',
  nurse_id      BIGINT NOT NULL COMMENT '护理员→sys_user',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '填写时间',
  KEY idx_elder_date (elder_id, record_date) COMMENT '按老人+日期查询',
  CONSTRAINT fk_record_elder FOREIGN KEY (elder_id) REFERENCES elder(id),
  CONSTRAINT fk_record_nurse FOREIGN KEY (nurse_id) REFERENCES sys_user(id)
) COMMENT='日常护理记录';

-- 1️⃣1️⃣ 费用项目字典：住宿、护理、餐饮、医疗、其他
DROP TABLE IF EXISTS fee_item;
CREATE TABLE fee_item (
  item_code  VARCHAR(20) PRIMARY KEY COMMENT '项目代码',
  item_name  VARCHAR(50) NOT NULL COMMENT '项目名称',
  unit_price DECIMAL(10,2) NOT NULL COMMENT '单价（元）',
  item_type  VARCHAR(20) NOT NULL COMMENT '类型：住宿/护理/餐饮/医疗/其他'
) COMMENT='费用项目字典';

-- 1️⃣2️⃣ 月度账单主表：每月为每个老人生成一条总账单
DROP TABLE IF EXISTS bill;
CREATE TABLE bill (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账单主键',
  elder_id     BIGINT NOT NULL COMMENT '老人→elder',
  bill_month   VARCHAR(7) NOT NULL COMMENT '账单月份 yyyy-MM',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '应付金额',
  paid_amount  DECIMAL(10,2) DEFAULT 0 COMMENT '已付金额',
  status       TINYINT DEFAULT 0 COMMENT '0 未缴清  1 已缴清',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  UNIQUE KEY uk_elder_month (elder_id, bill_month) COMMENT '每人每月唯一账单',
  CONSTRAINT fk_bill_elder FOREIGN KEY (elder_id) REFERENCES elder(id)
) COMMENT='月度账单';

-- 1️⃣3️⃣ 账单明细：一条账单对应多条费用明细
DROP TABLE IF EXISTS bill_detail;
CREATE TABLE bill_detail (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细主键',
  bill_id    BIGINT NOT NULL COMMENT '账单→bill',
  item_code  VARCHAR(20) NOT NULL COMMENT '费用项目→fee_item',
  quantity   INT DEFAULT 1 COMMENT '数量',
  unit_price DECIMAL(10,2) COMMENT '单价（冗余）',
  amount     DECIMAL(10,2) COMMENT '小计金额',
  CONSTRAINT fk_detail_bill FOREIGN KEY (bill_id) REFERENCES bill(id),
  CONSTRAINT fk_detail_item FOREIGN KEY (item_code) REFERENCES fee_item(item_code)
) COMMENT='账单明细';




-- 1️⃣8️⃣ 初始化必备数据
-- 8.1 角色
INSERT INTO sys_role VALUES
(1,'系统管理员','拥有所有权限'),
(2,'院长','查看各类统计报表'),
(3,'护理主管','管理护理计划与排班'),
(4,'护理员','填写护理记录'),
(5,'财务人员','费用计算与收款'),
(6,'前台接待','老人入住登记');

-- 8.2 管理员账号：用户名 admin  密码 123456（BCrypt 加密串）
INSERT INTO sys_user(id,username,pwd,real_name,role_id,status,create_time) VALUES
(1,'admin','$2a$10$8YrALx2z0kV8xLfxKfjEbeC8V0j2n2U5ta/qHY8pQjQUjV8Y1vU2q','超级管理员',1,1,NOW());

-- 8.3 房间与床位示例
INSERT INTO room VALUES
(1,'101','单人间',1,1),
(2,'102','双人间',1,2),
(3,'201','三人间',2,3);
INSERT INTO bed(room_id,bed_no) VALUES
(1,'101-1'),
(2,'102-1'),(2,'102-2'),
(3,'201-1'),(3,'201-2'),(3,'201-3');

-- 8.4 护理等级
INSERT INTO care_level VALUES
('L1','三级护理','自理老人',30),
('L2','二级护理','半自理老人',60),
('L3','一级护理','全护理老人',100);

-- 8.5 费用项目
INSERT INTO fee_item VALUES
('ACC','住宿费','50','住宿'),
('CARE','护理费','60','护理'),
('MEAL','餐饮费','30','餐饮'),
('MED','基础医疗','20','医疗');

-- 8.6 字典数据
INSERT INTO sys_dict VALUES
(1,'gender','M','男',1),
(2,'gender','F','女',2),
(3,'pay_method','cash','现金',1),
(4,'pay_method','wx','微信',2),
(5,'pay_method','ali','支付宝',3);

/* =========================================================
  脚本执行完毕！
  后续可按年月建立 care_record_202501 分表，代码中动态切换表名即可。
  所有字段均带中文注释，方便直接生成文档或逆向工程。
==========================================================*/