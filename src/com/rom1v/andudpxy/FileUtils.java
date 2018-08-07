package com.rom1v.andudpxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class with file util methods.
 * 
 * @author rom
 */
public class FileUtils {

    /** Do not instantiate. */
    private FileUtils() {}

    /**
     * Buffer-copy from {@code in} to {code out}.
     * 
     * @param in
     *            the input stream
     * @param os
     *            the output stream
     * @throws IOException
     *             if a I/O error occurs
     */
    public static void copy(InputStream in, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int read;
        while ((read = (in.read(buf))) != -1) {
            os.write(buf, 0, read);
        }
    }

    /**
     * Change the permissions of a file.
     * 
     * The {@code mode} is better expressed as octal value, for instance {@code 0755}.
     * 
     * @param file
     *            the file to have its permission changed
     * @param mode
     *            the mode
     * @return {@code true} if the native {@code chmod} command returns {@code 0}, {@code false}
     *         otherwise
     * @throws IOException
     *             if {@code chmod} cannot be called
     */
    public static boolean chmod(File file, int mode) throws IOException {
        String sMode = String.format("%03o", mode); // to string octal value
        String path = file.getAbsolutePath();
        String[] argv = { "chmod", sMode, path };
        try {
            return Runtime.getRuntime().exec(argv).waitFor() == 0;
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

}
