import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {
    private int id;
    FileOperations(){
        id=0;
    }

    public synchronized int getId() {
        id++;
        return id;
    }

    public List<Message> readFromFile(String clientName) throws Exception {
        List<Message> messageList= new ArrayList<>();
        File file=new File("D:\\NetworkOffline\\"+clientName+"\\messages.txt");
        FileReader fr=new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            if(!line.isEmpty()) {
                String[] tokens = line.split(";");
                Message m = new Message();
                m.setFrom(tokens[0]);
                m.setTo(tokens[1]);
                m.setRequestID(Integer.parseInt(tokens[2]));
                m.setFileName(tokens[3]);
                messageList.add(m);
            }
        }
        br.close();
        return messageList;
    }

    public synchronized void eraseMessage(String clientName) throws Exception{
        new FileWriter("D:\\NetworkOffline\\"+clientName+"\\messages.txt", false).close();
    }

    public synchronized void writeMessageToFile(List<Message> messages, String clientName) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:\\NetworkOffline\\"+clientName+"\\messages.txt")));
        // var bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));

        for(Message m: messages){
            bw.write(m.getFrom()+";"+m.getTo()+";"+m.getRequestID()+";"+m.getFileName());
            bw.write("\n");
        }

        bw.close();
    }


    public List<Request> readRequestFromFile(String clientName) throws Exception {
        List<Request> requestList= new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(new File("D:\\NetworkOffline\\"+clientName+"\\requests.txt")));
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                String[] tokens = line.split(";");
                Request r = new Request();
                r.setFrom(tokens[0]);
                r.setRequestID(Integer.parseInt(tokens[1]));
                r.setDescription(tokens[2]);
                requestList.add(r);
            }
        }
        br.close();
        return requestList;
    }

    public synchronized void writeRequestToFile(List<Request> requests, String clientName) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:\\NetworkOffline\\"+clientName+"\\requests.txt")));
        // var bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));

        for(Request r:requests){
            bw.write(r.getFrom()+";"+r.getRequestID()+";"+r.getDescription());
            bw.write("\n");
        }

        bw.close();
    }

}
