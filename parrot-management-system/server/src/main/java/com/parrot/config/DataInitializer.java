package com.parrot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.parrot.entity.Appointment;
import com.parrot.entity.FeedingRecord;
import com.parrot.entity.HealthRecord;
import com.parrot.entity.Notice;
import com.parrot.entity.Parrot;
import com.parrot.entity.ParrotSpecies;
import com.parrot.entity.SysMenu;
import com.parrot.entity.SysRoleMenu;
import com.parrot.entity.SysUser;
import com.parrot.entity.TrainingRecord;
import com.parrot.mapper.AppointmentMapper;
import com.parrot.mapper.FeedingRecordMapper;
import com.parrot.mapper.HealthRecordMapper;
import com.parrot.mapper.NoticeMapper;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.ParrotSpeciesMapper;
import com.parrot.mapper.SysMenuMapper;
import com.parrot.mapper.SysRoleMenuMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.mapper.TrainingRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 初始化演示账号，方便项目演示和答辩，正式环境可以按需要关闭。
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final AppointmentMapper appointmentMapper;
    private final ParrotSpeciesMapper speciesMapper;
    private final ParrotMapper parrotMapper;
    private final NoticeMapper noticeMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final HealthRecordMapper healthRecordMapper;
    private final FeedingRecordMapper feedingRecordMapper;
    private final TrainingRecordMapper trainingRecordMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper,
                           AppointmentMapper appointmentMapper,
                           ParrotSpeciesMapper speciesMapper,
                           ParrotMapper parrotMapper,
                           NoticeMapper noticeMapper,
                           SysMenuMapper menuMapper,
                           SysRoleMenuMapper roleMenuMapper,
                           HealthRecordMapper healthRecordMapper,
                           FeedingRecordMapper feedingRecordMapper,
                           TrainingRecordMapper trainingRecordMapper,
                           BCryptPasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.appointmentMapper = appointmentMapper;
        this.speciesMapper = speciesMapper;
        this.parrotMapper = parrotMapper;
        this.noticeMapper = noticeMapper;
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.healthRecordMapper = healthRecordMapper;
        this.feedingRecordMapper = feedingRecordMapper;
        this.trainingRecordMapper = trainingRecordMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<>());
            if (count != null && count > 0) {
                initSystemMenus();
                initBusinessDemoData();
                initDemoAppointments();
                return;
            }

            addUser("admin", "管理员", "ADMIN");
            addUser("keeper", "饲养员", "KEEPER");
            addUser("customer", "演示客户", "CUSTOMER");
            log.info("默认演示账号初始化完成：admin/keeper/customer，密码均为 admin123");
            initSystemMenus();
            initBusinessDemoData();
            initDemoAppointments();
        } catch (Exception e) {
            // 有些同学会先跑后端再建库，这里不让服务直接退出，建好库后重启即可初始化账号。
            log.warn("数据库暂时不可用，默认账号本次未初始化：{}", e.getMessage());
        }
    }

    private void addUser(String username, String realName, String role) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setRealName(realName);
        user.setRole(role);
        user.setStatus(1);
        sysUserMapper.insert(user);
    }

    private void initSystemMenus() {
        addMenuIfAbsent(1L, 0L, "dashboard", "首页看板", "MENU", "/admin/dashboard",
                "GET /api/admin/dashboard/**", "DataBoard", 10);
        addMenuIfAbsent(2L, 0L, "parrot", "鹦鹉管理", "DIR", null,
                null, "Star", 20);
        addMenuIfAbsent(3L, 2L, "parrot:list", "鹦鹉档案管理", "MENU", "/admin/parrot/list",
                "GET /api/admin/parrot/page,GET /api/admin/species/list", "Star", 21);
        addMenuIfAbsent(15L, 3L, "parrot:list:add", "新增鹦鹉", "BUTTON", null,
                "POST /api/admin/parrot", null, 211);
        addMenuIfAbsent(16L, 3L, "parrot:list:edit", "编辑鹦鹉", "BUTTON", null,
                "PUT /api/admin/parrot/**", null, 212);
        addMenuIfAbsent(17L, 3L, "parrot:list:delete", "删除鹦鹉", "BUTTON", null,
                "DELETE /api/admin/parrot/**", null, 213);
        addMenuIfAbsent(4L, 2L, "parrot:species", "品种管理", "MENU", "/admin/parrot/species",
                "GET /api/admin/species/page,GET /api/admin/species/list", "Collection", 22);
        addMenuIfAbsent(18L, 4L, "parrot:species:add", "新增品种", "BUTTON", null,
                "POST /api/admin/species", null, 221);
        addMenuIfAbsent(19L, 4L, "parrot:species:edit", "编辑品种", "BUTTON", null,
                "PUT /api/admin/species/**", null, 222);
        addMenuIfAbsent(20L, 4L, "parrot:species:delete", "删除品种", "BUTTON", null,
                "DELETE /api/admin/species/**", null, 223);
        addMenuIfAbsent(5L, 0L, "care", "养护管理", "DIR", null,
                null, "FirstAidKit", 30);
        addMenuIfAbsent(6L, 5L, "care:health", "健康记录管理", "MENU", "/admin/health",
                "GET /api/admin/health-record/page,GET /api/admin/parrot/page", "FirstAidKit", 31);
        addMenuIfAbsent(21L, 6L, "care:health:add", "新增健康记录", "BUTTON", null,
                "POST /api/admin/health-record", null, 311);
        addMenuIfAbsent(22L, 6L, "care:health:edit", "编辑健康记录", "BUTTON", null,
                "PUT /api/admin/health-record/**", null, 312);
        addMenuIfAbsent(23L, 6L, "care:health:delete", "删除健康记录", "BUTTON", null,
                "DELETE /api/admin/health-record/**", null, 313);
        addMenuIfAbsent(7L, 5L, "care:feeding", "喂养记录管理", "MENU", "/admin/feeding",
                "GET /api/admin/feeding-record/page,GET /api/admin/parrot/page", "Bowl", 32);
        addMenuIfAbsent(24L, 7L, "care:feeding:add", "新增喂养记录", "BUTTON", null,
                "POST /api/admin/feeding-record", null, 321);
        addMenuIfAbsent(25L, 7L, "care:feeding:edit", "编辑喂养记录", "BUTTON", null,
                "PUT /api/admin/feeding-record/**", null, 322);
        addMenuIfAbsent(26L, 7L, "care:feeding:delete", "删除喂养记录", "BUTTON", null,
                "DELETE /api/admin/feeding-record/**", null, 323);
        addMenuIfAbsent(8L, 5L, "care:training", "训练记录管理", "MENU", "/admin/training",
                "GET /api/admin/training-record/page,GET /api/admin/parrot/page", "Medal", 33);
        addMenuIfAbsent(27L, 8L, "care:training:add", "新增训练记录", "BUTTON", null,
                "POST /api/admin/training-record", null, 331);
        addMenuIfAbsent(28L, 8L, "care:training:edit", "编辑训练记录", "BUTTON", null,
                "PUT /api/admin/training-record/**", null, 332);
        addMenuIfAbsent(29L, 8L, "care:training:delete", "删除训练记录", "BUTTON", null,
                "DELETE /api/admin/training-record/**", null, 333);
        addMenuIfAbsent(9L, 0L, "appointment", "预约咨询管理", "MENU", "/admin/appointment",
                "GET /api/admin/appointment/page,GET /api/admin/parrot/page", "Calendar", 40);
        addMenuIfAbsent(30L, 9L, "appointment:confirm", "确认预约", "BUTTON", null,
                "PUT /api/admin/appointment/*/confirm", null, 401);
        addMenuIfAbsent(31L, 9L, "appointment:reject", "驳回预约", "BUTTON", null,
                "PUT /api/admin/appointment/*/reject", null, 402);
        addMenuIfAbsent(32L, 9L, "appointment:finish", "完成预约", "BUTTON", null,
                "PUT /api/admin/appointment/*/complete,PUT /api/admin/appointment/*/finish", null, 403);
        addMenuIfAbsent(33L, 9L, "appointment:cancel", "取消预约", "BUTTON", null,
                "PUT /api/admin/appointment/*/cancel", null, 404);
        addMenuIfAbsent(10L, 0L, "notice", "公告知识管理", "MENU", "/admin/notice",
                "GET /api/admin/notice/page", "Document", 50);
        addMenuIfAbsent(34L, 10L, "notice:add", "新增公告", "BUTTON", null,
                "POST /api/admin/notice", null, 501);
        addMenuIfAbsent(35L, 10L, "notice:edit", "编辑公告", "BUTTON", null,
                "PUT /api/admin/notice/**", null, 502);
        addMenuIfAbsent(36L, 10L, "notice:delete", "删除公告", "BUTTON", null,
                "DELETE /api/admin/notice/**", null, 503);
        addMenuIfAbsent(11L, 0L, "system", "系统管理", "DIR", null,
                null, "Setting", 90);
        addMenuIfAbsent(12L, 11L, "system:user", "用户管理", "MENU", "/admin/user",
                "GET /api/admin/user/page", "User", 91);
        addMenuIfAbsent(37L, 12L, "system:user:add", "新增用户", "BUTTON", null,
                "POST /api/admin/user", null, 911);
        addMenuIfAbsent(38L, 12L, "system:user:edit", "编辑用户", "BUTTON", null,
                "PUT /api/admin/user/*", null, 912);
        addMenuIfAbsent(39L, 12L, "system:user:disable", "禁用用户", "BUTTON", null,
                "DELETE /api/admin/user/**", null, 913);
        addMenuIfAbsent(40L, 12L, "system:user:reset", "重置密码", "BUTTON", null,
                "PUT /api/admin/user/*/reset-password", null, 914);
        addMenuIfAbsent(13L, 11L, "system:login-log", "登录日志", "MENU", "/admin/login-log",
                "GET /api/admin/login-log/page", "Tickets", 92);
        addMenuIfAbsent(14L, 11L, "system:menu", "菜单管理", "MENU", "/admin/menu",
                "GET /api/admin/menu/**", "Menu", 93);
        addMenuIfAbsent(41L, 14L, "system:menu:save", "保存菜单配置", "BUTTON", null,
                "PUT /api/admin/menu/**", null, 931);

        initRoleMenus("ADMIN", "dashboard", "parrot:list", "parrot:species", "care:health",
                "care:feeding", "care:training", "appointment", "notice", "system:user",
                "system:login-log", "system:menu",
                "parrot:list:add", "parrot:list:edit", "parrot:list:delete",
                "parrot:species:add", "parrot:species:edit", "parrot:species:delete",
                "care:health:add", "care:health:edit", "care:health:delete",
                "care:feeding:add", "care:feeding:edit", "care:feeding:delete",
                "care:training:add", "care:training:edit", "care:training:delete",
                "appointment:confirm", "appointment:reject", "appointment:finish", "appointment:cancel",
                "notice:add", "notice:edit", "notice:delete",
                "system:user:add", "system:user:edit", "system:user:disable", "system:user:reset",
                "system:menu:save");
        initRoleMenus("KEEPER", "dashboard", "parrot:list", "care:health", "care:feeding",
                "care:training", "appointment", "notice",
                "care:health:add", "care:health:edit",
                "care:feeding:add", "care:feeding:edit",
                "care:training:add", "care:training:edit",
                "appointment:confirm", "appointment:finish",
                "notice:add", "notice:edit");
    }

    private void addMenuIfAbsent(Long id, Long parentId, String code, String name, String type,
                                 String path, String apiPattern, String icon, Integer sortNo) {
        SysMenu old = findMenu(code);
        if (old != null) {
            old.setParentId(parentId);
            old.setMenuName(name);
            old.setMenuType(type);
            old.setPath(path);
            old.setApiPattern(apiPattern);
            old.setIcon(icon);
            old.setSortNo(sortNo);
            old.setStatus(1);
            menuMapper.updateById(old);
            return;
        }
        SysMenu menu = new SysMenu();
        menu.setId(id);
        menu.setParentId(parentId);
        menu.setMenuCode(code);
        menu.setMenuName(name);
        menu.setMenuType(type);
        menu.setPath(path);
        menu.setApiPattern(apiPattern);
        menu.setIcon(icon);
        menu.setSortNo(sortNo);
        menu.setStatus(1);
        menuMapper.insert(menu);
    }

    private void initRoleMenus(String role, String... menuCodes) {
        for (String code : menuCodes) {
            SysMenu menu = findMenu(code);
            if (menu == null) {
                continue;
            }
            Long count = roleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRole, role)
                    .eq(SysRoleMenu::getMenuId, menu.getId()));
            if (count != null && count > 0) {
                continue;
            }
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRole(role);
            roleMenu.setMenuId(menu.getId());
            roleMenuMapper.insert(roleMenu);
        }
    }

    private void initBusinessDemoData() {
        initSpecies();
        initParrots();
        initNotices();
        initCareRecords();
    }

    private void initSpecies() {
        addSpeciesIfAbsent("虎皮鹦鹉", "Budgerigar", "澳大利亚", "小型", "7-10年",
                "性格活泼，喜欢结伴活动，适合做入门饲养展示。", "简单", "/upload/demo/species-budgie.jpg");
        addSpeciesIfAbsent("玄凤鹦鹉", "Cockatiel", "澳大利亚", "中小型", "10-15年",
                "亲人温顺，冠羽明显，适合互动训练。", "中等", "/upload/demo/species-cockatiel.jpg");
        addSpeciesIfAbsent("和尚鹦鹉", "Monk Parakeet", "南美洲", "中型", "15-20年",
                "学习能力较强，叫声清亮，需要稳定陪伴。", "中等", "/upload/demo/species-monk.jpg");
    }

    private void addSpeciesIfAbsent(String name, String englishName, String origin, String size,
                                    String lifespan, String habits, String difficulty, String image) {
        if (findSpecies(name) != null) {
            return;
        }
        ParrotSpecies species = new ParrotSpecies();
        species.setName(name);
        species.setEnglishName(englishName);
        species.setOrigin(origin);
        species.setSize(size);
        species.setAvgLifespan(lifespan);
        species.setHabits(habits);
        species.setDifficulty(difficulty);
        species.setImage(image);
        species.setStatus(1);
        speciesMapper.insert(species);
    }

    private void initParrots() {
        ParrotSpecies budgie = findSpecies("虎皮鹦鹉");
        ParrotSpecies cockatiel = findSpecies("玄凤鹦鹉");
        ParrotSpecies monk = findSpecies("和尚鹦鹉");
        if (budgie == null || cockatiel == null || monk == null) {
            return;
        }
        addParrotIfAbsent("PAR-2026-001", "青团", budgie.getId(), "雄", 2, "2024-04-12", "黄绿",
                "/upload/demo/parrot-qingtuan.jpg", "正常", "园区繁育", "2024-08-01",
                "胆子大，看到人会主动靠近。", "每天观察食欲和饮水，换羽期补充青菜。", "在园",
                "一只很适合前台展示的虎皮鹦鹉，状态稳定，互动性好。", 1);
        addParrotIfAbsent("PAR-2026-002", "小梨", cockatiel.getId(), "雌", 3, "2023-03-20", "灰白",
                "/upload/demo/parrot-xiaoli.jpg", "正常", "救助转入", "2024-01-15",
                "比较安静，喜欢停在高处观察环境。", "训练前先给一点适应时间，不要突然靠近。", "在园",
                "玄凤鹦鹉小梨性格温顺，适合做预约咨询展示。", 1);
        addParrotIfAbsent("PAR-2026-003", "阿木", monk.getId(), "雄", 4, "2022-07-08", "绿色",
                "/upload/demo/parrot-amu.jpg", "观察中", "外部引进", "2025-02-10",
                "好奇心强，偶尔会啃咬笼具。", "最近需要留意精神状态，减少强度较高的训练。", "在园",
                "和尚鹦鹉阿木表达欲强，当前处于观察中，暂不做重点预约推荐。", 0);
        addParrotIfAbsent("PAR-2026-004", "米粒", budgie.getId(), "雌", 1, "2025-05-18", "蓝白",
                "/upload/demo/parrot-mili.jpg", "正常", "园区繁育", "2025-09-03",
                "活泼亲人，喜欢短时间互动。", "幼鸟阶段注意少量多次喂食，训练时间不要太长。", "在园",
                "米粒适合做新手饲养咨询展示，状态稳定。", 1);
        addParrotIfAbsent("PAR-2026-005", "橙子", cockatiel.getId(), "雄", 5, "2021-11-02", "黄灰",
                "/upload/demo/parrot-chengzi.jpg", "治疗中", "救助转入", "2024-11-20",
                "警惕性较高，需要慢慢建立信任。", "近期按医嘱观察羽毛和食欲，暂不安排预约。", "在园",
                "橙子正在恢复观察中，适合演示健康状态和养护记录。", 0);
    }

    private void addParrotIfAbsent(String code, String name, Long speciesId, String gender, Integer age,
                                   String birthDate, String color, String image, String healthStatus,
                                   String source, String entryDate, String personality, String careNotes,
                                   String currentStatus, String description, Integer isPublic) {
        if (findParrot(code) != null) {
            return;
        }
        Parrot parrot = new Parrot();
        parrot.setCode(code);
        parrot.setName(name);
        parrot.setSpeciesId(speciesId);
        parrot.setGender(gender);
        parrot.setAge(age);
        parrot.setBirthDate(LocalDate.parse(birthDate));
        parrot.setColor(color);
        parrot.setImage(image);
        parrot.setHealthStatus(healthStatus);
        parrot.setSource(source);
        parrot.setEntryDate(LocalDate.parse(entryDate));
        parrot.setPersonality(personality);
        parrot.setCareNotes(careNotes);
        parrot.setCurrentStatus(currentStatus);
        parrot.setDescription(description);
        parrot.setIsPublic(isPublic);
        parrotMapper.insert(parrot);
    }

    private void initNotices() {
        addNoticeIfAbsent("园区开放预约咨询服务", "园区已开放鹦鹉预约咨询服务，客户可在前台选择感兴趣的鹦鹉并提交咨询预约。工作人员会在后台统一处理。",
                "系统公告", "/upload/demo/notice-open.jpg", "已发布");
        addNoticeIfAbsent("换羽期的基础照护建议", "换羽期要保持环境安静，注意补充新鲜蔬果和干净饮水。若出现精神差、食欲明显下降等情况，应及时记录并安排复查。",
                "饲养知识", "/upload/demo/notice-care.jpg", "已发布");
    }

    private void addNoticeIfAbsent(String title, String content, String type, String image, String status) {
        Notice old = noticeMapper.selectOne(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getTitle, title)
                .last("limit 1"));
        if (old != null) {
            return;
        }
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType(type);
        notice.setCoverImage(image);
        notice.setPublishStatus(status);
        notice.setPublishTime("已发布".equals(status) ? LocalDateTime.now() : null);
        noticeMapper.insert(notice);
    }

    private void initCareRecords() {
        if (healthRecordMapper.selectCount(new LambdaQueryWrapper<>()) == 0) {
            addHealth("PAR-2026-001", BigDecimal.valueOf(35.20), "活跃", "正常", null, "日常检查状态稳定。");
            addHealth("PAR-2026-003", BigDecimal.valueOf(98.50), "略低", "一般", "精神偏低，粪便偏稀", "用于演示异常健康记录。");
            addHealth("PAR-2026-005", BigDecimal.valueOf(86.10), "一般", "偏少", "局部掉羽明显", "治疗中鹦鹉的跟踪记录。");
        }
        if (feedingRecordMapper.selectCount(new LambdaQueryWrapper<>()) == 0) {
            addFeeding("PAR-2026-001", "混合谷物", "青菜", "约25g", 1, "早间喂养正常。");
            addFeeding("PAR-2026-002", "鹦鹉粮", "苹果小块", "约30g", 1, "食欲稳定。");
        }
        if (trainingRecordMapper.selectCount(new LambdaQueryWrapper<>()) == 0) {
            addTraining("PAR-2026-001", "上手训练", 15, "较好", "完成");
            addTraining("PAR-2026-002", "召回训练", 10, "一般", "部分完成");
        }
    }

    private void addHealth(String code, BigDecimal weight, String spirit, String appetite, String abnormal, String remark) {
        Parrot parrot = findParrot(code);
        if (parrot == null) {
            return;
        }
        HealthRecord record = new HealthRecord();
        record.setParrotId(parrot.getId());
        record.setRecordDate(LocalDate.now());
        record.setWeight(weight);
        record.setSpirit(spirit);
        record.setAppetite(appetite);
        record.setFeather("正常");
        record.setExcrement("正常");
        record.setAbnormalSymptoms(abnormal);
        record.setTreatment(abnormal == null ? null : "减少训练，安排复查观察。");
        record.setNeedReview(abnormal == null ? 0 : 1);
        record.setReviewDate(abnormal == null ? null : LocalDate.now().plusDays(3));
        record.setRemark(remark);
        healthRecordMapper.insert(record);
    }

    private void addFeeding(String code, String mainFood, String supplement, String amount, Integer finished, String remark) {
        Parrot parrot = findParrot(code);
        if (parrot == null) {
            return;
        }
        FeedingRecord record = new FeedingRecord();
        record.setParrotId(parrot.getId());
        record.setFeedingDate(LocalDate.now());
        record.setFeedingTime(LocalTime.of(8, 30));
        record.setMainFood(mainFood);
        record.setSupplement(supplement);
        record.setAmount(amount);
        record.setWater("正常");
        record.setIsFinished(finished);
        record.setLeftover(finished == 1 ? "基本吃完" : "剩余较多");
        record.setRemark(remark);
        feedingRecordMapper.insert(record);
    }

    private void addTraining(String code, String project, Integer duration, String cooperation, String completion) {
        Parrot parrot = findParrot(code);
        if (parrot == null) {
            return;
        }
        TrainingRecord record = new TrainingRecord();
        record.setParrotId(parrot.getId());
        record.setTrainingDate(LocalDate.now());
        record.setDuration(duration);
        record.setProject(project);
        record.setContent("用于毕业设计演示的日常训练记录。");
        record.setCooperation(cooperation);
        record.setCompletion(completion);
        record.setReward("少量谷穗");
        record.setNextSuggestion("保持低强度重复训练。");
        trainingRecordMapper.insert(record);
    }

    private void initDemoAppointments() {
        Long count = appointmentMapper.selectCount(new LambdaQueryWrapper<>());
        if (count != null && count > 0) {
            return;
        }
        SysUser customer = findUser("customer");
        SysUser keeper = findUser("keeper");
        if (customer == null) {
            return;
        }
        Parrot first = findParrot("PAR-2026-001");
        Parrot second = findParrot("PAR-2026-002");
        if (first == null || second == null) {
            return;
        }

        addAppointment(customer.getId(), first.getId(), "待处理", LocalDate.now().plusDays(1), "09:00-10:00", null);
        addAppointment(customer.getId(), second.getId(), "已确认", LocalDate.now().plusDays(2), "14:00-15:00", keeper);
        addAppointment(customer.getId(), first.getId(), "已完成", LocalDate.now().minusDays(1), "10:00-11:00", keeper);
        log.info("预约咨询演示数据初始化完成");
    }

    private SysUser findUser(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
    }

    private ParrotSpecies findSpecies(String name) {
        return speciesMapper.selectOne(new LambdaQueryWrapper<ParrotSpecies>()
                .eq(ParrotSpecies::getName, name)
                .last("limit 1"));
    }

    private Parrot findParrot(String code) {
        return parrotMapper.selectOne(new LambdaQueryWrapper<Parrot>()
                .eq(Parrot::getCode, code)
                .last("limit 1"));
    }

    private SysMenu findMenu(String code) {
        return menuMapper.selectOne(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuCode, code)
                .last("limit 1"));
    }

    private void addAppointment(Long customerId,
                                Long parrotId,
                                String status,
                                LocalDate date,
                                String timeSlot,
                                SysUser handler) {
        Appointment appointment = new Appointment();
        appointment.setCustomerId(customerId);
        appointment.setParrotId(parrotId);
        appointment.setAppointmentDate(date);
        appointment.setTimeSlot(timeSlot);
        appointment.setPhone("13800000000");
        appointment.setEmail("customer@example.com");
        appointment.setConsultType("饲养咨询");
        appointment.setContent("想了解这只鹦鹉的日常照护和互动方式。");
        appointment.setStatus(status);
        if (handler != null) {
            appointment.setHandlerId(handler.getId());
            appointment.setHandleTime(LocalDateTime.now());
        }
        if ("已完成".equals(status)) {
            appointment.setResult("已电话沟通，建议先从日常喂养和环境适应开始。");
            appointment.setBackendRemark("演示用已完成预约。");
        }
        appointmentMapper.insert(appointment);
    }
}
