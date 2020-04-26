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
package konogonka.Tools.RomFs;

import konogonka.LoperConverter;
import konogonka.ModelControllers.LogPrinter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static konogonka.RainbowDump.formatDecHexString;

public class FileMeta4Debug {

    List<FileMeta> allFiles;

    FileMeta4Debug(long fileMetadataTableLength, byte[] fileMetadataTable) {
        allFiles = new ArrayList<>();
        int i = 0;
        while (i < fileMetadataTableLength) {
            FileMeta fileMeta = new FileMeta();
            fileMeta.containingDirectoryOffset = LoperConverter.getLEint(fileMetadataTable, i);
            i += 4;
            fileMeta.nextSiblingFileOffset = LoperConverter.getLEint(fileMetadataTable, i);
            i += 4;
            fileMeta.fileDataOffset = LoperConverter.getLElong(fileMetadataTable, i);
            i += 8;
            fileMeta.fileDataLength = LoperConverter.getLElong(fileMetadataTable, i);
            i += 8;
            fileMeta.nextFileOffset = LoperConverter.getLEint(fileMetadataTable, i);
            i += 4;
            fileMeta.fileNameLength = LoperConverter.getLEint(fileMetadataTable, i);
            i += 4;
            fileMeta.fileName = new String(Arrays.copyOfRange(fileMetadataTable, i, i + fileMeta.fileNameLength), StandardCharsets.UTF_8);
            ;
            i += getRealNameSize(fileMeta.fileNameLength);

            allFiles.add(fileMeta);
        }

        for (FileMeta fileMeta : allFiles){
            System.out.println(
                    "-------------------------FILE--------------------------------\n" +
                            "Offset of Containing Directory                    " + formatDecHexString(fileMeta.containingDirectoryOffset) + "\n" +
                            "Offset of next Sibling File                       " + formatDecHexString(fileMeta.nextSiblingFileOffset) + "\n" +
                            "Offset of File's Data                             " + formatDecHexString(fileMeta.fileDataOffset) + "\n" +
                            "Length of File's Data                             " + formatDecHexString(fileMeta.fileDataLength) + "\n" +
                            "Offset of next File in the same Hash Table bucket " + formatDecHexString(fileMeta.nextFileOffset) + "\n" +
                            "Name Length                                       " + formatDecHexString(fileMeta.fileNameLength) + "\n" +
                            "Name Length (rounded up to multiple of 4)         " + fileMeta.fileName + "\n"
            );
        }
    }

    private int getRealNameSize(int value){
        if (value % 4 == 0)
            return value;
        return value + 4 - value % 4;
    }

    private static class FileMeta{
        int containingDirectoryOffset;
        int nextSiblingFileOffset;
        long fileDataOffset;
        long fileDataLength;
        int nextFileOffset;
        int fileNameLength;
        String fileName;
    }
}
