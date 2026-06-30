"""
生成鹦鹉管理系统演示数据。

数据来源尽量选公开接口：
1. GBIF Species API：获取鹦形目下的真实物种学名和英文俗名。
2. Wikimedia Commons API：获取可公开访问的图片地址。

脚本只保存文字资料和图片 URL，不下载图片，不绕过登录，也不抓取页面正文。
网络不稳定时会使用下面的兜底资料，保证 data.sql 仍然能生成。
"""
from __future__ import annotations

import json
import random
import re
import sys
import time
import urllib.parse
import urllib.request
from datetime import date, datetime, timedelta
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
OUTPUT_SQL = ROOT / "server" / "src" / "main" / "resources" / "data.sql"

USER_AGENT = "parrot-management-demo/1.0 (student project demo data crawler)"
RANDOM_SEED = 20260630

SPECIES_FALLBACK = [
    ("虎皮鹦鹉", "Budgerigar", "Melopsittacus undulatus", "澳大利亚", "小型", "7-10年", "入门"),
    ("玄凤鹦鹉", "Cockatiel", "Nymphicus hollandicus", "澳大利亚", "中小型", "10-15年", "入门"),
    ("和尚鹦鹉", "Monk Parakeet", "Myiopsitta monachus", "南美洲", "中型", "15-20年", "中等"),
    ("牡丹鹦鹉", "Lovebird", "Agapornis roseicollis", "非洲西南部", "小型", "10-15年", "中等"),
    ("金刚鹦鹉", "Macaw", "Ara ararauna", "中南美洲", "大型", "40-60年", "较难"),
    ("葵花凤头鹦鹉", "Sulphur-crested Cockatoo", "Cacatua galerita", "澳大利亚、新几内亚", "大型", "40-70年", "较难"),
    ("亚马逊鹦鹉", "Amazon Parrot", "Amazona aestiva", "南美洲", "中大型", "30-50年", "较难"),
    ("折衷鹦鹉", "Eclectus Parrot", "Eclectus roratus", "新几内亚、澳大利亚东北部", "中大型", "30-40年", "中等"),
    ("灰鹦鹉", "African Grey Parrot", "Psittacus erithacus", "非洲中西部", "中型", "40-60年", "较难"),
    ("小太阳鹦鹉", "Sun Parakeet", "Aratinga solstitialis", "南美洲东北部", "中小型", "20-30年", "中等"),
    ("凯克鹦鹉", "Caique", "Pionites melanocephalus", "南美洲北部", "中型", "25-35年", "中等"),
    ("塞内加尔鹦鹉", "Senegal Parrot", "Poicephalus senegalus", "西非", "中小型", "25-30年", "中等"),
    ("红领绿鹦鹉", "Rose-ringed Parakeet", "Psittacula krameri", "非洲、南亚", "中型", "20-30年", "中等"),
    ("绯胸鹦鹉", "Red-breasted Parakeet", "Psittacula alexandri", "南亚、东南亚", "中型", "20-25年", "中等"),
    ("红额鹦鹉", "Kakariki", "Cyanoramphus novaezelandiae", "新西兰", "小型", "10-15年", "入门"),
    ("紫蓝金刚鹦鹉", "Hyacinth Macaw", "Anodorhynchus hyacinthinus", "巴西、玻利维亚、巴拉圭", "大型", "50-70年", "较难"),
]

COMMONS_FALLBACK_IMAGES = [
    "https://upload.wikimedia.org/wikipedia/commons/5/5d/189_Red-and-green_macaw_couple_flying_in_Chapada_dos_Guimar%C3%A3es_National_Park_Photo_by_Giles_Laurent.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/0/0c/Ara_ararauna_Luc_Viatour.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/9/9b/Melopsittacus_undulatus_-pet-8a.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/8/83/Nymphicus_hollandicus_-perching_on_branch-8a.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/9/9b/Psittacus_erithacus_-perching_on_tray-8d.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/5/5f/Cacatua_galerita_-perching_on_branch-8a.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/3/32/Amazona_aestiva_-Brazil-8.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/6/66/Eclectus_roratus_-upper_body-8a.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/1/1d/Agapornis_roseicollis_-Arizona_-USA-8.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/3/33/Aratinga_solstitialis_-two_captive-8a.jpg",
]

SKIP_IMAGE_WORDS = (
    "parrot_gb",
    "parrot_nes",
    "pocket_example",
    "vase_of_flowers",
    "bhl",
)

CN_PREFIX = ["青", "蓝", "橙", "小", "阿", "米", "豆", "栗", "星", "云", "暖", "墨", "团", "可", "安", "七"]
CN_SUFFIX = ["团", "豆", "梨", "米", "宝", "栗", "星", "云", "木", "橙", "羽", "可", "安", "点", "芽", "森"]


def http_json(url: str, params: dict[str, object] | None = None) -> dict:
    if params:
        url = f"{url}?{urllib.parse.urlencode(params)}"
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(request, timeout=20) as response:
        return json.loads(response.read().decode("utf-8"))


def clean_text(value: str | None) -> str:
    if not value:
        return ""
    value = re.sub(r"<[^>]+>", "", value)
    return value.replace("&quot;", '"').replace("&amp;", "&").strip()


def fetch_gbif_species() -> dict[str, str]:
    result: dict[str, str] = {}
    for _, _, scientific_name, *_ in SPECIES_FALLBACK:
        try:
            data = http_json(
                "https://api.gbif.org/v1/species/match",
                {"name": scientific_name, "rank": "SPECIES"},
            )
            if data.get("matchType") in ("EXACT", "HIGHERRANK") and data.get("canonicalName"):
                result[scientific_name] = data["canonicalName"]
            time.sleep(0.15)
        except Exception as exc:
            print(f"GBIF 校验失败：{scientific_name}，原因：{exc}")
    return result


def fetch_commons_images(search_words: list[str], limit: int = 40) -> list[str]:
    images: list[str] = []
    seen = set()
    for word in search_words:
        try:
            data = http_json(
                "https://commons.wikimedia.org/w/api.php",
                {
                    "action": "query",
                    "generator": "search",
                    "gsrsearch": f"file:{word} parrot",
                    "gsrnamespace": 6,
                    "gsrlimit": 8,
                    "prop": "imageinfo",
                    "iiprop": "url|extmetadata",
                    "format": "json",
                    "origin": "*",
                },
            )
            pages = data.get("query", {}).get("pages", {})
            for page in pages.values():
                info = (page.get("imageinfo") or [{}])[0]
                url = info.get("url")
                if not url or url in seen:
                    continue
                lower = url.lower()
                if any(word in lower for word in SKIP_IMAGE_WORDS):
                    continue
                if not lower.endswith((".jpg", ".jpeg", ".png", ".webp")):
                    continue
                meta = info.get("extmetadata") or {}
                license_name = clean_text((meta.get("LicenseShortName") or {}).get("value"))
                # Commons 图片通常是自由许可，这里排除明确限制使用的文件。
                if "fair use" in license_name.lower() or "non-free" in license_name.lower():
                    continue
                seen.add(url)
                images.append(url)
                if len(images) >= limit:
                    return images
            time.sleep(0.2)
        except Exception as exc:
            print(f"Wikimedia 图片获取失败：{word}，原因：{exc}")
    final_images = []
    for url in COMMONS_FALLBACK_IMAGES + images:
        if url not in final_images:
            final_images.append(url)
    return final_images[:limit]


def sql_value(value) -> str:
    if value is None:
        return "NULL"
    if isinstance(value, (int, float)):
        return str(value)
    text = str(value).replace("\\", "\\\\").replace("'", "''")
    return f"'{text}'"


def insert_sql(table: str, columns: list[str], rows: list[tuple]) -> str:
    if not rows:
        return ""
    lines = [f"INSERT INTO {table}", f"({', '.join(columns)})", "VALUES"]
    value_lines = []
    for row in rows:
        value_lines.append("(" + ", ".join(sql_value(value) for value in row) + ")")
    lines.append(",\n".join(value_lines) + ";")
    return "\n".join(lines) + "\n"


def build_species(verified_names: dict[str, str], images: list[str]) -> list[dict]:
    species = []
    for idx, base in enumerate(SPECIES_FALLBACK):
        name, english, scientific, origin, size, lifespan, difficulty = base
        scientific_name = verified_names.get(scientific, scientific)
        habits = (
            f"{name}资料参考公开物种库整理，学名为 {scientific_name}。"
            f"日常活动需要稳定光照、清洁饮水和适量互动，饲养时要注意噪声、啃咬和换羽期观察。"
        )
        species.append(
            {
                "id": idx + 1,
                "name": name,
                "english_name": english,
                "origin": origin,
                "size": size,
                "avg_lifespan": lifespan,
                "habits": habits,
                "difficulty": difficulty,
                "image": images[idx % len(images)] if images else "",
                "status": 1,
            }
        )
    return species


def build_parrots(species: list[dict], images: list[str]) -> list[dict]:
    parrots = []
    colors = ["黄绿", "蓝白", "灰白", "绿色", "橙黄", "红绿", "蓝黄金", "浅灰", "白黄", "深蓝"]
    sources = ["园区繁育", "救助转入", "外部引进", "合作机构转入"]
    personalities = ["亲人稳定", "活泼好奇", "比较谨慎", "喜欢观察环境", "胆子较大", "训练积极", "安静温顺"]
    notes = ["保持笼舍干燥，每日更换饮水。", "换羽期补充青菜和少量水果。", "训练前先给适应时间，避免突然靠近。", "留意啃咬行为，准备可替换玩具。"]
    health_statuses = ["正常"] * 42 + ["观察中"] * 10 + ["治疗中"] * 5 + ["已预约"] * 3
    current_statuses = ["在园"] * 50 + ["观察中"] * 5 + ["已预约"] * 5
    used_names = set()
    today = date(2026, 6, 30)

    for idx in range(60):
        species_item = species[idx % len(species)]
        name = f"{random.choice(CN_PREFIX)}{random.choice(CN_SUFFIX)}"
        while name in used_names:
            name = f"{random.choice(CN_PREFIX)}{random.choice(CN_SUFFIX)}{idx % 10}"
        used_names.add(name)
        age = random.randint(1, 12)
        birth = today - timedelta(days=age * 365 + random.randint(0, 260))
        entry = today - timedelta(days=random.randint(35, 900))
        health = health_statuses[idx]
        status = current_statuses[idx]
        is_public = 1 if health == "正常" and status != "已停用" and idx % 4 != 0 else 0
        parrots.append(
            {
                "id": idx + 1,
                "code": f"PAR-2026-{idx + 1:03d}",
                "name": name,
                "species_id": species_item["id"],
                "gender": random.choice(["雄性", "雌性", "未知"]),
                "age": age,
                "birth_date": birth.isoformat(),
                "color": random.choice(colors),
                "image": images[(idx + 5) % len(images)] if images else species_item["image"],
                "health_status": health,
                "source": random.choice(sources),
                "entry_date": entry.isoformat(),
                "personality": f"{random.choice(personalities)}，对熟悉的饲养员反应较好。",
                "care_notes": random.choice(notes),
                "current_status": status,
                "description": f"{name}是园区登记的{species_item['name']}，适合用于档案、预约和养护记录演示。",
                "is_public": is_public,
            }
        )
    return parrots


def build_care_rows(parrots: list[dict]):
    health_rows, feeding_rows, training_rows = [], [], []
    today = date(2026, 6, 30)
    food_list = ["混合谷物", "鹦鹉颗粒粮", "谷穗", "软食", "低脂种子配方"]
    supplement_list = ["青菜", "苹果小块", "胡萝卜丁", "维生素水", "少量坚果"]
    projects = ["上手训练", "召回训练", "定点停留", "口令回应", "环境适应"]

    for idx, parrot in enumerate(parrots[:45], start=1):
        abnormal = None
        treatment = None
        need_review = 0
        review_date = None
        if parrot["health_status"] in ("观察中", "治疗中") or idx % 11 == 0:
            abnormal = random.choice(["精神略低，需继续观察", "粪便偏稀", "换羽期羽毛略凌乱", "食欲比平时偏少"])
            treatment = "减少训练强度，连续记录饮食和精神状态。"
            need_review = 1
            review_date = (today + timedelta(days=random.randint(2, 7))).isoformat()
        health_rows.append(
            (
                idx,
                parrot["id"],
                (today - timedelta(days=random.randint(0, 28))).isoformat(),
                round(random.uniform(32, 980), 2),
                random.choice(["活跃", "正常", "一般", "略低"]),
                random.choice(["正常", "较好", "一般", "偏少"]),
                random.choice(["顺滑", "正常", "轻微凌乱", "换羽中"]),
                random.choice(["正常", "偏稀", "成形"]),
                abnormal,
                treatment,
                need_review,
                review_date,
                None,
                "日常巡检记录，便于后台健康模块演示。",
                "2026-06-30 09:00:00",
                "2026-06-30 09:00:00",
            )
        )

    for idx, parrot in enumerate(parrots[:50], start=1):
        finished = 0 if idx % 13 == 0 else 1
        feeding_rows.append(
            (
                idx,
                parrot["id"],
                (today - timedelta(days=random.randint(0, 20))).isoformat(),
                random.choice(["08:30:00", "09:00:00", "16:30:00", "17:20:00"]),
                random.choice(food_list),
                random.choice(supplement_list),
                f"约{random.randint(18, 55)}g",
                random.choice(["正常", "已更换", "偏少"]),
                finished,
                "基本吃完" if finished else "剩余较多",
                None,
                "按日常标准完成喂养登记。",
                "2026-06-30 10:00:00",
                "2026-06-30 10:00:00",
            )
        )

    for idx, parrot in enumerate(parrots[:32], start=1):
        project = random.choice(projects)
        training_rows.append(
            (
                idx,
                parrot["id"],
                (today - timedelta(days=random.randint(0, 25))).isoformat(),
                random.randint(8, 30),
                project,
                f"围绕{project}进行低强度重复练习，观察情绪和配合度。",
                random.choice(["很好", "较好", "一般", "需要引导"]),
                random.choice(["完成", "部分完成", "未完成"]),
                random.choice(["少量谷穗", "葵花籽", "口头奖励", "短暂休息"]),
                "下次继续保持短时间、多次数训练。",
                None,
                "训练记录用于展示日常行为管理过程。",
                "2026-06-30 11:00:00",
                "2026-06-30 11:00:00",
            )
        )
    return health_rows, feeding_rows, training_rows


def build_notices(images: list[str]) -> list[tuple]:
    items = [
        ("园区开放预约咨询服务", "客户可在前台选择公开展示的鹦鹉并提交咨询预约，工作人员会在后台统一处理。", "系统公告"),
        ("换羽期的基础照护建议", "换羽期要保持环境安静，注意补充新鲜蔬果和干净饮水，并持续观察精神状态。", "饲养知识"),
        ("新鹦鹉入园后的隔离观察", "新入园个体建议先进行隔离观察，记录体重、食欲和粪便情况，再逐步安排展示。", "饲养知识"),
        ("夏季笼舍通风提醒", "高温天气要注意通风降温，避免阳光长时间直晒，同时保持饮水清洁。", "系统公告"),
        ("训练时长不宜过长", "单次训练建议控制在十到三十分钟内，以正向奖励为主，避免强迫互动。", "饲养知识"),
        ("预约咨询材料准备", "客户预约前可先准备家庭环境、饲养经验和期望互动方式，便于工作人员给出建议。", "系统公告"),
        ("治疗中个体暂不开放展示", "处于治疗中或观察中的鹦鹉暂不开放前台预约，以保证动物福利和客户安全。", "系统公告"),
        ("后台演示用未发布公告", "这条公告用于演示未发布状态，前台公开列表中不会显示。", "系统公告"),
    ]
    rows = []
    for idx, (title, content, notice_type) in enumerate(items, start=1):
        status = "未发布" if idx == len(items) else "已发布"
        publish_time = None if status == "未发布" else f"2026-06-{20 + idx:02d} 09:00:00"
        rows.append(
            (
                idx,
                title,
                content,
                notice_type,
                images[(idx + 2) % len(images)] if images else None,
                status,
                publish_time,
                None,
                "2026-06-30 08:00:00",
                "2026-06-30 08:00:00",
            )
        )
    return rows


def build_sql() -> str:
    random.seed(RANDOM_SEED)
    verified_species = fetch_gbif_species()
    search_words = ["macaw", "budgerigar", "cockatiel", "lovebird", "cockatoo", "amazon parrot", "african grey"]
    images = fetch_commons_images(search_words)
    species = build_species(verified_species, images)
    parrots = build_parrots(species, images)
    health_rows, feeding_rows, training_rows = build_care_rows(parrots)
    notice_rows = build_notices(images)

    menu_rows = [
        (1, 0, "dashboard", "首页看板", "MENU", "/admin/dashboard", "GET /api/admin/dashboard/**", "DataBoard", 10, 1),
        (2, 0, "parrot", "鹦鹉管理", "DIR", None, None, "Star", 20, 1),
        (3, 2, "parrot:list", "鹦鹉档案管理", "MENU", "/admin/parrot/list", "GET /api/admin/parrot/page,GET /api/admin/species/list", "Star", 21, 1),
        (4, 2, "parrot:species", "品种管理", "MENU", "/admin/parrot/species", "GET /api/admin/species/page,GET /api/admin/species/list", "Collection", 22, 1),
        (5, 0, "care", "养护管理", "DIR", None, None, "FirstAidKit", 30, 1),
        (6, 5, "care:health", "健康记录管理", "MENU", "/admin/health", "GET /api/admin/health-record/page,GET /api/admin/parrot/page", "FirstAidKit", 31, 1),
        (7, 5, "care:feeding", "喂养记录管理", "MENU", "/admin/feeding", "GET /api/admin/feeding-record/page,GET /api/admin/parrot/page", "Bowl", 32, 1),
        (8, 5, "care:training", "训练记录管理", "MENU", "/admin/training", "GET /api/admin/training-record/page,GET /api/admin/parrot/page", "Medal", 33, 1),
        (9, 0, "appointment", "预约咨询管理", "MENU", "/admin/appointment", "GET /api/admin/appointment/page,GET /api/admin/parrot/page", "Calendar", 40, 1),
        (10, 0, "notice", "公告知识管理", "MENU", "/admin/notice", "GET /api/admin/notice/page", "Document", 50, 1),
        (11, 0, "system", "系统管理", "DIR", None, None, "Setting", 90, 1),
        (12, 11, "system:user", "用户管理", "MENU", "/admin/user", "GET /api/admin/user/page", "User", 91, 1),
        (13, 11, "system:login-log", "登录日志", "MENU", "/admin/login-log", "GET /api/admin/login-log/page", "Tickets", 92, 1),
        (14, 11, "system:menu", "菜单管理", "MENU", "/admin/menu", "GET /api/admin/menu/**", "Menu", 93, 1),
        (15, 3, "parrot:list:add", "新增鹦鹉", "BUTTON", None, "POST /api/admin/parrot", None, 211, 1),
        (16, 3, "parrot:list:edit", "编辑鹦鹉", "BUTTON", None, "PUT /api/admin/parrot/**", None, 212, 1),
        (17, 3, "parrot:list:delete", "删除鹦鹉", "BUTTON", None, "DELETE /api/admin/parrot/**", None, 213, 1),
        (18, 4, "parrot:species:add", "新增品种", "BUTTON", None, "POST /api/admin/species", None, 221, 1),
        (19, 4, "parrot:species:edit", "编辑品种", "BUTTON", None, "PUT /api/admin/species/**", None, 222, 1),
        (20, 4, "parrot:species:delete", "删除品种", "BUTTON", None, "DELETE /api/admin/species/**", None, 223, 1),
        (21, 6, "care:health:add", "新增健康记录", "BUTTON", None, "POST /api/admin/health-record", None, 311, 1),
        (22, 6, "care:health:edit", "编辑健康记录", "BUTTON", None, "PUT /api/admin/health-record/**", None, 312, 1),
        (23, 6, "care:health:delete", "删除健康记录", "BUTTON", None, "DELETE /api/admin/health-record/**", None, 313, 1),
        (24, 7, "care:feeding:add", "新增喂养记录", "BUTTON", None, "POST /api/admin/feeding-record", None, 321, 1),
        (25, 7, "care:feeding:edit", "编辑喂养记录", "BUTTON", None, "PUT /api/admin/feeding-record/**", None, 322, 1),
        (26, 7, "care:feeding:delete", "删除喂养记录", "BUTTON", None, "DELETE /api/admin/feeding-record/**", None, 323, 1),
        (27, 8, "care:training:add", "新增训练记录", "BUTTON", None, "POST /api/admin/training-record", None, 331, 1),
        (28, 8, "care:training:edit", "编辑训练记录", "BUTTON", None, "PUT /api/admin/training-record/**", None, 332, 1),
        (29, 8, "care:training:delete", "删除训练记录", "BUTTON", None, "DELETE /api/admin/training-record/**", None, 333, 1),
        (30, 9, "appointment:confirm", "确认预约", "BUTTON", None, "PUT /api/admin/appointment/*/confirm", None, 401, 1),
        (31, 9, "appointment:reject", "驳回预约", "BUTTON", None, "PUT /api/admin/appointment/*/reject", None, 402, 1),
        (32, 9, "appointment:finish", "完成预约", "BUTTON", None, "PUT /api/admin/appointment/*/complete,PUT /api/admin/appointment/*/finish", None, 403, 1),
        (33, 9, "appointment:cancel", "取消预约", "BUTTON", None, "PUT /api/admin/appointment/*/cancel", None, 404, 1),
        (34, 10, "notice:add", "新增公告", "BUTTON", None, "POST /api/admin/notice", None, 501, 1),
        (35, 10, "notice:edit", "编辑公告", "BUTTON", None, "PUT /api/admin/notice/**", None, 502, 1),
        (36, 10, "notice:delete", "删除公告", "BUTTON", None, "DELETE /api/admin/notice/**", None, 503, 1),
        (37, 12, "system:user:add", "新增用户", "BUTTON", None, "POST /api/admin/user", None, 911, 1),
        (38, 12, "system:user:edit", "编辑用户", "BUTTON", None, "PUT /api/admin/user/*", None, 912, 1),
        (39, 12, "system:user:disable", "禁用用户", "BUTTON", None, "DELETE /api/admin/user/**", None, 913, 1),
        (40, 12, "system:user:reset", "重置密码", "BUTTON", None, "PUT /api/admin/user/*/reset-password", None, 914, 1),
        (41, 14, "system:menu:save", "保存菜单配置", "BUTTON", None, "PUT /api/admin/menu/**", None, 931, 1),
    ]

    admin_menu_ids = [1, 3, 4, 6, 7, 8, 9, 10, 12, 13, 14] + list(range(15, 42))
    keeper_menu_ids = [1, 3, 6, 7, 8, 9, 10, 21, 22, 24, 25, 27, 28, 30, 32, 34, 35]
    role_rows = [("ADMIN", item) for item in admin_menu_ids] + [("KEEPER", item) for item in keeper_menu_ids]

    species_rows = [
        (
            item["id"],
            item["name"],
            item["english_name"],
            item["origin"],
            item["size"],
            item["avg_lifespan"],
            item["habits"],
            item["difficulty"],
            item["image"],
            item["status"],
            "2026-06-30 08:20:00",
            "2026-06-30 08:20:00",
        )
        for item in species
    ]
    parrot_rows = [
        (
            item["id"],
            item["code"],
            item["name"],
            item["species_id"],
            item["gender"],
            item["age"],
            item["birth_date"],
            item["color"],
            item["image"],
            item["health_status"],
            item["source"],
            item["entry_date"],
            item["personality"],
            item["care_notes"],
            item["current_status"],
            item["description"],
            item["is_public"],
            "2026-06-30 08:40:00",
            "2026-06-30 08:40:00",
        )
        for item in parrots
    ]

    parts = [
        "-- 鹦鹉管理系统演示数据",
        f"-- 生成时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
        "-- 数据来源：GBIF Species API、Wikimedia Commons API；脚本只保存公开文字资料和图片 URL。",
        "-- 注意：data.sql 不写入用户账号和密码，默认账号由后端 DataInitializer 初始化。",
        "USE parrot_management;",
        "",
        "SET NAMES utf8mb4;",
        "SET FOREIGN_KEY_CHECKS = 0;",
        "TRUNCATE TABLE login_log;",
        "TRUNCATE TABLE file_resource;",
        "TRUNCATE TABLE sys_user_menu;",
        "TRUNCATE TABLE sys_role_menu;",
        "TRUNCATE TABLE sys_menu;",
        "TRUNCATE TABLE notice;",
        "TRUNCATE TABLE appointment;",
        "TRUNCATE TABLE training_record;",
        "TRUNCATE TABLE feeding_record;",
        "TRUNCATE TABLE health_record;",
        "TRUNCATE TABLE parrot;",
        "TRUNCATE TABLE parrot_species;",
        "SET FOREIGN_KEY_CHECKS = 1;",
        "",
        insert_sql("sys_menu", ["id", "parent_id", "menu_code", "menu_name", "menu_type", "path", "api_pattern", "icon", "sort_no", "status"], menu_rows),
        insert_sql("sys_role_menu", ["role", "menu_id"], role_rows),
        insert_sql("parrot_species", ["id", "name", "english_name", "origin", "size", "avg_lifespan", "habits", "difficulty", "image", "status", "create_time", "update_time"], species_rows),
        insert_sql("parrot", ["id", "code", "name", "species_id", "gender", "age", "birth_date", "color", "image", "health_status", "source", "entry_date", "personality", "care_notes", "current_status", "description", "is_public", "create_time", "update_time"], parrot_rows),
        insert_sql("health_record", ["id", "parrot_id", "record_date", "weight", "spirit", "appetite", "feather", "excrement", "abnormal_symptoms", "treatment", "need_review", "review_date", "recorder_id", "remark", "create_time", "update_time"], health_rows),
        insert_sql("feeding_record", ["id", "parrot_id", "feeding_date", "feeding_time", "main_food", "supplement", "amount", "water", "is_finished", "leftover", "recorder_id", "remark", "create_time", "update_time"], feeding_rows),
        insert_sql("training_record", ["id", "parrot_id", "training_date", "duration", "project", "content", "cooperation", "completion", "reward", "next_suggestion", "recorder_id", "remark", "create_time", "update_time"], training_rows),
        insert_sql("notice", ["id", "title", "content", "type", "cover_image", "publish_status", "publish_time", "creator_id", "create_time", "update_time"], notice_rows),
    ]
    return "\n".join(part for part in parts if part != "")


def main() -> int:
    sql = build_sql()
    OUTPUT_SQL.write_text(sql, encoding="utf-8")
    print(f"已生成：{OUTPUT_SQL}")
    print("业务数据约 200 条：16 个品种、60 只鹦鹉、45 条健康、50 条喂养、32 条训练、8 条公告。")
    return 0


if __name__ == "__main__":
    sys.exit(main())
