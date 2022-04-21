package com.njust.ecommerce.notifier;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@SuppressWarnings("all")
public class JsfmNotifier extends AbstractEventNotifier {


    protected JsfmNotifier(InstanceRepository repository) {
        super(repository);
    }

    /**
     * 实现对事件的通知
     * */
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if(event instanceof InstanceStatusChangedEvent) {
                log.info("Instance status changed: [{}], [{}], [{}]",
                        instance.getRegistration().getName(), event.getInstance(),
                        instance.getStatusInfo().getStatus());
            } else {
                log.info("Instance info: [{}], [{}], [{}]", instance.getRegistration().getName(),
                        event.getInstance(), event.getType());
            }
        });
    }
}
