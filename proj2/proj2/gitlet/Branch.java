package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

public class Branch {
    public static final File BRANCH_DIR = join(Repository.GITLET_DIR, "branch");
    public static final File BRANCHES_DIR = join(BRANCH_DIR, "branches");
    public static final File HEAD = join(BRANCH_DIR, "HEAD");

    /**
     * 创建Branch，文件名为Branch名，文件内容是对应Commit的sha
     * @param name
     * @param sha
     * @throws IOException
     */
    public static void creatBranch(String name, String sha) throws IOException {
        File newBranch = join(BRANCHES_DIR, name);
        writeContents(newBranch, sha);
        newBranch.createNewFile();
    }

    /**
     * 创建新的分支并且新的分支指向HEAD指向的Commit
     * @param newBranchName
     */
    public static void creatNewBranch(String newBranchName) throws IOException {
        for (File file : BRANCHES_DIR.listFiles()) {
            if (file.getName().equals(newBranchName)) {
                System.out.println("A branch with that name already exists.");
                System.exit(0);
            }
        }
        creatBranch(newBranchName, headBranch());
    }

    /**
     * 更新HEAD指向的Branch
     * @param branchName
     */
    public static void updateHead(String branchName) {
        writeContents(HEAD, branchName);
    }
    /**
     * 返回HEAD指向的Commit的sha
     * @return
     */
    public static String headBranch() {
        File[] files = BRANCHES_DIR.listFiles();
        String headBranchName = readContentsAsString(HEAD);
        for (File file : files) {
            if (file.getName().equals(headBranchName)){
                String headBranchSha = readContentsAsString(file);
                return headBranchSha;
            }
        }
        return null;
    }

    /**
     * 返回HEAD所指的Branch的名字
     * @return
     */
    public static String headBranchName() {
        return readContentsAsString(HEAD);
    }
    public static void removeBranch(String branchName) {
        if (!isBranchExit(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(headBranchName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File targetBranch = join(BRANCHES_DIR, branchName);
        targetBranch.delete();
    }

    public static boolean isBranchExit(String branchName) {
        boolean branchNotExit = false;
        for (File file : BRANCHES_DIR.listFiles()) {
            if (file.getName().equals(branchName)) {
                branchNotExit = true;
            }
        }
        return branchNotExit;
    }
    /**
     * 更新当前HEAD指向的Branch,使其指向新的提交
     * @param newCommitSha
     */
    public static void updateBranch(String newCommitSha) {
        File[] files = BRANCHES_DIR.listFiles();
        String headBranchName = readContentsAsString(HEAD);
        for (File file : files) {
            if (file.getName().equals(headBranchName)){
                writeContents(file, newCommitSha);
            }
        }
    }

    /**
     * 通过branchName获取此Branch指向的Commit
     * @param branchName
     * @return
     */
    public static Commit getBranchCommit(String branchName) {
        for (File file : BRANCHES_DIR.listFiles()) {
            if (file.getName().equals(branchName)) {
                String commitSha = readContentsAsString(file);
                return Commit.getCommitBySha(commitSha);
            }
        }
        return null;
    }


}
