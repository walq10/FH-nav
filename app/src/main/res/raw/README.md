# Audio Assets - 录音素材目录

## 目录结构

```
raw/
├── voice/
│   ├── zh/
│   │   ├── ZH-W01.mp3    # 欢迎回来，飞龙
│   │   ├── ZH-W02.mp3    # 欢迎回来，漂移之王
│   │   ├── ZH-N01.mp3    # 开始导航
│   │   ├── ZH-N02.mp3    # 前方左转
│   │   ├── ZH-N03.mp3    # 前方右转
│   │   ├── ZH-N04.mp3    # 直行
│   │   ├── ZH-N05.mp3    # 前方掉头
│   │   ├── ZH-N06.mp3    # 靠左行驶
│   │   ├── ZH-N07.mp3    # 靠右行驶
│   │   ├── ZH-N08.mp3    # 进入环岛
│   │   ├── ZH-N09.mp3    # 到达目的地
│   │   ├── ZH-N10.mp3    # 偏航，正在重新规划
│   │   ├── ZH-N11.mp3    # 前方有测速摄像头
│   │   ├── ZH-F01.mp3    # Nice！
│   │   ├── ZH-F02.mp3    # 稳
│   │   ├── ZH-F03.mp3    # 快到了
│   │   ├── ZH-F04.mp3    # 冲！
│   │   └── ZH-F05.mp3    # 哎呀走错了
│   └── en/
│       ├── EN-W01.mp3    # Welcome back, Fly Dragon
│       ├── EN-W02.mp3    # Welcome back, Drift King
│       ├── EN-N01.mp3    # Navigation started
│       ├── EN-N02.mp3    # Turn left ahead
│       ├── EN-N03.mp3    # Turn right ahead
│       ├── EN-N04.mp3    # Go straight
│       ├── EN-N05.mp3    # Make a U-turn
│       ├── EN-N06.mp3    # Keep left
│       ├── EN-N07.mp3    # Keep right
│       ├── EN-N08.mp3    # Enter the roundabout
│       ├── EN-N09.mp3    # You have arrived
│       ├── EN-N10.mp3    # Off route, recalculating
│       ├── EN-N11.mp3    # Speed camera ahead
│       ├── EN-F01.mp3    # Nice!
│       ├── EN-F02.mp3    # Steady
│       ├── EN-F03.mp3    # Almost there
│       ├── EN-F04.mp3    # Let's go!
│       └── EN-F05.mp3    # Oops, wrong way
└── sfx/
    ├── engine_start.mp3   # 启动引擎声
    └── arrived.mp3        # 到达叮咚声
```

## 录音要求

- 格式：MP3 (128kbps+) 或 WAV
- 采样率：44.1kHz
- 语速：略快于正常说话，有节奏感
- 语气：轻松愉快，带点兴奋，不要播音腔
- 每条之间留 0.5 秒静音
- 后期：降噪 + 音量标准化 (-16 LUFS)

## 使用说明

录音完成后将文件放入对应目录，VoiceAssetMapper.kt 会自动映射。
如果某个文件缺失，系统会自动使用 TTS 模拟音色进行补全。
