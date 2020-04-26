/*
 * Copyright 2019-2020 Dmitry Isaenko
 *
 * This file is part of Konogonka.
 *
 * Konogonka is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Konogonka is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Konogonka.  If not, see <https://www.gnu.org/licenses/>.
 */

package konogonka.Tools.RomFs;

import konogonka.LoperConverter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileSystemEntry {
    private boolean directoryFlag;
    private String name;
    private List<FileSystemEntry> content;

    private static byte[] dirsMetadataTable;
    private static byte[] filesMetadataTable;

    private long fileOffset;
    private long fileSize;

    public FileSystemEntry(byte[] dirsMetadataTable, byte[] filesMetadataTable) throws Exception{
        FileSystemEntry.dirsMetadataTable = dirsMetadataTable;
        FileSystemEntry.filesMetadataTable = filesMetadataTable;
        this.content = new ArrayList<>();
        this.directoryFlag = true;
        DirectoryMetaData rootDirectoryMetaData = new DirectoryMetaData();
        if (rootDirectoryMetaData.dirName.isEmpty())
            this.name = "/";
        else
            this.name = rootDirectoryMetaData.dirName;
        if (rootDirectoryMetaData.parentDirectoryOffset != 0)
            throw new Exception("Offset of Parent Directory is incorrect. Expected 0 for root, received value is "+ rootDirectoryMetaData.parentDirectoryOffset);
        if (rootDirectoryMetaData.nextSiblingDirectoryOffset != -1)
            throw new Exception("Offset of next Sibling Directory is incorrect. Expected -1 for root, received value is "+ rootDirectoryMetaData.nextSiblingDirectoryOffset);
        if (rootDirectoryMetaData.firstSubdirectoryOffset != -1)
            content.add(getDirectory(rootDirectoryMetaData.firstSubdirectoryOffset));
        if (rootDirectoryMetaData.firstFileOffset != -1)
            content.add(getFile(this, rootDirectoryMetaData.firstFileOffset));
        content.sort(Comparator.comparingLong(FileSystemEntry::getFileOffset));
    }

    private FileSystemEntry(){
        this.content = new ArrayList<>();
    }

    private FileSystemEntry getDirectory(int childDirMetaPosition){
        FileSystemEntry fileSystemEntry = new FileSystemEntry();
        fileSystemEntry.directoryFlag = true;

        DirectoryMetaData directoryMetaData = new DirectoryMetaData(childDirMetaPosition);
        fileSystemEntry.name = directoryMetaData.dirName;

        if (directoryMetaData.nextSiblingDirectoryOffset != -1)
            this.content.add(getDirectory(directoryMetaData.nextSiblingDirectoryOffset));

        if (directoryMetaData.firstSubdirectoryOffset != -1)
            fileSystemEntry.content.add(getDirectory(directoryMetaData.firstSubdirectoryOffset));

        if (directoryMetaData.firstFileOffset != -1)
            fileSystemEntry.content.add(getFile(fileSystemEntry, directoryMetaData.firstFileOffset));

        fileSystemEntry.content.sort(Comparator.comparingLong(FileSystemEntry::getFileOffset));

        return fileSystemEntry;
    }

    private FileSystemEntry getFile(FileSystemEntry directoryContainer, int childFileMetaPosition){
        FileSystemEntry fileSystemEntry = new FileSystemEntry();
        fileSystemEntry.directoryFlag = false;

        FileMetaData fileMetaData = new FileMetaData(childFileMetaPosition);
        fileSystemEntry.name = fileMetaData.fileName;
        fileSystemEntry.fileOffset = fileMetaData.fileDataRealOffset;
        fileSystemEntry.fileSize = fileMetaData.fileDataRealLength;
        if (fileMetaData.nextSiblingFileOffset != -1)
            directoryContainer.content.add( getFile(directoryContainer, fileMetaData.nextSiblingFileOffset) );

        return fileSystemEntry;
    }

    public boolean isDirectory() { return directoryFlag; }
    public boolean isFile() { return ! directoryFlag; }
    public long getFileOffset() { return fileOffset; }
    public long getFileSize() { return fileSize; }
    public List<FileSystemEntry> getContent() { return content; }
    public String getName(){ return name; }


    private static class DirectoryMetaData {
        private int parentDirectoryOffset;
        private int nextSiblingDirectoryOffset;
        private int firstSubdirectoryOffset;
        private int firstFileOffset;

        private String dirName;

        private DirectoryMetaData(){
            this(0);
        }
        private DirectoryMetaData(int childDirMetaPosition){
            int i = childDirMetaPosition;
            parentDirectoryOffset = LoperConverter.getLEint(dirsMetadataTable, i);
            i += 4;
            nextSiblingDirectoryOffset = LoperConverter.getLEint(dirsMetadataTable, i);
            i += 4;
            firstSubdirectoryOffset = LoperConverter.getLEint(dirsMetadataTable, i);
            i += 4;
            firstFileOffset = LoperConverter.getLEint(dirsMetadataTable, i);
            i += 4;
            // int nextHashTableBucketDirectoryOffset = LoperConverter.getLEint(dirsMetadataTable, i);
            i += 4;
            int dirNameLength = LoperConverter.getLEint(dirsMetadataTable, i);
            i += 4;
            dirName = new String(Arrays.copyOfRange(dirsMetadataTable, i, i + dirNameLength), StandardCharsets.UTF_8);
            //i += getRealNameSize(dirNameLength);
        }

        private int getRealNameSize(int value){
            if (value % 4 == 0)
                return value;
            return value + 4 - value % 4;
        }
    }
    private static class FileMetaData {

        private int nextSiblingFileOffset;
        private long fileDataRealOffset;
        private long fileDataRealLength;

        private String fileName;

        private FileMetaData(){
            this(0);
        }
        
        private FileMetaData(int childFileMetaPosition){
            int i = childFileMetaPosition;
            // int containingDirectoryOffset = LoperConverter.getLEint(filesMetadataTable, i); // never used
            i += 4;
            nextSiblingFileOffset = LoperConverter.getLEint(filesMetadataTable, i);
            i += 4;
            fileDataRealOffset = LoperConverter.getLElong(filesMetadataTable, i);
            i += 8;
            fileDataRealLength = LoperConverter.getLElong(filesMetadataTable, i);
            i += 8;
            //int nextHashTableBucketFileOffset = LoperConverter.getLEint(filesMetadataTable, i);
            i += 4;
            int fileNameLength = LoperConverter.getLEint(filesMetadataTable, i);
            i += 4;
            fileName = new String(Arrays.copyOfRange(filesMetadataTable, i, i + fileNameLength), StandardCharsets.UTF_8);;
            //i += getRealNameSize(fileNameLength);
        }
    }

    public void printTreeForDebug(){
        System.out.println("/");
        for (FileSystemEntry entry: content)
            printEntry(2, entry);
    }
    private void printEntry(int cnt, FileSystemEntry entry) {
        for (int i = 0; i < cnt; i++)
            System.out.print(" ");

        if (entry.isDirectory()){
            System.out.println("|-" + entry.getName());
            for (FileSystemEntry e : entry.content)
                printEntry(cnt+2, e);
        }
        else
            System.out.println("|-" + entry.getName() + String.format("    0x%-10x 0x%-10x", entry.fileOffset, entry.fileSize));
    }
}
