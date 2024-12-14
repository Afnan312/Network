import java.io.Serializable;
import java.util.ArrayList;

public class OtherFiles implements Serializable{

    private ArrayList<ArrayList<String>> files;

    OtherFiles(){}
    public OtherFiles(ArrayList<ArrayList<String>> files) {
        this.files=files;
    }

    public ArrayList<ArrayList<String>> getFiles() {
        return files;
    }
}
