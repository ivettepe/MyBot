package com.mybot.bot;

import com.mybot.model.BotQuery;
import com.mybot.model.Step;
import com.mybot.model.UserSession;
import com.mybot.service.BotService;
import com.mybot.util.InlineKeyboardUtil;
import com.mybot.util.MessageExecutorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotCommandHandler {

    private final BotService botService;
    private final KeyboardFactory keyboards;

    private final static String greetingText = """
                            привет! Это твой личный Telegram-ассистент.
                            📌 Я помогу выбрать услугу и передам заявку нашей команде.
                            👉 Выбери действие ниже:
                            """;

    public void handle(Update update, TelegramLongPollingBot bot) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();
        UserSession session = botService.session(chatId);

        String userFullName = msg.getFrom().getFirstName() + " " + msg.getFrom().getLastName();

        String text = msg.getText();
        if (text.equalsIgnoreCase("/start")) {
            SendMessage reply = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(userFullName + ", " + greetingText)
                    .replyMarkup(keyboards.mainMenu2())
//                    .replyMarkup(InlineKeyboardUtil.generateInlineKeyboardMarkup())
                    .build();
            session.setStep(Step.START);
            MessageExecutorUtil.safeExecute(bot, reply);
        }
    }
}
