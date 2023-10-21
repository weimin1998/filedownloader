package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// 多线程分块下载
public class Downloader2 {

    public ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    public ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));

    public void download(String url) {

        HttpURLConnection httpURLConnection = null;
        try {
            String fileName = HttpUtils.getFileName(url);
            String path = "E:\\downloadfile";

            String fullPath = path + File.separator + fileName;

            // 本地文件的大小
            long localFileSize = FileUtil.getFileContentLength(fullPath);
            httpURLConnection = HttpUtils.getHttpURLConnection(url);

            // 要下载的文件的大小
            int contentLength = httpURLConnection.getContentLength();

            DownloadInfoThread2 downloadInfoThread = new DownloadInfoThread2(contentLength);
            pool.scheduleWithFixedDelay(downloadInfoThread, 1, 1, TimeUnit.SECONDS);

            // 切分任务
            List<Future> list = new ArrayList<>();
            split(url, list);

            for (Future future : list) {
                future.get();
            }

            if (merge(fileName)) {
                clearTemp(fileName);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            pool.shutdown();
            executor.shutdown();
        }
    }

    // 文件切分
    public void split(String url, List<Future> futureList) {
        long contentLength = HttpUtils.getHttpFileContentLength(url);

        // 假设切5块
        int num = 5;
        // 每块大小
        long size = contentLength / num;

        for (int i = 0; i < num; i++) {
            long startPos = i * size;
            long endPos;

            if (i == num - 1) {
                // 最后一块
                endPos = 0;
            } else {
                endPos = startPos + size;
            }

            //
            if (startPos != 0) {
                startPos++;
            }

            DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i);

            Future<Boolean> future = executor.submit(downloaderTask);

            futureList.add(future);

        }
    }

    public boolean merge(String fileName) {
        System.out.println("\r");
        System.out.println("开始合并文件");
        byte[] bytes = new byte[1024 * 100];

        int len;

        try (RandomAccessFile randomAccessFile = new RandomAccessFile("E:\\downloadfile\\" + fileName, "rw")) {
            for (int i = 0; i < 5; i++) {
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("E:\\downloadfile\\" + fileName + ".temp" + i))) {
                    while ((len = bufferedInputStream.read(bytes)) != -1) {
                        randomAccessFile.write(bytes, 0, len);
                    }
                }
            }

            System.out.println("\r");
            System.out.println("合并完成");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //删除临时文件
    public boolean clearTemp(String fileName) {
        for (int i = 0; i < 5; i++) {
            File file = new File("E:\\downloadfile\\" + fileName + ".temp" + i);

            file.deleteOnExit();
        }
        return true;
    }
}
