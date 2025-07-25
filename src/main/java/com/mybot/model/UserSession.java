package com.mybot.model;

import lombok.Data;

@Data
public class UserSession {
    private String name;
    private String phone;
    private String service;
    private String serviceDescription;
    private String promocode;
    private boolean edit;
    private Step step = Step.START;
}
