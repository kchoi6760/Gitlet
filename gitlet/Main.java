package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * <p>
 * \
 */

import java.io.File;
import java.util.Objects;

public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        } else if (!Objects.equals(args[0], "init") && !(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        } else if (Objects.equals(args[0], "init") && (new File(".gitlet")).exists()) {
            System.out.println("A Gitlet version-control system "
                    +
                    "already exists in the current directory.");
        }

        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                File gitletRepo = new File(".gitlet");
                gitletRepo.mkdir();
                File staging = new File(".gitlet/staging");
                staging.mkdir();
                File commits = new File(".gitlet/commits");
                commits.mkdir();
                File blobs = new File(".gitlet/blobs");
                blobs.mkdir();
                Commands.init();
                break;
            case "add":
                Commands.add(args[1]);
                break;
            case "commit":
                if (args.length != 2 || args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                } else {
                    Commands.commit(args[1]);
                }
                break;
            case "log":
                Commands.log();
                break;
            case "checkout":
                if (!Commands.checkout(args)) {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "global-log":
                Commands.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                Commands.find(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                Commands.rm(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Commands.status();
                break;
            case "branch":
                validateNumArgs(args, 2);
                Commands.branch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                Commands.reset(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                Commands.rmBranch(args[1]);
                break;
            case "merge":
                validateNumArgs(args, 2);
                Commands.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                return;
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
        }
    }
}
