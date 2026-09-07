package io.robrichardson.inventorycount;

public enum InventoryCountMode {
    FREE("Free slots"),
    USED("Used slots"),
    BOTH("Both");

    private final String label;

    InventoryCountMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
