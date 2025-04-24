package com.example.backend.Monitoring;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Component;

@Component
public class DiscordNotifier {

    private final JDA jda;

    public DiscordNotifier(JDA jda) {
        this.jda = jda;
    }

    // 텍스트 메시지 전송
    public void sendToDiscordByChannelId(String channelId, String message) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        } else {
            System.err.println("❌ [봇] 채널 ID를 찾을 수 없습니다: " + channelId);
        }
    }

    // Embed 메시지 전송
    public void sendEmbedToChannel(String channelId, MessageEmbed embed) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessageEmbeds(embed).queue();
        } else {
            System.err.println("❌ [봇] 채널 ID를 찾을 수 없습니다: " + channelId);
        }
    }

    // EmbedBuilder를 직접 받아서 전송하는 편의 메서드
    public void sendEmbedBuilder(String channelId, EmbedBuilder builder) {
        sendEmbedToChannel(channelId, builder.build());
    }
}
