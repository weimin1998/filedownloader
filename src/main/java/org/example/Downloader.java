package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 单线程下载
public class Downloader {

    public ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    public void download(String url) {
        String fileName = HttpUtils.getFileName(url);
        String path = "E:\\downloadfile";

        String fullPath = path + File.separator + fileName;

        // 本地文件的大小
        long localFileSize = FileUtil.getFileContentLength(fullPath);


        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url);

        // 要下载的文件的大小
        int contentLength = httpURLConnection.getContentLength();

        DownloadInfoThread downloadInfoThread = new DownloadInfoThread(contentLength);
        pool.scheduleWithFixedDelay(downloadInfoThread, 1, 1, TimeUnit.SECONDS);


        try (InputStream inputStream = httpURLConnection.getInputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(fullPath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        ) {
            int len;
            byte[] bytes = new byte[1024 * 100];
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                downloadInfoThread.downSize += len;
                bufferedOutputStream.write(bytes, 0, len);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpURLConnection.disconnect();
            pool.shutdown();
        }
    }
}
