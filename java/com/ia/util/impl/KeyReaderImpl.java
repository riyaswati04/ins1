package com.ia.util.impl;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.tuple.Pair.of;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.openssl.PEMReader;

import com.google.inject.Singleton;
import com.ia.util.KeyReader;

@Singleton
public final class KeyReaderImpl implements KeyReader {

    @Override
    public Pair<RSAPublicKey, PrivateKey> readKeyPair(final String privateKeySerialised)
            throws IOException {

        PEMReader pemReader = null;

        try {
            final StringReader reader = new StringReader(privateKeySerialised);
            pemReader = new PEMReader(reader);
            final KeyPair keyPair = (KeyPair) pemReader.readObject();

            final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            final PrivateKey privateKey = keyPair.getPrivate();
            return of(publicKey, privateKey);

        }
        finally {
            closeQuietly(pemReader);
        }
    }

    @Override
    public RSAPublicKey readPublicKey(final String publicKeySerialised) throws IOException {
        PEMReader pemReader = null;

        try {
            final StringReader reader = new StringReader(publicKeySerialised);
            pemReader = new PEMReader(reader);
            return (RSAPublicKey) pemReader.readObject();
        }
        finally {
            closeQuietly(pemReader);
        }
    }

}
