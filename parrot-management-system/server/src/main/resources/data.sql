USE parrot_management;

SET NAMES utf8mb4;

INSERT INTO parrot_species
(id, name, english_name, origin, size, avg_lifespan, habits, difficulty, image, status, create_time, update_time)
VALUES
(1, '虎皮鹦鹉', 'Budgerigar', '澳大利亚', '小型', '7-10年', '性格活泼，喜欢结伴活动，适合做入门饲养展示。', '简单', '/upload/demo/species-budgie.jpg', 1, NOW(), NOW()),
(2, '玄凤鹦鹉', 'Cockatiel', '澳大利亚', '中小型', '10-15年', '亲人温顺，冠羽明显，适合互动训练。', '中等', '/upload/demo/species-cockatiel.jpg', 1, NOW(), NOW()),
(3, '和尚鹦鹉', 'Monk Parakeet', '南美洲', '中型', '15-20年', '学习能力较强，叫声清亮，需要稳定陪伴。', '中等', '/upload/demo/species-monk.jpg', 1, NOW(), NOW());

INSERT INTO parrot
(id, code, name, species_id, gender, age, birth_date, color, image, health_status, source, entry_date, personality, care_notes, current_status, description, is_public, create_time, update_time)
VALUES
(1, 'PAR-2026-001', '青团', 1, '雄', 2, '2024-04-12', '黄绿', '/upload/demo/parrot-qingtuan.jpg', '正常', '园区繁育', '2024-08-01', '胆子大，看到人会主动靠近。', '每天观察食欲和饮水，换羽期补充青菜。', '在园', '一只很适合前台展示的虎皮鹦鹉，状态稳定，互动性好。', 1, NOW(), NOW()),
(2, 'PAR-2026-002', '小梨', 2, '雌', 3, '2023-03-20', '灰白', '/upload/demo/parrot-xiaoli.jpg', '正常', '救助转入', '2024-01-15', '比较安静，喜欢停在高处观察环境。', '训练前先给一点适应时间，不要突然靠近。', '在园', '玄凤鹦鹉小梨性格温顺，适合做预约咨询展示。', 1, NOW(), NOW()),
(3, 'PAR-2026-003', '阿木', 3, '雄', 4, '2022-07-08', '绿色', '/upload/demo/parrot-amu.jpg', '观察中', '外部引进', '2025-02-10', '好奇心强，偶尔会啃咬笼具。', '最近需要留意精神状态，减少强度较高的训练。', '在园', '和尚鹦鹉阿木表达欲强，当前处于观察中，暂不做重点预约推荐。', 0, NOW(), NOW());

INSERT INTO notice
(id, title, content, type, cover_image, publish_status, publish_time, creator_id, create_time, update_time)
VALUES
(1, '园区开放预约咨询服务', '园区已开放鹦鹉预约咨询服务，客户可在前台选择感兴趣的鹦鹉并提交咨询预约。工作人员会在后台统一处理。', '系统公告', '/upload/demo/notice-open.jpg', '已发布', NOW(), NULL, NOW(), NOW()),
(2, '换羽期的基础照护建议', '换羽期要保持环境安静，注意补充新鲜蔬果和干净饮水。若出现精神差、食欲明显下降等情况，应及时记录并安排复查。', '饲养知识', '/upload/demo/notice-care.jpg', '已发布', NOW(), NULL, NOW(), NOW()),
(3, '后台演示用未发布公告', '这条公告用于后台发布状态演示，前台公开列表中不应该显示。', '系统公告', NULL, '未发布', NULL, NULL, NOW(), NOW());
