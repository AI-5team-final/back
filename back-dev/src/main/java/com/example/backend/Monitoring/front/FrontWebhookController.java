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
public class FrontWebhookController {

    private final DiscordNotifier notifier;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String FRONT_ISSUE_CHANNEL_ID = "1355438955696492555";
    private static final String FRONT_PR_CHANNEL_ID = "1356087411817316352";
    private static final String FRONT_PUSH_CHANNEL_ID = "1355439860676034651";

    @PostMapping("/webhook-front")
    public ResponseEntity<String> handleGitHubWebhook(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestBody String payloadJson
    ) {
        try {
            switch (event) {
                case "issues" -> handleIssue(payloadJson);
                case "pull_request" -> handlePullRequest(payloadJson);
                case "push" -> handlePush(payloadJson);
                default -> System.out.println("Unhandled GitHub event: " + event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error while handling webhook.");
        }

        return ResponseEntity.ok("GitHub Webhook received.");
    }

    private void handleIssue(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String title = node.get("issue").get("title").asText();
        String body = node.get("issue").get("body").asText("");
        String url = node.get("issue").get("html_url").asText();
        String user = node.get("issue").get("user").get("login").asText();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🐛 New Issue: " + escape(title), url)
                .setDescription(escape(body))
                .setFooter("by " + escape(user))
                .setColor(0xE67E22); // 주황색

        notifier.sendEmbedBuilder(FRONT_ISSUE_CHANNEL_ID, embed);
    }

    private void handlePullRequest(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String title = node.get("pull_request").get("title").asText();
        String url = node.get("pull_request").get("html_url").asText();
        String user = node.get("pull_request").get("user").get("login").asText();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📦 Pull Request: " + escape(title), url)
                .setDescription("새로운 PR이 등록되었습니다.")
                .setFooter("by " + escape(user))
                .setColor(0x9B59B6); // 보라색

        notifier.sendEmbedBuilder(FRONT_PR_CHANNEL_ID, embed);
    }

    private void handlePush(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String pusher = node.get("pusher").get("name").asText();
        String branch = node.get("ref").asText().replace("refs/heads/", "");
        String repo = node.get("repository").get("full_name").asText();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🚀 New Push to " + escape(repo))
                .setDescription("브랜치: `" + escape(branch) + "`\n푸셔: **" + escape(pusher) + "**")
                .setColor(0x2ECC71); // 초록색

        notifier.sendEmbedBuilder(FRONT_PUSH_CHANNEL_ID, embed);
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
