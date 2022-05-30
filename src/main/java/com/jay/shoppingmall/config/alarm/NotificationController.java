package com.jay.shoppingmall.config.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessage getMessage(@Payload Message message) {
        return new ResponseMessage(HtmlUtils.htmlEscape(message.getMessageContent()));
    }
}