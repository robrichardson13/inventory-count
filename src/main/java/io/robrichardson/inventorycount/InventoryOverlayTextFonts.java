package io.robrichardson.inventorycount;
import java.awt.Font;

public enum InventoryOverlayTextFonts
{
    ARIAL_BOLD("Arial Bold", "Arial", Font.BOLD),
    TIMES_NEW_ROMAN("Times New Roman", "Times New Roman", Font.PLAIN),
    COURIER_NEW("Courier New", "Courier New", Font.PLAIN),
    VERDANA("Verdana", "Verdana", Font.PLAIN),
    TAHOMA("Tahoma", "Tahoma", Font.PLAIN);

    private final String displayName;
    private final String fontName;
    private final int fontStyle;

    InventoryOverlayTextFonts(String displayName, String fontName, int fontStyle)
    {
        this.displayName = displayName;
        this.fontName = fontName;
        this.fontStyle = fontStyle;
    }

    @Override
    public String toString()
    {
        return displayName;
    }

    public String getFontName()
    {
        return fontName;
    }

    public int getFontStyle()
    {
        return fontStyle;
    }
}
