# FH Navigation — 项目交接文档

> 本文档供 AI 助手（MiMo-Claw）快速了解项目全貌，启动后请先阅读本文件。

---

## 一、项目概述

**FH Navigation** 是一款以《极限竞速：地平线4》(Forza Horizon 4) 语音和视觉风格为特色的导航 App，面向国内市场。

- **核心卖点**：游戏化导航体验（FH4 风格 HUD + 语音播报 + 暗色主题）
- **社交特色**：大世界地图交流感（好友位置共享 + 预设短语交流）
- **目标用户**：年轻驾驶者、赛车游戏爱好者
- **平台**：仅 Android，仅国内市场

---

## 二、技术栈（已确定，不要改）

| 层级 | 技术 | 说明 |
|------|------|------|
| 语言 | Kotlin | 唯一开发语言 |
| UI | Jetpack Compose | 声明式 UI |
| 架构 | MVVM + Clean Architecture | data / domain / ui 三层 |
| DI | Hilt | Google 官方推荐 |
| 地图 | 高德 SDK（3D Map + Navi） | 国内数据最全 |
| 后端 | Supabase | Auth + Postgrest + Realtime |
| 数据库 | Room + DataStore | 本地存储 |
| 语音 | 录音素材 + TTS 补全 | 优先用录音，TTS 兜底 |
| 异步 | Coroutines + Flow | Kotlin 原生 |

**绝对不要**引入 Google Play Services（地图用高德）、Firebase（后端用 Supabase）、Jetpack Compose 以外的 UI 框架。

---

## 三、当前进度

### ✅ 已完成（V1 MVP 代码已生成）

| 模块 | 状态 | 文件数 |
|------|------|--------|
| 项目脚手架（Gradle、Manifest、ProGuard） | ✅ | 6 |
| 数据层（模型、仓库接口、Room、Supabase 实现） | ✅ | 25 |
| 领域层（UseCase、状态模型） | ✅ | 7 |
| UI 层（登录、地图、HUD、社交、设置） | ✅ | 37 |
| 语音系统（播放引擎、TTS 补全、语音管理） | ✅ | 5 |
| 服务层（前台定位、语音服务） | ✅ | 3 |
| DI 模块（Hilt 依赖注入） | ✅ | 4 |
| 工具类 | ✅ | 3 |
| 资源文件（strings、colors、themes） | ✅ | 6 |
| **合计** | | **103 文件，8868 行** |

### ❌ 未完成

| 项目 | 说明 |
|------|------|
| 录音素材 | 需要用户自己录制，放入 `res/raw/voice/` |
| 高德 API Key | 需要用户申请替换 `AndroidManifest.xml` 中的占位值 |
| Supabase 配置 | 需要在 Supabase 控制台建表（SQL 见 TESTING_GUIDE.md） |
| TTS 音色克隆 | 当前用 Android 系统 TTS，需接入 GPT-SoVITS 或 CosyVoice |
| google-services.json | 当前是占位文件 |
| 实际测试 | 代码未经过编译测试 |

---

## 四、架构决策记录

### 为什么选高德不选 Mapbox？
- 国内数据全、路况准、合规省心
- Mapbox 在国内数据不如高德
- 高德自定义样式够用（暗色/亮色双风格）

### 为什么选 Supabase 不选 Firebase？
- Firebase 有 Google 服务墙问题（国内访问受限）
- Supabase 开源、支持自托管
- 手机号 Auth 开箱支持
- Realtime 功能满足位置同步需求

### 为什么 HUD 只保留速度，不要指南针？
- 用户明确要求精简
- 大号数字更有 FH4 感
- 指针式表盘反而信息密度低

### 为什么预设短语不开放自定义？
- MVP 阶段先用内置预设
- 架构已预留扩展（`is_custom` 字段）
- V2 开放用户自定义添加

### 为什么昵称用预设不用自由输入？
- 简化 MVP 流程
- 预设昵称可以做录音素材（"欢迎回来，飞龙"）
- V2 开放自定义（需 TTS 拼接欢迎语）

---

## 五、项目结构

```
fh-navigation/
├── app/
│   ├── src/main/
│   │   ├── java/com/fhnav/app/
│   │   │   ├── FHNavApplication.kt      # @HiltAndroidApp
│   │   │   ├── MainActivity.kt           # 入口，权限处理，欢迎播报
│   │   │   ├── di/                       # Hilt 模块（4个）
│   │   │   ├── data/
│   │   │   │   ├── model/                # 数据模型（7个）
│   │   │   │   ├── repository/           # 仓库接口（6个）
│   │   │   │   ├── local/                # Room + DataStore
│   │   │   │   └── remote/               # Supabase + 高德 实现
│   │   │   ├── domain/
│   │   │   │   ├── usecase/              # 业务逻辑（5个）
│   │   │   │   └── model/                # 领域模型
│   │   │   ├── ui/
│   │   │   │   ├── auth/                 # 登录流程
│   │   │   │   ├── map/                  # 地图主界面
│   │   │   │   ├── navigation_ui/        # 导航 HUD
│   │   │   │   ├── social/               # 好友 + 短语
│   │   │   │   ├── settings/             # 设置
│   │   │   │   ├── welcome/              # 欢迎页
│   │   │   │   ├── theme/                # FH4 主题
│   │   │   │   └── components/           # 通用组件
│   │   │   ├── voice/                    # 语音系统（5个）
│   │   │   ├── service/                  # 后台服务（3个）
│   │   │   └── util/                     # 工具类（3个）
│   │   ├── res/
│   │   │   ├── raw/voice/zh/             # 中文录音素材
│   │   │   ├── raw/voice/en/             # 英文录音素材
│   │   │   ├── values/strings.xml        # 中文字符串
│   │   │   └── values-en/strings.xml     # 英文字符串
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── google-services.json              # 占位文件
├── gradle/libs.versions.toml             # 版本目录
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties                     # Supabase 配置在这里
├── FH_NAV_DEVELOPMENT_PLAN.md            # 完整开发计划
├── FH_NAV_VOICE_SCRIPT.md                # 录音脚本清单
└── TESTING_GUIDE.md                      # 测试流程指南
```

---

## 六、关键配置文件说明

### gradle.properties（需要用户填写）
```properties
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=xxx
```

### AndroidManifest.xml（需要用户填写）
```xml
<meta-data android:name="com.amap.api.v2.apikey" android:value="高德KEY"/>
```

### app/google-services.json（需要用户替换）
当前是占位文件，FCM 推送不可用。

---

## 七、数据库表结构（Supabase）

```sql
-- 用户表
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  phone TEXT UNIQUE NOT NULL,
  nickname TEXT NOT NULL,
  avatar_url TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- 好友关系
CREATE TABLE friendships (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  friend_id UUID REFERENCES users(id) ON DELETE CASCADE,
  status TEXT DEFAULT 'PENDING',
  created_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(user_id, friend_id)
);

-- 位置共享
CREATE TABLE user_locations (
  user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  lat DOUBLE PRECISION NOT NULL,
  lng DOUBLE PRECISION NOT NULL,
  bearing REAL DEFAULT 0,
  speed REAL DEFAULT 0,
  updated_at TIMESTAMPTZ DEFAULT now()
);

-- 短语消息
CREATE TABLE phrase_messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sender_id UUID REFERENCES users(id) ON DELETE CASCADE,
  receiver_id UUID REFERENCES users(id) ON DELETE CASCADE,
  phrase_id TEXT NOT NULL,
  phrase_text TEXT NOT NULL,
  sender_nickname TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);
```

---

## 八、后续开发计划

### V1 收尾（当前）
- [ ] 实际编译测试 + 修复编译错误
- [ ] 录音素材对接测试
- [ ] UI 细节打磨
- [ ] 性能优化

### V2 功能扩展
- [ ] 多路线偏好设置（避开高速/收费等）
- [ ] 离线地图支持
- [ ] 用户自定义短语添加
- [ ] 语音指令识别（"导航去XXX"）
- [ ] 自定义昵称（需 TTS 拼接欢迎语）

### V3 社交增强
- [ ] 群组/车队功能
- [ ] 附近陌生人（开放世界感）
- [ ] 位置历史轨迹回放
- [ ] 自定义语音包

---

## 九、已知问题

| 问题 | 严重程度 | 说明 |
|------|----------|------|
| 代码未编译测试 | 高 | 可能有 import 错误、API 不匹配 |
| 录音素材缺失 | 中 | 走 TTS 兜底，体验一般 |
| TTS 音色普通 | 中 | Android 系统 TTS，需接入专业 TTS |
| RLS 未配置 | 中 | Supabase 安全策略开发阶段关闭 |
| google-services.json 占位 | 低 | FCM 推送不可用 |

---

## 十、给 AI 助手的建议

1. **先读本文件**，再读 `FH_NAV_DEVELOPMENT_PLAN.md` 了解完整计划
2. **遇到不确定的技术决策**，先问用户再动手
3. **不要改技术栈**，用户已确认所有选型
4. **代码修改后记得 git commit + push**
5. **录音素材相关**参考 `FH_NAV_VOICE_SCRIPT.md`
6. **测试流程**参考 `TESTING_GUIDE.md`
7. **UI 风格**统一用 FH4 暗色主题（Color.kt 中已定义）
8. **国内优先**：所有服务选国内方案，不用 Google/Firebase 做核心功能
