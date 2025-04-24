package com.example.backend.Monitoring.back;

import com.example.backend.Monitoring.DiscordNotifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BackWebhookController {

    private final DiscordNotifier notifier;
    private final ObjectMapper mapper = new ObjectMapper();

    // 채널 ID는 .env 또는 application.yml → EnvUtils 통해 가져옴
    private static final String ISSUE_CHANNEL_ID = "1355364022672691271";
    private static final String PR_CHANNEL_ID = "1355368829592539224";
    private static final String PUSH_CHANNEL_ID = "1355368814140723340";

    @PostMapping("/webhook-back")
    public ResponseEntity<String> receiveWebhook(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestBody String payloadJson) {
        try {
            switch (event) {
                case "issues" -> handleIssue(payloadJson);
                case "pull_request" -> handlePullRequest(payloadJson);
                case "push" -> handlePush(payloadJson);
                default -> System.out.println("Unhandled event: " + event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Received");
    }

    private void handleIssue(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String title = node.get("issue").get("title").asText();
        String body = node.get("issue").get("body").asText("");
        String url = node.get("issue").get("html_url").asText();
        String user = node.get("issue").get("user").get("login").asText();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📝 New Issue: " + title, url)
                .setDescription(body)
                .setFooter("by " + user)
                .setColor(0x3498db); // 파란색

        notifier.sendEmbedBuilder(ISSUE_CHANNEL_ID, embed);
    }

    private void handlePullRequest(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String title = node.get("pull_request").get("title").asText();
        String url = node.get("pull_request").get("html_url").asText();
        String user = node.get("pull_request").get("user").get("login").asText();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📦 Pull Request: " + title, url)
                .setDescription("새로운 PR이 등록되었습니다.")
                .setFooter("by " + user)
                .setColor(0x9b59b6); // 보라색

        notifier.sendEmbedBuilder(PR_CHANNEL_ID, embed);
    }

    private void handlePush(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String pusher = node.get("pusher").get("name").asText();
        String branch = node.get("ref").asText().replace("refs/heads/", "");
        String repo = node.get("repository").get("full_name").asText();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🚀 New Push to " + repo)
                .setDescription("브랜치: `" + branch + "`\n푸셔: **" + pusher + "**")
                .setColor(0x2ecc71); // 초록색

        notifier.sendEmbedBuilder(PUSH_CHANNEL_ID, embed);
    }
}
