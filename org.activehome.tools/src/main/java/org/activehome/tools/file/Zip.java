package org.activehome.tools.file;

import org.kevoree.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class facilitate zip stuff.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final class Zip {

    /**
     * Utility class.
     */
    private Zip() {
    }

    public static void unZip(String zipFile, String outputFolder){

        byte[] buffer = new byte[1024];

        try {
            //create output directory is not exists
            File folder = new File(outputFolder);
            if(!folder.exists()) {
                if (!folder.mkdirs()) {
                    Log.warn("Cannot create folder " + folder.getName());
                }
            }

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

//                System.out.println("File unzip : "+ newFile.getAbsoluteFile());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                if (!new File(newFile.getParent()).mkdirs()) {
                    Log.warn("Cannot create folder " + newFile.getParent());
                }

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

//            System.out.println("Done");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}