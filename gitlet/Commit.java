package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a gitlet commit object.
 * commits
 * does at a high level.
 */
public class Commit implements Serializable {
    /**
     * The message of this Commit.
     */
    private final String message;
    private final String timeStamp;
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private HashMap<String, String> blobs = new HashMap<>();
    private String parent;


    public Commit(String message, String parent, HashMap<String, String> blobs) {
        this.message = message;
        this.setParent(parent);
        this.timeStamp = Time.timeStamp();
        this.blobs = blobs;
    }

    public Commit() {
        this.timeStamp = Time.initTime();
        this.message = "initial commit";
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public String getParent() {
        return parent;
    }

    public String getLog(String hash) {
        String result = "";
        result += "commit " + hash + "\n";
        result += "Date: " + timeStamp + "\n";
        result += message + "\n";
        return result;
    }

    public String getMessage() {
        return this.message;
    }

    public void checkFile(String fileName) {
        String hashFile = blobs.get(fileName);
        if (hashFile == null) {
            System.out.println("File does not exist in that commit.");
        } else {
            File f = new File(".gitlet/blobs/" + hashFile);
            File newF = new File(fileName);
            if (newF.exists()) {
                Utils.writeContents(newF, Utils.readContents(f));
            } else {
                try {
                    newF.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Utils.writeContents(newF, Utils.readContents(f));
            }
        }
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
