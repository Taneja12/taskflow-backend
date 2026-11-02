package com.deepanshu.backend.notification.event;

import com.deepanshu.backend.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WorkspaceNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceNotificationListener.class);
    private final EmailService emailService;

    public WorkspaceNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleWorkspaceCreated(
            WorkspaceCreatedEvent event
    )
    {
        log.info("Processing notification on thread: {}",
                Thread.currentThread().getName());

        try {
            emailService.sendWorkspaceCreatedEmail(
                    event.ownerEmail(),
                    event.ownerName(),
                    event.workspaceName()
            );

            log.info("Workspace creation email sent to {}",
                    event.ownerEmail());

        } catch (Exception e) {
            log.error("Failed to send workspace creation email to {}",
                    event.ownerEmail(), e);
        }
    }
}
