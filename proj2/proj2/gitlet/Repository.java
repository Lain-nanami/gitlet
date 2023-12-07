package gitlet;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Leonard
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */

    /**
     * Description:
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that contains no files
     * and has the commit message initial commit (just like that, with no punctuation).
     * It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp for this initial commit will be
     * 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates (this
     * is called “The (Unix) Epoch”, represented internally by the time 0.) Since the initial
     * commit in all repositories created by Gitlet will have exactly the same content, it
     * follows that all repositories will automatically share this commit (they will all have
     * the same UID) and all commits in all repositories will trace back to it.
     *
     * Failure cases: If there is already a Gitlet version-control system in the current directory,
     * it should abort. It should NOT overwrite the existing system with a new one. Should print the
     * error message A Gitlet version-control system already exists in the current directory.
     */
    public static void init() throws IOException {

        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            Staging_Area.STAGING_AREA_DIR.mkdir();
            Commit.COMMITS_DIR.mkdir();
            Branch.BRANCH_DIR.mkdir();
            Branch.BRANCHES_DIR.mkdir();
            Branch.HEAD.createNewFile();
            Blobs.BLOBS_DIR.mkdir();
            Staging_Area stageState = new Staging_Area();
            stageState.saveStageState();
            Commit first = new Commit();                         //initial commit
            String newCommitSha = first.saveCommit();            //save commit file
            Branch.creatBranch("master", newCommitSha);    //creat master branch
            Branch.updateHead("master");             //更新HEAD
        }
    }

    /**
     * 查询在CWD目录下是否存在名为fileName的文件，若存在则返回此文件，否则返回null
     * @param fileName
     * @return
     */
    public static File weatherFileExit (String fileName) {
        File[] files = CWD.listFiles();
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }
    /**
     * Description:
     * Displays what branches currently exist, and marks
     * the current branch with a *. Also displays what files have been
     * staged for addition or removal. An example of the exact format
     * it should follow is as follows.There is an empty line between
     * sections, and the entire status ends in an empty line as well.
     * Entries should be listed in lexicographic order, using the Java
     * string-comparison order (the asterisk doesn’t count). A file in
     * the working directory is “modified but not staged” if it is
     */
    public static void status() {
        printBranchStatus();
        printStagedFileStatus();
        printRemovedFileStatus();
        printMnsfcStatus();
        printUntrackedFilesStatus();
    }

    /**
     * 打印Branches
     */
    public static void printBranchStatus() {
        List<String> branchList = plainFilenamesIn(Branch.BRANCHES_DIR);
        String headCommitName = Branch.headBranchName();
        System.out.println("=== Branches ===");
        for (String branchName : branchList) {
            if (branchName.equals(headCommitName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();
    }
    /**
     * 打印StagedStatus
     */
    public static void printStagedFileStatus() {
        Staging_Area stageState = Staging_Area.readStageState();
        TreeMap<String, String> addition = stageState.getAddition();
        System.out.println("=== Staged Files ===");
        printStageState(addition);
    }
    /**
     * 打印Staged Files
     */
    public static void printRemovedFileStatus() {
        Staging_Area stageState = Staging_Area.readStageState();
        TreeMap<String, String> removal = stageState.getRemoval();
        System.out.println("=== Removed Files ===");
        printStageState(removal);
    }
    /**
     * 打印Modifications Not Staged For Commit
     */
    private static void printMnsfcStatus() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }
    /**
     * 打印Untracked Files
     */
    private static void printUntrackedFilesStatus() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    /**
     * 打印Removed Files
     * @param removal
     */
    private static void printStageState(TreeMap<String, String> removal) {
        List<String> removedFiles = new ArrayList<>();
        for (String fileName : removal.keySet()) {
            removedFiles.add(fileName);
        }
        Collections.sort(removedFiles);
        for (String fileName : removedFiles) {
            System.out.println(fileName);
        }
        System.out.println();
    }

//    /**
//     * 返回未被跟踪的文件名列表
//     * @return
//     */
//    public static List<String> fileNotTracked() {
//        List<String> result = new ArrayList<>();
//        List<String> fileNames = plainFilenamesIn(CWD);
//        Commit headCommit = Commit.readHeadCommit();
//        Staging_Area stageState = Staging_Area.readStageState();
//        for (String fileName : fileNames) {
//            if (!headCommit.fileExitInCommit(fileName) && !stageState.fileExitInAddition(fileName)) {
//                result.add(fileName);
//            }
//        }
//        return result;
//    }

    /**
     * 返回未被跟踪的文件名列表
     * @return
     */
    public static List<String> fileNotTracked() {
        Map<String, String> fileMap = fileNotTrackedMap();
        List<String> result = new ArrayList<>();
        for (String fileName : fileMap.keySet()) {
            result.add(fileName);
        }
        return result;
    }

    /**
     * 返回未被跟踪的<文件名， 文件对应的sha>映射
     * @return
     */
    public static Map<String, String> fileNotTrackedMap() {
        Map<String, String> fileNameShaMap = plainFileMapIn(CWD);
        Map<String, String> result = new TreeMap<>();
        Commit headCommit = Commit.readHeadCommit();
        Staging_Area stageState = Staging_Area.readStageState();
        for (Map.Entry<String, String> entry : fileNameShaMap.entrySet()) {
            String fileName = entry.getKey();
            String sha = entry.getValue();
            if (!headCommit.fileExitInCommit(fileName) && !stageState.fileExitInAddition(fileName)) {
                result.put(fileName, sha);
            }
        }
        return result;
    }

    /**
     * checkout 主函数
     * @param args
     * @throws IOException
     */
    public static void checkout(String[] args) throws IOException {
        if (args.length == 3)   {                                                   // checkout -- [file name]
            checkoutHelperThreeArgs(args);
        } else if (args.length == 4) {                                              // checkout [commit id] -- [file name]
            checkoutHelperFourArgs(args);
        } else if (args.length == 2) {                                              // checkout [branch name]
            checkoutHelperTwoArgs(args);
        } else {
            System.out.println("Incorrect operands");                               // 参数错误
            System.exit(0);
        }
    }

    /**
     * checkout -- [file name] 命令具体执行过程
     * @param args
     * @throws IOException
     */
    private static void checkoutHelperThreeArgs(String[] args) throws IOException {
        if (!args[1].equals("--")) {                                            // 命令格式错误
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String fileName = args[2];
        Commit headCommit = Commit.readHeadCommit();                            // 获取headCommit
        checkoutHelper(headCommit, fileName);                                   // 辅助函数，作用见辅助函数注释
    }

    /**
     * checkout [commit id] -- [file name] 具体执行过程
     * @param args
     * @throws IOException
     */
    private static void checkoutHelperFourArgs(String[] args) throws IOException {
        String commitSha = args[1];
        Commit commit = Commit.getCommitBySha(commitSha);                       // 获取给定的commit
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (!args[2].equals("--")) {                                            // 命令格式错误
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String fileName = args[3];
        checkoutHelper(commit, fileName);                                       // 辅助函数，作用见辅助函数注释
    }

    /**
     * checkout [branch name] 具体执行过程
     * @param args
     * @throws IOException
     */
    private static void checkoutHelperTwoArgs(String[] args) throws IOException {
        String branchName = args[1];
        Commit commit = Branch.getBranchCommit(branchName);
        Staging_Area stageState = Staging_Area.readStageState();
        validateCheckoutForSwitchBranch(branchName);
        deleteFileInCwd();
        readFilesInCommitToCWD(commit);
        Branch.updateHead(branchName);
        stageState.clearStage();
        stageState.saveStageState();
    }

    /**
     * 删除CWD目录下的文件
     */
    private static void deleteFileInCwd() {
        for (File file : CWD.listFiles()) {
            restrictedDelete(file);
        }
    }

    /**
     * 将给定Commit跟踪的文件读到CWD中
     * @param commit
     * @throws IOException
     */
    private static void readFilesInCommitToCWD(Commit commit) throws IOException {
        for (String fileName : commit.fileNameInBlob()) {
            File blob = commit.blobInCommit(fileName);
            File file = join(CWD, fileName);
            byte[] blobByte = readContents(blob);
            writeContents(file, blobByte);
            file.createNewFile();
        }
    }

    /**
     * 辅助函数，传入commit和fileName，若commit不存在
     * 此file的映射则报错，若存在则将原文件删除，替换为commit映射的版本
     * @param commit
     * @param fileName
     * @throws IOException
     */
    private static void checkoutHelper(Commit commit, String fileName) throws IOException {
        File targetFile = commit.blobInCommit(fileName);
        if (targetFile == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        //restrictedDelete(fileName);
        File deleteFile = join(CWD, fileName);
        deleteFile.delete();
        File newFile = join(CWD, fileName);
        byte[] fileByte = readContents(targetFile);
        writeContents(newFile, fileByte);
        newFile.createNewFile();
    }

    /**
     * checkout [branch name] 正确性验证
     * 若branch不存在报错并退出
     * 若切换的分支和HEAD指向分支相同则报错并退出
     * 如果有文件未被跟踪且将会被切换到的分支重写则报错并退出
     * @param branchName
     */
    private static void validateCheckoutForSwitchBranch(String branchName) {
        Commit commit = Branch.getBranchCommit(branchName);
        if (commit == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String headBranch = Branch.headBranchName();
        if (headBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        validateForSwitchCommit(commit);
    }

    /**
     * 若切换到给定的Commit(假设已经存在）
     * 若有文件未被跟踪且会被重写则报错并退出
     * @param commitId
     * @return
     */
    private static void validateForSwitchCommit(String commitId) {
        File commitFile = join(Commit.COMMITS_DIR, commitId);
        Commit commit = readObject(commitFile, Commit.class);
        validateForSwitchCommit(commit);
    }

    /**
     * 若切换到给定的Commit(假设已经存在）
     * 若有文件未被跟踪且会被重写则报错并退出
     * @param commit
     */
    private static void validateForSwitchCommit(Commit commit) {
        Map<String, String> notTrackedFileMap = fileNotTrackedMap();
        for (Map.Entry<String, String> entry : notTrackedFileMap.entrySet()) {
            String fileName = entry.getKey();
            String sha = entry.getValue();
            if (!commit.sameFileExitInCommit(fileName, sha)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    /**
     * Description:
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit. Also
     * moves the current branch’s head to that commit node. See the intro
     * for an example of what happens to the head pointer after using reset.
     * The [commit id] may be abbreviated as for checkout. The staging area
     * is cleared. The command is essentially checkout of an arbitrary commit
     * that also changes the current branch head.
     *
     * Failure case:
     * If no commit with the given id exists, print No commit with that id exists.
     * If a working file is untracked in the current branch and would be overwritten
     * by the reset, print `There is an untracked file in the way; delete it, or add
     * and commit it first.`and exit; perform this check before doing anything else.
     * @param commitId
     */
    public static void reset(String commitId) throws IOException {
        if (!Commit.validateCommitId(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        validateForSwitchCommit(commitId);
        Commit commit = Commit.getCommitBySha(commitId);
        deleteFileInCwd();
        readFilesInCommitToCWD(commit);
        Staging_Area stageState = Staging_Area.readStageState();
        stageState.clearStage();
        stageState.saveStageState();
        Branch.updateBranch(commitId);
    }

    /**
     * merge
     * @param branchName
     */
    public static void merge(String branchName) throws IOException {
        Staging_Area stageState = Staging_Area.readStageState();                                            // 读取stageState
        if (!stageState.isStageEmpty()) {                                                                   // 暂存区不为空则报错退出
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        Commit branchCommit = Branch.getBranchCommit(branchName);                                           // 读取给定branch指向的Commit
        if (branchCommit == null) {                                                                         // 读取给定branch指向的Commit，若此branch不存在则报错退出
            System.out.println("A branch with that name does not exist.");
        }
        String headBranchName = Branch.headBranchName();                                                    // 读取head指向的branch的名字
        if (headBranchName.equals(branchName)) {                                                            // 若给定branch与当前HEAD指向branch相同则报错退出
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit headCommit = Commit.readHeadCommit();                                                        // 读取headCommit
        Commit splitCommit = Commit.getSpiltCommit(headCommit, branchCommit);                               // 读取splitCommit
        Map<String, String> unTrackedFileMap = fileNotTrackedMap();                                         // 读取未被跟踪的<文件名, sha>
        for (Map.Entry<String, String> entry : unTrackedFileMap.entrySet()) {                               // 若有未被跟踪的文件将会被merge重写则报错
            String untrackedFileName = entry.getKey();
            String untrackedFileSha = entry.getValue();
            if (splitCommit.fileExitInCommit(untrackedFileName)) {                                          // 若未被跟踪的文件名存在于splitCommit中
                File fileInSpiltCommit = splitCommit.blobInCommit(untrackedFileName);
                File fileInBranchCommit = branchCommit.blobInCommit(untrackedFileName);
                if (!sha1(fileInSpiltCommit).equals(fileInBranchCommit)) {
                    System.out.println("There is an untracked file in the way; delete it, or add an commit it first.");
                    System.exit(0);
                }
            } else {                                                                                        // 若被跟踪的文件名不存在splitCommit中
                File fileInBranchCommit = branchCommit.blobInCommit(untrackedFileName);
                String fileSha = sha1(fileInBranchCommit);
                if (!fileSha.equals(untrackedFileSha)) {
                    System.out.println("There is an untracked file in the way; delete it, or add an commit it first.");
                    System.exit(0);
                }
            }

        }
        if (splitCommit.getTimestamp().equals(branchCommit.getTimestamp())) {                               // 若给定的branchCommit与splitCommit相同则报错并退出
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitCommit.getTimestamp().equals(headCommit.getTimestamp())) {                                 // 若headCommit指向splitCommit
            String[] args = {"checkout", branchName};
            checkout(args);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        mergeHelper(splitCommit, headCommit, branchCommit);                                                 // 遍历splitCommit, headCommit, branchCommit中的文件，执行merge操作
        String message = "Merged " + branchName + " into " + headBranchName + ".";                          // 生成merge message
        List<String> parent = new ArrayList<>();                                                            // 生成新Commit的parent
        parent.add(sha1(headCommit));
        parent.add(sha1(branchCommit));
        Commit.makeNewCommit(message, parent);                                                              // 提交新的CommitW
    }

    /**
     * 遍历splitCommit, headCommit, branchCommit 中的文件，调用mergeFileLogic()实现具体merge操作
     * @param splitCommit
     * @param headCommit
     * @param branchCommit
     * @throws IOException
     */
    private static void mergeHelper(Commit splitCommit, Commit headCommit, Commit branchCommit) throws IOException {
        Set<String> fileUsed = new TreeSet<>();
        for (String fileName : headCommit.fileNameInBlob()) {
            if (!fileUsed.contains(fileName)) {
                String splitBlobSha = splitCommit.getBlobShaByName(fileName);
                String headBlobSha = headCommit.getBlobShaByName(fileName);
                String branchBlobSha = branchCommit.getBlobShaByName(fileName);
                File fileInSplitCommit = splitCommit.blobInCommit(fileName);
                File fileInBranchCommit = branchCommit.blobInCommit(fileName);
                File fileInHeadCommit = headCommit.blobInCommit(fileName);
                mergeFileLogic(fileName, splitBlobSha, headBlobSha, branchBlobSha, fileInSplitCommit, fileInHeadCommit, fileInBranchCommit);
                fileUsed.add(fileName);
            }
        }
        for (String fileName : branchCommit.fileNameInBlob()) {
            if (!fileUsed.contains(fileName)) {
                String splitBlobSha = splitCommit.getBlobShaByName(fileName);
                String headBlobSha = headCommit.getBlobShaByName(fileName);
                String branchBlobSha = branchCommit.getBlobShaByName(fileName);
                File fileInSplitCommit = splitCommit.blobInCommit(fileName);
                File fileInBranchCommit = branchCommit.blobInCommit(fileName);
                File fileInHeadCommit = headCommit.blobInCommit(fileName);
                mergeFileLogic(fileName, splitBlobSha, headBlobSha, branchBlobSha, fileInSplitCommit, fileInHeadCommit, fileInBranchCommit);
                fileUsed.add(fileName);
            }
        }
        for (String fileName : splitCommit.fileNameInBlob()) {
            if (!fileUsed.contains(fileName)) {
                String splitBlobSha = splitCommit.getBlobShaByName(fileName);
                String headBlobSha = headCommit.getBlobShaByName(fileName);
                String branchBlobSha = branchCommit.getBlobShaByName(fileName);
                File fileInSplitCommit = splitCommit.blobInCommit(fileName);
                File fileInBranchCommit = branchCommit.blobInCommit(fileName);
                File fileInHeadCommit = headCommit.blobInCommit(fileName);
                mergeFileLogic(fileName, splitBlobSha, headBlobSha, branchBlobSha, fileInSplitCommit, fileInHeadCommit, fileInBranchCommit);
                fileUsed.add(fileName);
            }
        }
    }

    /**
     * 根据文件在splitCommit,headCommit,branchCommit中的三种状态执行具体的merge操作
     * @param fileName
     * @param splitBlobSha
     * @param headBlobSha
     * @param branchBlobSha
     * @param fileInSplitCommit
     * @param fileInHeadCommit
     * @param fileInBranchCommit
     * @throws IOException
     */
    private static void mergeFileLogic(String fileName, String splitBlobSha, String headBlobSha, String branchBlobSha, File fileInSplitCommit, File fileInHeadCommit, File fileInBranchCommit) throws IOException {
        if (splitBlobSha != null && splitBlobSha.equals(headBlobSha) &&
                !headBlobSha.equals(branchBlobSha) && branchBlobSha != null) {                                       // modified in given branch, not modified in headBranch
            Blobs.readBlobToCWD(fileInBranchCommit,fileName);
            Staging_Area.add(fileName);
        }
        else if (splitBlobSha != null && splitBlobSha.equals(branchBlobSha) &&
                !splitBlobSha.equals(headBlobSha) && headBlobSha != null) {                                          // modified in headBranch, not modified in given Branch

        }
        else if (splitBlobSha != null && headBlobSha == null && branchBlobSha == null) {                             // modified headBranch and given branch in the same way, absent in headBranch and given branch

        }
        else if (splitBlobSha != null && headBlobSha != null && headBlobSha.equals(branchBlobSha)) {                 // modified headBranch and given branch in the same way

        }
        else if (splitBlobSha == null && branchBlobSha == null && headBlobSha != null) {                             // not present at splitBranch,only present in headBranch

        }
        else if (splitBlobSha == null && headBlobSha == null && branchBlobSha != null) {                             // not present at splitBranch, only present in given branch
            Blobs.readBlobToCWD(fileInBranchCommit, fileName);
            Staging_Area.add(fileName);
        }
        else if (splitBlobSha != null && splitBlobSha.equals(headBlobSha) && branchBlobSha == null) {                // present at splitBranch, unmodified in the headBranch, absent in given Branch
            Staging_Area.rm(fileName);
        }
        else if (splitBlobSha != null && splitBlobSha.equals(branchBlobSha) && headBlobSha == null) {                // present at splitBranch, unmodified in the given branch, absent in headBranch

        }
        else if (splitBlobSha != null && headBlobSha == null &&
                !splitBlobSha.equals(branchBlobSha) && branchBlobSha != null) {                                      // present at splitBranch, delete in headBranch, modified in given branch
            writeConflictFile(fileInHeadCommit, fileInBranchCommit, fileName);
        }
        else if (splitBlobSha != null && headBlobSha != null && !headBlobSha.equals(branchBlobSha)) {                // present at splitBranch, delete in given branch, modified in headBranch
            writeConflictFile(fileInHeadCommit, fileInBranchCommit, fileName);                                       // present at splitBranch, modified in different ways in given branch and headBranch
        }
        else if (splitBlobSha == null && headBlobSha != null && branchBlobSha != null
                && !headBlobSha.equals(branchBlobSha)) {                                                             // absent at splitBranch, has different contents in given and head branch
            writeConflictFile(fileInHeadCommit, fileInBranchCommit, fileName);
        }
    }

    /**
     * 将在headCommit与branchCommit中不同的文件以特定格式merge，并且覆盖原文件
     * @param fileInHeadCommit
     * @param fileInBranchCommit
     * @param fileName
     * @throws IOException
     */
    private static void writeConflictFile (File fileInHeadCommit, File fileInBranchCommit, String fileName) throws IOException {
        System.out.println("Encountered a merge conflict.");
        String result = "<<<<<<< HEAD\n";
        if (fileInHeadCommit != null) {
            String headContent = readContentsAsString(fileInHeadCommit);
            result += headContent + "\n";
        }
        result += "=======\n";
        if (fileInBranchCommit != null) {
            String branchContent = readContentsAsString(fileInBranchCommit);
            result += branchContent + "\n";
        }
        result += ">>>>>>>" + "\n";
        File newFile = join(CWD, fileName);
        writeContents(newFile, result);
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
    }
    private static void validateForMerge(String branchName) {


    }
}
