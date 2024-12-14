import java.io.Serializable;
import java.util.List;

public class AllMessages implements Serializable{
    private List<Message> all;
    private String to;
    private boolean read;

    AllMessages(){}
    AllMessages(List<Message> all){
        this.all=all;
    }

    public void setAll(List<Message> all) {
        this.all = all;
    }

    public List<Message> getAll() {
        return all;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }
}
