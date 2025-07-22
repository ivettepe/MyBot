package com.mybot.service;

import com.mybot.config.BotConfig;
import com.mybot.model.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final GoogleSheetsService sheetsService;
    private final BotConfig config;

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession session(Long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new UserSession());
    }

    public void save(Long chatId) {
        var s = sessions.get(chatId);
        sheetsService.appendRow(config.getSheetId(), List.of(
                s.getName(),
                s.getPhone(),
                s.getService(),
                LocalDateTime.now().toString()
        ));
        sessions.remove(chatId);
    }

    public String getServicesDescription() {
        return """
               📋 *Наши услуги:*

               *Разработка Telegram-ботов под ключ:*
               – Автоматизация заявок, рассылок, FAQ, квизов
               – Воронки, формы, CRM-интеграции

               *Создание Mini Apps:*
               – Интерфейс с кнопками, формами, каталогами
               – Подключение к API, базам данных, платёжным системам

               *Сопровождение и доработка:*
               – Поддержка существующих решений
               – Рефакторинг, новые функции
               – Оптимизация скорости

               *Консультации и проектирование:*
               – Спроектируем логику под задачу
               – Оценим сложность и сроки
               – Лучшие практики
               """;
    }
}
