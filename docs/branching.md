# 分支管理策略

## 分支结构

| 分支 | 用途 | 环境 | 推送触发 |
|------|------|------|----------|
| `dev` | 开发分支 | 开发环境 | 自动构建 + 测试 |
| `test` | 测试分支 | 测试环境 | 自动构建 + 测试 |
| `staging` | 预生产分支 (UAT) | 预生产 | 手动部署 |
| `main` | 生产分支 | 生产环境 | 手动部署 |

## 开发流程

```
开发 → dev → test → staging → main
             ↓      ↓
           自       UAT
          动       测试
          测试     通过
```

### 步骤说明

1. **开发分支 (dev)**
   - 开发完成后推送代码到 dev 分支
   - 自动触发 CI 构建 + 测试
   - 测试失败自动创建 Issue

2. **测试分支 (test)**
   - dev 测试通过后，创建 PR 合并到 test
   - 自动触发测试
   - 修复测试发现的 bug（创建 issue → dev 修改 → 重复）

3. **预生产分支 (staging)**
   - test 通过后，合并到 staging
   - 用于 UAT 用户验收测试
   - 验收通过后合并到 main

4. **生产分支 (main)**
   - 合并 staging 到 main
   - 手动部署到生产环境

## Issue 管理

- 测试失败自动创建 Issue，包含：
  - 分支信息
  - 失败详情
  - 标签: `bug`, `auto-test`

- 开发根据 Issue 修改 dev 分支代码
- 修复后推送 dev，重复流程

## PR 合并规则

建议启用：
- Require review from maintainer
- Require pass CI checks
- Require all status checks pass