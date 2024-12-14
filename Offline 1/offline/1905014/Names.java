import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class Names implements Serializable{
    private List<String> online;
    private List<String> offline;
    Names(){}

    public Names(List<String> online, List<String> offline){
        this.offline=offline;
        this.online=online;
    }

    public List<String> getOffline() {
        return offline;
    }

    public List<String> getOnline() {
        return online;
    }
}
