package com.deepanshu.backend.notification.event;

public record WorkspaceCreatedEvent(String workspaceName,
                                    String ownerName,
                                    String ownerEmail) {
}
