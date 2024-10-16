package io.robrichardson.inventorycount;

public enum InventoryOverlayTextFontSizes
{
    SIZE_8(8),
    SIZE_9(9),
    SIZE_10(10),
    SIZE_11(11),
    SIZE_12(12),
    SIZE_13(13),
    SIZE_14(14),
    SIZE_15(15),
    SIZE_16(16),
    SIZE_17(17),
    SIZE_18(18),
    SIZE_19(19),
    SIZE_20(20);

    private final int size;

    InventoryOverlayTextFontSizes(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return Integer.toString(size);
    }

    public int getSize() {
        return size;
    }
}
