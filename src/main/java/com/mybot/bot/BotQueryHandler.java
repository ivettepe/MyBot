package com.mybot.bot;

import com.mybot.model.Service;
import com.mybot.model.Step;
import com.mybot.service.BotService;
import com.mybot.util.InlineKeyboardUtil;
import com.mybot.util.MessageExecutorUtil;
import com.mybot.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

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
        } else if (ServiceUtil.isBotOrAppsByName(callData) || callData.equals(Service.ANOTHER_CHOICE.getName())) {
            Service service = ServiceUtil.getBotService(callData);
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

}
