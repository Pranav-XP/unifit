package dev.forge.unifit.facility;

public enum FacilityStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    MAINTENANCE("Maintenance"),
    DELETED("Removed");

    private final String displayName;

    FacilityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStatusColor() {
        return switch (this) {
            case AVAILABLE -> "bg-green-300 text-sm text-green-800 p-0.5 rounded-md font-semibold";
            case UNAVAILABLE -> "bg-red-300 text-sm text-red-800 p-0.5 rounded-md font-semibold";
            case MAINTENANCE -> "bg-yellow-300 text-yellow-800 text-sm rounded-md font-semibold";
            case DELETED -> "bg-gray-200 text-gray-800 text-sm rounded-md font-semibold";
        };
    }
}

