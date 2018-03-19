package com.ia.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface Digest {

    String sha1(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException;
}
