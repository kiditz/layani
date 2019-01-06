package com.layani.pulsa.integration.transaction.api;

import org.slerp.core.Domain;
import org.springframework.messaging.Message;

public interface ApiCaller {
    Message<Domain> execute(Domain payload);
}
