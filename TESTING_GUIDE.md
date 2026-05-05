# FH Navigation — 测试流程指南

> 版本：MVP v1.0.0
> 更新日期：2026-05-06

---

## 一、环境准备

### 1.1 安装 Android Studio

- 下载最新稳定版：https://developer.android.com/studio
- 安装完成后确保 SDK Manager 中安装了：
  - Android SDK Platform 34
  - Android SDK Build-Tools 34.0.0
  - Android Emulator
  - Android SDK Platform-Tools

### 1.2 克隆项目

```bash
git clone https://github.com/walq10/FH-nav.git
cd FH-nav
```

### 1.3 打开项目

1. Android Studio → File → Open → 选择 `FH-nav` 目录
2. 等待 Gradle 同步完成（首次约 5-10 分钟）
3. 如果同步失败，检查网络或配置 Gradle 代理

---

## 二、配置密钥

### 2.1 高德 API Key

**申请步骤：**

1. 访问 https://console.amap.com 注册/登录
2. 创建应用 → 添加 Key → 选择 Android 平台
3. 填写包名：`com.fhnav.app`
4. 获取 SHA1 指纹，在 Android Studio Terminal 运行：

```bash
# Debug 签名
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android

# 找到 SHA1 值，复制填入高德控制台
```

5. 复制生成的 Key

**配置到项目：**

打开 `app/src/main/AndroidManifest.xml`，找到：

```xml
<meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="YOUR_AMAP_KEY_HERE"/>
```

替换 `YOUR_AMAP_KEY_HERE` 为你申请的 Key。

### 2.2 Supabase 配置

**创建项目：**

1. 访问 https://supabase.com 注册/登录
2. 创建新项目，记住数据库密码
3. 进入 Settings → API，复制：
   - Project URL
   - anon / public key

**配置到项目：**

打开 `gradle.properties`，填入：

```properties
SUPABASE_URL=https://你的项目ID.supabase.co
SUPABASE_ANON_KEY=你的anon_key
```

**创建数据库表：**

进入 Supabase Dashboard → SQL Editor，执行以下 SQL：

```sql
-- 用户表
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  phone TEXT UNIQUE NOT NULL,
  nickname TEXT NOT NULL,
  avatar_url TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- 好友关系表
CREATE TABLE friendships (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  friend_id UUID REFERENCES users(id) ON DELETE CASCADE,
  status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
  created_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(user_id, friend_id)
);

-- 位置表
CREATE TABLE user_locations (
  user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  lat DOUBLE PRECISION NOT NULL,
  lng DOUBLE PRECISION NOT NULL,
  bearing REAL DEFAULT 0,
  speed REAL DEFAULT 0,
  updated_at TIMESTAMPTZ DEFAULT now()
);

-- 短语消息表
CREATE TABLE phrase_messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sender_id UUID REFERENCES users(id) ON DELETE CASCADE,
  receiver_id UUID REFERENCES users(id) ON DELETE CASCADE,
  phrase_id TEXT NOT NULL,
  phrase_text TEXT NOT NULL,
  sender_nickname TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- 创建索引
CREATE INDEX idx_friendships_user ON friendships(user_id);
CREATE INDEX idx_friendships_friend ON friendships(friend_id);
CREATE INDEX idx_friendships_status ON friendships(status);
CREATE INDEX idx_phrase_messages_receiver ON phrase_messages(receiver_id);
CREATE INDEX idx_phrase_messages_created ON phrase_messages(created_at DESC);
```

**开启 Realtime：**

1. Database → Replication
2. 开启以下表的 Realtime：
   - ✅ user_locations
   - ✅ phrase_messages
   - ✅ friendships

**关闭 RLS（开发阶段）：**

```sql
ALTER TABLE users DISABLE ROW LEVEL SECURITY;
ALTER TABLE friendships DISABLE ROW LEVEL SECURITY;
ALTER TABLE user_locations DISABLE ROW LEVEL SECURITY;
ALTER TABLE phrase_messages DISABLE ROW LEVEL SECURITY;
```

> ⚠️ 上线前必须重新开启 RLS 并配置正确的安全策略！

### 2.3 Firebase（可选）

如果需要 FCM 推送功能：

1. 访问 https://console.firebase.google.com
2. 创建项目 → 添加 Android 应用（包名：`com.fhnav.app`）
3. 下载 `google-services.json`
4. 替换 `app/google-services.json` 占位文件

如不需要推送，可暂时忽略此步骤。

---

## 三、构建与运行

### 3.1 构建项目

```
1. Android Studio → Build → Make Project (Ctrl+F9)
2. 等待编译完成
3. 检查 Build 窗口是否有错误
```

**常见构建问题：**

| 问题 | 解决方案 |
|------|----------|
| 依赖下载失败 | 配置代理或使用国内镜像（阿里云 Maven） |
| SDK 版本不匹配 | SDK Manager → 安装对应版本 |
| Gradle 版本错误 | 确保使用 Gradle 8.9 |
| KSP 版本冲突 | 确保 Kotlin 版本与 KSP 版本匹配 |

### 3.2 运行到模拟器

```
1. Tools → Device Manager → Create Virtual Device
2. 推荐配置：
   - 设备：Pixel 7
   - 系统：API 34 (Android 14)
   - 内存：4GB+
3. 点击 ▶ Run 按钮
4. 选择模拟器设备
```

> ⚠️ 模拟器无法测试真实定位，需要手动设置虚拟位置

**模拟器设置虚拟位置：**

```
1. 模拟器右侧工具栏 → 三点菜单 → Location
2. 设置经纬度（如北京：39.9042, 116.4074）
3. 点击 Set Location
```

### 3.3 运行到真机（推荐）

```
1. 手机 → 设置 → 关于手机 → 连续点击"版本号" 7 次 → 开启开发者模式
2. 设置 → 开发者选项 → 开启 USB 调试
3. USB 数据线连接电脑
4. 手机上确认允许 USB 调试
5. Android Studio 自动识别设备（顶部工具栏显示设备名）
6. 点击 ▶ Run
```

---

## 四、功能逐项测试

### 4.1 登录流程

**测试步骤：**

1. 启动 App → 应显示登录页
2. 输入手机号（如 13800138000）
3. 点击「发送验证码」
4. 输入收到的 6 位验证码
5. 验证通过 → 进入昵称选择页
6. 选择「飞龙」或「漂移之王」
7. 应听到播报："欢迎回来，飞龙"
8. 自动跳转到地图主界面

**验证清单：**

- [ ] 登录页 UI 正确显示（暗色主题）
- [ ] 手机号输入框有 +86 国家码
- [ ] 发送验证码按钮可点击
- [ ] 验证码倒计时 60 秒工作正常
- [ ] 6 位验证码输入框自动前进
- [ ] 昵称选择页显示预设选项
- [ ] 选中昵称有发光/高亮效果
- [ ] 欢迎语音播放
- [ ] 跳转到地图页

**二次启动测试：**

- [ ] 杀掉 App 重新打开 → 直接进入地图（免登录）
- [ ] 清除 App 数据后 → 重新进入登录页

---

### 4.2 地图功能

**测试步骤：**

1. 进入地图页 → 应自动定位到当前位置
2. 地图显示暗色风格（默认）
3. 点击右上角 ☀️/🌙 图标 → 切换亮色/暗色
4. 点击搜索框 → 输入关键词（如"星巴克"、"万达"）
5. 搜索结果显示列表
6. 点击某个结果 → 地图标注标记 + 弹出信息卡
7. 自动进行路线规划

**验证清单：**

- [ ] 地图正常加载（需有效高德 Key）
- [ ] 定位蓝点显示在正确位置
- [ ] 暗色/亮色切换流畅（动画过渡）
- [ ] 搜索框输入正常
- [ ] 搜索结果列表正确显示
- [ ] 点击结果后地图移动到该位置
- [ ] 路线绘制在地图上（不同颜色）
- [ ] 显示 2-3 条备选路线
- [ ] 每条路线显示距离、时间
- [ ] 推荐路线有标识

**真机额外验证：**

- [ ] 实时位置跟随移动
- [ ] 车头朝上旋转正确

---

### 4.3 导航模式

**测试步骤：**

1. 路线规划完成后 → 选择一条路线
2. 点击「开始导航」按钮
3. 进入全屏导航界面
4. 检查 HUD 显示元素
5. 点击「结束导航」返回地图

**验证清单：**

- [ ] 导航界面全屏显示
- [ ] HUD 面板半透明深色背景
- [ ] 速度显示：大号数字 + km/h 单位
- [ ] 速度初始值为 0（模拟器）或当前速度（真机）
- [ ] 当前道路名显示
- [ ] 转弯指示图标显示
- [ ] 转弯距离显示（如 "200m"）
- [ ] 进度条有渐变色（青→绿）
- [ ] 剩余时间显示
- [ ] 剩余距离显示
- [ ] 预计到达时间显示
- [ ] 结束导航按钮可点击
- [ ] 结束后返回地图页

**真机驾驶测试：**

- [ ] 实时速度随车速变化
- [ ] 速度颜色变化：正常(绿) → 较快(黄) → 很快(红)
- [ ] 转弯时语音播报触发
- [ ] 偏航后自动重新规划
- [ ] 到达目的地播报

---

### 4.4 语音系统

**测试步骤：**

1. 登录时听欢迎播报
2. 开始导航听"开始导航"播报
3. 模拟转弯事件听对应语音
4. 到达目的地听"到达目的地"

**验证清单：**

- [ ] 欢迎播报播放（预设昵称）
- [ ] 导航语音与事件匹配
- [ ] 中文语音播放正常
- [ ] 切换英文后语音变化
- [ ] 音量调节生效
- [ ] 关闭语音开关后静音
- [ ] 播报时不会重叠（队列机制）
- [ ] FH4 语气随机触发（10% 概率）

**录音素材测试：**

将录音文件放入 `app/src/main/res/raw/voice/` 目录后：
- [ ] 有录音的短语播放录音
- [ ] 无录音的短语自动用 TTS 补全
- [ ] TTS 语言与设置一致

---

### 4.5 社交功能

> ⚠️ 需要两台设备或两个账号测试

**好友系统测试：**

设备 A 和设备 B 分别登录不同账号。

```
设备 A：
1. 进入好友列表 → 点击添加好友
2. 搜索设备 B 的手机号
3. 发送好友请求

设备 B：
1. 进入好友列表 → 切换到"请求"标签
2. 看到设备 A 的请求
3. 点击「接受」

设备 A：
1. 好友列表中出现设备 B
```

**验证清单：**

- [ ] 搜索用户正常
- [ ] 好友请求发送成功
- [ ] 对方收到请求通知
- [ ] 接受请求后双方好友列表更新
- [ ] 拒绝请求正常

**位置共享测试：**

```
设备 B：
1. 设置 → 开启位置共享
2. 保持 App 在前台或后台

设备 A：
1. 地图上应看到设备 B 的位置标记
2. 标记显示头像 + 昵称
3. 设备 B 移动后标记实时更新
```

**验证清单：**

- [ ] 位置共享开关生效
- [ ] 开启后位置每 10 秒更新
- [ ] 地图上好友标记显示正确
- [ ] 好友移动时标记实时更新
- [ ] 关闭共享后标记消失

**短语交流测试：**

```
设备 A：
1. 点击短语面板（底部按钮）
2. 选择分类（日常/路况/招呼/FH4）
3. 点击「我在路上」

设备 B：
1. 地图上弹出气泡："设备A昵称：我在路上"
2. 3 秒后气泡自动消失
3. 收到提示音
```

**验证清单：**

- [ ] 短语面板正常弹出
- [ ] 4 个分类正确显示
- [ ] 点击短语发送成功
- [ ] 对方收到气泡弹出
- [ ] 气泡 3 秒后自动消失
- [ ] 收到时有提示音
- [ ] 发送方有确认反馈

---

### 4.6 设置页

**测试步骤：**

1. 进入设置页
2. 逐项测试各设置项

**验证清单：**

- [ ] 语言切换：中文 → 英文
- [ ] 切换后 App 重启生效
- [ ] 语音音量滑块调节
- [ ] 语音开关关闭/开启
- [ ] 地图风格偏好设置
- [ ] 位置共享开关
- [ ] 关于页面显示版本号
- [ ] 退出登录 → 返回登录页
- [ ] 设置项重启后保持

---

## 五、边界情况测试

### 5.1 网络异常

- [ ] 断网后打开 App → 显示错误提示
- [ ] 断网时搜索目的地 → 显示网络错误
- [ ] 断网时导航中 → 提示网络不可用
- [ ] 网络恢复后自动恢复功能

### 5.2 权限处理

- [ ] 拒绝定位权限 → 显示权限说明
- [ ] 拒绝后再次请求 → 引导去设置页
- [ ] 拒绝通知权限 → 不影响核心功能
- [ ] 后台定位权限（Android 10+）→ 正常请求

### 5.3 性能与稳定性

- [ ] 快速连续点击按钮 → 不重复提交
- [ ] 导航中来电 → 返回后导航恢复
- [ ] 切到后台再回来 → 定位恢复
- [ ] 长时间导航（30分钟+）→ 无内存泄漏
- [ ] 低电量下 → 定位正常工作

### 5.4 多语言

- [ ] 中文界面所有文案正确
- [ ] 英文界面所有文案正确
- [ ] 切换语言后日期格式变化
- [ ] 切换语言后语音变化

---

## 六、测试设备建议

| 测试类型 | 推荐设备 | 说明 |
|----------|----------|------|
| UI 开发 | 模拟器 Pixel 7 API 34 | 快速迭代 |
| 定位测试 | 真机（任意 Android 8.0+） | 模拟器定位不准 |
| 导航测试 | 真机 + 车载支架 | 实际驾驶测试 |
| 社交测试 | 两台设备 / 两个模拟器 | 需要双端 |
| 兼容性 | Android 8 / 10 / 12 / 14 | 覆盖主要版本 |

---

## 七、已知限制与待优化

| 项目 | 当前状态 | 后续计划 |
|------|----------|----------|
| 录音素材 | 需手动录制放入 res/raw/ | 录制后替换 TTS |
| TTS 音色 | Android 系统默认 | 接入 GPT-SoVITS/CosyVoice |
| FCM 推送 | 占位配置，不可用 | 替换真实 google-services.json |
| 离线地图 | 未实现 | V2 开发 |
| 多路线偏好 | 固定 2-3 条 | V2 增加偏好设置 |
| RLS 安全 | 开发阶段关闭 | 上线前配置策略 |

---

## 八、问题反馈

测试中遇到问题请记录：

1. **设备信息**：型号、Android 版本
2. **复现步骤**：具体操作流程
3. **预期行为**：应该是什么样
4. **实际行为**：实际发生了什么
5. **截图/录屏**：如有 UI 问题附带截图

提交到 GitHub Issues：https://github.com/walq10/FH-nav/issues
