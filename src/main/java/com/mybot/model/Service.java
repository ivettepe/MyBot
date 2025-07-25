package com.mybot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Service {
    INFO_BOT("💬 1. Информационный бот"),
    SERVICE_BOT("📦 2. Сервисные бот (Услуги)"),
    AI_BOT("🤖 3. ИИ-боты (нейросети и ML)"),
    QUIZ_BOT("📲 4. Квиз-боты и воронки"),
    SHOP_BOT("🛒 5. Боты-продажники / каталоги / магазины"),
    MINI_APPS("🧩 6. MiniApps (боты с WebApp-интерфейсом)"),
    ADMIN_BOT("📢 7. Боты для админов/групп/чатов"),
    NOTIFY_BOT("📬 8. Рассылочные и уведомительные боты"),
    INTERNAL_BOT("🔒 9. Закрытые/внутренние боты"),
    INTEGRATION_BOT("⚙️ 10. Интеграционные и технические боты"),
    ANOTHER_CHOICE("11. Другой вариант");

    private final String description;

    public String getName() {
        return this.name().toLowerCase();
    }
}
