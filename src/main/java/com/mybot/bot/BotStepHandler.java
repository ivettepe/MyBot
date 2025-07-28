package com.mybot.bot;

import com.mybot.model.BotQuery;
import com.mybot.model.Step;
import com.mybot.model.UserSession;
import com.mybot.service.BotService;
import com.mybot.util.InlineKeyboardUtil;
import com.mybot.util.MessageExecutorUtil;
import com.mybot.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotStepHandler {

    private final BotService botService;
    private final KeyboardFactory keyboards;

    public void handle(Update update, TelegramLongPollingBot bot) {
        var msg = update.getMessage();

        Long chatId = getChatId(update);
        String text = getMsgText(update);
        UserSession session = botService.session(chatId);

        SendMessage reply = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .build();

        boolean isEdit = session.isEdit();
        boolean isQuery = false;
        log.info("session {}", session);
        switch (session.getStep()) {
            case NONE, START -> isQuery = handleStart(update, bot, text, session, reply);
            case TASK_DESCRIPTION -> handleTaskDescription(text, session, reply, isEdit);
            case NAME -> handleName(text, session, reply, isEdit);
            case PHONE -> handlePhone(msg, text, session, reply, isEdit);
            case PROMOCODE -> handlePromocode(text, session, reply, isEdit);
            case SERVICE -> handleService(text, session, reply);
            case SAVE_OR_EDIT -> handleSaveOrEdit(chatId, text, session, reply);
            case EDIT -> handleEdit(text, session, reply);
            case MAIN_MENU -> handleMainMenu(reply);
            default -> {
                reply.setText("❌ Неизвестное состояние. Начните заново /start");
                reply.setReplyMarkup(keyboards.mainMenu());
                session.setStep(Step.START);
            }
        }
        if (!isQuery) {
            MessageExecutorUtil.safeExecute(bot, reply);
        }
    }

    private boolean handleStart(Update update, TelegramLongPollingBot bot, String text, UserSession session, SendMessage reply) {
        boolean isQuery = false;
        if (text.equalsIgnoreCase(BotQuery.SERVICES.getDescription())) {
            reply.setText(botService.getServicesDescription());
            reply.setReplyMarkup(keyboards.mainMenu2());
            session.setStep(Step.START);
        } else if (text.equalsIgnoreCase(BotQuery.PROMOCODES.getDescription())) {
            reply.setText("Какие-то промокоды");
            reply.setReplyMarkup(keyboards.mainMenu2());
            session.setStep(Step.START);
        } else if (text.equalsIgnoreCase(BotQuery.FAQ.getDescription())) {
            reply.setText("""
                    Часто задавыемые вопросы:
                    1. ...
                    2. ...
                    3. ...
                    4. ...
                    """);
            reply.setReplyMarkup(keyboards.mainMenu2());
            session.setStep(Step.START);
        } else if (text.equalsIgnoreCase(BotQuery.DEMO.getDescription())) {
            reply.setText("Тут должно быть демо");
            reply.setReplyMarkup(keyboards.mainMenu2());
            session.setStep(Step.START);
        } else if (text.equalsIgnoreCase(BotQuery.GET_SERVICES.getDescription())) {
            reply = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Как вы хотите связаться с нами?")
                    .replyMarkup(InlineKeyboardUtil.generateGetServiceMenu())
                    .build();
            isQuery = true;
            MessageExecutorUtil.safeExecute(bot, reply);
        } else {
            reply.setText("👉 Выберите действие:");
            reply.setReplyMarkup(keyboards.mainMenu2());
        }
        return isQuery;
    }

    private void handleTaskDescription(String text, UserSession session, SendMessage reply, boolean isEdit) {
        if (text.length() > 500) {
            reply.setText("❌ Пожалуйста, опишите услугу более кратко. Максимальная длина сообщения 500 символов");
            reply.setReplyMarkup(keyboards.removeKeyboard());
            reply.setReplyMarkup(keyboards.skip());
        } else {
            session.setServiceDescription(!"Пропустить".equals(text) ? text : "");
            if (isEdit) {
                session.setEdit(false);
                setApplicationText(session, reply);
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.getApplicationButtons());
                session.setStep(Step.SAVE_OR_EDIT);
            } else {
                reply.setText("3. Введите ваше имя");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                session.setStep(Step.NAME);
            }
        }
    }

    private void handleName(String text, UserSession session, SendMessage reply, boolean isEdit) {
        if (text.length() < 2) {
            reply.setText("❌ Пожалуйста, проверьте свои имя. Оно не должно быть короче 2 символов.");
            reply.setReplyMarkup(keyboards.removeKeyboard());
        } else {
            session.setName(text);
            if (isEdit) {
                session.setEdit(false);
                setApplicationText(session, reply);
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.getApplicationButtons());
                session.setStep(Step.SAVE_OR_EDIT);
            } else {
                reply.setText("4. Укажите ваш номер телефона или нажмите кнопку поделиться контактом");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.contactButton());
                session.setStep(Step.PHONE);
            }
        }
    }

    private void handlePhone(Message message, String text, UserSession session, SendMessage reply, boolean isEdit) {
        if (message.hasContact()) {
            Contact contact = message.getContact();
            session.setPhone(contact.getPhoneNumber());
            if (isEdit) {
                session.setEdit(false);
                setApplicationText(session, reply);
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.getApplicationButtons());
                session.setStep(Step.SAVE_OR_EDIT);
            } else {
                reply.setText("5. Введите промокод или нажмите пропустить");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.skip());
                session.setStep(Step.PROMOCODE);
            }
        } else if (!text.matches("^\\+?\\d{10,13}$") || (!text.startsWith("+7") && !text.startsWith("8"))) {
            reply.setText("❌ Неверный формат. Пример: +71234567890");
        } else {
            session.setPhone(text);
            if (isEdit) {
                session.setEdit(false);
                setApplicationText(session, reply);
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.getApplicationButtons());
                session.setStep(Step.SAVE_OR_EDIT);
            } else {
                reply.setText("5. Введите промокод или нажмите пропустить");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.skip());
                session.setStep(Step.PROMOCODE);
            }
        }
    }

    private void handleService(String text, UserSession session, SendMessage reply) {
        if (!ServiceUtil.isServiceByDiscription(text)) {
            reply.setText("❌ Выберете одну из услуг ниже.");
        } else {
            session.setService(text);
            session.setEdit(false);
            setApplicationText(session, reply);
            reply.setReplyMarkup(keyboards.removeKeyboard());
            reply.setReplyMarkup(keyboards.getApplicationButtons());
            session.setStep(Step.SAVE_OR_EDIT);
        }
    }

    private void handleEdit(String text, UserSession session, SendMessage reply) {
        switch (text) {
            case "Имя" -> {
                reply.setText("3. Введите ваше имя");
                session.setStep(Step.NAME);
            }
            case "Телефон" -> {
                reply.setText("4. Укажите ваш номер телефона или нажмите кнопку поделиться контактом");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.contactButton());
                session.setStep(Step.PHONE);
            }
            case "Услуга" -> {
                reply.setText("1. Выберите услугу");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.serviceMenu());
                session.setStep(Step.SERVICE);
            }
            case "Описание услуги" -> {
                reply.setText("2. Кратко опишите задачу, или нажмите кнопку пропустить (Не более 500 символов)");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.skip());
                session.setStep(Step.TASK_DESCRIPTION);
            }
            case "Промокод" -> {
                reply.setText("5. Введите промокод или нажмите пропустить");
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.skip());
                session.setStep(Step.PROMOCODE);
            }
            case null, default -> reply.setText("❌ Пожалуйста, выберете одно из действий ниже.");
        }
    }

    private void handlePromocode(String text, UserSession session, SendMessage reply, boolean isEdit) {
        if (false) {

        } else {
            session.setPromocode(!"Пропустить".equals(text) ? text : "");
            if (isEdit) {
                session.setEdit(false);
                setApplicationText(session, reply);
                reply.setReplyMarkup(keyboards.removeKeyboard());
                reply.setReplyMarkup(keyboards.getApplicationButtons());
                session.setStep(Step.SAVE_OR_EDIT);
            } else {
                setApplicationText(session, reply);
                reply.setReplyMarkup(keyboards.getApplicationButtons());
                session.setStep(Step.SAVE_OR_EDIT);
            }
        }
    }

    private void handleSaveOrEdit(Long chatId, String text, UserSession session, SendMessage reply) {
        if ("Сохранить".equals(text)) {
            botService.save(chatId);
            reply.setText("""
                    ✅ Заявка сохранена.
                    
                    👤 Имя: %s
                    📞 Телефон: %s
                    📌 Услуга: %s
                    Описание услуги: %s
                    Промокод: %s
                    
                    🔁 Оставьте ещё заявку или узнайте про услуги!
                    """.formatted(session.getName(), session.getPhone(),
                    session.getService(), session.getServiceDescription(), session.getPromocode()));
            reply.setReplyMarkup(keyboards.mainMenu2());
            session.setStep(Step.START);
        } else if ("Внести изменения".equals(text)) {
            reply.setText("Какое поле бы вы хотели изменить");
            reply.setReplyMarkup(keyboards.removeKeyboard());
            reply.setReplyMarkup(keyboards.getEditFieldButtons());
            session.setStep(Step.EDIT);
            session.setEdit(true);
        } else {
            reply.setText("❌ Пожалуйста, выберете одно из действий ниже.");
        }
    }

    private void setApplicationText(UserSession session, SendMessage reply) {
        reply.setText("""
                Ваша заявка:
                
                👤 Имя: %s
                📞 Телефон: %s
                📌 Услуга: %s
                Описание услуги: %s
                Промокод: %s
                
                🔁 Нажмите кнопку исправить или сохранить.
                """.formatted(session.getName(), session.getPhone(),
                session.getService(), session.getServiceDescription(), session.getPromocode()));
    }

    private void handleMainMenu(SendMessage reply) {
        reply.setText("👉 Выберите действие:");
        reply.setReplyMarkup(keyboards.mainMenu2());
    }

    private Long getChatId(Update update) {
        return update.getMessage() != null ? update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
    }

    private String getMsgText(Update update) {
        return update.getMessage() != null && update.getMessage().hasText() ? update.getMessage().getText() : "";
    }
}
