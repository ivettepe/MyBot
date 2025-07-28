package com.mybot.util;

import com.mybot.model.Service;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceUtil {
    public boolean isBotOrAppsByName(String data) {
        return data.equals(Service.INFO_BOT.getName()) ||
                data.equals(Service.SERVICE_BOT.getName()) ||
                data.equals(Service.AI_BOT.getName()) ||
                data.equals(Service.QUIZ_BOT.getName()) ||
                data.equals(Service.SHOP_BOT.getName()) ||
                data.equals(Service.MINI_APPS.getName()) ||
                data.equals(Service.ADMIN_BOT.getName()) ||
                data.equals(Service.NOTIFY_BOT.getName()) ||
                data.equals(Service.INTERNAL_BOT.getName()) ||
                data.equals(Service.INTEGRATION_BOT.getName());
    }

    public boolean isBotOrAppsByDiscription(String data) {
        return data.equals(Service.INFO_BOT.getDescription()) ||
                data.equals(Service.SERVICE_BOT.getDescription()) ||
                data.equals(Service.AI_BOT.getDescription()) ||
                data.equals(Service.QUIZ_BOT.getDescription()) ||
                data.equals(Service.SHOP_BOT.getDescription()) ||
                data.equals(Service.MINI_APPS.getDescription()) ||
                data.equals(Service.ADMIN_BOT.getDescription()) ||
                data.equals(Service.NOTIFY_BOT.getDescription()) ||
                data.equals(Service.INTERNAL_BOT.getDescription()) ||
                data.equals(Service.INTEGRATION_BOT.getDescription());
    }

    public boolean isServiceByDiscription(String data) {
        return isBotOrAppsByDiscription(data) || data.equals(Service.ANOTHER_CHOICE.getDescription());
    }

    public Service getBotService(String callData) {
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
