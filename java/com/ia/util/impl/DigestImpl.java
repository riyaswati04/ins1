package com.ia.util.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;

import com.google.inject.Singleton;
import com.ia.util.Digest;

@Singleton
public class DigestImpl implements Digest {
    @Override
    public String sha1(final String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(input.getBytes("UTF-8"));
        final byte[] digest = md.digest();
        final byte[] encoded = Hex.encode(digest);
        return new String(encoded);
    }
}
