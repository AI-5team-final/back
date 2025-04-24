package com.example.backend.Monitoring.back;

import com.example.backend.Monitoring.DiscordNotifier;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
@RequiredArgsConstructor
public class BackErrorHandler {

    private final DiscordNotifier notifier;
    private static final String SPRING_ERROR_CHANNEL_ID = "1355363528936132639";

    @ExceptionHandler(Exception.class) // 모든 예외 감지
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        String className = ex.getStackTrace()[0].getClassName();
        String methodName = ex.getStackTrace()[0].getMethodName();
        int lineNumber = ex.getStackTrace()[0].getLineNumber();

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🔥 Spring 서버 예외 발생")
                .setColor(0xE74C3C)
                .setDescription(String.format("📍 **%s.%s()** 에서 예외 발생\n📎 **%d번째 줄**", className, methodName, lineNumber))
                .addField("🧾 예외 메시지", message != null ? "```" + message + "```" : "`(메시지 없음)`", false);

        try {
            notifier.sendEmbedBuilder(SPRING_ERROR_CHANNEL_ID, embed);
        } catch (Exception e) {
            e.printStackTrace(); // 또는 log.error("디스코드 전송 실패", e);
        }
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
