package com.ia.crypto;

import java.io.UnsupportedEncodingException;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class BlowFishDecryptor {

    // Make sure its platform independent. Hence UTF-8
    private static final String ENCODING = "UTF-8";

    private final BlowfishEngine cipher;

    private final KeyParameter _key;

    public BlowFishDecryptor(final byte[] key) {
        cipher = new BlowfishEngine();
        _key = new KeyParameter(key);
    }

    public BlowFishDecryptor(final String aKey) throws UnsupportedEncodingException {
        this(aKey.getBytes(ENCODING));
    }

    private void callCipher(final byte[] block, final int index) throws CryptoException {
        cipher.processBlock(block, index, block, index);
    }

    public synchronized void decrypt(final byte[] block, final int index) throws CryptoException {
        cipher.init(false, _key);
        callCipher(block, index);
    }
}
