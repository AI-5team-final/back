package com.example.backend.Monitoring.front;

import com.example.backend.Monitoring.DiscordNotifier;
import com.example.backend.config.aws.EnvUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FrontErrorController {

    private final DiscordNotifier notifier;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String FRONT_ERROR_CHANNEL_ID = "1356087532474859572";

    @PostMapping("/front-error")
    public ResponseEntity<String> receiveFrontError(@RequestBody String payloadJson) {
        try {
            JsonNode node = mapper.readTree(payloadJson);

            String message = node.has("message") ? node.get("message").asText() : "(no message)";
            String stack = node.has("stack") ? node.get("stack").asText() : "(no stack)";
            String url = node.has("url") ? node.get("url").asText() : "(unknown)";
            String userAgent = node.has("userAgent") ? node.get("userAgent").asText() : "(unknown)";
            String time = node.has("time") ? node.get("time").asText() : "(unknown)";

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🚨 React 앱 에러 발생")
                    .setDescription(String.format("**메시지:** %s\n**페이지:** %s\n**시간:** %s", escape(message), escape(url), escape(time)))
                    .addField("Stack", escapeShort(stack), false)
                    .addField("브라우저", escape(userAgent), false)
                    .setColor(0xE74C3C); // 붉은 색

            notifier.sendEmbedBuilder(FRONT_ERROR_CHANNEL_ID, embed);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error while processing front error.");
        }

        return ResponseEntity.ok("React error received.");
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private String escapeShort(String text) {
        return escape(text.length() > 200 ? text.substring(0, 200) + "..." : text);
    }
}
