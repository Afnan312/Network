
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import util.NetworkUtil;

public class Server {

    private ServerSocket serverSocket;
    public HashMap<String, NetworkUtil> clientMap; // HashMap of client's name and socket information
    public List<String> clients;
    private FileOperations fileOperations;
    private HashMap<Integer, String> requests;
    private ServerFiles serverFiles;

    Server() {
        clientMap = new HashMap<>();
        requests=new HashMap<>();
        clients=new ArrayList<>();
        fileOperations=new FileOperations();
        serverFiles=new ServerFiles(90000000, 40000, 10000, clientMap, requests, fileOperations);
        try {
            serverSocket = new ServerSocket(33333);
            //new WriteThreadServer(clientMap, "Server");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                serve(clientSocket);
            }
        } catch (Exception e) {
            System.out.println("Server starts:" + e);
        }
    }

    public void serve(Socket clientSocket) throws IOException, ClassNotFoundException {
        NetworkUtil networkUtil = new NetworkUtil(clientSocket);
        String clientName = (String) networkUtil.read();
        if(clientMap.containsKey(clientName) && clientMap.get(clientName).isActive()){
            try {
                networkUtil.closeConnection();
            }catch (IOException e){
                //System.out.println("Disconnected from the server");
            }
            try {
                clientMap.get(clientName).closeConnection();
            }catch (IOException e){
                System.out.println(clientName+" has been disconnected from the server for multiple log-in");
            }
        }
        else {
            clientMap.put(clientName, networkUtil);
            if(!clients.contains(clientName))
                clients.add(clientName);

            new ReadThreadServer(clientName, clients, clientMap, networkUtil, fileOperations, requests, serverFiles);
            System.out.println(clientName+" has connected to the server");
            File file1 = new File("D:\\NetworkOffline\\" + clientName);
            if (file1.mkdirs()) {
                //System.out.println("Successful");
                File file2 = new File("D:\\NetworkOffline\\" + clientName + "\\Public");
                File file3 = new File("D:\\NetworkOffline\\" + clientName + "\\Private");
                File file4=new File("D:\\NetworkOffline\\" + clientName+"\\messages.txt");
                File file5 = new File("D:\\NetworkOffline\\" + clientName+"\\requests.txt");
                file2.mkdir();
                file3.mkdir();
                file4.createNewFile();
                file5.createNewFile();
            }
        }
    }

    public static void main(String args[]) {
        Server server = new Server();
    }
}
