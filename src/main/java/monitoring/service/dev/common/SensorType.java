package monitoring.service.dev.common;

public enum SensorType {

    COLD_WATER_METERS("COLD"), HOT_WATER_METERS("HOT");

    private final String name;

    SensorType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}