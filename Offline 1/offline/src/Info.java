import java.io.Serializable;
import java.util.List;

public class Info implements Serializable{
    private String from;
    private boolean files;
    private boolean mine;
    private List<String> myPublicFiles;
    private List<String> myPrivateFiles;

    public Info(){}

    public Info(String from, boolean files, boolean mine){
        this.files=files;
        this.from=from;
        this.mine=mine;
    }

    public String getFrom() {
        return from;
    }

    public boolean getFiles(){
        return files;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMyPublicFiles(List<String> myFiles) {
        this.myPublicFiles = myFiles;
    }

    public List<String> getMyPublicFiles() {
        return myPublicFiles;
    }

    public void setMyPrivateFiles(List<String> myPrivateFiles) {
        this.myPrivateFiles = myPrivateFiles;
    }

    public List<String> getMyPrivateFiles() {
        return myPrivateFiles;
    }
}
