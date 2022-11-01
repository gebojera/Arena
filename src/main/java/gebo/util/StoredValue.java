package gebo.util;

public class StoredValue {

    /**
     * Utility class to allow access to mutable values
     * by object instances such as Scoreboards
     */

    private Object value;

    public <T> StoredValue(T value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public <T> void setValue(T value) {
        this.value = value;
    }
}
