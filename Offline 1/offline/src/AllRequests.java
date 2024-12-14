
import java.io.Serializable;
import java.util.List;

public class AllRequests implements Serializable{
    private List<Request> all;
    private String to;

    AllRequests(){}

    AllRequests(List<Request> all){
        this.all=all;
    }

    public void setAll(List<Request> all) {
        this.all = all;
    }

    public List<Request> getAll() {
        return all;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }
}

