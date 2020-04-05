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
package konogonka.Tools.XCI;

public class HFS0File {
    private String name;
    private long offset;
    private long size;
    private long hashedRegionSize;
    private boolean padding;
    private byte[] SHA256Hash;
    
    public HFS0File(String name, long offset, long size, long hashedRegionSize, boolean padding, byte[] SHA256Hash){
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.hashedRegionSize = hashedRegionSize;
        this.padding = padding;
        this.SHA256Hash = SHA256Hash;
    }
    
    public String getName() { return name; }
    public long getOffset() { return offset; }
    public long getSize() { return size; }
    public long getHashedRegionSize() { return hashedRegionSize; }
    public boolean isPadding() { return padding; }
    public byte[] getSHA256Hash() { return SHA256Hash; }
}
