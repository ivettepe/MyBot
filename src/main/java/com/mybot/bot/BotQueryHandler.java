package com.mybot.bot;

import com.mybot.model.Service;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.mybot.model.BotQuery.*;
        import static java.lang.Math.toIntExact;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotQueryHandler {
    private final BotService botService;
    private final KeyboardFactory keyboards;

    public void handle(Update update, TelegramLongPollingBot bot) {
        var msg = update.getCallbackQuery().getMessage();
        long chatId = msg.getChatId();
        long msgId = msg.getMessageId();

        String callData = update.getCallbackQuery().getData();
        if (callData.equals(GET_CONTACTS.getName())) {
            getContactsHandle(chatId, msgId, bot);
        } else if (callData.equals(GET_FORM.getName())) {
            getFormHandle(chatId, msgId, bot);
        } else if (isBotOrApps(callData)) {
            Service service = getBotService(callData);
            formBotOrAppsChoice(chatId, msgId, bot, service);
        } else if (callData.equals(Service.ANOTHER_CHOICE.getName())) {
            Service service = getBotService(callData);
            formBotOrAppsChoice(chatId, msgId, bot, service);
        } else {
            log.error("QueryHandler failed");
        }
    }

    private void getContactsHandle(Long chatId, Long msgId, TelegramLongPollingBot bot) {
        String answer = "Если не хотите ждать, напишите нам @___ или позвоните по номеру +7";
        EditMessageText newMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(msgId))
                .text(answer)
                .build();
        MessageExecutorUtil.safeExecute(bot, newMessage);
    }

    private void getFormHandle(Long chatId, Long msgId, TelegramLongPollingBot bot) {
        String answer = "1. Выберите услугу:";
        EditMessageText newMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(msgId))
                .replyMarkup(InlineKeyboardUtil.generateServicesList())
                .text(answer)
                .build();
        botService.session(chatId).setStep(Step.START);
        MessageExecutorUtil.safeExecute(bot, newMessage);
    }

    private void formBotOrAppsChoice(Long chatId, Long msgId, TelegramLongPollingBot bot, Service service) {
        String answer = "1. Выберите услугу: " + service.getDescription();
        EditMessageText newMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(msgId))
                .text(answer)
                .build();
        botService.session(chatId).setStep(Step.TASK_DESCRIPTION);
        botService.session(chatId).setService(service.getDescription());
        MessageExecutorUtil.safeExecute(bot, newMessage);

        SendMessage reply = SendMessage.builder()
                .chatId(chatId.toString())
                .text("2. Кратко опишите задачу, или нажмите кнопку пропустить (Не более 500 символов)")
                .parseMode("Markdown")
                .replyMarkup(keyboards.removeKeyboard())
                .replyMarkup(keyboards.skip())
                .build();
        MessageExecutorUtil.safeExecute(bot, reply);

    }

    private boolean isBotOrApps(String callData) {
        return callData.equals(Service.INFO_BOT.getName()) ||
                callData.equals(Service.SERVICE_BOT.getName()) ||
                callData.equals(Service.AI_BOT.getName()) ||
                callData.equals(Service.QUIZ_BOT.getName()) ||
                callData.equals(Service.SHOP_BOT.getName()) ||
                callData.equals(Service.MINI_APPS.getName()) ||
                callData.equals(Service.ADMIN_BOT.getName()) ||
                callData.equals(Service.NOTIFY_BOT.getName()) ||
                callData.equals(Service.INTERNAL_BOT.getName()) ||
                callData.equals(Service.INTEGRATION_BOT.getName());
    }

    private Service getBotService(String callData) {
        if (callData.equals(Service.INFO_BOT.getName())) {
            return Service.INFO_BOT;
        } else if (callData.equals(Service.SERVICE_BOT.getName())) {
            return Service.SERVICE_BOT;
        } else if (callData.equals(Service.AI_BOT.getName())) {
            return Service.AI_BOT;
        } else if (callData.equals(Service.QUIZ_BOT.getName())) {
            return Service.QUIZ_BOT;
        } else if (callData.equals(Service.SHOP_BOT.getName())) {
            return Service.SHOP_BOT;
        } else if (callData.equals(Service.MINI_APPS.getName())) {
            return Service.MINI_APPS;
        } else if (callData.equals(Service.ADMIN_BOT.getName())) {
            return Service.ADMIN_BOT;
        } else if (callData.equals(Service.NOTIFY_BOT.getName())) {
            return Service.NOTIFY_BOT;
        } else if (callData.equals(Service.INTERNAL_BOT.getName())) {
            return Service.INTERNAL_BOT;
        } else if (callData.equals(Service.INTEGRATION_BOT.getName())) {
            return Service.INTEGRATION_BOT;
        } else {
            return Service.ANOTHER_CHOICE;
        }
    }

}
