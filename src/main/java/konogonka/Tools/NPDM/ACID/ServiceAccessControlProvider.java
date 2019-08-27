package konogonka.Tools.NPDM.ACID;

import konogonka.Tools.NPDM.LCollectionEntry;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class ServiceAccessControlProvider {

    private LinkedList<LCollectionEntry> collection;

    public ServiceAccessControlProvider(byte[] bytes){
        collection = new LinkedList<>();
        byte key;
        String value;

        int i = 0;

        while (i < bytes.length){
            key = bytes[i];
            value = new String(bytes, i+1, getSize(key), StandardCharsets.UTF_8);
            collection.add(new LCollectionEntry(key, value));
            i += getSize(key)+1;
        }
    }

    private int getSize(byte control) {
        return ((byte) 0x7 & control) + (byte) 0x01;
    }

    public LinkedList<LCollectionEntry> getCollection() { return collection; }
}
