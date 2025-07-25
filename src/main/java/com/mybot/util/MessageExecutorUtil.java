package com.mybot.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Slf4j
@UtilityClass
public class MessageExecutorUtil {
    public void safeExecute(TelegramLongPollingBot bot, SendMessage message) {
        try {
            bot.execute(message);
        } catch (Exception e) {
            log.error("CommandHandler failed to send", e);
        }
    }

    public void safeExecute(TelegramLongPollingBot bot, EditMessageText message) {
        try {
            bot.execute(message);
        } catch (Exception e) {
            log.error("CommandHandler failed to send", e);
        }
    }
}
