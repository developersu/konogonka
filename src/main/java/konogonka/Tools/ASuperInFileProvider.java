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
package konogonka.Tools;

import java.io.IOException;
import java.io.PipedInputStream;

/**
 * Any class of this type must be able to accept data from stream (and file as any other).
 * */

public abstract class ASuperInFileProvider {
    protected byte[] readFromStream(PipedInputStream pis, int size) throws IOException {
        byte[] buffer = new byte[size];
        int startingPos = 0;
        int readCnt;
        while (size > 0){
            readCnt = pis.read(buffer, startingPos, size);
            if (readCnt == -1)
                return null;
            startingPos += readCnt;
            size -= readCnt;
        }
        return buffer;
    }
}
