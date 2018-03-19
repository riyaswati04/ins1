package com.ia.util;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.lang3.tuple.Pair;

public interface KeyReader {

    Pair<RSAPublicKey, PrivateKey> readKeyPair(String privateKeySerialised) throws IOException;

    RSAPublicKey readPublicKey(String publicKeySerialised) throws IOException;

}
