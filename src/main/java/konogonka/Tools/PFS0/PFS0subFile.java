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
package konogonka.Tools.PFS0;

public class PFS0subFile {
    private String name;
    private long offset;     // REAL in file (including offset in NCA/NSP file)
    private long size;
    private byte[] zeroes;

    public PFS0subFile(String name, long offset, long size, byte[] zeroesInTable){
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.zeroes = zeroesInTable;
    }

    public String getName() { return name; }
    public long getOffset() { return offset; }
    public long getSize() { return size; }
    public byte[] getZeroes() { return zeroes; }
}
