package com.paysecure.transactionservice.service;

import com.paysecure.transactionservice.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void sendNotification(TransactionEvent event) {
        log.info("Sending notification event for Transaction ID: {}", event.getTransactionId());
        
        Message<TransactionEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "transaction-events")
                .build();

        kafkaTemplate.send(message);
    }
}