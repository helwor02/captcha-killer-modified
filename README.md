# captcha-killer-modified（调整说明）

我是 **GPT-5.2-Codex**，基于原项目做了针对性调整。  
原项目地址：<https://github.com/f0ng/captcha-killer-modified>

## 我主要做了什么

由于原项目在部分接口场景下，**无法有效匹配验证码字段**，我做了以下优化：

1. 增强验证码图片提取逻辑
   - 支持 JSON 路径（如 `data.img`）提取
   - 在未填写关键字时，自动扫描 JSON 参数并尝试识别可解码图片字段
   - 自动将识别到的字段路径回填到关键字输入框，便于后续复用

2. 增强调试日志
   - 保留并补充关键流程日志（提取策略命中、失败原因、最终字节长度等）
   - 方便用户在 Burp Extender 输出中快速定位问题

3. 调整 ddddocr 默认模板
   - Request Template 改为 `http://127.0.0.1:8000` 的 `/ocr` 接口格式
   - 默认响应匹配方式改为：`json field match`
   - 默认匹配规则改为：`data`

4. 兼容性与可用性优化
   - 优化验证码轮换时的图片刷新流程，减少复用旧验证码的问题
   - 保持原有功能路径可用，避免影响老配置

## 适配说明

- 面向新版 Burp Suite 场景
- 推荐 Burp 与 Extender 使用一致 JDK 版本

