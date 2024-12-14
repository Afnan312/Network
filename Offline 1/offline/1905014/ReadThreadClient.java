
import util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadThreadClient implements Runnable {
    private Thread thr;
    private NetworkUtil networkUtil;
    private ClientFiles clientFiles;
    private Scanner scn;
    private String name;

    public ReadThreadClient(NetworkUtil networkUtil, String name, ClientFiles clientFiles) {
        this.networkUtil = networkUtil;
        this.clientFiles=clientFiles;
        scn=new Scanner(System.in);
        this.name=name;
        this.thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            while (true) {
                Object o = networkUtil.read();

                if(o instanceof Names){
                    Names names=(Names) o;
                    System.out.println("Online:");
                    for(String s:names.getOnline())
                        System.out.println(s);
                    System.out.println("Offline");
                    if(names.getOffline().size()==0)
                        System.out.println("None are offline");
                    else
                        for (String s: names.getOffline())
                            System.out.println(s);
                }

                else if(o instanceof OtherFiles){
                    OtherFiles otherFiles =(OtherFiles) o;
                    for(ArrayList<String> x: otherFiles.getFiles()){
                        System.out.println(x.get(0)+":");
                        if(x.size()==0)
                            System.out.println("No public file yet.");
                        else {
                            for (int i = 1; i < x.size(); i++) {
                                System.out.println(i + ". " + x.get(i));
                            }
                        }
                    }
                }
                else if(o instanceof Info){
                    Info info=(Info) o;
                    System.out.println("Public files:");
                    if(info.getMyPublicFiles().size()==0)
                        System.out.println("No Public Files");
                    else {
                        for (int i = 0; i < info.getMyPublicFiles().size(); i++) {
                            System.out.println((i + 1) + ". " + info.getMyPublicFiles().get(i));
                        }
                    }
                    System.out.println("Private files:");
                    if(info.getMyPrivateFiles().size()==0)
                        System.out.println("No private files");
                    else {
                        for (int i = 0; i < info.getMyPrivateFiles().size(); i++) {
                            System.out.println((i + 1) + ". " + info.getMyPrivateFiles().get(i));
                        }
                    }
                }

                else if(o instanceof Request){
                    Request request=(Request) o;
                    System.out.println("The request id of your file is "+request.getRequestID());
                }
                else if(o instanceof AllRequests) {
                    AllRequests allRequests=(AllRequests) o;
                    List<Request> requests=allRequests.getAll();
                    if(requests.size()==0)
                        System.out.println("No request received");
                    else {
                        for (Request r : requests) {
                            System.out.println("Request ID: " + r.getRequestID() + " Request By: " + r.getFrom() + " Description: " + r.getDescription());
                        }
                    }
                }

                else if(o instanceof AllMessages){
                    AllMessages allMessages=(AllMessages) o;
                    List<Message> messages=allMessages.getAll();
                    if(messages.size()==0){
                        System.out.println("No messages received");
                    }
                    else {
                        int i = 1;
                        for (Message m : messages) {
                            System.out.println(i + ". From: " + m.getFrom() + " Filename: " + m.getFileName() + " ID: " + m.getRequestID() + " has been uploaded");
                            i++;
                        }
                    }
                    allMessages.setRead(true);
                    networkUtil.write(allMessages);
                }

                else if(o instanceof Upload){
                    Upload upload=(Upload) o;
                    if(upload.getChunk()==0){
                        System.out.println("File too big for the buffer. Cannot upload.");
                    }
                    else{
                        clientFiles.fileUpload(upload);
                    }
                }
                else if(o instanceof Download){
                    Download download=(Download) o;
                    clientFiles.fileDownload(download);
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
        } finally {
            try {
                networkUtil.closeConnection();

            } catch (IOException e) {
                System.out.println("Disconnected from server");
                System.exit(0);
                //e.printStackTrace();
            }
        }
    }
}