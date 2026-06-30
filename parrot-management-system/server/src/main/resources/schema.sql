CREATE DATABASE IF NOT EXISTS parrot_management
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE parrot_management;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS login_log;
DROP TABLE IF EXISTS file_resource;
DROP TABLE IF EXISTS sys_user_menu;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS training_record;
DROP TABLE IF EXISTS feeding_record;
DROP TABLE IF EXISTS health_record;
DROP TABLE IF EXISTS parrot;
DROP TABLE IF EXISTS parrot_species;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(200) NOT NULL COMMENT '加密后的密码',
  real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  role VARCHAR(20) NOT NULL COMMENT '角色',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_username (username),
  KEY idx_role (role),
  KEY idx_status (status),
  CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'KEEPER', 'CUSTOMER')),
  CONSTRAINT chk_user_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级菜单ID',
  menu_code VARCHAR(80) NOT NULL COMMENT '菜单编码',
  menu_name VARCHAR(80) NOT NULL COMMENT '菜单名称',
  menu_type VARCHAR(20) NOT NULL COMMENT 'DIR目录 MENU菜单 BUTTON按钮',
  path VARCHAR(200) DEFAULT NULL COMMENT '前端路由地址',
  api_pattern VARCHAR(500) DEFAULT NULL COMMENT '后端接口匹配规则，多个用逗号分隔',
  icon VARCHAR(80) DEFAULT NULL COMMENT '前端图标',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  UNIQUE KEY uk_menu_code (menu_code),
  KEY idx_parent_id (parent_id),
  KEY idx_status (status),
  CONSTRAINT chk_menu_type CHECK (menu_type IN ('DIR', 'MENU', 'BUTTON')),
  CONSTRAINT chk_menu_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

CREATE TABLE sys_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
  role VARCHAR(20) NOT NULL COMMENT '身份类型',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  UNIQUE KEY uk_role_menu (role, menu_id),
  KEY idx_menu_id (menu_id),
  CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id),
  CONSTRAINT chk_role_menu_role CHECK (role IN ('ADMIN', 'KEEPER', 'CUSTOMER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关系表';

CREATE TABLE sys_user_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  UNIQUE KEY uk_user_menu (user_id, menu_id),
  KEY idx_menu_id (menu_id),
  CONSTRAINT fk_user_menu_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_user_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户菜单关系表';

CREATE TABLE parrot_species (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '品种ID',
  name VARCHAR(100) NOT NULL COMMENT '品种名称',
  english_name VARCHAR(100) DEFAULT NULL COMMENT '英文名',
  origin VARCHAR(200) DEFAULT NULL COMMENT '产地',
  size VARCHAR(50) DEFAULT NULL COMMENT '体型',
  avg_lifespan VARCHAR(50) DEFAULT NULL COMMENT '平均寿命',
  habits TEXT COMMENT '生活习性',
  difficulty VARCHAR(20) DEFAULT NULL COMMENT '饲养难度',
  image VARCHAR(500) DEFAULT NULL COMMENT '品种图片',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_status (status),
  CONSTRAINT chk_species_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鹦鹉品种表';

CREATE TABLE parrot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '鹦鹉ID',
  code VARCHAR(50) NOT NULL COMMENT '档案编号',
  name VARCHAR(100) NOT NULL COMMENT '鹦鹉名称',
  species_id BIGINT NOT NULL COMMENT '品种ID',
  gender VARCHAR(10) DEFAULT NULL COMMENT '性别',
  age INT DEFAULT NULL COMMENT '年龄',
  birth_date DATE DEFAULT NULL COMMENT '出生日期',
  color VARCHAR(100) DEFAULT NULL COMMENT '羽毛颜色',
  image VARCHAR(500) DEFAULT NULL COMMENT '图片',
  health_status VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '健康状态',
  source VARCHAR(50) DEFAULT NULL COMMENT '来源',
  entry_date DATE DEFAULT NULL COMMENT '入园日期',
  personality VARCHAR(500) DEFAULT NULL COMMENT '性格特点',
  care_notes TEXT COMMENT '养护说明',
  current_status VARCHAR(20) NOT NULL DEFAULT '在园' COMMENT '当前状态',
  description TEXT COMMENT '详细介绍',
  is_public TINYINT NOT NULL DEFAULT 0 COMMENT '1公开 0不公开',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_code (code),
  KEY idx_species_id (species_id),
  KEY idx_is_public (is_public),
  KEY idx_current_status (current_status),
  CONSTRAINT fk_parrot_species FOREIGN KEY (species_id) REFERENCES parrot_species(id),
  CONSTRAINT chk_parrot_health CHECK (health_status IN ('正常', '观察中', '治疗中', '已预约', '已停用')),
  CONSTRAINT chk_parrot_public CHECK (is_public IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鹦鹉档案表';

CREATE TABLE health_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '健康记录ID',
  parrot_id BIGINT NOT NULL COMMENT '鹦鹉ID',
  record_date DATE NOT NULL COMMENT '记录日期',
  weight DECIMAL(5,2) DEFAULT NULL COMMENT '体重',
  spirit VARCHAR(50) DEFAULT NULL COMMENT '精神状态',
  appetite VARCHAR(50) DEFAULT NULL COMMENT '食欲',
  feather VARCHAR(50) DEFAULT NULL COMMENT '羽毛状态',
  excrement VARCHAR(50) DEFAULT NULL COMMENT '粪便情况',
  abnormal_symptoms TEXT COMMENT '异常症状',
  treatment TEXT COMMENT '处理或治疗情况',
  need_review TINYINT DEFAULT 0 COMMENT '是否需要复查',
  review_date DATE DEFAULT NULL COMMENT '复查日期',
  recorder_id BIGINT DEFAULT NULL COMMENT '记录人ID',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_parrot_id (parrot_id),
  KEY idx_record_date (record_date),
  KEY idx_recorder_id (recorder_id),
  CONSTRAINT fk_health_parrot FOREIGN KEY (parrot_id) REFERENCES parrot(id),
  CONSTRAINT fk_health_recorder FOREIGN KEY (recorder_id) REFERENCES sys_user(id),
  CONSTRAINT chk_health_review CHECK (need_review IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

CREATE TABLE feeding_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '喂养记录ID',
  parrot_id BIGINT NOT NULL COMMENT '鹦鹉ID',
  feeding_date DATE NOT NULL COMMENT '喂养日期',
  feeding_time TIME DEFAULT NULL COMMENT '喂养时间',
  main_food VARCHAR(100) DEFAULT NULL COMMENT '主食',
  supplement VARCHAR(100) DEFAULT NULL COMMENT '辅食',
  amount VARCHAR(50) DEFAULT NULL COMMENT '食量',
  water VARCHAR(50) DEFAULT NULL COMMENT '饮水量',
  is_finished TINYINT DEFAULT 1 COMMENT '是否吃完',
  leftover VARCHAR(100) DEFAULT NULL COMMENT '剩余情况',
  recorder_id BIGINT DEFAULT NULL COMMENT '记录人ID',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_parrot_id (parrot_id),
  KEY idx_feeding_date (feeding_date),
  KEY idx_recorder_id (recorder_id),
  CONSTRAINT fk_feeding_parrot FOREIGN KEY (parrot_id) REFERENCES parrot(id),
  CONSTRAINT fk_feeding_recorder FOREIGN KEY (recorder_id) REFERENCES sys_user(id),
  CONSTRAINT chk_feeding_finished CHECK (is_finished IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='喂养记录表';

CREATE TABLE training_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '训练记录ID',
  parrot_id BIGINT NOT NULL COMMENT '鹦鹉ID',
  training_date DATE NOT NULL COMMENT '训练日期',
  duration INT DEFAULT NULL COMMENT '训练时长，单位分钟',
  project VARCHAR(100) NOT NULL COMMENT '训练项目',
  content TEXT COMMENT '训练内容',
  cooperation VARCHAR(50) DEFAULT NULL COMMENT '配合程度',
  completion VARCHAR(50) DEFAULT NULL COMMENT '完成情况',
  reward VARCHAR(200) DEFAULT NULL COMMENT '奖励',
  next_suggestion TEXT COMMENT '下次建议',
  recorder_id BIGINT DEFAULT NULL COMMENT '记录人ID',
  remark TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_parrot_id (parrot_id),
  KEY idx_training_date (training_date),
  KEY idx_project (project),
  KEY idx_recorder_id (recorder_id),
  CONSTRAINT fk_training_parrot FOREIGN KEY (parrot_id) REFERENCES parrot(id),
  CONSTRAINT fk_training_recorder FOREIGN KEY (recorder_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练记录表';

CREATE TABLE appointment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  parrot_id BIGINT NOT NULL COMMENT '鹦鹉ID',
  appointment_date DATE NOT NULL COMMENT '预约日期',
  time_slot VARCHAR(50) NOT NULL COMMENT '时间段',
  phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  consult_type VARCHAR(50) DEFAULT NULL COMMENT '咨询类型',
  content TEXT COMMENT '咨询内容',
  status VARCHAR(20) NOT NULL DEFAULT '待处理' COMMENT '预约状态',
  handler_id BIGINT DEFAULT NULL COMMENT '处理人ID',
  handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
  result TEXT COMMENT '处理结果',
  backend_remark TEXT COMMENT '后台备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_customer_id (customer_id),
  KEY idx_status (status),
  KEY idx_parrot_id (parrot_id),
  KEY idx_handler_id (handler_id),
  CONSTRAINT fk_appointment_customer FOREIGN KEY (customer_id) REFERENCES sys_user(id),
  CONSTRAINT fk_appointment_parrot FOREIGN KEY (parrot_id) REFERENCES parrot(id),
  CONSTRAINT fk_appointment_handler FOREIGN KEY (handler_id) REFERENCES sys_user(id),
  CONSTRAINT chk_appointment_status CHECK (status IN ('待处理', '已确认', '已完成', '已取消', '已驳回'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约咨询表';

CREATE TABLE notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT NOT NULL COMMENT '内容',
  type VARCHAR(20) NOT NULL COMMENT '类型',
  cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图',
  publish_status VARCHAR(20) NOT NULL DEFAULT '未发布' COMMENT '发布状态',
  publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
  creator_id BIGINT DEFAULT NULL COMMENT '创建人ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_type (type),
  KEY idx_publish_status (publish_status),
  KEY idx_creator_id (creator_id),
  CONSTRAINT fk_notice_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id),
  CONSTRAINT chk_notice_type CHECK (type IN ('系统公告', '饲养知识')),
  CONSTRAINT chk_notice_publish CHECK (publish_status IN ('已发布', '未发布'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告知识表';

CREATE TABLE file_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
  original_name VARCHAR(200) NOT NULL COMMENT '原始文件名',
  access_url VARCHAR(500) NOT NULL COMMENT '访问地址',
  file_type VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
  file_size BIGINT DEFAULT NULL COMMENT '文件大小',
  uploader_id BIGINT DEFAULT NULL COMMENT '上传人ID',
  upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  KEY idx_uploader_id (uploader_id),
  CONSTRAINT fk_file_uploader FOREIGN KEY (uploader_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资源表';

CREATE TABLE login_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  role VARCHAR(20) DEFAULT NULL COMMENT '角色',
  ip VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  result VARCHAR(20) NOT NULL COMMENT '登录结果',
  fail_reason VARCHAR(200) DEFAULT NULL COMMENT '失败原因',
  KEY idx_username (username),
  KEY idx_result (result),
  KEY idx_login_time (login_time),
  CONSTRAINT chk_login_result CHECK (result IN ('成功', '失败'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';
