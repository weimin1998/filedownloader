package org.example;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

public class DownloaderTask implements Callable<Boolean> {

    private String url;
    private long startPos;

    private long endPos;

    private int part;

    public DownloaderTask(String url, long startPos, long endPos, int part) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
    }

    @Override
    public Boolean call() throws Exception {
        String fileName = HttpUtils.getFileName(url);

        fileName = fileName + ".temp" + part;

        String fullPath = "E:\\downloadfile\\" + fileName;

        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos);

        try (InputStream inputStream = httpURLConnection.getInputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             RandomAccessFile randomAccessFile = new RandomAccessFile(fullPath, "rw");
        ) {

            int len;
            byte[] bytes = new byte[1024 * 100];
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                // 1秒内下载了多少数据
                DownloadInfoThread2.downSize.add(len);
                randomAccessFile.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            httpURLConnection.disconnect();
        }
        return true;
    }
}
