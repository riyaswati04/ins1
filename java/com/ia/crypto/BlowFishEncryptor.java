package com.ia.crypto;

import static com.ia.crypto.CryptHandler.toHex;
import static java.lang.System.arraycopy;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.crypto.Cipher.getInstance;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.CryptoException;

public class BlowFishEncryptor {

    // Make sure its platform independent. Hence UTF-8
    private static final String ENCODING = "UTF-8";

    private static final String ALGORITHM = "Blowfish";

    private static final String BLOWFISH_ALGORITHM = "Blowfish/ECB/NoPadding";

    public BlowFishEncryptor() {}

    public synchronized String encrypt(final String data, final String secretKey)
            throws CryptoException, UnsupportedEncodingException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {

        final SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(ENCODING), ALGORITHM);

        final Cipher cipher = getInstance(BLOWFISH_ALGORITHM);
        cipher.init(ENCRYPT_MODE, key);

        final int totalLength = data.length();
        int padRightLength = totalLength % 8;

        if (padRightLength != 0) {
            padRightLength = 8 - padRightLength;
        }

        final byte[] initialData = data.getBytes();
        final byte[] finalData = new byte[totalLength + padRightLength];

        arraycopy(initialData, 0, finalData, 0, totalLength);

        for (int i = totalLength; i < totalLength + padRightLength; i++) {
            finalData[i] = (char) ' ';
        }

        return toHex(cipher.doFinal(finalData));
    }
}
