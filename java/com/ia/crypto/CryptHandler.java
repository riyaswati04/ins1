package com.ia.crypto;

import static com.ia.common.IAProperties.getProperty;
import static com.ia.crypto.KuberaKeys.setPemPass;
import static com.ia.log.LogUtil.getLogger;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.inject.Inject;
import com.ia.log.Logger;

/**
 * Handles all encryption-decryption requirements of Job server.
 * <p>
 * Includes the following:
 * <ul>
 * <li>Encryption and decryption of account credentials. Encryption is done by ia Server and
 * decryption is done at the Job server.
 * <li>Encryption and decryption of Perl scripts.
 * <li>Encryption and decryption of message exchanges with the ia Server.
 * </ul>
 */
public final class CryptHandler {

    private static char[] HexChars =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    // Max number of bytes for async encryption.
    // How to arrive at 53? Start from modulus. 512 / 8 - 11 = 53. (11 bytes for
    // padding)
    public static int MAX_INPUT_BYTES_LEN = 53;

    private static final String LOG_ERROR_DECRYPTING = "Error decrypting signature [signature=%s]";

    private static HashMap<String, byte[]> decryptedCodes = new HashMap<String, byte[]>();

    private static HashMap<String, Long> decryptedCodeTimeStamp = new HashMap<String, Long>();

    private static Cipher scriptDecipher, userDataDecipher;

    public static void decryptScript(final InputStream in, final OutputStream out)
            throws IOException {
        // // Buffer used to transport the bytes from one stream to another
        // final byte[] buf = new byte[1024];
        //
        // // Bytes read from in will be decrypted
        // in = new CipherInputStream(in, scriptDecipher);
        //
        // // Read in the decrypted bytes and write the cleartext to out
        // int numRead = 0;
        // while ((numRead = in.read(buf)) >= 0) {
        // out.write(buf, 0, numRead);
        // }
        // out.close();
        doEncryptionDecryptionWithStream(scriptDecipher, in, out);
    }

    public static void doEncryptionDecryptionWithStream(final Cipher ecipher, final InputStream in,
            OutputStream out) throws IOException {
        // Buffer used to transport the bytes from one stream to another
        final byte[] buf = new byte[1024];

        // Bytes written to out will be encrypted/decrypted
        out = new CipherOutputStream(out, ecipher);

        // Read in the cleartext/decrypted bytes and write cleartext/encrypted to out
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }

    static void encryptFile(final String[] args) throws Exception {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Illegal usage");
            return;
        }
        // First parameter is source file
        final String inFileName = args[0];
        // Second parameter is the destination file
        final String outFileName = args[1];
        // Third parameter, if present, is the crypt passphrase
        String passPhrase;
        if (args.length > 2) {
            passPhrase = args[2];
        }
        else {
            // Not present. Get from kubera.properties file
            final ResourceBundle rb = ResourceBundle.getBundle("ia");
            passPhrase = rb.getString("crypt.passPhrase");
        }
        if (passPhrase.length() == 0) {
            System.out.println("Passphrase is undefined");
            return;
        }

        final Object[] cipherInitParams = getCipherInitParams(passPhrase);
        final SecretKey key = (SecretKey) cipherInitParams[0];
        final AlgorithmParameterSpec paramSpec = (AlgorithmParameterSpec) cipherInitParams[1];
        final Cipher ecipher = Cipher.getInstance(key.getAlgorithm());

        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

        System.out.println("Encrypting file and copying to " + outFileName);

        encryptScript(ecipher, new FileInputStream(new File(inFileName)),
                new FileOutputStream(new File(outFileName)));
    }

    public static void encryptScript(final Cipher ecipher, final InputStream in,
            final OutputStream out) throws IOException {
        doEncryptionDecryptionWithStream(ecipher, in, out);
    }

    public static byte[] fromHex(final String str) {
        final byte[] out = new byte[str.length() / 2];

        for (int i = 0; i < str.length(); i += 2) {
            final char c1 = str.charAt(i);
            final char c2 = str.charAt(i + 1);
            final int b = 16 * (c1 >= 'a' ? c1 - 'a' + 10 : c1 - '0')
                    + (c2 >= 'a' ? c2 - 'a' + 10 : c2 - '0');
            out[i / 2] = (byte) b;
        }

        return out;
    }

    /**
     * Generate a public-private key pair for the organisation and return them serialized as String.
     */
    public synchronized static KuberaKeys generateKeyPair()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(512);
        final KeyPair pair = generator.generateKeyPair();
        final RSAPublicKey pubKey = (RSAPublicKey) pair.getPublic();
        final Key privKey = pair.getPrivate();
        // logger.debug("Public Key = " + pubKey);
        // logger.debug("Private Key = " + privKey);
        return new KuberaKeys(pubKey, privKey);
    }

    public static Object[] getCipherInitParams(final String passPhrase) throws Exception {
        // 8-byte Salt
        final byte[] salt = {(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56,
                (byte) 0x35, (byte) 0xE3, (byte) 0x03};

        // Iteration count
        final int iterationCount = 9;

        // Create the key
        final KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);

        final SecretKey key =
                SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        // Prepare the parameter to the ciphers
        final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

        final Object[] cipherInitParams = new Object[2];
        cipherInitParams[0] = key;
        cipherInitParams[1] = paramSpec;

        return cipherInitParams;
    }

    public static String getPassPhrase() {
        return getProperty("crypt.passPhrase");
    }

    public static void main(final String[] args) throws Exception {
        if (args.length > 0) {
            // Package all input scripts to encrypted files under /scripts
            // directory.
            // Called during build time, not at run time.
            encryptFile(args);
        }
        else {
        }
    }

    public static String toHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte c : bytes) {
            final int b = c & 0xff;
            sb.append(HexChars[b / 16]).append(HexChars[b % 16]);
        }
        return sb.toString();
    }

    private final Logger logger = getLogger(getClass());

    @Inject
    public CryptHandler() {}

    public String decrypt(final String signature, final PrivateKey privateKey) {
        try {
            return decryptUserData(privateKey, signature);
        }
        catch (final Exception e) {
            logger.error(LOG_ERROR_DECRYPTING, signature);
        }

        return null;
    }

    /**
     * Given a key, decrypt a Blowfish encrypted cipher
     *
     * Following is the current usage:
     * <ul>
     * <li>Cipher data will be used for Blowfish Decryption.
     * <li>It pads the data with spaces.
     * <li>Then it picks up 8 bytes of data in each go and decrypts them.
     * <li>This happens over and over again until the neatly padded data is decrypted.
     * </ul>
     *
     * @param cipher
     * @param key
     * @return
     */
    public String decryptBlowfish(final String cipher, final String key) {
        try {
            final BlowFishDecryptor blowFishDecryptor = new BlowFishDecryptor(key);
            final byte[] cipherBytes = fromHex(cipher);
            final int blocks = cipherBytes.length / 8;
            for (int i = 0; i < blocks; ++i) {
                blowFishDecryptor.decrypt(cipherBytes, i * 8);
            }
            return new String(cipherBytes);
        }
        catch (final CryptoException e) {
            logger.error(e, "CryptoException: Error while decrypting");
            return null;
        }
        catch (final UnsupportedEncodingException e) {
            logger.error(e, "UnsupportedEncodingException: Error while decrypting");
            return null;
        }
        catch (final Exception e) {
            logger.error(e, "Exception: Error while decrypting");
            return null;
        }
    }

    public synchronized String decryptUserData(final Key privKey, String str) throws Exception {
        // str may be longer than 128 characters.
        // Decrypt 128 characters at a time.
        String decryptedStr = "";
        while (str.length() > 0) {
            String item;
            if (str.length() > 128) {
                item = str.substring(0, 128);
                str = str.substring(128);
            }
            else {
                item = str;
                str = "";
            }
            try {
                userDataDecipher.init(DECRYPT_MODE, privKey);
                final byte[] decryptedBytes = userDataDecipher.doFinal(fromHex(item));
                decryptedStr += new String(decryptedBytes, "UTF-8");
            }
            catch (final Exception ex) {
                try {
                    userDataDecipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
                }
                catch (final Exception e) {
                    logger.error(e, "Exception re-initializing user data decipher");
                }
                throw ex;
            }
        }
        return decryptedStr;
    }

    public synchronized String encryptUserData(final Key pubKey, final String str)
            throws Exception {
        // Number of bytes for the string may be more than MAX_INPUT_BYTES_LEN.
        // Encrypt MAX_INPUT_BYTES_LEN bytes at a time.
        byte[] bytes = str.getBytes("UTF-8");
        String encryptedStr = "";
        while (bytes.length > 0) {
            byte item[];
            if (bytes.length > MAX_INPUT_BYTES_LEN) {
                item = new byte[MAX_INPUT_BYTES_LEN];
                System.arraycopy(bytes, 0, item, 0, MAX_INPUT_BYTES_LEN);
                final int newLength = bytes.length - MAX_INPUT_BYTES_LEN;
                final byte[] _bytes = new byte[newLength];
                System.arraycopy(bytes, MAX_INPUT_BYTES_LEN, _bytes, 0, newLength);
                bytes = _bytes;
            }
            else {
                item = bytes;
                bytes = new byte[0];
            }
            try {
                userDataDecipher.init(ENCRYPT_MODE, pubKey);
                final String encryptedItem = toHex(userDataDecipher.doFinal(item));
                encryptedStr += encryptedItem;
            }
            catch (final Exception ex) {
                try {
                    userDataDecipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
                }
                catch (final Exception e) {
                    logger.error(e, "Exception re-initializing user data decipher");
                }
                throw ex;
            }
        }
        return encryptedStr;
    }

    /**
     * This code should be preceded by blocking of the corresponding job. When the job is unblocked
     * later, the call to getCode will force reading the (modified) script from the corresponding
     * file.
     */
    public synchronized void forgetCode(final String scriptName) {
        // logger.debug("Removing cached code for script " + scriptName);
        decryptedCodes.remove(scriptName);
        decryptedCodeTimeStamp.remove(scriptName);
    }

    public String generateSHA256(final String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        try {
            final byte[] bytes = str.getBytes("UTF-8");
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digest = md.digest(bytes);
            return toHex(digest);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public synchronized byte[] generateSymmetricKey()
            throws NoSuchAlgorithmException, NoSuchProviderException {
        final KeyGenerator generator = KeyGenerator.getInstance("AES", "BC");
        generator.init(128);
        final SecretKey secretKey = generator.generateKey();
        return secretKey.getEncoded();
    }

    public synchronized byte[] getCode(final String scriptName) {
        // If the code is already decrypted and cached, return it
        // logger.debug("Trying to get cached code for script " + scriptName);
        byte[] code = decryptedCodes.get(scriptName);
        long existingTS = 0;
        if (code != null) {
            existingTS = decryptedCodeTimeStamp.get(scriptName);
        }
        ClassLoader cl = this.getClass().getClassLoader();
        // Do not use getResourceAsStream() because Tomcat will cache the file
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        final URL url = cl.getResource("scripts/" + scriptName);
        if (url == null) {
            logger.error("Unable to find script code for " + scriptName);
            return null;
        }
        final String filePath = url.getFile();
        final File scriptFile = new File(filePath);
        final long modifiedTS = scriptFile.lastModified();
        if (existingTS == modifiedTS && code != null) {
            return code;
        }
        // Else get the code, decrypt, cache and return
        InputStream in = null;
        try {
            in = url.openStream();
            final ByteArrayOutputStream bo = new ByteArrayOutputStream(1024);
            decryptScript(in, bo);
            code = bo.toByteArray();
            logger.info("Caching code for script " + scriptName);
            decryptedCodes.put(scriptName, code);
            decryptedCodeTimeStamp.put(scriptName, modifiedTS);
            in.close();
            in = null;
            return code;
        }
        catch (final Exception e) {
            logger.error("Unable to decrypt script code for " + scriptName, e);
            return null;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Exception ee) {
                }
            }
        }
    }

    public String getHash(final String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        try {
            final byte[] bytes = str.getBytes("UTF-8");
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            final byte[] digest = md.digest(bytes);
            return toHex(digest);
        }
        catch (final Exception e) {
            return "";
        }
    }

    public void shutdown() {}

    public void start() {
        // start() is called by the servlet at startup or after a shutdown.

        try {
            Security.addProvider(new BouncyCastleProvider());
            // Perl script decipher is pass-phrase based PBEWithMD5AndDES
            final String passPhrase = getPassPhrase();

            setPemPass(getProperty("crypt.rsa.pem.pass"));

            final Object[] cipherInitParams = getCipherInitParams(passPhrase);
            final SecretKey key = (SecretKey) cipherInitParams[0];
            final AlgorithmParameterSpec paramSpec = (AlgorithmParameterSpec) cipherInitParams[1];
            scriptDecipher = Cipher.getInstance(key.getAlgorithm());
            scriptDecipher.init(DECRYPT_MODE, key, paramSpec);

            // User data decipher is RSA based with PKCS1 Padding
            userDataDecipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
        }
        catch (final Exception e) {
            logger.error(e, "Crypto: initialization failed");
        }
    }

}
