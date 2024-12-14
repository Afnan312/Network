
import java.io.Serializable;

public class Message implements Serializable {
    private String from;
    private String to;
    private int requestID;
    private String fileName;

    public Message() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getRequestID() {
        return requestID;
    }

    public String getFileName() {
        return fileName;
    }
}