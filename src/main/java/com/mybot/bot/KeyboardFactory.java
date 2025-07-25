package com.mybot.bot;

import com.mybot.model.BotQuery;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class KeyboardFactory {

    public ReplyKeyboard mainMenu() {
        return markup(List.of(row("📝 Оставить заявку", "📋 Узнать про услуги")), false);
    }

    public ReplyKeyboard mainMenu2() {
        return markup(List.of(row(BotQuery.SERVICES.getDescription()),
                row(BotQuery.PROMOCODES.getDescription()),
                row(BotQuery.FAQ.getDescription()),
                row(BotQuery.DEMO.getDescription()),
                row(BotQuery.GET_SERVICES.getDescription())), false);
    }

    public ReplyKeyboard contactChoiceMenu() {
        return markup(List.of(
                row(KeyboardButton.builder().text("📲 Поделиться контактом").requestContact(true).build()),
                row(new KeyboardButton("✏️ Ввести вручную"))
        ), true);
    }


    public ReplyKeyboard contactButton() {
        return markup(List.of(
                row(KeyboardButton.builder().text("📲 Поделиться контактом").requestContact(true).build())
        ), true);
    }


    public ReplyKeyboard getServiceDialogMethod() {
        return markup(List.of(
                row(new KeyboardButton("Заполнить форму")),
                row(new KeyboardButton("Связаться напрямую"))
        ), true);
    }

    public ReplyKeyboard getApplicationButtons() {
        return markup(List.of(
                row(new KeyboardButton("Сохранить")),
                row(new KeyboardButton("Внести изменения"))
        ), true);
    }

    public ReplyKeyboard getEditFieldButtons() {
        return markup(List.of(
                row(new KeyboardButton("Имя")),
                row(new KeyboardButton("Телефон")),
                row(new KeyboardButton("Услуга")),
                row(new KeyboardButton("Описание услуги")),
                row(new KeyboardButton("Промокод"))
        ), true);
    }

    public ReplyKeyboard serviceMenu() {
        return markup(List.of(
                row("🤖 Разработка Telegram-ботов"),
                row("🧩 Создание Mini Apps"),
                row("🔧 Сопровождение и доработка"),
                row("💡 Консультации и проектирование")
        ), true);
    }

    public ReplyKeyboard skip() {
        return markup(List.of(
                row("Пропустить")
        ), true);
    }

    public ReplyKeyboard removeKeyboard() {
        return new ReplyKeyboardRemove(true);
    }

    private KeyboardRow row(String... buttons) {
        KeyboardRow row = new KeyboardRow();
        for (String b : buttons) row.add(new KeyboardButton(b));
        return row;
    }

    private KeyboardRow row(KeyboardButton... buttons) {
        KeyboardRow row = new KeyboardRow();
        for (KeyboardButton b : buttons) row.add(b);
        return row;
    }

    private ReplyKeyboard markup(List<KeyboardRow> rows, boolean oneTime) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(oneTime);
        return markup;
    }
}
