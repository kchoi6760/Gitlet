package gitlet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commits implements Serializable {
    private String head;
    private HashMap<String, String> branches = new HashMap<>();

    private String currentBranch = "main";

    static Commits load() {
        File f = new File(".gitlet/commits/" + "commits");
        if (f.exists()) {
            return Utils.readObject(f, Commits.class);
        } else {
            return new Commits();
        }
    }

    static Commit getCommit(String sha) {
        List<String> files = Utils.plainFilenamesIn(".gitlet/commits/");
        for (String file : files) {
            if (file.indexOf(sha) == 0) {
                File f = new File(".gitlet/commits/" + file);
                if (f.exists()) {
                    return Utils.readObject(f, Commit.class);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public void save() {
        File f = new File(".gitlet/commits/" + "commits");
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

    public boolean branch(String branchName) {
        if (getBranches().containsKey(branchName)) {
            return false;
        } else {
            getBranches().put(branchName, getHead());
            return true;
        }
    }

    public void init() {
        Commit initial = new Commit();
        setHead(Utils.sha1(Utils.serialize(initial)));
        File f = new File(".gitlet/commits/" + getHead());
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(f, initial);
        getBranches().put(getCurrentBranch(), getHead());
        save();
    }

    public void commit(String message, HashMap<String, String> blobs, ArrayList<String> rms) {
        if (blobs.isEmpty() && rms.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        Commit prevCommit = getCommit(getHead());

        HashMap<String, String> newBlobs = prevCommit.getBlobs();

        for (String key : blobs.keySet()) {
            newBlobs.put(key, blobs.get(key));
        }

        for (String rm : rms) {
            newBlobs.remove(rm);
        }

        Commit newCommit = new Commit(message, getHead(), newBlobs);

        setHead(Utils.sha1(Utils.serialize(newCommit)));
        getBranches().replace(getCurrentBranch(), getHead());
        File f = new File(".gitlet/commits/" + getHead());
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(f, newCommit);
    }

    public void getLog() {
        String currHash = getHead();
        while (currHash != null) {
            Commit currCommit = getCommit(currHash);
            System.out.println("===");
            System.out.println(currCommit.getLog(currHash));
            currHash = currCommit.getParent();
        }
    }


    public void checkout(String fileName) {
        Commit c = getCommit(getHead());
        c.checkFile(fileName);
    }

    public void checkout(Commit c, String fileName) {
        c.checkFile(fileName);
    }

    private void sync(Commit commit) {
        List<String> files = Utils.plainFilenamesIn(".");
        for (String file : files) {
            File f = new File(file);
            f.delete();
        }
        for (String blob : commit.getBlobs().keySet()) {
            File nf = new File(blob);
            try {
                nf.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte[] bts = Utils.readContents(
                    new File(".gitlet/blobs/" + commit.getBlobs().get(blob))
            );
            Utils.writeContents(nf, bts);
        }
    }

    public void checkoutBr(String branchName) {
        if (getCurrentBranch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Staging prevStaging = Staging.load();
        if (getBranches().containsKey(branchName)) {
            List<String> files = Utils.plainFilenamesIn(".");
            Commit c = getCommit(getHead());
            for (String file : files) {
                if (c.getBlobs().containsKey(file)
                        || prevStaging.getAddStaging().containsKey(file)
                        || prevStaging.getRmStaging().contains(file)) {
                    continue;
                } else {
                    System.out.println("There is an untracked file in the "
                            + "way; delete it, or add and commit it first.");
                    return;
                }
            }
            setCurrentBranch(branchName);
            setHead(getBranches().get(branchName));
            sync(getCommit(getHead()));
            Staging staging = Staging.load();
            staging.reset();
            staging.save();
            return;
        }
        System.out.println("No such branch exists.");
    }

    public void reset(String commitName) {
        List<String> files = Utils.plainFilenamesIn(".");
        Commit c = getCommit(getHead());
        Staging prevStaging = Staging.load();
        for (String file : files) {
            if (c.getBlobs().containsKey(file)
                    || prevStaging.getAddStaging().containsKey(file)
                    || prevStaging.getRmStaging().contains(file)) {
                continue;
            } else {
                System.out.println("There is an untracked file in the way; delete it,"
                        + " or add and commit it first.");
                return;
            }
        }
        List<String> lists = Utils.plainFilenamesIn(".gitlet/commits/");
        if (lists.contains(commitName)) {
            sync(getCommit(commitName));
            setHead(commitName);
            branches.replace(currentBranch, commitName);
            Staging staging = Staging.load();
            staging.reset();
            staging.save();
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public void rmBranch(String branchName) {
        if (getBranches().containsKey(branchName)) {
            if (!getCurrentBranch().equals(branchName)) {
                getBranches().remove(branchName);
            } else {
                System.out.println("Cannot remove the current branch.");
            }
        } else {
            System.out.println("A branch with that name does not exist.");
        }
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public HashMap<String, String> getBranches() {
        return branches;
    }

    public void setBranches(HashMap<String, String> branches) {
        this.branches = branches;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public void setCurrentBranch(String currentBranch) {
        this.currentBranch = currentBranch;
    }
}
