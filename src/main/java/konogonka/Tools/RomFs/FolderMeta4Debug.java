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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static konogonka.RainbowDump.formatDecHexString;

public class FolderMeta4Debug {

    List<FolderMeta> allFolders;

    FolderMeta4Debug(long directoryMetadataTableLength, byte[] directoryMetadataTable){
        allFolders = new ArrayList<>();
        int i = 0;
        while (i < directoryMetadataTableLength){
            FolderMeta folderMeta = new FolderMeta();
            folderMeta.parentDirectoryOffset = LoperConverter.getLEint(directoryMetadataTable, i);
            i += 4;
            folderMeta.nextSiblingDirectoryOffset = LoperConverter.getLEint(directoryMetadataTable, i);
            i += 4;
            folderMeta.firstSubdirectoryOffset = LoperConverter.getLEint(directoryMetadataTable, i);
            i += 4;
            folderMeta.firstFileOffset = LoperConverter.getLEint(directoryMetadataTable, i);
            i += 4;
            folderMeta.nextDirectoryOffset = LoperConverter.getLEint(directoryMetadataTable, i);
            i += 4;
            folderMeta.dirNameLength = LoperConverter.getLEint(directoryMetadataTable, i);
            i += 4;
            folderMeta.dirName = new String(Arrays.copyOfRange(directoryMetadataTable, i, i + folderMeta.dirNameLength), StandardCharsets.UTF_8);
            i += getRealNameSize(folderMeta.dirNameLength);

            allFolders.add(folderMeta);
        }

        for (FolderMeta headersDirectory : allFolders)
            System.out.println(
                    "---------------------------DIRECTORY------------------------\n" +
                            "Offset of Parent Directory (self if Root)              " + formatDecHexString(headersDirectory.parentDirectoryOffset     ) +"\n" +
                            "Offset of next Sibling Directory                       " + formatDecHexString(headersDirectory.nextSiblingDirectoryOffset) +"\n" +
                            "Offset of first Child Directory (Subdirectory)         " + formatDecHexString(headersDirectory.firstSubdirectoryOffset   ) +"\n" +
                            "Offset of first File (in File Metadata Table)          " + formatDecHexString(headersDirectory.firstFileOffset           ) +"\n" +
                            "Offset of next Directory in the same Hash Table bucket " + formatDecHexString(headersDirectory.nextDirectoryOffset       ) +"\n" +
                            "Name Length                                            " + formatDecHexString(headersDirectory.dirNameLength             ) +"\n" +
                            "Name Length (rounded up to multiple of 4)              " + headersDirectory.dirName +                                       "\n"
            );
    }

    private int getRealNameSize(int value){
        if (value % 4 == 0)
            return value;
        return value + 4 - value % 4;
    }

    private static class FolderMeta {
        int parentDirectoryOffset;
        int nextSiblingDirectoryOffset;
        int firstSubdirectoryOffset;
        int firstFileOffset;
        int nextDirectoryOffset;
        int dirNameLength;
        String dirName;
    }
}
