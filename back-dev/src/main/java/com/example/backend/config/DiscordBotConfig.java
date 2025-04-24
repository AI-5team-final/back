package com.example.backend.config;

import com.example.backend.config.aws.EnvUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordBotConfig {

    private static final String botToken = EnvUtils.get("DiSCORD_BOT_TOKEN");
    @Bean
    public JDA jda() throws Exception {
        return JDABuilder.createDefault(botToken).build().awaitReady();
    }
}