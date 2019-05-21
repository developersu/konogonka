package konogonka.ctraes;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class AesCtr {

    private static boolean BCinitialized = false;

    private void initBCProvider(){
        Security.addProvider(new BouncyCastleProvider());
        BCinitialized = true;
    }

    private Cipher cipher;
    private SecretKeySpec key;

    public AesCtr(byte[] keyArray) throws Exception{
        if ( ! BCinitialized)
            initBCProvider();

        key = new SecretKeySpec(keyArray, "AES");
        cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    }

    public byte[] decrypt(byte[] encryptedData, byte[] IVarray) throws Exception{
        IvParameterSpec iv = new IvParameterSpec(IVarray);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }
}
