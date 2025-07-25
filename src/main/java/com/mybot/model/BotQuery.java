package com.mybot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotQuery {
    SERVICES("1. Услуги"),
    PROMOCODES("2. Промокоды"),
    FAQ("3. Частые вопросы (FAQ)"),
    DEMO("4. Демо"),
    GET_SERVICES("5. Заказать услугу"),
    GET_CONTACTS("Связаться напрямую"),
    GET_FORM("Заполнить форму");

    private final String description;

    public String getName() {
        return this.name().toLowerCase();
    }

}
