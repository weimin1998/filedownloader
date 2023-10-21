package org.example;

public class DownloadInfoThread implements Runnable {

    public DownloadInfoThread(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }

    // 文件大小
    private long httpFileContentLength;

    // 已下载的大小
    private double finishedSize;

    // 本次累计下载的大小
    public volatile double downSize;

    // 前一次下载的大小
    private double prevSize;

    @Override
    public void run() {

        // 文件总大小 mb
        String fileSize = String.format("%.2f", httpFileContentLength / (1024d * 1024d));

        // 每秒下载速度 kb
        int speed = (int) ((downSize - prevSize) / 1024d);

        prevSize = downSize;

        double remainSize = httpFileContentLength - finishedSize - downSize;

        String remainTime = String.format("%.1f", remainSize / 1024 / speed);
        if(remainTime.equals("Infinity")){
            remainTime = "-";
        }

        // 已下载大小
        String currentFileSize = String.format("%.2f", (downSize - finishedSize) / (1024d * 1024d));

        String downInfo = String.format("已下载 %smb/%smb, 当前下载速度%skb/s, 剩余下载时间 %ss", currentFileSize, fileSize, speed, remainTime);

        System.out.print("\r");
        System.out.print(downInfo);
    }
}
