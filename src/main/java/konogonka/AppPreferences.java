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
            String headerKey,
            String applicationKey0,
            String applicationKey1,
            String applicationKey2,
            String applicationKey3,
            String applicationKey4,
            String applicationKey5,
            String applicationKey6,
            String applicationKey7,
            String oceanKey0,
            String oceanKey1,
            String oceanKey2,
            String oceanKey3,
            String oceanKey4,
            String oceanKey5,
            String oceanKey6,
            String oceanKey7,
            String systemKey0,
            String systemKey1,
            String systemKey2,
            String systemKey3,
            String systemKey4,
            String systemKey5,
            String systemKey6,
            String systemKey7
    ) {
        setXciHeaderKey(xciHeaderKey);
        setHeaderKey(headerKey);

        setApplicationKey(0, applicationKey0);
        setApplicationKey(1, applicationKey1);
        setApplicationKey(2, applicationKey2);
        setApplicationKey(3, applicationKey3);
        setApplicationKey(4, applicationKey4);
        setApplicationKey(5, applicationKey5);
        setApplicationKey(6, applicationKey6);
        setApplicationKey(7, applicationKey7);

        setOceanKey(0, oceanKey0);
        setOceanKey(1, oceanKey1);
        setOceanKey(2, oceanKey2);
        setOceanKey(3, oceanKey3);
        setOceanKey(4, oceanKey4);
        setOceanKey(5, oceanKey5);
        setOceanKey(6, oceanKey6);
        setOceanKey(7, oceanKey7);

        setSystemKey(0, systemKey0);
        setSystemKey(1, systemKey1);
        setSystemKey(2, systemKey2);
        setSystemKey(3, systemKey3);
        setSystemKey(4, systemKey4);
        setSystemKey(5, systemKey5);
        setSystemKey(6, systemKey6);
        setSystemKey(7, systemKey7);
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



    public int getTitleKeysCount(){
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
