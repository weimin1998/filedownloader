package org.example;

import java.util.concurrent.atomic.LongAdder;

public class DownloadInfoThread2 implements Runnable {

    public DownloadInfoThread2(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }

    // 文件大小
    private long httpFileContentLength;

    // 已下载的大小
    public static LongAdder finishedSize = new LongAdder();

    // 本次累计下载的大小
    public static volatile LongAdder downSize = new LongAdder();

    // 前一次下载的大小
    private double prevSize;

    @Override
    public void run() {

        // 文件总大小 mb
        String fileSize = String.format("%.2f", httpFileContentLength / (1024d * 1024d));

        // 每秒下载速度 kb
        int speed = (int) ((downSize.doubleValue() - prevSize) / 1024d);

        prevSize = downSize.doubleValue();

        double remainSize = httpFileContentLength - finishedSize.doubleValue() - downSize.doubleValue();

        String remainTime = String.format("%.1f", remainSize / 1024 / speed);
        if (remainTime.equals("Infinity")) {
            remainTime = "-";
        }

        // 已下载大小
        String currentFileSize = String.format("%.2f", (downSize.doubleValue() - finishedSize.doubleValue()) / (1024d * 1024d));

        String downInfo = String.format("已下载 %smb/%smb, 当前下载速度%skb/s, 剩余下载时间 %ss", currentFileSize, fileSize, speed, remainTime);

        System.out.print("\r");
        System.out.print(downInfo);
    }
}
