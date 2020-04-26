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
package konogonka.Controllers.RFS;

import konogonka.Controllers.IRowModel;
import konogonka.Tools.RomFs.FileSystemEntry;

public class RFSEntry implements IRowModel {
    private FileSystemEntry fileSystemEntry;
    private boolean check;

    public RFSEntry(FileSystemEntry fileSystemEntry){
        this.fileSystemEntry = fileSystemEntry;
    }

    public boolean isDirectory(){
        return fileSystemEntry.isDirectory();
    }

    @Override
    public String toString(){
        return fileSystemEntry.getName();
    }


    @Override
    public int getNumber() { return 0; }

    @Override
    public String getFileName() { return fileSystemEntry.getName(); }

    @Override
    public long getFileSize() { return fileSystemEntry.getFileSize(); }

    @Override
    public long getFileOffset() { return fileSystemEntry.getFileOffset(); }

    @Override
    public boolean isMarkSelected() { return check; }

    @Override
    public void setMarkSelected(boolean value) { check = value; }
}