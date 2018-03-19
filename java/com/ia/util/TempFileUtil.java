package com.ia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TempFileUtil {
    public static String rootDir;

    private static int nextDirName = 1;

    public static File createTempDir() {
        synchronized (rootDir) {
            final File tempDir = new File(rootDir, String.valueOf(nextDirName));
            ++nextDirName;
            if (tempDir.mkdir()) {
                return tempDir;
            }
            return null;
        }
    }

    public static File createTempDir(final String dir) {
        final File tempDir = new File(rootDir, dir);
        if (tempDir.isDirectory()) {
            deleteTempDir(tempDir);
        }
        if (tempDir.mkdir()) {
            return tempDir;
        }
        return null;
    }

    /**
     * Delete the specified directory/file and all files/directories under it.
     */
    public static boolean deleteTempDir(final File f) {
        boolean success = true;

        if (f.isDirectory()) {
            final String[] children = f.list();
            for (final String element : children) {
                final File child = new File(f, element);
                if (!deleteTempDir(child)) {
                    success = false;
                }
            }
        }

        // The directory is now empty so delete it
        if (success && !f.delete()) {
            success = false;
        }
        return success;
    }

    public static void init(final String tempRoot) throws IOException {
        rootDir = tempRoot;

        final File f = new File(tempRoot);
        if (!f.exists()) {
            throw new IOException("Temp directory root " + tempRoot + " is not found!");
        }
        if (!f.isDirectory()) {
            throw new IOException("Temp directory root " + tempRoot + " is not a directory!");
        }

        /*
         * Look for sub-directories under the script execution directory, and get the last numbered
         * sub-directory. For example, say the script execution directory contains subdirectories
         * named 1, 2 and 3. Then the next sub-directory should be named 4.
         */

        final String[] dirs = new File(rootDir).list();
        if (dirs != null && dirs.length != 0) {
            final int length = dirs.length;
            final int[] temp = new int[length];
            for (int i = 0; i < length; ++i) {
                try {
                    temp[i] = Integer.parseInt(dirs[i]);
                }
                catch (final NumberFormatException e) {
                    // for files/directories w/o a numbered name
                    temp[i] = 0;
                }
            }
            Arrays.sort(temp);
            nextDirName = temp[length - 1] + 1;
        }
    }

    public static final void zip(final File directory, final File base, final ZipOutputStream zos)
            throws IOException {
        FileInputStream in = null;

        try {
            final File[] files = directory.listFiles();
            final byte[] buffer = new byte[8192];
            int readCount = 0;
            final int n = files == null ? 0 : files.length;
            for (int i = 0; i < n; i++) {
                // Recursively browse and zip each sub-dir inside
                if (files[i].isDirectory()) {
                    zip(files[i], base, zos);
                }
                else if (files[i].getName().endsWith(".zip")) {
                    continue;
                }
                else {
                    in = new FileInputStream(files[i]);
                    final ZipEntry entry =
                            new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
                    zos.putNextEntry(entry);
                    while (-1 != (readCount = in.read(buffer))) {
                        zos.write(buffer, 0, readCount);
                    }
                    in.close();
                    in = null;
                }
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Exception ignore) {
                }
            }
        }
    }

    public static final void zip(final File base, final String zipFileName, final File toZipFile)
            throws IOException {
        final byte[] buffer = new byte[1024];
        final FileOutputStream fos = new FileOutputStream(new File(base, zipFileName));
        final ZipOutputStream zos = new ZipOutputStream(fos);
        final ZipEntry ze = new ZipEntry(toZipFile.getName());
        zos.putNextEntry(ze);
        final FileInputStream in = new FileInputStream(toZipFile);
        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        in.close();
        zos.closeEntry();
        zos.close();
    }

    public static final void zip(final ZipOutputStream zos, final File fileName)
            throws IOException {
        FileInputStream in = null;
        try {
            final byte[] buffer = new byte[8192];
            int readCount = 0;
            in = new FileInputStream(fileName);
            final ZipEntry entry = new ZipEntry(fileName.getName());
            zos.putNextEntry(entry);
            while (-1 != (readCount = in.read(buffer))) {
                zos.write(buffer, 0, readCount);
            }
            in.close();
            in = null;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Exception ignore) {
                }
            }
        }
    }

    public static final void zipDirectory(final File directory, final File zip) throws IOException {
        ZipOutputStream zos = null;

        try {
            zos = new ZipOutputStream(new FileOutputStream(zip));
            zip(directory, directory, zos);
            zos.close();
            zos = null;
        }
        finally {
            if (zos != null) {
                try {
                    zos.close();
                }
                catch (final Exception ignore) {
                }
            }
        }
    }

    public static final void zipFiles(final File base, final String zipFileName,
            final HashSet<String> filesToZip) throws IOException {
        final String[] filesInDir = base.list();

        if (filesInDir == null || filesInDir.length == 0) {
            return;
        }

        FileOutputStream fout = null;
        ZipOutputStream zout = null;

        try {
            fout = new FileOutputStream(new File(base, zipFileName));
            zout = new ZipOutputStream(fout);
            final byte[] buffer = new byte[1024];

            for (final String element : filesInDir) {
                final String fileName = element;
                if (!filesToZip.contains(fileName)) {
                    continue;
                }
                final File attachFile = new File(base, fileName);
                final FileInputStream fin = new FileInputStream(attachFile);
                zout.putNextEntry(new ZipEntry(attachFile.getName()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                zout.closeEntry();
                fin.close();
            }
            zout.close();
            fout.close();
            fout = null;
            zout = null;
        }
        finally {
            if (zout != null) {
                zout.close();
                zout = null;
            }
            if (fout != null) {
                fout.close();
                fout = null;
            }
        }
    }
}
