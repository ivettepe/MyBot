package com.mybot.bot;

import com.mybot.model.Step;
import com.mybot.model.UserSession;
import com.mybot.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotStepHandler {

    private final BotService botService;
    private final KeyboardFactory keyboards;

    public void handle(Update update, TelegramLongPollingBot bot) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String text = msg.hasText() ? msg.getText() : "";
        UserSession session = botService.session(chatId);

        SendMessage reply = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .build();

        try {
            switch (session.getStep()) {
                case NONE, START -> handleStart(text, session, reply);
                case CONTACT_CHOICE, CONTACT_METHOD -> handleContact(msg, text, session, reply);
                case NAME -> handleName(text, session, reply);
                case PHONE -> handlePhone(text, session, reply);
                case SERVICE -> handleService(text, session, reply, chatId);
                default -> {
                    reply.setText("❌ Неизвестное состояние. Начните заново /start");
                    reply.setReplyMarkup(keyboards.mainMenu());
                    session.setStep(Step.START);
                }
            }
            bot.execute(reply);
        } catch (Exception e) {
            log.error("StepHandler failed", e);
        }
    }

    private void handleStart(String text, UserSession session, SendMessage reply) {
        if (text.equalsIgnoreCase("📝 Оставить заявку")) {
            reply.setText("📞 Как вы хотите оставить контактные данные?");
            reply.setReplyMarkup(keyboards.contactChoiceMenu());
            session.setStep(Step.CONTACT_METHOD);
        } else if (text.equalsIgnoreCase("📋 Узнать про услуги")) {
            reply.setText( botService.getServicesDescription());
            reply.setReplyMarkup(keyboards.mainMenu());
            session.setStep(Step.START);
        } else {
            reply.setText("👉 Выберите действие:");
            reply.setReplyMarkup(keyboards.mainMenu());
        }
    }

    private void handleContact(Message msg, String text, UserSession session, SendMessage reply) {
        if (msg.hasContact()) {
            Contact contact = msg.getContact();
            session.setName(contact.getFirstName());
            session.setPhone(contact.getPhoneNumber());
            reply.setText("📌 Выберите услугу:");
            reply.setReplyMarkup(keyboards.serviceMenu());
            session.setStep(Step.SERVICE);
        } else if (text.equalsIgnoreCase("✏️ Ввести вручную")) {
            reply.setText("✏️ Введите ваше имя:");
            reply.setReplyMarkup(keyboards.removeKeyboard());
            session.setStep(Step.NAME);
        } else {
            reply.setText("❌ Пожалуйста, выберите вариант или поделитесь контактом.");
            reply.setReplyMarkup(keyboards.contactChoiceMenu());
        }
    }

    private void handleName(String text, UserSession session, SendMessage reply) {
        if (text.isBlank()) {
            reply.setText("❌ Имя не может быть пустым. Введите имя:");
        } else {
            session.setName(text.trim());
            reply.setText("📞 Введите телефон (например: +491234567890):");
            reply.setReplyMarkup(keyboards.removeKeyboard());
            session.setStep(Step.PHONE);
        }
    }

    private void handlePhone(String text, UserSession session, SendMessage reply) {
        if (!text.matches("^\\+?\\d{7,15}$")) {
            reply.setText("❌ Неверный формат. Пример: +491234567890");
        } else {
            session.setPhone(text.trim());
            reply.setText("📌 Выберите услугу:");
            reply.setReplyMarkup(keyboards.serviceMenu());
            session.setStep(Step.SERVICE);
        }
    }

    private void handleService(String text, UserSession session, SendMessage reply, Long chatId) {
        List<String> services = List.of(
                "🤖 Разработка Telegram-ботов",
                "🧩 Создание Mini Apps",
                "🔧 Сопровождение и доработка",
                "💡 Консультации и проектирование"
        );
        if (!services.contains(text)) {
            reply.setText("❌ Выберите услугу из списка.");
            reply.setReplyMarkup(keyboards.serviceMenu());
        } else {
            session.setService(text);
            botService.save(chatId);
            reply.setText("""
                    ✅ Заявка сохранена.

                    👤 Имя: %s
                    📞 Телефон: %s
                    📌 Услуга: %s

                    🔁 Оставьте ещё заявку или узнайте про услуги!
                    """.formatted(session.getName(), session.getPhone(), session.getService()));
            reply.setReplyMarkup(keyboards.mainMenu());
            session.setStep(Step.START);
        }
    }
}
