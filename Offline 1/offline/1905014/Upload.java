import java.io.Serializable;

public class Upload implements Serializable{
    private String clientName;
    private boolean priv;
    private boolean request;
    private int requestID;
    private String filepath;
    private int filesize;
    private int chunk;
    private int fileID;
    private byte[] buffer;
    private String name;
    private boolean complete;
    private boolean fail;
    private String desc;

    Upload(){
        chunk=0;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public String getName() {
        return name;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setPriv(boolean priv) {
        this.priv = priv;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public String getFilepath() {
        return filepath;
    }

    public boolean isPriv() {
        return priv;
    }

    public boolean isRequest() {
        return request;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public int getChunk() {
        return chunk;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public boolean isFail() {
        return fail;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getRequestID() {
        return requestID;
    }
}

