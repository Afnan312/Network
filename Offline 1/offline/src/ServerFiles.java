import util.NetworkUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ServerFiles {
    private int bufferSize;
    private int currBuffer;
    private int maxChunk;
    private int minChunk;
    private HashMap<String, NetworkUtil> clientMap;
    private HashMap<Integer, String> allRequests;
    private FileOperations fileOperations;
    private int id;
    private HashMap<Integer, List<byte[]>> fileMap;
    ServerFiles(){
    }

    ServerFiles(int bufferSize, int maxChunk, int minChunk, HashMap<String, NetworkUtil> map, HashMap<Integer, String> allRequests, FileOperations fileOperations){
        this.bufferSize=bufferSize;
        this.maxChunk=maxChunk;
        this.minChunk=minChunk;
        currBuffer=bufferSize;
        this.clientMap=map;
        this.allRequests=allRequests;
        this.fileOperations=fileOperations;
        this.id=0;
        fileMap=new HashMap<>();
    }

    synchronized void getFromClient(Upload upload) throws IOException {
        int size=upload.getFilesize();
        NetworkUtil networkUtil=clientMap.get(upload.getClientName());
        if(!getBuffer(size)) {
            upload.setChunk(0);
            networkUtil.write(upload);
        }
        else {
            id++;
            upload.setChunk(getChunk());
            System.out.println("The chunk size of upload is "+upload.getChunk());
            upload.setFileID(id);
            networkUtil.write(upload);
        }
    }

    void fileUpload(Upload upload) throws IOException {////////////////////
        NetworkUtil networkUtil=clientMap.get(upload.getClientName());
        int currentSize=0;
        String savepath="D:\\NetworkOffline\\" +upload.getClientName();
        if(upload.isPriv())
            savepath+="\\Private\\";
        else
            savepath+="\\Public\\";
        savepath+=upload.getName();
        System.out.println("The save path is "+savepath);
        File file=new File(savepath);
        file.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        try {
            bufferedOutputStream.write(upload.getBuffer(), 0, upload.getBuffer().length);
            currBuffer-=upload.getBuffer().length;
            currentSize+=upload.getBuffer().length;
            Ack ack=new Ack();
            //networkUtil.write(ack);
            while (!upload.isComplete()) {
                Object o = networkUtil.read();
                if(o instanceof Upload){
                    upload=(Upload) o;
                    if(upload.isFail() || !networkUtil.isActive())
                    {
                        if(file.delete())
                            System.out.println("File deleted");
                        System.out.println("Upload failed");
                        currBuffer+=currentSize;
                        break;
                    }
                    bufferedOutputStream.write(upload.getBuffer(), 0, upload.getBuffer().length);
                    currBuffer-=upload.getBuffer().length;
                    currentSize+=upload.getBuffer().length;
                    System.out.println("The current Uploaded size is "+currentSize);
                    //networkUtil.write(new Ack());
                }
            }
            if(upload.isComplete()) {
                System.out.println("Upload is completed");
                currBuffer += currentSize;
            }
            if(upload.isComplete() && upload.isRequest()){/////delete request with the id from uploader, send the upload message to
                                                            //the one who requested the file
                int id=upload.getRequestID();
                String name=allRequests.get(id);
                Message message=new Message();
                message.setFrom(upload.getClientName());
                message.setTo(name);
                message.setRequestID(id);
                message.setFileName(upload.getName());
                List<Request> requests=fileOperations.readRequestFromFile(upload.getClientName());
                requests.removeIf(r -> r.getRequestID() == id);
                fileOperations.writeRequestToFile(requests, upload.getClientName());
                List<Message> messages=fileOperations.readFromFile(name);
                messages.add(message);
                fileOperations.writeMessageToFile(messages, name);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        bufferedOutputStream.close();
    }

    void fileUpload2(Upload upload) throws IOException, ClassNotFoundException {
        NetworkUtil networkUtil=clientMap.get(upload.getClientName());
        int currentSize=0;
        String savepath="D:\\NetworkOffline\\" +upload.getClientName();
        if(upload.isPriv())
            savepath+="\\Private\\";
        else
            savepath+="\\Public\\";
        savepath+=upload.getName();
        System.out.println("The save path is "+savepath);
        int fileId= upload.getFileID();
        if (!fileMap.containsKey(fileId)) {
            fileMap.put(fileId, new ArrayList<>());
        }
        List<byte[]> chunks = fileMap.get(fileId);
        chunks.add(upload.getBuffer());
        currBuffer-=upload.getBuffer().length;
        currentSize+=upload.getBuffer().length;
        Ack ack=new Ack();
        networkUtil.write(ack);
        while (!upload.isComplete()) {
            try {
                Object o = networkUtil.read();
                if (o instanceof Upload) {
                    upload = (Upload) o;
                    if (upload.isFail()) {//when the client does not receive ack
                        //delete the chunks with fileid in hashmap
                        fileMap.remove(fileId);
                        System.out.println("Upload failed");
                        currBuffer += currentSize;
                        break;
                    }
                    chunks.add(upload.getBuffer());
                    currBuffer -= upload.getBuffer().length;
                    currentSize += upload.getBuffer().length;
                    networkUtil.write(new Ack());
                }
            }catch (Exception e){//when the client gets disconnected
                fileMap.remove(fileId);
                System.out.println("Upload failed");
                currBuffer += currentSize;
                break;
            }
        }
        if(upload.isComplete() && (currentSize == upload.getFilesize())) {
            File file=new File(savepath);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            for (byte[] chunk : chunks) {
                fileOutputStream.write(chunk);
            }
            fileOutputStream.close();
            System.out.println("Upload is completed");
            currBuffer += currentSize;
            fileMap.remove(fileId);
        }
        else if(upload.isComplete() && (currentSize != upload.getFilesize())){
            System.out.println("Upload failed. Some chunks are missing");
            currBuffer += currentSize;
            fileMap.remove(fileId);
        }
        if(upload.isComplete() && (currentSize == upload.getFilesize()) && upload.isRequest()){/////delete request with the
            // id from uploader, send the upload message to
            //the one who requested the file
            try {
                int id = upload.getRequestID();
                String name = allRequests.get(id);
                Message message = new Message();
                message.setFrom(upload.getClientName());
                message.setTo(name);
                message.setRequestID(id);
                message.setFileName(upload.getName());
                List<Request> requests = fileOperations.readRequestFromFile(upload.getClientName());
                requests.removeIf(r -> r.getRequestID() == id);
                fileOperations.writeRequestToFile(requests, upload.getClientName());
                List<Message> messages = fileOperations.readFromFile(name);
                messages.add(message);
                fileOperations.writeMessageToFile(messages, name);
            }catch (Exception e){

            }
        }
    }

    void fileDownload(Download download) throws IOException {
        NetworkUtil networkUtil=clientMap.get(download.getClientName());
        String path=download.getFilepath();
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        try {

            int totalBytesRead = 0;
            int chunk=maxChunk;

            while ( totalBytesRead < file.length() )
            {
                int bytesRemaining = (int) (file.length()-totalBytesRead);

                if ( bytesRemaining < chunk )
                {
                    chunk = bytesRemaining;
                    download.setComplete(true);
                }
                byte[] buffer = new byte[chunk];
                int bytesRead = bufferedInputStream.read(buffer, 0, chunk);

                if ( bytesRead > 0)
                {
                    totalBytesRead += bytesRead;
                }
                download.setBuffer(buffer);
                networkUtil.write(download);

            }
        }catch (IOException e){
            System.out.println("File download error");
        }
        bufferedInputStream.close();
    }

    public int getChunk(){
        Random random=new Random();
        return random.nextInt((maxChunk- minChunk) + 1) + minChunk;
    }
    public boolean getBuffer(int size){
        System.out.println("Size of the file: "+size);
        System.out.println("Size of current buffer= "+currBuffer);
        return currBuffer >= size;
    }
}
