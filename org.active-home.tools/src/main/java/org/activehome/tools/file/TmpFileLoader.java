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

import java.io.*;
import java.net.URL;

/**
 * This class facilitate loading of temporary files.
 *
 * @author Jacky Bourgeois
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
