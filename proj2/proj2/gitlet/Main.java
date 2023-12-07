package gitlet;

import java.io.IOException;

import static gitlet.Staging_Area.add;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs("init", args, 1);
                try {
                    Repository.init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validInit();
                validateNumArgs("add", args, 2);
                try {
                    add(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "commit":
                // TODO: handle the 'commit [message]' command
                validInit();
                validateNumArgs("commit", args, 2);
                try {
                    Commit.makeNewCommit(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "rm":
                // TODO: handle the 'rm [file name]' command
                validInit();
                validateNumArgs("rm", args, 2);
                try {
                    Staging_Area.rm(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "log":
                // TODO: handle the 'log' command
                validInit();
                validateNumArgs("log", args, 1);
                Commit.log();
                break;
            case "global-log":
                // TODO: handle the 'global-log' command
                validInit();
                validateNumArgs("global-log", args, 1);
                Commit.global_log();
                break;
            case "find":
                // TODO: handle the 'find [commit message]' command
                validInit();
                validateNumArgs("find", args, 2);
                Commit.find(args[1]);
                break;
            case "status":
                // TODO: handle the 'status' command
                validInit();
                validateNumArgs("status", args, 1);
                Repository.status();
                break;
            case "checkout":
                // TODO: handle the 'checkout -- [file name]' command
                // TODO: handle the 'checkout [commit id] -- [file name]' command
                // TODO: handle the 'checkout [branch name]' command
                validInit();
                try {
                    Repository.checkout(args);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "branch":
                // TODO: handle the 'branch [branch name]' command
                validInit();
                validateNumArgs("branch", args, 2);
                try {
                    Branch.creatNewBranch(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "rm-branch":
                // TODO: handle the 'rm-branch [branch name]' command
                validInit();
                validateNumArgs("rm-branch", args, 2);
                Branch.removeBranch(args[1]);
                break;
            case "reset":
                // TODO: handle the 'reset [commit id]' command
                validInit();
                try {
                    Repository.reset(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "merge":
                // TODO: handle the 'merge [branch name]' command
                validInit();
                validateNumArgs("merge", args, 2);
                try {
                    Repository.merge(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "add-remote":
                // TODO: handle the 'add-remote [remote name] [name of remote directory]/.gitlet' command
                validInit();
                break;
            case "rm-remote":
                // TODO: handle the 'rm-remote [remote name]' command
                validInit();
                break;
            case "push":
                // TODO: handle the 'push [remote name] [remote branch name]' command
                validInit();
                break;
            case "fetch":
                // TODO: handle the 'fetch [remote name] [remote branch name]' command
                validInit();
                break;
            case "pull":
                // TODO: handle the 'pull [remote name] [remote branch name]' command
                validInit();
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands");
            System.exit(0);
        }
    }

    public static void validInit() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
