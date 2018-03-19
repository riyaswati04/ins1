package com.ia.crypto;

import static org.apache.commons.lang3.tuple.Pair.of;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * A data structure representing RSA public-private key pair.
 */
public class KuberaKeys {

    // Password of private key in db
    private static char[] pass;

    private static final String ALGO = "DESEDE";

    private static final SecureRandom random = new SecureRandom();

    // Done by cryptHandler on start
    static void setPemPass(final String pemPass) {
        pass = pemPass.toCharArray();
    }

    private Key privKey;

    private RSAPublicKey pubKey;

    public KuberaKeys(final RSAPublicKey pubKeyParam, final Key prvKeyParam) {
        pubKey = pubKeyParam;
        privKey = prvKeyParam;
    }

    public KuberaKeys(final String encodedPrivKey) throws IOException {
        final PEMReader keyReader =
                new PEMReader(new StringReader(encodedPrivKey), new PasswordFinder() {
                    @Override
                    public char[] getPassword() {
                        return pass;
                    }
                });
        final KeyPair pair = (KeyPair) keyReader.readObject();
        pubKey = (RSAPublicKey) pair.getPublic();
        privKey = pair.getPrivate();
        keyReader.close();
    }

    public Pair<String, String> getModulusExponentPair() {
        return of(pubKey.getModulus().toString(16), pubKey.getPublicExponent().toString(16));
    }

    public Key getPrivateKey() {
        return privKey;
    }

    public String getPrivateKeySerialized() throws IOException {
        final StringWriter sw = new StringWriter();
        final PEMWriter keyWriter = new PEMWriter(sw);
        keyWriter.writeObject(privKey, ALGO, pass, random);
        keyWriter.flush();
        keyWriter.close();
        return sw.toString();
    }

    public Key getPublicKey() {
        return pubKey;
    }

    public String getPublicKeySerialized() {
        return new StringBuilder().append(pubKey.getModulus().toString(16))
                .append(pubKey.getPublicExponent().toString(16)).toString();
    }
}
