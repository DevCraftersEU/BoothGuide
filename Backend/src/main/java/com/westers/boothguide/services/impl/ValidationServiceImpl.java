package com.westers.boothguide.services.impl;

import com.westers.boothguide.services.ValidationService;
import jakarta.annotation.PostConstruct;
import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.CzechSequenceData;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.GermanCharacterData;
import org.passay.GermanSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PolishSequenceData;
import org.passay.PropertiesMessageResolver;
import org.passay.RepeatCharactersRule;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.passay.WhitespaceRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Service
public class ValidationServiceImpl implements ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);

    private final URL resource = this.getClass().getClassLoader().getResource("password-rules-messages.properties");

    private MessageResolver messageResolver;

    @PostConstruct
    public void init() {
        try {
            Properties props = new Properties();
            assert resource != null;
            props.load(new FileInputStream(resource.getPath()));
            messageResolver = new PropertiesMessageResolver(props);
        } catch (IOException e) {
            logger.error(e.getMessage());
            messageResolver = new PropertiesMessageResolver(new Properties());
        }
    }

    @Override
    public RuleResult isValidPassword(String username, String password) {
        var complexityRule = new CharacterCharacteristicsRule(password.trim().length() < 15 ? 3 : 2,
                new CharacterRule(GermanCharacterData.LowerCase),
                new CharacterRule(GermanCharacterData.UpperCase),
                new CharacterRule(EnglishCharacterData.Digit),
                new CharacterRule(EnglishCharacterData.Special));

        PasswordValidator validator = new PasswordValidator(
                new LengthRule(8, 4096),
                new RepeatCharactersRule(3, 3),
                new UsernameRule(),
                complexityRule,
                new IllegalSequenceRule(GermanSequenceData.Alphabetical),
                new IllegalSequenceRule(GermanSequenceData.DEQwertz),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical),
                new IllegalSequenceRule(EnglishSequenceData.Numerical),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty),
                new IllegalSequenceRule(CzechSequenceData.Alphabetical),
                new IllegalSequenceRule(PolishSequenceData.Alphabetical),
                new WhitespaceRule());

        PasswordData passwordData = new PasswordData(password);
        passwordData.setUsername(username);
        return validator.validate(passwordData);
    }

    @Bean
    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
}
