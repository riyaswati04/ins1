package com.ia.util;

import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;

public class EncryptUtil {

    public static PrivateKey buildPrivateKey(final String privateKeySerialized) {
        final StringReader reader = new StringReader(privateKeySerialized);
        PrivateKey pKey = null;
        try {
            final PEMReader pemReader = new PEMReader(reader);
            final KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPrivate();
            pemReader.close();
        }
        catch (final IOException i) {
            i.printStackTrace();
        }
        return pKey;
    }

    public static PublicKey buildPublicKey(final String privateKeySerialized) {
        final StringReader reader = new StringReader(privateKeySerialized);
        PublicKey pKey = null;
        try {
            final PEMReader pemReader = new PEMReader(reader);
            final KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPublic();
            pemReader.close();
        }
        catch (final IOException i) {
            i.printStackTrace();
        }
        return pKey;
    }

    public static String encrypt(final String raw, final String encryptAlgo, final Key k) {
        String strEncrypted = "";
        try {
            final Cipher cipher = Cipher.getInstance(encryptAlgo);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            final byte[] encrypted = cipher.doFinal(raw.getBytes("UTF-8"));
            final byte[] encoded = Hex.encode(encrypted);
            strEncrypted = new String(encoded);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return strEncrypted;
    }

    public static String getSignature(final String encryptAlgo, final String digestAlgo,
            final Key k, final String xml) {
        final String dig = makeDigest(xml, digestAlgo);
        return encrypt(dig, encryptAlgo, k);
    }

    public static String makeDigest(final String payload, final String digestAlgo) {
        String strDigest = "";
        try {
            final MessageDigest md = MessageDigest.getInstance(digestAlgo);
            md.update(payload.getBytes("UTF-8"));
            final byte[] digest = md.digest();
            final byte[] encoded = Hex.encode(digest);
            strDigest = new String(encoded);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return strDigest;
    }

}
