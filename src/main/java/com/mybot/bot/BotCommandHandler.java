package com.mybot.bot;

import com.mybot.model.Step;
import com.mybot.model.UserSession;
import com.mybot.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotCommandHandler {

    private final BotService botService;
    private final KeyboardFactory keyboards;

    public void handle(Update update, TelegramLongPollingBot bot) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();
        UserSession session = botService.session(chatId);

        String text = msg.getText();
        if (text.equalsIgnoreCase("/start")) {
            SendMessage reply = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("""
                            👋 Привет! Это твой личный Telegram-ассистент.
                            📌 Я помогу выбрать услугу и передам заявку нашей команде.
                            👉 Выбери действие ниже:
                            """)
                    .replyMarkup(keyboards.mainMenu())
                    .build();
            session.setStep(Step.START);
            safeExecute(bot, reply);
        }
    }

    private void safeExecute(TelegramLongPollingBot bot, SendMessage message) {
        try {
            bot.execute(message);
        } catch (Exception e) {
            log.error("CommandHandler failed to send", e);
        }
    }
}
