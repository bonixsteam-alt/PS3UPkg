import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Downloader {
    URL[] dLinks = {};
    Long[] bytes;
    File dLoc = new File("");

    public void init(URL[] urls, File location, Long[] bytes) {
        this.bytes = bytes;
        dLinks = urls;
        if(!location.isDirectory() || location.getAbsolutePath().equalsIgnoreCase("") || !location.canWrite()) {
            while(true) {
                String defaultDir = getDefaultDownloadDir();
                print(System.lineSeparator() + "You either have no download directory selected, it's invalid or you don't have permissions for it. " + System.lineSeparator() + "Would you like to have your files downloaded to '" + defaultDir + "?' [y/n]");
                Scanner in = new Scanner(System.in);

                String prompt = in.nextLine();
                switch (prompt) {
                    case "y": {
                        dLoc = new File(defaultDir);
                        download();
                        break;
                    }
                    case "n": {
                        print("No download directory. Exiting...");
                        System.exit(0);
                    }
                    default: {
                    }
                }
            }

        } else {
            dLoc = location;
            download();
        }
    }

    String getDefaultDownloadDir () {
        switch(Main.sys) {
            case 1: {
                return System.getProperty("user.home") + "\\Downloads\\" + Main.gameId;
            }
            case 2:
            case 3: {
                return System.getProperty("user.home") + "/Downloads/" + Main.gameId;
            }
            case 0:
            default: {
                print("Unknown system. Exiting...");
                System.exit(1);
            }
        }
        return null;
    }

    void download() {
        print("Starting download...");

        int loop = 0;
        for(URL u : dLinks) {
            long size = bytes[loop];
            String[] cleanup = u.getFile().split("/");
            String dFile = cleanup[cleanup.length-1];

            File file = new File(dLoc.getAbsolutePath() + "/" + dFile);

            Thread download = new Thread(() -> {
                try {
                    FileUtils.copyURLToFile(u, file, 60000, 60000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }); download.start();

            String downloadText = "";

            int percentage;
            while(file.length() < size) {
                percentage = (int)((float)((file.length() * 100) / size));

                downloadText = "Downloading " + file.getName() + "...  " + percentage + "%\r";

                System.out.print(downloadText);
                System.out.flush();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            String empty = new String(new char[downloadText.length()]).replace("\0", " ").concat("\r");
            System.out.print(empty); System.out.flush();

            System.out.print("Downloaded " + file.getName() + ".");
            System.out.flush(); System.out.println("");
            download.interrupt();

            loop++;
        }

        print(System.lineSeparator() + "All files downloaded. Have a nice day.");
        System.exit(0);
    }
    void print(Object s) {
        System.out.println(s);
     }
}
