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

    public AesCtr(byte[] keyArray, byte[] IVarray) throws Exception{
        if ( ! BCinitialized)
            initBCProvider();

        IvParameterSpec iv = new IvParameterSpec(IVarray);
        SecretKeySpec key = new SecretKeySpec(keyArray, "AES");
        cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
    }

    public byte[] decrypt(byte[] encryptedData) throws Exception{
        return cipher.doFinal(encryptedData);
    }
}
