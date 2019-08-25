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
