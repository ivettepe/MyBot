package com.mybot.model;

import lombok.Data;

@Data
public class UserSession {
    private String name;
    private String phone;
    private String service;
    private Step step = Step.START;
}
