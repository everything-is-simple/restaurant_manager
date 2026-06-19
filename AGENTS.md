# Repository Guidelines

## Project Structure & Module Organization

This repository contains planning docs plus a runnable RuoYi-Vue application. Treat `G:\restaurant_manager\RuoYi-Vue\` as the primary runtime workspace.

- `RuoYi-Vue/ruoyi-admin`: Spring Boot entry module and web controllers.
- `RuoYi-Vue/ruoyi-system`: business domain, service, mapper, and MyBatis XML.
- `RuoYi-Vue/ruoyi-ui`: Vue 2 + Element UI frontend.
- `docs/plan_ruoyi_classic`: restaurant-specific plans, schema, and menu SQL.
- `sql/` and `RuoYi-Vue/sql/`: base RuoYi initialization SQL.

Restaurant features follow the existing split: backend controllers under `com/ruoyi/web/controller/restaurant`, services under `com/ruoyi/system/service`, and frontend pages under `ruoyi-ui/src/views/restaurant`.

## Build, Test, and Development Commands

Run commands from `RuoYi-Vue/` unless noted otherwise.

- `mvn -pl ruoyi-admin -am compile`: compile backend modules.
- `mvn -pl ruoyi-admin -am package -DskipTests`: build the backend jar.
- `cd ruoyi-ui && npm install`: install frontend dependencies.
- `cd ruoyi-ui && npm run dev`: start the frontend dev server.
- `cd ruoyi-ui && npm run build:prod`: create a production frontend build.

If you change restaurant menus or schema, review `docs/plan_ruoyi_classic/menu_init.sql` and `docs/plan_ruoyi_classic/schema.sql` alongside code changes.

## Coding Style & Naming Conventions

- Java: 4-space indentation, `UpperCamelCase` classes, `lowerCamelCase` fields/methods.
- Vue: keep page folders under `src/views/restaurant/<module>/index.vue`; API files under `src/api/restaurant/<module>.js`.
- Reuse RuoYi generator patterns for controller, service, mapper, and XML naming.
- Prefer small, targeted changes; avoid refactoring unrelated generated code.

## Testing Guidelines

There is no strong automated test suite in this repo yet. For most changes, validate with:

- backend compile: `mvn -pl ruoyi-admin -am compile`
- frontend build: `cd ruoyi-ui && npm run build:prod`
- manual flow checks for affected CRUD pages, menus, and permission buttons

When adding business logic, verify transaction-sensitive flows such as stock deduction and rollback with realistic seed data.

## Commit & Pull Request Guidelines

Follow the existing history style: concise Conventional Commit prefixes such as `feat:`, `docs:`, `chore:`, or scoped variants like `docs(计划):` and `feat(权限):`.

PRs should include: purpose, affected modules, SQL/doc updates, validation commands run, and screenshots for UI changes. Note clearly if work touches both `docs/` and `RuoYi-Vue/` so reviewers can verify plan/code alignment.

## Security & Configuration Tips

Do not commit real secrets. Keep local DB and Redis settings in environment-specific configs only. Preserve the current menu/permission naming strategy unless the related backend, frontend, and SQL entries are updated together.
