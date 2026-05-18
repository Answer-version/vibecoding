# 项目迁移文档

## 迁移概述

将外贸独立站项目从混用目录迁移到独立目录 `vibecoding-site/`。

## 迁移时间
2026-05-18

## 旧目录结构
```
/Users/answer/dev/code/vibecoding/
├── backend/           # 外贸独立站后端 ✅
├── frontend/         # 外贸独立站前端 ✅
├── deploy/           # 部署配置 ✅
├── docs/             # 文档 ✅
├── .github/          # CI/CD ✅
├── app_forward/      # 其他项目 ❌
├── claudio/          # 其他项目 ❌
├── 演唱会抢票助手/    # 其他项目 ❌
└── ...
```

## 新目录结构
```
/Users/answer/dev/code/vibecoding/vibecoding-site/
├── backend/           # Spring Boot 3.2 微服务
│   ├── common/       # 公共模块
│   ├── gateway/     # 网关
│   ├── product-service/
│   ├── order-service/
│   ├── user-service/
│   └── cms-service/
├── frontend/
│   ├── nuxt3/       # 商城前端
│   └── vue-admin/   # 管理后台
├── deploy/           # Docker 部署
├── docs/            # 技术文档
├── .github/workflows/
│   └── ci.yml       # CI/CD 工作流
├── .gitignore
└── README.md
```

## Git 仓库

- **URL**: https://github.com/Answer-version/vibecoding
- **类型**: 公开仓库

### 分支结构
| 分支 | 用途 | 保护规则 |
|------|------|----------|
| `main` | 生产分支 | PR + 1 审查 |
| `staging` | 预生产 | PR + 1 审查 |
| `test` | 测试分支 | CI 通过 |
| `dev` | 开发分支 | 直接推送 |

## 迁移步骤

1. **创建新目录**
   ```bash
   mkdir vibecoding-site
   ```

2. **复制项目文件**
   ```bash
   cp -r backend frontend deploy docs .github .gitignore README.md vibecoding-site/
   ```

3. **初始化 Git**
   ```bash
   cd vibecoding-site
   git init
   git add .
   git commit -m "feat: 外贸独立站初始代码"
   ```

4. **推送到 GitHub**
   ```bash
   git remote add origin https://github.com/Answer-version/vibecoding.git
   git push -u origin main
   ```

## 开发工作流

```
dev → test → staging → main
  ↓      ↓         ↓
自       测试      UAT
动       通过     通过
测                    ↓
试              部署生产
```

## 本地开发命令

```bash
# 进入项目目录
cd /Users/answer/dev/code/vibecoding/vibecoding-site

# 构建后端
cd backend && ./mvnw clean install -DskipTests

# 启动服务
cd backend/gateway && ./mvnw spring-boot:run

# 前端开发
cd frontend/nuxt3 && npm run dev
```

## 注意事项

- target 目录已加入 .gitignore，不上传到仓库
- jar 文件较大，首次 clone 可能需要较长时间