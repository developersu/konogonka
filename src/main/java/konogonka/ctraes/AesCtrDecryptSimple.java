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
package konogonka.ctraes;

import konogonka.LoperConverter;
/**
 * Simplify decryption of the CTR
 */
public class AesCtrDecryptSimple {

    private long realMediaOffset;
    private byte[] IVarray;
    private AesCtr aesCtr;

    public AesCtrDecryptSimple(byte[] key, byte[] sectionCTR, long realMediaOffset) throws Exception{
        this.realMediaOffset = realMediaOffset;
        aesCtr = new AesCtr(key);
        // IV for CTR == 16 bytes
        IVarray = new byte[0x10];
        // Populate first 8 bytes taken from Header's section Block CTR
        System.arraycopy(LoperConverter.flip(sectionCTR), 0x0, IVarray, 0x0, 0x8);
    }

    public void skipNext(){
        realMediaOffset += 0x200;
    }

    public void skipNext(long blocksNum){
        if (blocksNum > 0)
            realMediaOffset += blocksNum * 0x200;
    }

    public byte[] dectyptNext(byte[] enctyptedBlock) throws Exception{
        updateIV(realMediaOffset);
        byte[] decryptedBlock = aesCtr.decrypt(enctyptedBlock, IVarray);
        realMediaOffset += 0x200;
        return decryptedBlock;
    }
    // Populate last 8 bytes calculated. Thanks hactool project!
    private void updateIV(long offset){
        offset >>= 4;
        for (int i = 0; i < 0x8; i++){
            IVarray[0x10-i-1] = (byte)(offset & 0xff);         // Note: issues could be here
            offset >>= 8;
        }
    }
}
