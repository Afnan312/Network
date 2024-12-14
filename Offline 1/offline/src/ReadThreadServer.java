import java.io.File;
import util.NetworkUtil;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.util.HashMap;

public class ReadThreadServer implements Runnable {
    private Thread thr;
    private String currClient;
    private NetworkUtil networkUtil;
    public HashMap<String, NetworkUtil> clientMap;
    public List<String> clients;
    private FileOperations fileOperations;
    private HashMap<Integer, String> allRequests;
    private ServerFiles serverFiles;

    public ReadThreadServer(String currClient, List<String> clients, HashMap<String, NetworkUtil> map, NetworkUtil networkUtil, FileOperations fileOperations, HashMap<Integer, String> requests, ServerFiles serverFiles) {
        this.currClient=currClient;
        this.clients=clients;
        this.clientMap = map;
        this.networkUtil = networkUtil;
        this.fileOperations=fileOperations;
        this.allRequests=requests;
        this.serverFiles=serverFiles;
        this.thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            while (true) {
                Object o = networkUtil.read();
                if(o instanceof Info){
                    Info info=(Info) o;
                    if(!info.getFiles()){
                        List<String> online=new ArrayList<>();
                        List<String> offline=new ArrayList<>();
                        for(String s:clients){
                            NetworkUtil nu = clientMap.get(s);
                            if (nu.isActive())
                                online.add(s);
                            else
                                offline.add(s);
                        }
                        Names names=new Names(online, offline);
                        networkUtil.write(names);
                    }
                    else if(!info.isMine()){
                        ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
                        for(String s:clients) {
                            if(s.equalsIgnoreCase(((Info) o).getFrom()))
                                continue;
                            ArrayList<String> fileNames=new ArrayList<>();
                            fileNames.add(s);
                            File folder = new File("D:\\NetworkOffline\\"+s+"\\Public");
                            File[] listOfFiles = folder.listFiles();

                            assert listOfFiles != null;
                            for (File listOfFile : listOfFiles) {

                                if (listOfFile.isFile()) {
                                    System.out.println("File " + listOfFile.getName());
                                    fileNames.add(listOfFile.getName());
                                }
                            }
                            lists.add(fileNames);
                        }
                        OtherFiles otherFiles1 =new OtherFiles(lists);
                        networkUtil.write(otherFiles1);
                    }
                    else{
                        List<String> fileNames1=new ArrayList<>();
                        List<String> fileNames2=new ArrayList<>();
                        File folder1=new File("D:\\NetworkOffline\\"+info.getFrom()+"\\Public");
                        File folder2=new File("D:\\NetworkOffline\\"+info.getFrom()+"\\Private");
                        File[] listOfFiles1 = folder1.listFiles();
                        File[] listOfFiles2 = folder2.listFiles();
                        assert listOfFiles1 != null;
                        for (File listOfFile : listOfFiles1) {

                            if (listOfFile.isFile()) {
                                fileNames1.add(listOfFile.getName());
                            }
                        }
                        assert listOfFiles2 != null;
                        for (File listOfFile : listOfFiles2) {

                            if (listOfFile.isFile()) {
                                fileNames2.add(listOfFile.getName());
                            }
                        }
                        info.setMyPublicFiles(fileNames1);
                        info.setMyPrivateFiles(fileNames2);
                        networkUtil.write(info);
                    }
                }
                else if(o instanceof Request){
                    Request request=(Request) o;
                    request.setRequestID(fileOperations.getId());
                    System.out.println("Request id is "+request.getRequestID());
                    allRequests.put(request.getRequestID(), request.getFrom());
                    String name=request.getFrom();
                    if(clients.size()!=0) {
                        for (String clientName : clients) {
                            if (!clientName.equalsIgnoreCase(name)) {
                                List<Request> requests = fileOperations.readRequestFromFile(clientName);
                                requests.add(request);
                                fileOperations.writeRequestToFile(requests, clientName);
                            }
                        }
                    }
                    networkUtil.write(request);
                }
                else if(o instanceof AllRequests){
                    AllRequests allRequests=(AllRequests) o;
                    String name=allRequests.getTo();
                    List<Request> requests=fileOperations.readRequestFromFile(name);
                    allRequests.setAll(requests);
                    networkUtil.write(allRequests);
                }
                else if(o instanceof AllMessages){
                    AllMessages allMessages=(AllMessages) o;
                    String name=allMessages.getTo();
                    if(allMessages.isRead())
                        fileOperations.eraseMessage(name);
                    else {
                        List<Message> messages = fileOperations.readFromFile(name);
                        allMessages.setAll(messages);
                        networkUtil.write(allMessages);
                    }
                }
                else if(o instanceof Upload){
                    Upload upload=(Upload) o;
                    if(upload.getChunk()==0) {
                        serverFiles.getFromClient(upload);
                    }
                    else {
                        serverFiles.fileUpload2(upload);
                    }
                }
                else if(o instanceof Download){
                    Download download=(Download) o;
                    String path="D:\\NetworkOffline\\";
                    if(download.isPriv())
                        path+= download.getFrom()+"\\Private\\";
                    else
                        path+= download.getFrom()+"\\Public\\";
                    path+= download.getName();
                    download.setFilepath(path);
                    System.out.println("The download path is "+path);
                    serverFiles.fileDownload(download);
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
        } finally {
            try {
                networkUtil.closeConnection();
            } catch (IOException e) {
                System.out.println(currClient+" has been disconnected from server");
                //e.printStackTrace();
            }
        }
    }
}