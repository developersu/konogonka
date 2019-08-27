package konogonka.Tools.NPDM;

public class LCollectionEntry {
    private byte key;
    private String value;

    public LCollectionEntry(byte key, String value){
        this.key = key;
        this.value = value;
    }

    public byte getKey() { return key; }

    public String getValue() { return value; }
}
