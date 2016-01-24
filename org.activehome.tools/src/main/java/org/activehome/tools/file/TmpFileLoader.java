package org.activehome.tools.file;

import org.kevoree.log.Log;

import java.io.*;
import java.net.URL;

/**
 * This class facilitate loading of temporary files.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class TmpFileLoader {

    private File templateDir;
    private final ClassLoader loader;

    public TmpFileLoader(String name, ClassLoader loader) {
        this.loader = loader;
        createTempDir(name);
    }

    boolean createTempDir(String tempDir) {
        templateDir = new File(System.getProperty("activehome.tmp")+tempDir);
        return templateDir.mkdirs();
    }

    public String addFile(String fileName) {
        return addFile(fileName,fileName, true);
    }

    public String addFileIfNotExist(String fileName) {
        return addFile(fileName,fileName, false);
    }

    public String addFileIfNotExist(String urlSrc, String fileNameDest) {
        return addFile(urlSrc,fileNameDest, false);
    }

    String addFile(String fileNameSrc, String fileNameDest, boolean override) {
        String[] path = fileNameDest.split("/");
        if (path.length>1) {
            String tmpPath = fileNameDest.substring(0,fileNameDest.lastIndexOf("/"));
            File dir = new File(templateDir +"/"+tmpPath);
            if (!dir.mkdirs()) {
                Log.warn("Cannot create folder " + dir.getName());
            }
        }

        try {
            File temporaryFile = new File(templateDir +"/"+fileNameDest);
//            System.out.println("[TmpFileLoader] Loading source " + temporaryFile);
            if (!temporaryFile.exists() || override) {
                InputStream is;
                if (fileNameSrc.startsWith("http://")) {
                    URL url = new URL(fileNameSrc);
                    is = url.openConnection().getInputStream();
                } else {
                    is = loader.getResourceAsStream(fileNameSrc);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile);
                byte buf[] = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNameDest;
    }

    public void addFolder(File folder, String folderNameDest) {
        File dir = new File(templateDir +"/"+folderNameDest);
        if (!dir.mkdirs()) {
            Log.warn("Cannot create folder " + dir.getName());
        }

        File files[] = folder.listFiles();

        for (File file : files != null ? files : new File[0]) {
            File temporaryFile = new File(templateDir +"/"+folderNameDest+file.getName());
            try {
                InputStream is = new FileInputStream(file);
                FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile);
                byte buf[]=new byte[1024];
                int len;
                while((len=is.read(buf))>0) {
                    fileOutputStream.write(buf,0,len);
                }
                fileOutputStream.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPath() {
        return templateDir.getPath();
    }


}
