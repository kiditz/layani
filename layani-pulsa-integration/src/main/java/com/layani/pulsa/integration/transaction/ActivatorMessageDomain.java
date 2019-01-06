package com.layani.pulsa.integration.transaction;

import org.slerp.core.Domain;
import org.springframework.messaging.Message;

public interface ActivatorMessageDomain {
    Message<Domain> execute(Message<Domain> message);
}
