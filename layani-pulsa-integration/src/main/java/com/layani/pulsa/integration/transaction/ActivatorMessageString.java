package com.layani.pulsa.integration.transaction;

import org.slerp.core.Domain;
import org.springframework.messaging.Message;

public interface ActivatorMessageString {
    Message<Domain> execute(Message<String> message);
}
