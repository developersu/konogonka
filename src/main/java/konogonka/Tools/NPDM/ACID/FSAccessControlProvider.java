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
package konogonka.Tools.NPDM.ACID;

import konogonka.LoperConverter;

import java.util.Arrays;

/**
 * For ACID Provider
 * */
public class FSAccessControlProvider {

    private byte version;
    private byte[] padding;
    private long permissionsBitmask;
    private byte[] reserved;

    public FSAccessControlProvider(byte[] bytes) {
        version = bytes[0];
        padding = Arrays.copyOfRange(bytes, 1, 0x4);
        permissionsBitmask =  LoperConverter.getLElong(bytes, 0x4);
        reserved = Arrays.copyOfRange(bytes, 0xC, 0x2C);
    }

    public byte getVersion() { return version; }
    public byte[] getPadding() { return padding; }
    public long getPermissionsBitmask() { return permissionsBitmask; }
    public byte[] getReserved() { return reserved; }
}
