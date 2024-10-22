package dev.forge.unifit.notification;

public enum NotificationStatus {
            READ("Read"),
            UNREAD("Unread");


           private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
