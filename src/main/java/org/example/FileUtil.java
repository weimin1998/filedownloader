package org.example;

import java.io.File;

public class FileUtil {

    public static long getFileContentLength(String path) {
        File file = new File(path);

        return file.exists() && file.isFile() ? file.length() : 0;
    }
}
