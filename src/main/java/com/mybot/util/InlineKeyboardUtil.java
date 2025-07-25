package com.mybot.util;

import com.mybot.model.BotQuery;
import com.mybot.model.Service;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@UtilityClass
public class InlineKeyboardUtil {
    public InlineKeyboardMarkup generateMainMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.SERVICES.getDescription())
                        .callbackData(BotQuery.SERVICES.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.PROMOCODES.getDescription())
                        .callbackData(BotQuery.PROMOCODES.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.FAQ.getDescription())
                        .callbackData(BotQuery.FAQ.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.DEMO.getDescription())
                        .callbackData(BotQuery.DEMO.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.GET_SERVICES.getDescription())
                        .callbackData(BotQuery.GET_SERVICES.getName())
                        .build()))
                .build();
    }

    public InlineKeyboardMarkup generateGetServiceMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.GET_FORM.getDescription())
                        .callbackData(BotQuery.GET_FORM.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(BotQuery.GET_CONTACTS.getDescription())
                        .callbackData(BotQuery.GET_CONTACTS.getName())
                        .build()))
                .build();
    }

    public InlineKeyboardMarkup generateServicesList() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.INFO_BOT.getDescription())
                        .callbackData(Service.INFO_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.SERVICE_BOT.getDescription())
                        .callbackData(Service.SERVICE_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.AI_BOT.getDescription())
                        .callbackData(Service.AI_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.QUIZ_BOT.getDescription())
                        .callbackData(Service.QUIZ_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.SHOP_BOT.getDescription())
                        .callbackData(Service.SHOP_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.MINI_APPS.getDescription())
                        .callbackData(Service.MINI_APPS.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.ADMIN_BOT.getDescription())
                        .callbackData(Service.ADMIN_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.NOTIFY_BOT.getDescription())
                        .callbackData(Service.NOTIFY_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.INTERNAL_BOT.getDescription())
                        .callbackData(Service.INTERNAL_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.INTEGRATION_BOT.getDescription())
                        .callbackData(Service.INTEGRATION_BOT.getName())
                        .build()))
                .keyboardRow(List.of(InlineKeyboardButton
                        .builder()
                        .text(Service.ANOTHER_CHOICE.getDescription())
                        .callbackData(Service.ANOTHER_CHOICE.getName())
                        .build()))
                .build();
    }
}
