# FH Navigation — MVP 开发任务拆解

> 目标：完成一个可运行的 MVP，实现登录→导航→语音播报→社交的完整闭环。

---

## Phase 1: 项目脚手架与基础设施

### Task 1.1 — 项目初始化
- [ ] Android Studio 创建 Kotlin 项目（minSdk 26, targetSdk 34）
- [ ] 配置 Gradle Version Catalog（libs.versions.toml）
- [ ] 配置多模块结构（`:app`, `:core`, `:feature:*`）
- [ ] 配置 ktlint / detekt 代码规范

### Task 1.2 — 依赖注入 (Hilt)
- [ ] 集成 Hilt
- [ ] 创建 Application 类 @HiltAndroidApp
- [ ] 基础 Module 结构（NetworkModule, DatabaseModule, RepositoryModule）

### Task 1.3 — 网络层
- [ ] 集成 Retrofit + OkHttp
- [ ] 配置 Supabase Kotlin SDK
- [ ] 创建基础 API 接口定义
- [ ] 错误处理拦截器

### Task 1.4 — 本地存储
- [ ] 集成 Room（数据库 migration 策略）
- [ ] 集成 DataStore（偏好设置）
- [ ] 定义基础 Entity / DAO

### Task 1.5 — 导航框架
- [ ] 集成 Navigation Compose
- [ ] 定义 NavGraph（登录、地图、导航、社交、设置）
- [ ] 全局导航状态管理

---

## Phase 2: 登录系统

### Task 2.1 — Supabase Auth 配置
- [ ] Supabase 项目创建 + 配置
- [ ] 开启手机号 OTP 认证
- [ ] 客户端 Auth SDK 集成
- [ ] JWT Token 管理（存储 + 刷新）

### Task 2.2 — 登录 UI
- [ ] 手机号输入页（带国家码选择，默认 +86）
- [ ] 验证码输入页（6 位，倒计时重发）
- [ ] 登录状态检测（已登录直接跳转）

### Task 2.3 — 昵称选择
- [ ] 首次登录进入昵称选择页
- [ ] 预设昵称展示（飞龙、漂移之王）+ 可扩展模块预留
- [ ] 选中后写入 Supabase user metadata
- [ ] 选中后触发欢迎播报："欢迎回来，{昵称}"

### Task 2.4 — 登录态管理
- [ ] AuthRepository（登录/登出/获取当前用户）
- [ ] AuthState（Flow 驱动，全局监听登录态变化）
- [ ] 未登录拦截（自动跳转登录页）

---

## Phase 3: 地图核心

### Task 3.1 — 高德 SDK 集成
- [ ] 高德地图 SDK 接入（API Key 配置）
- [ ] MapView 嵌入 Compose（AndroidView 包装）
- [ ] 基础地图展示（定位、缩放、旋转）

### Task 3.2 — 暗色/亮色地图样式
- [ ] 高德自定义样式 JSON（暗色 FH4 风格）
- [ ] 高德自定义样式 JSON（亮色风格）
- [ ] 样式切换机制（右上角按钮）
- [ ] 跟随系统深色模式（可选联动）

### Task 3.3 — 实时定位
- [ ] 高德定位 SDK 集成
- [ ] 定位权限请求（前台 + 后台）
- [ ] 跟随模式（地图中心锁定当前位置，车头朝上）
- [ ] 定位精度指示

### Task 3.4 — 搜索目的地
- [ ] 高德 POI 搜索 API
- [ ] 搜索 UI（顶部搜索框 + 结果列表）
- [ ] 搜索历史（Room 存储）
- [ ] 选中 POI → 地图标注 + 弹出信息卡片

### Task 3.5 — 路线规划
- [ ] 高德路线规划 API（驾车）
- [ ] 返回 2-3 条备选路线
- [ ] 路线展示（不同颜色绘制在地图上）
- [ ] 每条路线信息卡（距离 + 时间 + 路况颜色编码）
- [ ] 点击路线高亮预览
- [ ] 选择路线后进入导航

---

## Phase 4: 导航模式

### Task 4.1 — 导航引擎
- [ ] 高德导航 SDK 集成
- [ ] 实时导航引导（转向、变道、限速）
- [ ] 偏航检测 + 自动重新规划
- [ ] 到达目的地检测

### Task 4.2 — FH4 风格 HUD
- [ ] HUD Compose 组件（半透明面板）
- [ ] 当前速度（大号数字 + km/h 单位）
- [ ] 当前道路名显示
- [ ] 下一转弯图标 + 距离
- [ ] 剩余距离 + 预计到达时间
- [ ] 导航进度条（路线完成百分比）
- [ ] HUD 动画（数值变化过渡动画）

### Task 4.3 — 导航 UI 布局
- [ ] 全屏导航界面（地图占主体 + HUD 浮层）
- [ ] 底部信息栏（到达时间、距离）
- [ ] 结束导航按钮
- [ ] 导航状态管理（ViewModel）

---

## Phase 5: 语音系统

### Task 5.1 — 音频资源管理
- [ ] 音频资源目录结构（`res/raw/zh/`, `res/raw/en/`）
- [ ] 录音素材导入（中/英各一套）
- [ ] 音频资源映射表（事件 → 音频文件）
- [ ] 音效资源（启动声、到达声等）

### Task 5.2 — 播报引擎
- [ ] VoiceEngine 核心类
- [ ] 事件驱动播报（导航事件 → 触发对应语音）
- [ ] 同场景多语音随机轮换（避免重复感）
- [ ] 播报队列管理（防重叠）
- [ ] 音频焦点管理（播报时降低其他音频）

### Task 5.3 — TTS 补全层
- [ ] TTS 引擎集成（GPT-SoVITS 或 CosyVoice）
- [ ] 音色克隆（用录音素材训练/微调）
- [ ] 动态短语播报（路名、距离、时间 → TTS 拼接）
- [ ] TTS 缓存（常用短语生成后缓存音频文件）

### Task 5.4 — 欢迎播报
- [ ] App 启动 → 登录态检测 → 获取昵称
- [ ] 播报 "欢迎回来，{昵称}"
- [ ] 预设昵称用录音，自定义昵称用 TTS
- [ ] 播报完毕后进入地图主界面

### Task 5.5 — 语音设置
- [ ] 语言切换（中文 / English）
- [ ] 音量调节滑块
- [ ] 播报总开关
- [ ] 设置持久化（DataStore）

---

## Phase 6: 社交功能

### Task 6.1 — Supabase 数据库设计
- [ ] users 表（id, phone, nickname, avatar_url, created_at）
- [ ] friendships 表（user_id, friend_id, status, created_at）
- [ ] locations 表（user_id, lat, lng, heading, speed, updated_at）
- [ ] phrases 表（id, text, category, is_builtin, user_id, sort_order）
- [ ] phrase_messages 表（id, sender_id, receiver_id, phrase_id, created_at）
- [ ] RLS（Row Level Security）策略配置

### Task 6.2 — 好友系统
- [ ] 搜索用户（手机号 / 昵称）
- [ ] 发送好友请求
- [ ] 接受 / 拒绝好友请求
- [ ] 好友列表页
- [ ] 好友详情（昵称、最后在线）

### Task 6.3 — 位置共享
- [ ] 位置上传服务（后台 Service，每 10 秒更新）
- [ ] Supabase Realtime 订阅好友位置
- [ ] 地图上好友标记渲染（头像 + 昵称 + 方向箭头）
- [ ] 位置共享开关（隐私设置，默认关闭）
- [ ] 好友可见范围控制

### Task 6.4 — 预设短语交流
- [ ] 内置短语库（约 15-20 条）
- [ ] 短语选择面板（底部弹出，分类展示）
- [ ] 发送短语 → Supabase 写入
- [ ] 接收短语 → 地图气泡弹出（3 秒消失）+ 提示音
- [ ] 短语数据结构预留可扩展（is_custom 字段）

---

## Phase 7: 设置页

### Task 7.1 — 设置 UI
- [ ] 设置列表页（Jetpack Compose）
- [ ] 语言切换（中文 / English，App 重启生效）
- [ ] 语音设置入口
- [ ] 隐私设置（位置共享开关）
- [ ] 关于页面（版本号、隐私协议、用户协议）
- [ ] 退出登录

---

## Phase 8: 多语言

### Task 8.1 — 国际化
- [ ] strings.xml 中/英双语
- [ ] Compose 中使用 stringResource
- [ ] 日期/时间格式本地化
- [ ] 语言切换后 App 重启刷新

---

## Phase 9: 收尾与优化

### Task 9.1 — 异常处理
- [ ] 网络错误处理（断网提示、重试机制）
- [ ] 定位失败处理
- [ ] 高德 API 错误处理
- [ ] Supabase 连接异常处理
- [ ] 全局错误边界（CrashHandler）

### Task 9.2 — 性能优化
- [ ] 地图渲染性能（减少不必要的重绘）
- [ ] 位置上传频率动态调整（移动时高频，静止时低频）
- [ ] 内存泄漏检查（Service、定位、地图）
- [ ] 启动速度优化（懒加载、异步初始化）

### Task 9.3 — 测试
- [ ] 核心流程手动测试清单
- [ ] ViewModel 单元测试
- [ ] Repository 单元测试
- [ ] 导航流程集成测试

### Task 9.4 — 打包发布准备
- [ ] ProGuard / R8 配置
- [ ] 签名配置
- [ ] 版本号管理
- [ ] 隐私政策页面
- [ ] 用户协议页面

---

## 开发顺序建议

```
Week 1-2:  Phase 1 (脚手架) + Phase 2 (登录)
Week 3-4:  Phase 3 (地图核心)
Week 5:    Phase 4 (导航模式)
Week 6:    Phase 5 (语音系统)
Week 7-8:  Phase 6 (社交功能)
Week 9:    Phase 7 (设置) + Phase 8 (多语言)
Week 10:   Phase 9 (收尾优化)
```

预估总工期：**10 周**（单人全栈）

---

## 关键技术风险

| 风险 | 影响 | 缓解方案 |
|------|------|----------|
| 高德自定义样式自由度有限 | FH4 感打折扣 | HUD + 音效补偿，暗色样式尽量调 |
| TTS 音色克隆效果不稳定 | 语音体验不一致 | 优先用录音，TTS 只补动态内容 |
| Supabase 国内访问速度 | 社交功能延迟 | 考虑 Supabase 自托管或加 CDN |
| 后台定位耗电 | 用户体验差 | 动态频率 + 电量优化 |
