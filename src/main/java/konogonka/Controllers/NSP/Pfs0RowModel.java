/*
    Copyright 2019-2022 Dmitry Isaenko

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
package konogonka.Controllers.NSP;

import konogonka.Controllers.IRowModel;

public class Pfs0RowModel implements IRowModel {

    private static int numberCnt = 0;
    public static void resetNumCnt(){ numberCnt = 0; }

    private int number;
    private String fileName;
    private long fileSize;
    private long fileOffset;
    private boolean markForUpload;

    Pfs0RowModel(String fileName, long size, long offset){
        this.markForUpload = false;
        this.fileName = fileName;
        this.fileOffset = offset;
        this.fileSize = size;
        this.number = numberCnt++;
    }
    // Model methods start
    public int getNumber(){
        return number;
    }
    public String getFileName(){
        return fileName;
    }
    public long getFileSize() { return fileSize; }
    public long getFileOffset() {return fileOffset; }
    public boolean isMarkSelected() {
        return markForUpload;
    }
    // Model methods end

    public void setMarkSelected(boolean value){
        markForUpload = value;
    }
}
