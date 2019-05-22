package konogonka;

import java.util.prefs.Preferences;

public class AppPreferences {
    private static final AppPreferences INSTANCE = new AppPreferences();
    public static AppPreferences getInstance() { return INSTANCE; }

    private Preferences preferences;
    private int titleKeysCount;

    private AppPreferences(){
        preferences = Preferences.userRoot().node("konogonka");
        titleKeysCount = getTitleKeysCount();
    }

    public void setAll(
            String xciHeaderKey,
            String headerKey
    ) {
        setXciHeaderKey(xciHeaderKey);
        setHeaderKey(headerKey);
    }

    public String getRecentPath(){return preferences.get("recent_path", System.getProperty("user.home"));}
    public void setRecentPath(String path){preferences.put("recent_path", path);}

    /*
    public HashMap<String, String> getKeys(){

    }
    public void setKeys(HashMap<String, String> keysMap){
        preferences.put("xci_header_key", );
        preferences.put("header_key", );
    }
     */

    public String getXciHeaderKey(){ return preferences.get("xci_header_key", "");}
    public void setXciHeaderKey(String key){ preferences.put("xci_header_key", key); }

    public String getHeaderKey(){ return preferences.get("header_key", "");}
    public void setHeaderKey(String key){ preferences.put("header_key", key); }

    public String getApplicationKey(int number){ return preferences.get("key_area_key_application_0"+number, "");}
    public void setApplicationKey(int number, String key){ preferences.put("key_area_key_application_0"+number, key); }

    public String getOceanKey(int number){ return preferences.get("key_area_key_ocean_0"+number, "");}
    public void setOceanKey(int number, String key){ preferences.put("key_area_key_ocean_0"+number, key); }

    public String getSystemKey(int number){ return preferences.get("key_area_key_system_0"+number, "");}
    public void setSystemKey(int number, String key){ preferences.put("key_area_key_system_0"+number, key); }

    public String getTitleKek(int number){ return preferences.get("titlekek_"+number, "");}
    public void setTitleKek(int number, String key){ preferences.put("titlekek_"+number, key); }



    public int getTitleKeysCount(){                                                                                     // TODO: do the same for other multi-keys and single
        return preferences.getInt("title_keys_count", 0);
    }
    // Since we don't want to store title keys that are no longer in use, we have to (try to) remove them.
    // This part of code works as a charm. Don't touch.
    public void setTitleKeysCount(int number){
        if (this.titleKeysCount > number){
            for (int i = number; i < this.titleKeysCount; i++) {
                preferences.remove("title_key_"+i);
            }
        }
        preferences.putInt("title_keys_count", number);
        this.titleKeysCount = number;
    }

    public String[] getTitleKeyPair(int number){
        return  preferences.get("title_key_"+number, "0 = 0").split(" = ", 2);
    }
    public void setTitleKey(int number, String pair){ preferences.put("title_key_"+number, pair); }
}
