package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url;
        if (args == null || args.length == 0) {
            while (true) {
                System.out.println("请输入下载链接：");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if (url != null) {
                    break;
                }
            }
        } else {
            url = args[0];
        }

        System.out.println("下载链接：" + url);

        // new Downloader().download(url);

        new Downloader2().download(url);

        System.out.print("\r");
        System.out.print("下载完成");
    }
}