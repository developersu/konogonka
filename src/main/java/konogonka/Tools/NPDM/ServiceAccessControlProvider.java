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
package konogonka.Tools.NPDM;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class ServiceAccessControlProvider {

    private LinkedHashMap<String, Byte> collection;

    public ServiceAccessControlProvider(byte[] bytes){
        collection = new LinkedHashMap<>();
        byte key;
        String value;

        int i = 0;

        while (i < bytes.length){
            key = bytes[i];
            value = new String(bytes, i+1, getSize(key), StandardCharsets.UTF_8);
            collection.put(value, key);
            i += getSize(key)+1;
        }
    }

    private int getSize(byte control) {
        return ((byte) 0x7 & control) + (byte) 0x01;
    }

    public LinkedHashMap<String, Byte> getCollection() { return collection; }
}
