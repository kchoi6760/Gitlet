package gitlet;

import java.io.File;
import java.util.*;

public class Commands {
    static void init() {
        Commits commits = Commits.load();
        commits.init();
        commits.save();
    }

    static void add(String filename) {
        Staging staging = Staging.load();
        staging.add(filename);
        staging.save();
    }

    static void commit(String message) {
        Commits commits = Commits.load();
        Staging staging = Staging.load();
        HashMap<String, String> blobs = staging.getAddStaging();
        ArrayList<String> rms = staging.getRmStaging();
        commits.commit(message, blobs, rms);
        staging.reset();
        staging.save();
        commits.save();
    }

    static void log() {
        Commits commits = Commits.load();
        commits.getLog();
        commits.save();
    }

    static boolean checkout(String[] args) {
        Commits commits = Commits.load();
        if (args[1].equals("--") && args.length == 3) {
            commits.checkout(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            Commit c = Commits.getCommit(args[1]);
            if (c == null) {
                System.out.println("No commit with that id exists.");
            } else {
                commits.checkout(c, args[3]);
            }
        } else if (args.length == 2) {
            commits.checkoutBr(args[1]);
        } else {
            commits.save();
            return false;
        }
        commits.save();
        return true;
    }

    static void globalLog() {
        List dirs = Utils.plainFilenamesIn(".gitlet/commits");
        for (int i = 0; i < dirs.size(); i++) {
            String fileName = (String) dirs.get(i);
            if (!fileName.equals("commits")) {
                File f = new File(".gitlet/commits/" + fileName);
                Commit c = Utils.readObject(f, Commit.class);
                String log = c.getLog(fileName);
                System.out.println("===");
                System.out.println(log);
            }

        }
    }

    static void find(String message) {
        List<String> dirs = Utils.plainFilenamesIn(".gitlet/commits");
        int counter = 0;
        for (String fileName : dirs) {
            if (!fileName.equals("commits")) {
                File f = new File(".gitlet/commits/" + fileName);
                Commit c = Utils.readObject(f, Commit.class);
                if (c.getMessage().equals(message)) {
                    System.out.println(fileName);
                    counter += 1;
                }
            }

        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    static void rm(String fileName) {
        Staging staging = Staging.load();
        staging.rm(fileName);
        staging.save();
    }

    static boolean unchanged(String fileName, String blob) {
        return Arrays.equals(Utils.readContents(new File(fileName)),
                Utils.readContents(new File(".gitlet/blobs/" + blob)));
    }

    static void status() {
        Staging staging = Staging.load();
        Commits commits = Commits.load();
        ArrayList<String> branches = new ArrayList<>(commits.getBranches().keySet());
        ArrayList<String> addStaging = new ArrayList<>(staging.getAddStaging().keySet());
        ArrayList<String> rmStaging = staging.getRmStaging();
        Collections.sort(branches);
        Collections.sort(addStaging);
        Collections.sort(rmStaging);

        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals(commits.getCurrentBranch())) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String add : addStaging) {
            System.out.println(add);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String add : rmStaging) {
            System.out.println(add);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> lst = Utils.plainFilenamesIn(".");
        Commit curr = Commits.getCommit(commits.getHead());

        for (String fileName : lst) {
            File f = new File(fileName);

            if (f.exists() && curr.getBlobs().containsKey(fileName)
                    && !unchanged(fileName, curr.getBlobs().get(fileName))
                    && !staging.getAddStaging().containsKey(fileName)) {
                System.out.println(fileName);
            } else if (f.exists()
                    && staging.getAddStaging().containsKey(fileName)
                    && !unchanged(fileName, staging.getAddStaging().get(fileName))) {
                System.out.println(fileName);
            } else if (!f.exists() && staging.getAddStaging().containsKey(fileName)) {
                System.out.println(fileName);
            } else if (!staging.getRmStaging().contains(fileName)
                    && curr.getBlobs().containsKey(fileName)
                    && !f.exists()) {
                System.out.println(fileName);
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String fileName : lst) {
            if (!curr.getBlobs().containsKey(fileName)
                    && !staging.getAddStaging().containsKey(fileName)) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println();

        commits.save();
        staging.save();
    }

    static void branch(String branchName) {
        Commits commits = Commits.load();
        if (commits.branch(branchName)) {
            commits.save();
            return;
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    static void reset(String commitName) {
        Commits commits = Commits.load();
        commits.reset(commitName);
        commits.save();
    }

    static void rmBranch(String branchName) {
        Commits commits = Commits.load();
        commits.rmBranch(branchName);
        commits.save();
    }

    static void merge(String branchName) {
        Commits commits = Commits.load();
        commits.rmBranch(branchName);
        commits.save();
    }
}
