package com.example.kotlin_app_study.bp.data

import com.example.kotlin_app_study.bp.theme.BPColors
import kotlin.random.Random

/**
 * 内置样本数据，App 启动后注入到 Repository。
 * 让首页 / 历史 / 趋势图都有真实可见的数据。
 */
object Samples {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    fun seedBP(now: Long): List<BPRecord> {
        // 截图所示：4月21日,11:44 -> 100/75 70bpm 一条
        return listOf(
            BPRecord(1, now - 3 * 60 * 60 * 1000L, 100, 75, 70)
        )
    }

    fun seedHR(now: Long): List<HRRecord> {
        // 截图所示：4月21日,15:52 66bpm 压力28%；4月21日,11:42 67bpm 压力22%
        return listOf(
            HRRecord(2, now - 1 * 60 * 60 * 1000L, 66, stress = 28),
            HRRecord(1, now - 5 * 60 * 60 * 1000L, 67, stress = 22),
        )
    }

    fun seedBS(now: Long): List<BSRecord> {
        // 截图所示：3 条都是默认状态，80/85/75 mg/dL
        return listOf(
            BSRecord(3, now - 30 * 60 * 1000L, 80f, BSPeriod.DEFAULT),
            BSRecord(2, now - 60 * 60 * 1000L, 85f, BSPeriod.DEFAULT),
            BSRecord(1, now - 90 * 60 * 1000L, 75f, BSPeriod.DEFAULT),
        )
    }

    fun seedReminders(): List<ReminderItem> = listOf(
        ReminderItem(1, ReminderType.HR, 12, 0),
        ReminderItem(2, ReminderType.BP, 8, 0),
        ReminderItem(3, ReminderType.BS, 18, 0),
    )

    // ==================== AI ====================

    val recommendQuestions = listOf(
        "我的血压有点高，是不是高血压？",
        "经常感觉胸痛，是不是心脏病的征兆？",
        "为什么我经常感到呼吸急促，总是喘不上气？",
        "我头疼是什么原因？",
        "我时常感觉焦虑、无法入睡，该如何缓解？",
        "我经常肚子不舒服，是不是得了胃炎？",
        "什么食物对降压最有效？",
        "心率多少算正常？",
    )

    val faqList = recommendQuestions.map { q ->
        FaqItem(q, "AI 健康咨询由 ChatGPT 提供。请注意核实信息，因 ChatGPT 可能会出现错误。")
    }

    val healthTests: List<HealthTest> = listOf(
        HealthTest(
            testId = "ada",
            title = "ADA糖尿病风险测试",
            subtitle = "2型糖尿病是指身体无法正常生成或利用胰岛素，导致血糖水平升高。本测试可帮助评估成年人未来患2型糖尿病的风险。",
            duration = "1分钟",
            lowMax = 4, midMax = 7,
            questions = listOf(
                TestQuestion("你的年龄是？", listOf(
                    TestOption("40岁以下", 0),
                    TestOption("40–49岁", 1),
                    TestOption("50–59岁", 2),
                    TestOption("60岁及以上", 3)
                )),
                TestQuestion("你的性别？", listOf(TestOption("男", 1), TestOption("女", 0))),
                TestQuestion("家庭成员有糖尿病？", listOf(TestOption("有", 1), TestOption("没有", 0))),
                TestQuestion("是否被告知患高血压？", listOf(TestOption("是", 1), TestOption("否", 0))),
                TestQuestion("是否经常运动？", listOf(TestOption("是", 0), TestOption("否", 1))),
                TestQuestion("你的BMI范围？", listOf(
                    TestOption("< 25", 0), TestOption("25–30", 1), TestOption("> 30", 2)
                )),
                TestQuestion("是否吸烟？", listOf(TestOption("是", 1), TestOption("否", 0))),
            )
        ),
        HealthTest(
            testId = "hbp",
            title = "高血压风险测试",
            subtitle = "评估你患高血压的整体风险，并给出生活方式建议。",
            duration = "2分钟",
            lowMax = 3, midMax = 7,
            questions = (1..8).map { i ->
                TestQuestion("高血压风险题目 $i：你近期感受到了哪种症状？", listOf(
                    TestOption("无", 0), TestOption("偶尔", 1), TestOption("经常", 2)
                ))
            }
        ),
        HealthTest(
            testId = "neck",
            title = "颈椎病风险测试",
            subtitle = "评估你的颈椎健康状况，识别久坐或低头族潜在风险。",
            duration = "3分钟",
            lowMax = 4, midMax = 9,
            questions = (1..12).map { i ->
                TestQuestion("颈椎风险题目 $i：你的颈椎是否有以下感受？", listOf(
                    TestOption("从不", 0), TestOption("偶尔", 1), TestOption("经常", 2)
                ))
            }
        ),
        HealthTest(
            testId = "lipid",
            title = "高血脂风险测试",
            subtitle = "通过 8 道题评估你血脂异常的风险。",
            duration = "2分钟",
            lowMax = 4, midMax = 9,
            questions = (1..8).map { i ->
                TestQuestion("血脂题目 $i：你目前的饮食 / 体重情况？", listOf(
                    TestOption("否", 0), TestOption("偶尔", 1), TestOption("是", 2)
                ))
            }
        ),
        HealthTest(
            testId = "stroke",
            title = "中风风险测试",
            subtitle = "ASA Stroke Risk 14 题评估你的卒中风险。",
            duration = "3分钟",
            lowMax = 5, midMax = 10,
            questions = (1..14).map { i ->
                TestQuestion("中风风险题目 $i：你的心血管健康状况？", listOf(
                    TestOption("否", 0), TestOption("不确定", 1), TestOption("是", 2)
                ))
            }
        )
    )

    // ==================== 知识库（截图 6 个彩色横幅） ====================

    val knowledgeArticles: List<KnowledgeArticle> = listOf(
        KnowledgeArticle(
            id = 1,
            title = "血压的正常范围",
            bannerColor = BPColors.BannerTeal,
            emoji = "🩺",
            intro = "什么范围可以确定为高血压？如果您的一个读数达到高血压标准，而另一个未达到，该如何确定？要读懂您的血压数字，您可以查看以下血压类别。",
            sections = listOf(
                KnowledgeSection("低血压", "收缩压<90 或舒张压<60"),
                KnowledgeSection("正常", "收缩压90-119，舒张压60-79"),
                KnowledgeSection("偏高", "收缩压120-129，舒张压60-79"),
                KnowledgeSection("高血压·一级", "收缩压130-139或舒张压80-89"),
                KnowledgeSection("高血压·二级", "收缩压140-180或舒张压90-120"),
                KnowledgeSection("重度高血压", "收缩压>180或舒张压>120")
            )
        ),
        KnowledgeArticle(
            id = 2,
            title = "什么是血压?",
            bannerColor = BPColors.BannerYellow,
            emoji = "❤️",
            intro = "无论您是在家还是在医院测量血压，您一定对血压的含义或设备屏幕上的两列数字感到困惑。别着急，看完这篇文章，一切都会有答案的。",
            sections = listOf(
                KnowledgeSection("什么是血压?", "心脏就像一个水泵，不断地将血液泵入全身的血管。当血液流动时，它会推动血管壁，推的力量就是血压。"),
                KnowledgeSection("BP设备上的两个数字是什么?", "血压计上的两个读数代表收缩压和舒张压。顶部的第一个数字是收缩压，意思是心脏推动血液通过动脉时动脉中的压力量。底部的第二个数字是舒张压，指的是心脏在两次心跳之间时的动脉压。"),
                KnowledgeSection("理想血压", "理想的血压是 90/60 mmHg 到 120/80 mmHg 之间。请保持健康的生活方式与定期监测。")
            )
        ),
        KnowledgeArticle(
            id = 3,
            title = "找出你的血压类型",
            bannerColor = BPColors.BannerRed,
            emoji = "🩻",
            intro = "高血压、低血压、白大衣高血压、隐匿性高血压…一文带你认识所有常见血压类型。",
            sections = listOf(
                KnowledgeSection("原发性高血压", "占成人高血压 90% 以上，无明确病因，与遗传、年龄、肥胖、高盐饮食等有关。"),
                KnowledgeSection("继发性高血压", "由肾病、内分泌疾病等明确原因引起，治疗原发病往往能让血压恢复正常。"),
                KnowledgeSection("白大衣高血压", "在医生面前测量时血压偏高，但在家测正常。建议家庭自测 + 24 小时动态监测。")
            )
        ),
        KnowledgeArticle(
            id = 4,
            title = "打破对血压的误解",
            bannerColor = BPColors.BannerGreen,
            emoji = "🩺",
            intro = "关于血压有许多误解，今天我们一次性帮你澄清。",
            sections = listOf(
                KnowledgeSection("误解一：感觉不到症状就不需要测", "高血压被称为\"无声杀手\"，许多人没有任何症状直到出现并发症。"),
                KnowledgeSection("误解二：血压药一旦吃就停不掉", "正确观念是：血压控制达标后由医生评估是否减量。绝不可自行停药。"),
                KnowledgeSection("误解三：年轻人不会得高血压", "近年来高血压人群明显年轻化，30~40 岁高血压患者越来越多。")
            )
        ),
        KnowledgeArticle(
            id = 5,
            title = "不可不知的高血压类型",
            bannerColor = BPColors.BannerBlue,
            emoji = "🫀",
            intro = "了解你的血压类型，才能选对管理方法。",
            sections = listOf(
                KnowledgeSection("单纯收缩期高血压", "常见于老年人，收缩压≥140，舒张压<90。"),
                KnowledgeSection("舒张期高血压", "舒张压≥90 而收缩压<140，多见于中青年。"),
                KnowledgeSection("混合型高血压", "收缩压与舒张压均升高，最为常见。"),
            )
        ),
        KnowledgeArticle(
            id = 6,
            title = "注意高血压的症状",
            bannerColor = BPColors.BannerCoral,
            emoji = "🤒",
            intro = "虽然高血压常常\"无声\"，但出现这些症状要警惕。",
            sections = listOf(
                KnowledgeSection("头痛眩晕", "尤其是清晨头痛或后枕部胀痛。"),
                KnowledgeSection("视物模糊", "高血压可损伤眼底动脉。"),
                KnowledgeSection("耳鸣心悸", "持续耳鸣、心慌应及时就医测量。")
            )
        ),
        KnowledgeArticle(
            id = 7,
            title = "了解高血压的并发症",
            bannerColor = BPColors.BannerTeal,
            emoji = "🩺",
            intro = "长期不控制的高血压会带来多种致命并发症。",
            sections = listOf(
                KnowledgeSection("心脑血管", "中风、心肌梗塞、心衰。"),
                KnowledgeSection("肾脏", "慢性肾病、肾衰竭。"),
                KnowledgeSection("眼底", "视网膜病变、视力下降甚至失明。")
            )
        ),
        KnowledgeArticle(
            id = 8,
            title = "了解低血压：症状与类型",
            bannerColor = BPColors.BannerCoral,
            emoji = "💧",
            intro = "血压不是越低越好，太低也会让人头晕昏厥。",
            sections = listOf(
                KnowledgeSection("常见症状", "头晕、乏力、视物模糊、晕厥。"),
                KnowledgeSection("常见类型", "体位性低血压（突然站起头晕）、神经介导性低血压、餐后低血压。"),
                KnowledgeSection("如何调整", "缓慢起立、保证摄水、避免过度脱水、适度增加食盐摄入。")
            )
        ),
        KnowledgeArticle(
            id = 9,
            title = "治疗高血压的首选药物",
            bannerColor = BPColors.BannerGreen,
            emoji = "💊",
            intro = "根据指南，目前一线降压药主要分 5 大类。",
            sections = listOf(
                KnowledgeSection("ACEI / ARB", "如卡托普利、缬沙坦，保护心肾。"),
                KnowledgeSection("钙拮抗剂 (CCB)", "如氨氯地平、硝苯地平。"),
                KnowledgeSection("利尿剂", "如氢氯噻嗪。"),
                KnowledgeSection("β 受体阻滞剂", "如美托洛尔，适合合并冠心病者。"),
                KnowledgeSection("个体化", "请遵循医生建议，切勿自行更换或停用。")
            )
        ),
        KnowledgeArticle(
            id = 10,
            title = "如何控制血压?",
            bannerColor = BPColors.BannerBlue,
            emoji = "🥗",
            intro = "不只是吃药，生活方式同样关键。",
            sections = listOf(
                KnowledgeSection("饮食", "DASH 饮食：蔬菜、水果、全谷物、低脂乳制品；每日盐 < 5g。"),
                KnowledgeSection("运动", "每周 150 分钟中等强度有氧 + 抗阻训练。"),
                KnowledgeSection("体重", "BMI 控制在 24 以下；腰围男 < 90cm，女 < 85cm。"),
                KnowledgeSection("睡眠 / 减压", "保证 7~8 小时睡眠；冥想、深呼吸有助稳压。")
            )
        ),
        KnowledgeArticle(
            id = 11,
            title = "通过运动降低血压",
            bannerColor = BPColors.BannerYellow,
            emoji = "🏃",
            intro = "规律运动可降收缩压 4–9 mmHg，效果堪比一种降压药。",
            sections = listOf(
                KnowledgeSection("推荐项目", "快走、游泳、骑行、慢跑、太极。"),
                KnowledgeSection("强度", "心率达到 (220-年龄)×60% 即可。"),
                KnowledgeSection("注意", "高血压未控制达标者运动前先咨询医生。")
            )
        ),
    )
}
