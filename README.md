# captcha-killer-modified

本项目继承于：**c0ny1/captcha-killer**  
原项目地址：<https://github.com/c0ny1/captcha-killer>

---

## 作者信息

- 原项目作者：**c0ny1**
- 修改版维护者：**f0ng**
- 修改版项目地址：<https://github.com/f0ng/captcha-killer-modified>

---

## 本修改版做了什么（核心改动）

相对原版，`captcha-killer-modified` 重点做了以下增强：

1. **新版 Burp / JDK 兼容性修复**
   - 处理老版本编码实现导致的新 JDK 环境报错问题
   - 适配新版 Burp 扩展加载与运行场景

2. **验证码提取能力增强**
   - 支持自定义关键字提取验证码
   - 支持响应中 token 联动提取
   - 支持 `@captcha@` 与 `@captcha-killer-modified@` 占位替换

3. **JSON 响应提取增强**
   - 支持 JSON 路径规则（如 `data.img`）
   - 当规则提取失败时，可自动扫描 JSON 字段值，尝试定位可解码图片数据

4. **图片/Base64 处理增强**
   - 兼容 `data:image/...`、纯 Base64、URL 编码、换行等格式
   - 兼容 Base64 URL Safe 场景（`-` / `_`）

5. **识别能力增强**
   - 集成 ddddocr 模板能力
   - 增强算术验证码场景支持

---

## 适配 Burp 版本

- **面向新版 Burp Suite**（含较新 JDK 运行环境）
- 推荐：Burp 主程序与 Extender 使用一致的 Java 版本

---

## 新增特点（重点）

- ✅ 更好的新版 Burp 兼容性
- ✅ 关键字提取 + JSON 路径提取 + JSON 自动扫描三重策略
- ✅ 更稳健的 Base64 图片识别与解码能力
- ✅ 支持 token 联动与占位符替换
- ✅ 支持 Java 21 的 CI 自动打包（GitHub Actions）

---

## 免责声明

本项目仅用于合法授权的安全测试与安全研究，禁止用于未授权攻击行为。
