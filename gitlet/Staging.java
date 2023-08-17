package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Staging implements Serializable {

    private HashMap<String, String> addStaging = new HashMap<>();
    private ArrayList<String> rmStaging = new ArrayList<>();

    static Staging load() {
        File f = new File(".gitlet/staging/" + "staging");
        if (f.exists()) {
            return Utils.readObject(f, Staging.class);
        } else {
            return new Staging();
        }
    }

    public void save() {
        File f = new File(".gitlet/staging/" + "staging");
        if (f.exists()) {
            Utils.writeObject(f, this);
        } else {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Utils.writeObject(f, this);
        }
    }

    public void add(String filename) {
        File f = new File(filename);
        if (f.exists()) {
            getRmStaging().remove(filename);
            String hash = Utils.sha1(Utils.readContentsAsString(f) + filename);
            File blobFile = new File(".gitlet/blobs/" + hash);
            if (blobFile.exists()) {
                return;
            } else {
                try {
                    blobFile.createNewFile();
                    Utils.writeContents(blobFile, Utils.readContents(f));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                getAddStaging().put(filename, hash);
            }
        } else {
            System.out.println("File does not exist.");
            return;
        }
    }

    public void rm(String fileName) {
        Commits commits = Commits.load();
        Commit commit = Commits.getCommit(commits.getHead());
        boolean err = true;
        if (getAddStaging().containsKey(fileName)) {
            getAddStaging().remove(fileName);
            err = false;
        }
        if (commit.getBlobs().containsKey(fileName)) {
            getRmStaging().add(fileName);
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
            err = false;
        }
        if (err) {
            System.out.println("No reason to remove the file.");
        }
        commits.save();
    }

    public void reset() {
        setAddStaging(new HashMap<>());
        setRmStaging(new ArrayList<>());
    }

    public HashMap<String, String> getAddStaging() {
        return addStaging;
    }

    public void setAddStaging(HashMap<String, String> addStaging) {
        this.addStaging = addStaging;
    }

    public ArrayList<String> getRmStaging() {
        return rmStaging;
    }

    public void setRmStaging(ArrayList<String> rmStaging) {
        this.rmStaging = rmStaging;
    }
}
