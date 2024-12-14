import java.io.Serializable;

public class Request implements Serializable{
    private String from;
    private int requestID;
    private String description;

    public Request(){}

    public Request(String from, String description){
        this.description=description;
        this.from=from;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getRequestID(){
        return requestID;
    }

    public String getFrom() {
        return from;
    }

    public String getDescription() {
        return description;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
