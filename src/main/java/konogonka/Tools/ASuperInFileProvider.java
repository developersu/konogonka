package konogonka.Tools;

import java.io.IOException;
import java.io.PipedInputStream;

/**
 * Create prototype of the provider that created and works with pipes only
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
