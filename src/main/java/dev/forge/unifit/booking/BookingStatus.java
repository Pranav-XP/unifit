package dev.forge.unifit.booking;

public enum BookingStatus {
    RESERVED("Reserved"),
    COMPLETE("Completed"),
    DELETED("Cancelled"),
    MAINTENANCE("Maintenance");

    private final String displayName;
    private

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStatusColor() {
        return switch (this) {
            case RESERVED -> "bg-yellow-200 text-yellow-800 font-semibold text-center border-b";
            case COMPLETE -> "bg-blue-200 text-blue-800 font-semibold text-center border-b";
            case DELETED -> "bg-red-200 text-red-800 font-semibold text-center border-b";
            case MAINTENANCE -> "bg-gray-200 text-gray-800 font-semibold text-center border-b";
        };
    }
}

