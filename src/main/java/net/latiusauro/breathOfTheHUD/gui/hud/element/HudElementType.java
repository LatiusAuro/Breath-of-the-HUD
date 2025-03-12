package net.latiusauro.breathOfTheHUD.gui.hud.element;

import net.minecraft.client.resources.language.I18n;

public enum HudElementType {
    DEBUG("name.debug"),
    HOTBAR("name.hotbar"),
    HEALTH("name.health"),
    ARMOR("name.armor"),
    FOOD("name.food"),
    HEALTH_MOUNT("name.health_mount"),
    AIR("name.air"),
    JUMP_BAR("name.jump_bar"),
    EXPERIENCE("name.experience"),
    LEVEL("name.level"),
    MISC("name.misc");

    private String displayName;

    private HudElementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return I18n.get(this.displayName);
    }
}
