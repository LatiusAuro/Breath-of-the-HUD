package net.latiusauro.breathOfTheHUD.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.latiusauro.breathOfTheHUD.gui.hud.element.HudElementType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;


public class Settings {
    private final String CONFIG_VERSION = "1.0";
    private Map<String, Setting> settings = new LinkedHashMap<String, Setting>();
    private File file;
    public static final String NEW_LINE = System.getProperty("line.separator");

    public static final String hud_type = "hud_type";
    public static final String health_position = "health_position";
    public static final String experience_position = "experience_position";

    public static final String quarter_hearts = "quarter_hearts";
    public static final String hearts_per_row = "hearts_per_row";

    private File botHUDDir() {
        Minecraft mc = Minecraft.getInstance();
        return (new File(mc.gameDirectory.getPath(), "config" + File.separator + "BotHUD"));
    }

    public Settings() {
        file = botHUDDir();
        init();
        this.load();
        this.save();
    }

    public void init() {
        addSetting(hud_type, new SettingHudType(hud_type, "vanilla"));

        addSetting(health_position, new SettingPosition(health_position, HudElementType.HEALTH, 0, 0));
        addSetting(quarter_hearts, new SettingBoolean(quarter_hearts, HudElementType.HEALTH, true));
        addSetting(hearts_per_row, new SettingInteger(hearts_per_row, HudElementType.HEALTH, 15, 10, 20));

        addSetting(experience_position, new SettingPosition(experience_position, HudElementType.EXPERIENCE, 0, 0));

    }

    public Setting getSetting(String id) {
        return this.settings.get(id);
    }

    public int[] getPositionValue(String i) {
        String[] postions = this.settings.get(i).getValue().toString().split("_");
        int[] values = { Integer.valueOf(postions[0]), Integer.valueOf(postions[1]) };
        return values;
    }

    public Object getValue(String i) {
        return this.settings.get(i).getValue();
    }

    public double getDoubleValue(String i) {
        return this.settings.get(i).getDoubleValue();
    }

    public Integer getIntValue(String i) {
        return this.settings.get(i).getIntValue();
    }

    public Boolean getBoolValue(String i) {
        return this.settings.get(i).getBoolValue();
    }

    public Float getFloatValue(String i) {
        return this.settings.get(i).getFloatValue();
    }

    public String getStringValue(String i) {
        return this.settings.get(i).getStringValue();
    }

    public void resetSetting(String i) {
        Setting setting = this.settings.get(i);
        setting.resetValue();
        this.settings.put(i, setting);
    }

    public void increment(String i) {
        Setting setting = this.settings.get(i);
        setting.increment();
        this.settings.put(i, setting);
    }

    public void setSetting(String i, Object o) {
        Setting setting = this.settings.get(i);
        setting.setValue(o);
        this.settings.put(i, setting);
    }

    public void addSetting(String id, Setting setting) {
        this.settings.put(id, setting);
    }

    public boolean doesSettingExist(String i) {
        return this.settings.containsKey(i);
    }

    public String getButtonString(String id) {
        Setting setting = this.settings.get(id);
        String s = I18n.get(setting.getName(), new Object[0]) + ": ";
        if(setting instanceof SettingBoolean) {
            return s + (setting.getBoolValue() ? I18n.get("options.on", new Object[0]) : I18n.get("options.off", new Object[0]));
        } else if(setting instanceof SettingString || setting instanceof SettingHudType) {
            return s + I18n.get(setting.getStringValue(), new Object[0]);
        } else if(setting instanceof SettingColor) {
            return s + intToHexString(setting.getIntValue());
        } else if(setting instanceof SettingInteger) {
            return s + setting.getIntValue();
        } else if(setting instanceof SettingFloat) {
            SettingFloat sf = (SettingFloat) setting;
            return s + (id == pickup_duration ? Math.ceil(SettingFloat.snapToStepClamp(sf, sf.getFloatValue())) + " " + I18n.get("gui.rpg.sec", new Object[0])
                    : String.valueOf(SettingFloat.snapToStepClamp(sf, sf.getFloatValue())));
        } else if(setting instanceof SettingPosition || setting instanceof SettingDouble) {
            return s;
        } else {
            return s + "error";
        }
    }

    public static String intToHexString(int hex) {
        String s = Integer.toHexString(hex).toUpperCase();
        if(hex <= 0xFFFFF) {
            s = "0" + s;
            if(hex <= 0xFFFF) {
                s = "0" + s;
                if(hex <= 0xFFF) {
                    s = "0" + s;
                    if(hex <= 0xFF) {
                        s = "0" + s;
                        if(hex <= 0xF) {
                            s = "0" + s;
                        }
                    }
                }
            }
        }
        return "#" + s;
    }

    public void saveSettings() {
        this.save();
    }

    public List<String> getSettingsOf(HudElementType type) {
        List<String> settings = new ArrayList<String>();
        for(String key : this.settings.keySet()) {
            if(this.settings.get(key).associatedType == type)
                settings.add(key);
        }
        return settings;
    }

    public List<String> getSettingsOf(String type) {
        List<String> settings = new ArrayList<String>();
        for(String key : this.settings.keySet()) {
            if(this.settings.get(key).associatedType != null && this.settings.get(key).associatedType.name() == type)
                settings.add(key);
            else if(type == "general" && this.settings.get(key).associatedType == null)
                settings.add(key);
        }
        return settings;
    }

    public void save() {
        try {
            if(file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            if(!file.exists() && !file.createNewFile()) {
                return;
            }

            if(file.canWrite()) {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

                buffer.write("Version=" + CONFIG_VERSION + NEW_LINE);

                save(buffer);

                buffer.close();
                fos.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        BufferedReader buffer = null;
        try {
            if(file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            if(!file.exists()) {
                // Either a previous load attempt failed or the file is new; clear maps
                if(!file.createNewFile())
                    return;
            }

            if(file.canRead()) {
                buffer = new BufferedReader(new FileReader(file));

                String line;

                while(true) {
                    line = buffer.readLine();
                    if(line == null || line.isEmpty())
                        break;
                    if(line.contains(":") && line.contains("=")) {
                        String[] type = line.split(":");
                        String[] setting = type[1].split("=");
                        if(this.getSetting(setting[0]) != null) {
                            if(type[0].matches("B")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(Boolean.valueOf(setting[1])));
                            } else if(type[0].matches("S")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(setting[1]));
                            } else if(type[0].matches("C")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(Integer.valueOf(setting[1])));
                            } else if(type[0].matches("H")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(setting[1]));
                            } else if(type[0].matches("I")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(Integer.valueOf(setting[1])));
                            } else if(type[0].matches("F")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(Float.valueOf(setting[1])));
                            } else if(type[0].matches("D")) {
                                this.setSetting(setting[0], this.getSetting(setting[0]).setValue(Double.valueOf(setting[1])));
                            } else if(type[0].matches("P")) {
                                this.setSetting(setting[0], setting[1]);
                            } else {
                                // TODO: Logger
                            }
                        }

                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void save(BufferedWriter out) throws IOException {
        for(Setting setting : settings.values()) {
            if(setting instanceof SettingBoolean) {
                out.write("B:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingString) {
                out.write("S:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingHudType) {
                out.write("H:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingColor) {
                out.write("C:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingInteger) {
                out.write("I:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingFloat) {
                out.write("F:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingDouble) {
                out.write("D:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else if(setting instanceof SettingPosition) {
                out.write("P:" + setting.ID + "=" + setting.getValue() + NEW_LINE);
            } else {
                out.write("E:" + setting.ID + "=" + "ERROR" + NEW_LINE);
            }
        }
    }
}
