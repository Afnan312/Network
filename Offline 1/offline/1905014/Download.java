import java.io.Serializable;

public class Download implements Serializable {
    private String clientName;
    private String from;
    private String filepath;
    private boolean priv;
    private int chunk;
    private int fileID;
    private byte[] buffer;
    private String name;
    private boolean complete;
    private String savePath;

    Download(){}

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public String getClientName() {
        return clientName;
    }

    public String getName() {
        return name;
    }

    public String getFilepath() {
        return filepath;
    }

    public int getFileID() {
        return fileID;
    }

    public int getChunk() {
        return chunk;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isPriv() {
        return priv;
    }

    public void setPriv(boolean priv) {
        this.priv = priv;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSavePath() {
        return savePath;
    }
}
