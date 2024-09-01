package dev.forge.unifit.booking;

public enum BookingStatus {
    PENDING("Pending"),
    COMPLETE("Completed"),
    DELETED("Cancelled"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String getStatusColor(BookingStatus status) {
        return switch (status) {
            case PENDING -> "bg-yellow-200 text-yellow-800 font-semibold text-center border-b";
            case COMPLETE -> "bg-blue-200 text-blue-800 font-semibold text-center border-b";
            case DELETED -> "bg-red-200 text-red-800 font-semibold text-center border-b";
            case MAINTENANCE -> "bg-gray-200 text-gray-800 font-semibold text-center border-b";
        };
    }
}

