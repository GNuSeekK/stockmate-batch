package com.stockmate.batch.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlackService {

    @Value("${slack.token}")
    private String slackToken;

    @Async
    public void sendMessage(String message, String channel) {
        String channelAddress = channel.startsWith("#") ? channel : "#" + channel;

        try {
            MethodsClient methods = Slack.getInstance().methods(slackToken);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelAddress)
                .text(message)
                .build();

            methods.chatPostMessage(request);

        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, SlackConstant.TRADING_CHANNEL);
    }

}
