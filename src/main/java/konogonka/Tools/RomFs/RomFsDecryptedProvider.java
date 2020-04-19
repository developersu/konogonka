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

import konogonka.Tools.ISuperProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PipedInputStream;

public class RomFsDecryptedProvider implements ISuperProvider {

    private static final long LEVEL_6_DEFAULT_OFFSET = 0x14000;

    private File decryptedFSImage;
    private Level6Header header;

    public RomFsDecryptedProvider(File decryptedFSImage) throws Exception{     // TODO: add default setup AND using meta-data headers from NCA RomFs section (?)
        this.decryptedFSImage = decryptedFSImage;

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(decryptedFSImage));

        skipTo(bis, LEVEL_6_DEFAULT_OFFSET);

        byte[] rawDataChunk = new byte[0x50];

        if (bis.read(rawDataChunk) != 0x50)
            throw new Exception("Failed to read header (0x50)");

        this.header = new Level6Header(rawDataChunk);

        bis.close();
    }
    private void skipTo(BufferedInputStream bis, long size) throws Exception{
        long mustSkip = size;
        long skipped = 0;
        while (mustSkip > 0){
            skipped += bis.skip(mustSkip);
            mustSkip = size - skipped;
        }
    }
    private int getRealNameSize(int value){
        if (value % 4 == 0)
            return value;
        return value + 4 - value % 4;
    }

    public Level6Header getHeader() { return header; }

    @Override
    public PipedInputStream getProviderSubFilePipedInpStream(String subFileName) throws Exception {
        return null;
    }

    @Override
    public PipedInputStream getProviderSubFilePipedInpStream(int subFileNumber) throws Exception {
        return null;
    }

    @Override
    public File getFile() {
        return decryptedFSImage;
    }

    @Override
    public long getRawFileDataStart() {
        return 0;
    }
}
