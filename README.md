# 📋 Журнал посещаемости

> Мобильное и десктопное приложение для старосты учебной группы.
> Заменяет бумажный журнал: отметка присутствия, цепочка подтверждений (Староста → Учитель → Куратор → Директор), статистика, экспорт CSV.

---

## 🗂 Структура репозитория

```
attendance-journal/
├── android/          # Android-приложение (Kotlin, Jetpack Compose, Room)
├── desktop/          # Десктоп-версия (Compose Multiplatform — Windows/macOS/Linux)
└── .github/
    └── workflows/
        ├── android.yml   # CI: сборка Debug APK при каждом пуше
        └── desktop.yml   # CI: сборка Desktop JAR
```

---

## 📱 Android

### Требования
- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17
- Android SDK API 26+
- Файл `android/app/google-services.json` (Firebase — см. ниже)

### Запуск
```bash
cd android
./gradlew assembleDebug
```
APK будет в: `android/app/build/outputs/apk/debug/app-debug.apk`

### Скачать APK
Готовый APK скачивается на вкладке **[Releases](../../releases)** или в **[Actions](../../actions)** → последний успешный build → Artifacts → `app-debug.apk`.

### Firebase (FCM — push-уведомления)
1. Создай проект на [firebase.google.com](https://firebase.google.com)
2. Добавь Android-приложение с package name `com.attendance.app`
3. Скачай `google-services.json` и положи в `android/app/google-services.json`
4. Пересобери проект

---

## 🖥 Desktop (Compose Multiplatform)

### Требования
- JDK 17+
- IntelliJ IDEA или любая IDE с поддержкой Kotlin

### Запуск
```bash
cd desktop
./gradlew run
```

### Сборка в нативный исполняемый файл
```bash
# Windows → .exe + installer
./gradlew packageMsi

# macOS → .dmg
./gradlew packageDmg

# Linux → .deb
./gradlew packageDeb
```

---

## 🏗 Архитектура

| Слой | Технология |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Архитектура | Clean Architecture + MVVM |
| DI | Hilt |
| Локальная БД | Room (офлайн-кэш + черновики) |
| Сеть | Retrofit2 + OkHttp3 + Moshi |
| Async | Coroutines + Flow |
| Auth | JWT + 2FA (email / SMS) |
| Push | Firebase Cloud Messaging (FCM) |
| Фоновые задачи | WorkManager |
| Экспорт | CSV через FileProvider + ShareSheet |

### Роли и цепочка подтверждения
```
Староста → отмечает посещаемость
    ↓
Учитель → подтверждает / корректирует
    ↓
Куратор → финальное одобрение (данные уходят на сервер)
    ↓
Директор → видит общие отчёты
```

---

## 📦 Статусы посещаемости

| Статус | Обозначение |
|---|---|
| Присутствует | ✓ |
| Отсутствует (уважительная) | УП |
| Отсутствует (неуважительная) | НП |

---

## 🛠 Tech Stack

- **Kotlin** 1.9+
- **Jetpack Compose** + Material 3
- **Room** 2.6+
- **Hilt** 2.50+
- **Retrofit** 2.9+
- **Firebase** (FCM)
- **WorkManager** 2.9+
- **Compose Multiplatform** 1.6+ (desktop)

---

## 📄 Лицензия

MIT License — свободно для учебных и некоммерческих проектов.
