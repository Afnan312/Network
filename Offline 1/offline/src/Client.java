
import java.util.List;
import java.util.Scanner;

import util.NetworkUtil;

public class Client {
    private ClientFiles clientFiles;

    public Client(String serverAddress, int serverPort) {
        try {
            System.out.print("Enter name of the client: ");
            Scanner scanner = new Scanner(System.in);
            String clientName = scanner.nextLine();
            NetworkUtil networkUtil = new NetworkUtil(serverAddress, serverPort);
            networkUtil.write(clientName);
            clientFiles=new ClientFiles(networkUtil);
            new ReadThreadClient(networkUtil, clientName, clientFiles);
            new WriteThreadClient(networkUtil, clientName, clientFiles);

        } catch (Exception e) {
            //System.out.println(e);
        }
    }

    public static void main(String args[]) {
        String serverAddress = "127.0.0.1";
        int serverPort = 33333;
        Client client = new Client(serverAddress, serverPort);

    }
}

