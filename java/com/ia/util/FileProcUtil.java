package com.ia.util;

import static com.ia.crypto.CryptHandler.doEncryptionDecryptionWithStream;
import static com.ia.crypto.CryptHandler.getCipherInitParams;
import static com.ia.crypto.CryptHandler.getPassPhrase;
import static com.ia.crypto.CryptHandler.toHex;
import static com.ia.log.LogUtil.getLogger;
import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;
import static org.apache.commons.io.FileUtils.readFileToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.ia.common.IAException;
import com.ia.log.Logger;

public class FileProcUtil {
    private static int BYTES_TO_READ[] = {4, 2, 1};

    private static String DENY_FILE_EXTENSION[] = {"exe", "dll", "msi", "so"};

    private static Logger logger = getLogger(FileProcUtil.class);

    // Ref URL http://www.garykessler.net/library/file_sigs.html
    private static String[][] DENY_FILES_SIGNATURE = {
            /* 4 Bytes for Windows exe/dll etc and Unix executable */
            {"4d5a9000", "7f454c46"},
            /* 2 Bytes for Windows exe/dll etc and Unix executable */
            {"4d5a", "7f45"},
            /* 1 Byte for com/sys file */
            {"e8", "e9", "eb"}};

    public static String FILE_NAME = "__fname_";

    public static boolean copyFile(final File oldFile, final File newFile) {
        try {
            FileUtils.copyFile(oldFile, newFile);
            return true;
        }
        catch (final Exception e) {
            logger.error("Copying file " + oldFile + " to " + newFile + " failed.", e);
            return false;
        }
    }

    public static void decryptFile(final File file, final boolean deleteSourceFile)
            throws Exception {
        try {
            if (file != null) {
                final Object[] cipherInitParams = getCipherInitParams(getPassPhrase());
                final SecretKey key = (SecretKey) cipherInitParams[0];
                final AlgorithmParameterSpec paramSpec =
                        (AlgorithmParameterSpec) cipherInitParams[1];
                final Cipher ecipher = Cipher.getInstance(key.getAlgorithm());

                ecipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

                final File cryptoFile = new File(file.getAbsolutePath() + ".decrypted");
                doEncryptionDecryptionWithStream(ecipher, new FileInputStream(file),
                        new FileOutputStream(cryptoFile));
                if (deleteSourceFile) {
                    file.delete();
                    cryptoFile.renameTo(file);
                }
            }
        }
        catch (final Exception e) {
            logger.error(e, "File decryption failed");
        }
    }

    public static void encryptFile(final File file, final boolean deleteSourceFile)
            throws Exception {
        try {
            if (file != null) {
                final Object[] cipherInitParams = getCipherInitParams(getPassPhrase());
                final SecretKey key = (SecretKey) cipherInitParams[0];
                final AlgorithmParameterSpec paramSpec =
                        (AlgorithmParameterSpec) cipherInitParams[1];
                final Cipher ecipher = Cipher.getInstance(key.getAlgorithm());

                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

                final File cryptoFile = new File(file.getAbsolutePath() + ".encrypted");
                doEncryptionDecryptionWithStream(ecipher, new FileInputStream(file),
                        new FileOutputStream(cryptoFile));
                if (deleteSourceFile) {
                    file.delete();
                    cryptoFile.renameTo(file);
                }
            }
        }
        catch (final Exception e) {
            logger.error(e, "File encryption failed");
        }
    }

    public static boolean isFileSecure(InputStream is, final String fileName) {
        try {
            for (int i = 0; i < BYTES_TO_READ.length; i++) {
                final int readBytes = BYTES_TO_READ[i];
                final byte b[] = new byte[readBytes];
                is.read(b, 0, readBytes);
                String signature = toHex(b);
                if (signature == null || signature.length() == 0) {
                    continue;
                }
                signature = signature.toLowerCase();
                final String[] compareBytes = DENY_FILES_SIGNATURE[i];
                for (final String compareByte : compareBytes) {
                    if (signature.equals(compareByte)) {
                        return false;
                    }
                }
                // Inputstream would have positioned at readBytes position, now come back to 0
                // position.
                is.skip(-1 * readBytes);
            }
            is.close();
            is = null;
        }
        catch (final Exception e) {
            logger.error("Exception during checking file security", e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ee) {
                }
            }
        }
        // If content secure now check for extension
        final int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex > -1) {
            final String fileExtension =
                    fileName.substring(extensionIndex + 1, fileName.length()).toLowerCase();
            for (final String element : DENY_FILE_EXTENSION) {
                if (element.equals(fileExtension)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<FileItem> parseRequest(final File tempDir,
            final HttpServletRequest request) {

        List<FileItem> items = new ArrayList<FileItem>();

        if (!isMultipartContent(request)) {
            logger.error("No file sent with the HTTP request [act=uploadFile]");
        }

        // Create a factory for disk-based file items
        // The value 1024 bytes is intentional decided small value
        // so that file of any size uploaded by the user never processed in
        // memory
        final DiskFileItemFactory factory = new DiskFileItemFactory(1024, tempDir);

        // Create a new file upload handler
        final ServletFileUpload upload = new ServletFileUpload(factory);

        // Set overall request size constraint
        upload.setSizeMax(83886080);

        // Parse the request

        try {
            items = upload.parseRequest(request);
        }
        catch (final FileUploadException e) {
            final String msg = e.getMessage();
            if (msg != null && msg.contains("Stream ended unexpectedly")) {
                // no point logging exception when the channel is closed for a
                // possible network drop
                logger.error(
                        "Error receiving uploaded file [act=uploadFile, " + "error=" + msg + "]");
            }
            else {
                logger.exception("Error while parsing uploaded file [act=uploadFile]", e);
            }
        }
        return items;
    }

    public static String readDecryptedFile(final File file) throws Exception {
        try {
            if (file != null) {
                final Object[] cipherInitParams = getCipherInitParams(getPassPhrase());
                final SecretKey key = (SecretKey) cipherInitParams[0];
                final AlgorithmParameterSpec paramSpec =
                        (AlgorithmParameterSpec) cipherInitParams[1];
                final Cipher ecipher = Cipher.getInstance(key.getAlgorithm());

                ecipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

                final File cryptoFile = new File(file.getAbsolutePath() + ".decrypted");
                doEncryptionDecryptionWithStream(ecipher, new FileInputStream(file),
                        new FileOutputStream(cryptoFile));

                final String fileData = readFileToString(cryptoFile);

                cryptoFile.delete();
                return fileData;
            }
        }
        catch (final Exception e) {
            logger.error(e, "File decryption failed, filePath=%s", file.getAbsolutePath());
        }
        return null;
    }

    public static String readFile(final String fileName) throws IOException {
        return readFileToString(new File(fileName));
    }

    public static boolean writeLocal(final File tempDir, final HttpServletRequest request,
            final HashMap<String, String> formParams) {

        // Parse the request
        final List<FileItem> items = parseRequest(tempDir, request);
        if (items.size() == 0) {
            logger.error("Error while parsing file contents from HTTP request [act=uploadFile]");
            return false;
        }

        // Process the uploaded items.
        // Write the form parameters into the hashmap for client to query.
        // Note: It is possible that a single request object might have
        // multiple files. Consider only the first file, but all form
        // parameters.
        boolean fileWritten = false;
        final StringBuilder sb = new StringBuilder();
        String k = null;
        String v = null;
        for (final FileItem item : items) {
            if (item.isFormField()) {
                k = item.getFieldName();
                v = item.getString();
                formParams.put(k, v);
                // do not log parameter whose name starts with underscore (_)
                if (k.charAt(0) == '_') {
                    continue;
                }
                sb.append(k).append("=").append(v).append(";");
            }
            else if (!fileWritten) {
                // Process a file upload
                fileWritten = true;
                try {
                    if (writeLocalFile(FILE_NAME, formParams, tempDir, item,
                            item.getName()) == null) {
                        return false;
                    }
                }
                catch (final IAException ke) {
                    return false;
                }
            }
        }
        logger.info("File upload form params:" + sb.toString());

        if (!fileWritten) {
            logger.error("No file was sent during this upload [act=uploadFile]");
            return false;
        }

        return true;
    }

    private static File writeLocalFile(final String fileKey,
            final HashMap<String, String> formParams, final File tempDir, final FileItem item,
            String fileName) throws IAException {
        // IE gives us a filename which is the entire client file path, in such
        // case get just the fileName
        // @see http://commons.apache.org/fileupload/faq.html#whole-path-from-IE
        if (fileName != null) {
            fileName = FilenameUtils.getName(fileName);
        }
        final String contentType = item.getContentType();
        final boolean isInMemory = item.isInMemory();
        final long sizeInBytes = item.getSize();
        // Dump the file name into the hashmap
        formParams.put(fileKey, fileName);

        logger.info(new StringBuilder().append("User uploaded file [filename=").append(fileName)
                .append(", contentType=").append(contentType).append(", isInMemory=")
                .append(isInMemory).append(", sizeInBytes=").append(sizeInBytes)
                .append(", datadir=").append(tempDir.getName()).append("]").toString());

        final File localFile = new File(tempDir, fileName);
        try {
            final boolean isSecureFile = isFileSecure(item.getInputStream(), fileName);
            if (!isSecureFile) {
                logger.error("Unsecured file uploaded by user");
                return null;
            }
            item.write(localFile);
        }
        catch (final Exception e) {
            throw new IAException("Error while writing uploaded file to temporary location "
                    + "[act=uploadFile,localFile=" + localFile + "]", e);
        }
        return localFile;
    }
}
