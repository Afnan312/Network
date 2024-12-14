import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import util.NetworkUtil;

public class WriteThreadClient implements Runnable {

    private Thread thr;
    private NetworkUtil networkUtil;
    private String name;
    private ClientFiles clientFiles;
    private Scanner scanner;

    public WriteThreadClient(NetworkUtil networkUtil, String name, ClientFiles clientFiles) {
        this.networkUtil = networkUtil;
        this.name = name;
        this.clientFiles=clientFiles;
        scanner = new Scanner(System.in);
        this.thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            int act;
            while(true) {
                System.out.println("Choose activity: ");
                System.out.println("1. See other connected accounts\n" +
                        "2. See My Uploaded Files\n" +
                        "3. See others' files\n" +
                        "4. Request file\n" +
                        "5. See others' requests\n" +
                        "6. My Inbox\n" +
                        "7. Upload file\n" +
                        "8. Logout");
                act = scanner.nextInt();
                scanner.nextLine();

                if (act == 1) {//see other accounts
                    Info info = new Info(name, false, false);
                    networkUtil.write(info);
                }

                else if (act == 2) {//see own uploaded files
                    Info info = new Info(name, true, true);
                    networkUtil.write(info);
                    Thread.sleep(500);
                    System.out.println("Do you want to download any file? enter yes or no");
                    if(scanner.nextLine().equalsIgnoreCase("yes")) {
                        Download download = new Download();
                        System.out.println("Enter public or private:");
                        download.setPriv(scanner.nextLine().equalsIgnoreCase("private"));
                        System.out.println("Enter the file name (case-sensitive):");
                        String filename = scanner.nextLine();
                        System.out.println("Enter the directory where you want to download the file:");
                        download.setSavePath(scanner.nextLine());
                        download.setFrom(name);
                        download.setName(filename);
                        download.setClientName(name);
                        networkUtil.write(download);
                    }
                }

                else if (act == 3) {//see others' uploaded files
                    Info info = new Info(name, true, false);
                    networkUtil.write(info);
                    Thread.sleep(500);
                    System.out.println("Do you want to download any file? enter yes or no");
                    if(scanner.nextLine().equalsIgnoreCase("yes")){
                        Download download=new Download();
                        System.out.println("Enter the user's name (case-sensitive):");
                        download.setFrom(scanner.nextLine());
                        System.out.println("Enter the file name (case-sensitive):");
                        download.setName(scanner.nextLine());
                        System.out.println("Enter the directory where you want to download the file:");
                        download.setSavePath(scanner.nextLine());
                        download.setClientName(name);
                        download.setPriv(false);
                        networkUtil.write(download);
                    }
                }

                else if (act == 4) {//request for a file
                    String desc;
                    System.out.println("Write a description of the file in a line (shouldn't contain ';'):");
                    desc = scanner.nextLine();
                    Request request = new Request(name, desc);
                    networkUtil.write(request);
                }

                else if (act == 5) {//see file requests
                    AllRequests allRequests=new AllRequests();
                    allRequests.setTo(name);
                    networkUtil.write(allRequests);
                    Thread.sleep(500);
                    System.out.println("Would you like to respond to any request? enter yes or no");
                    if(scanner.nextLine().equalsIgnoreCase("yes")){
                        Upload upload=new Upload();
                        System.out.println("Enter the request ID: ");
                        upload.setRequestID(scanner.nextInt());
                        scanner.nextLine();
                        System.out.println("Enter the filepath: ");
                        upload.setFilepath(scanner.nextLine());
                        File file=new File(upload.getFilepath());
                        upload.setFilesize((int) file.length());
                        System.out.println("Enter file name: ");
                        upload.setName(scanner.nextLine());
                        upload.setPriv(false);
                        upload.setClientName(name);
                        upload.setRequest(true);
                        System.out.println("In the client write thread");
                        networkUtil.write(upload);
                    }
                }

                else if(act == 6){//see inbox
                    AllMessages allMessages=new AllMessages();
                    allMessages.setTo(name);
                    allMessages.setRead(false);
                    networkUtil.write(allMessages);
                }

                else if(act == 7){//upload file/////////////////////////////////////////////////////////////
                    Upload upload=new Upload();
                    System.out.println("Enter the filepath: ");
                    upload.setFilepath(scanner.nextLine());
                    File file=new File(upload.getFilepath());
                    upload.setFilesize((int) file.length());
                    System.out.println("Enter file name: ");
                    upload.setName(scanner.nextLine());
                    System.out.println("Enter public or private: ");
                    upload.setPriv(scanner.nextLine().equalsIgnoreCase("private"));
                    upload.setClientName(name);
                    upload.setRequest(false);
                    System.out.println("In the client write thread");
                    networkUtil.write(upload);
                }

                else if (act == 8){//logout
                    networkUtil.closeConnection();
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
        } finally {
            try {
                networkUtil.closeConnection();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}