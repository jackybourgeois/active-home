package org.activehome.tools.file;

/*
 * #%L
 * Active Home :: Tools
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.active-home
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import org.kevoree.log.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

/**
 * This class facilitate reading from/saving into files.
 *
 * @author Jacky Bourgeois
 */
public final class FileHelper {

    /**
     * Size of chunks for image stream.
     */
    private static final int BUFFER_SIZE =  16384;

    /**
     * Utility class.
     */
    private FileHelper() {
    }

    /**
     * @param fileName The name of the file to load
     * @param loader The specific class loader
     * @return the file content as string, empty string otherwise
     */
    public static String fileToString(final String fileName,
                                      final ClassLoader loader) {
        InputStream is = loader.getResourceAsStream(fileName);
        if (is!=null) {
            if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".ico")) {
                return imgStreamToString(is);
            }
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } else {
            Log.error("FileHelper.fileToString => InputStream is null for " + fileName);
        }
        return "";
    }

    /**
     * @param file The file to load
     * @return The file content as string, empty string otherwise
     */
    public static String fileToString(final File file) {
        try {
            InputStream is = new FileInputStream(file);
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param file The file to remove
     */
    public static void removeFile(final File file) {
        try {
            if (!file.delete()) {
                Log.error("Delete operation is failed on " + file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName The name of the file to remove
     */
    public static void removeFile(final String fileName) {
        try {
            File file = new File(fileName);
            if (!file.delete()) {
                Log.error("Delete operation is failed on " + file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param folder The folder to clean
     * @return true if success
     */
    public static boolean cleanFolder(final File folder) {
        boolean result = false;

        if (folder.exists()) {
            if (folder.isDirectory()) {
                if (folder.list().length == 0) {
                    result = folder.delete();
                } else {
                    String[] strFiles = folder.list();

                    for (String strFilename: strFiles) {
                        File fileToDelete = new File(folder, strFilename);

                        cleanFolder(fileToDelete);
                    }
                }
            } else {
                result = folder.delete();
            }
        }

        return result;
    }

    /**
     * Write in file - append.
     *
     * @param message The message to log
     * @param file The destination file
     */
    public static void logln(final String message, final String file) {
        log(message + "\n", file);
    }

    /**
     * Write in file - append.
     *
     * @param message The message to log
     * @param file The destination file
     */
    public static void log(final String message, final String file) {
        FileWriter out = null;
        try {
            out = new FileWriter(file, true);
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Write in file - overwrite.
     *
     * @param content The content to save
     * @param file The destination file
     */
    public static void save(final String content, final String file) {
        FileWriter out = null;
        try {
            out = new FileWriter(file, false);
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param input Stream to read
     * @throws IOException InputStream IO Exception
     */
    public static void process(final InputStream input) throws IOException {
        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }

    /**
     * @param is The image as stream
     * @return The image as string
     */
    public static String imgStreamToString(final InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[BUFFER_SIZE];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            return "data:image/png;base64,"
                    + Base64.getEncoder().encodeToString(buffer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
