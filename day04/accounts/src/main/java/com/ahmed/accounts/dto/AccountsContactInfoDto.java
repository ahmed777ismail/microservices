package com.ahmed.accounts.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;



@ConfigurationProperties(prefix = "accounts")
@Getter
@Setter
public class AccountsContactInfoDto {

    private String message;
    private Map<String, String> contactDetails;
    private List<String> onCallSupport;

}