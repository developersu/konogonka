/*
    Copyright 2019-2020 Dmitry Isaenko

    This file is part of Konogonka.

    Konogonka is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Konogonka is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Konogonka.  If not, see <https://www.gnu.org/licenses/>.
*/
package konogonka;

import java.util.prefs.Preferences;

public class AppPreferences {
    private static final AppPreferences INSTANCE = new AppPreferences();
    public static AppPreferences getInstance() { return INSTANCE; }

    private Preferences preferences;

    private int
            kakAppCount,
            kakOceanCount,
            kakSysCount,
            titleKeksCount,
            titleKeysCount;

    private AppPreferences(){
        preferences = Preferences.userRoot().node("konogonka");

        kakAppCount = getKAKAppCount();
        kakOceanCount = getKAKOceanCount();
        kakSysCount = getKAKSysCount();
        titleKeksCount = getTitleKeksCount();
        titleKeysCount = getTitleKeysCount();
    }

    public void setExtractFilesDir(String path){preferences.put("extract_path", path);}
    public String getExtractFilesDir(){return preferences.get("extract_path", System.getProperty("user.dir"));}

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

    // KAKs: Application/Ocean/System
    public void setKAKAppCount(int number){
        if (this.kakAppCount > number){
            for (int i = number; i < this.kakAppCount; i++) {
                preferences.remove(String.format("key_area_key_application_%02x", number));
            }
        }
        preferences.putInt("key_area_key_application_count", number);
        this.kakAppCount = number;
    }
    public void setKAKOceanCount(int number){
        if (this.kakOceanCount > number){
            for (int i = number; i < this.kakOceanCount; i++) {
                preferences.remove(String.format("key_area_key_ocean_%02x", number));
            }
        }
        preferences.putInt("key_area_key_ocean_count", number);
        this.kakOceanCount = number;
    }
    public void setKAKSysCount(int number){
        if (this.kakSysCount > number){
            for (int i = number; i < this.kakSysCount; i++) {
                preferences.remove(String.format("key_area_key_system_%02x", number));
            }
        }
        preferences.putInt("key_area_key_system_count", number);
        this.kakSysCount = number;
    }

    public void setTitleKeksCount(int number){
        if (this.titleKeksCount > number){
            for (int i = number; i < this.titleKeksCount; i++) {
                preferences.remove(String.format("titlekek_%02x", number));
            }
        }
        preferences.putInt("titlekek_count", number);
        this.titleKeksCount = number;
    }

    public int getKAKAppCount(){ return preferences.getInt("key_area_key_application_count", 0); }
    public int getKAKOceanCount(){ return preferences.getInt("key_area_key_ocean_count", 0); }
    public int getKAKSysCount(){ return preferences.getInt("key_area_key_system_count", 0); }
    public int getTitleKeksCount(){ return preferences.getInt("titlekek_count", 0); }

    public String getApplicationKey(int number){ return preferences.get(String.format("key_area_key_application_%02x", number), "");}
    public void setApplicationKey(int number, String key){ preferences.put(String.format("key_area_key_application_%02x", number), key); }

    public String getOceanKey(int number){ return preferences.get(String.format("key_area_key_ocean_%02x", number), "");}
    public void setOceanKey(int number, String key){ preferences.put(String.format("key_area_key_ocean_%02x", number), key); }

    public String getSystemKey(int number){ return preferences.get(String.format("key_area_key_system_%02x", number), "");}
    public void setSystemKey(int number, String key){ preferences.put(String.format("key_area_key_system_%02x", number), key); }

    public String getTitleKek(int number){ return preferences.get(String.format("titlekek_%02x", number), "");}
    public void setTitleKek(int number, String key){ preferences.put(String.format("titlekek_%02x", number), key); }


    // Title keys
    public int getTitleKeysCount(){ return preferences.getInt("title_keys_count", 0); }
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