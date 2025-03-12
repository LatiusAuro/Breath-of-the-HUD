package net.latiusauro.breathOfTheHUD.gui;

import net.latiusauro.breathOfTheHUD.breathOfTheHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class botHUDOverlay {
    public static final ResourceLocation HUD_ELEMENTS = new ResourceLocation(breathOfTheHUD.MOD_ID, "textures/gui/elements.png");

    protected int posX;
    protected int posY;
    protected int elementWidth;
    protected int elementHeight;
    protected Minecraft mc;


    public static final IGuiOverlay BOTW_HUD = (((gui, guiGraphics, v, width, height) -> {

    }));

}
