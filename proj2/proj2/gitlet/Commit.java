package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    public static final File COMMITS_DIR = join(Repository.GITLET_DIR, "commits");
    /** The message of this Commit. */
    private String message;
    private Date date;
    private List<String> parent;
    private TreeMap<String, String> blobs;

    /* TODO: fill in the rest of this class. */


    Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = null;
        this.blobs = new TreeMap<>();
    }

    Commit(String message, String parentSha, Commit oldCommit) {
        this.message = message;
        this.date = new Date();
        this.parent = new ArrayList<>();
        this.parent.add(parentSha);
        this.blobs = new TreeMap<>((Map<String, String>) oldCommit.blobs);
    }

    Commit(String message, List<String> parentSha, Commit oldCommit) {
        this.message = message;
        this.date = new Date();
        this.parent = new ArrayList<>();
        this.parent.addAll(parentSha);
        this.blobs = new TreeMap<>((Map<String, String>) oldCommit.blobs);
    }
    public static Commit getSpiltCommit(Commit headCommit, Commit branchCommit) {
        List<String> headCommitParentShaList = new ArrayList<>();
        List<Commit> headCommitParentCommitList = new ArrayList<>();
        headCommitParentShaList.add(sha1(headCommit));
        headCommitParentCommitList.add(headCommit);
        List<String> parentList = headCommit.parent;
        while (!parentList.isEmpty()) {
            Commit parentCommit = getCommitBySha(parentList.get(0));
            headCommitParentShaList.add(parentList.get(0));
            headCommitParentCommitList.add(parentCommit);
            parentList = parentCommit.parent;
            if (parentList == null) {
                break;
            }
        }
        String branchCommitSha = sha1(branchCommit);
        for (int i = 0; i < headCommitParentShaList.size(); i += 1) {
            if (headCommitParentShaList.get(i).equals(branchCommitSha)) {
                Commit result = headCommitParentCommitList.get(i);
                return result;
            }
        }
        parentList = branchCommit.parent;
        while (!parentList.isEmpty()) {
            for (int i = 0; i < headCommitParentShaList.size(); i += 1) {
                if (headCommitParentShaList.get(i).equals(parentList.get(0))) {
                    Commit result = headCommitParentCommitList.get(i);
                    return result;
                }
            }
            Commit parentCommit = getCommitBySha(parentList.get(0));
            parentList = parentCommit.parent;
            if (parentCommit.parent == null) {
                break;
            }
        }
        return null;
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /**
     * 保存Commit，返回此Commit的sha
     * @return
     * @throws IOException
     */
    public String saveCommit() throws IOException {
        File tmp = new File("tm");
        writeObject(tmp, this);
        String sha = sha1(tmp);
        tmp.delete();
        File newCommit = join(COMMITS_DIR, sha);
        writeObject(newCommit, this);
        newCommit.createNewFile();
        return sha;
    }

    /**
     * 返回HEAD所指向的Commit
     * @return
     */
    public static Commit readHeadCommit() {
        File[] files = COMMITS_DIR.listFiles();
        String headSha = Branch.headBranch();
        for (File file : files) {
            if (file.getName().equals(headSha)) {
                Commit headCommit = readObject(file, Commit.class);
                return headCommit;
            }
        }
        return null;
    }

    /**
     * 判断在Commit是否存在<fileName, sha>的映射
     * @param fileName
     * @param sha
     * @return
     */
    public boolean sameFileExitInCommit(String fileName, String sha) {
        String blobSha = blobs.get(fileName);
        if (blobSha == null || !blobSha.equals(sha)) {
            return false;
        }
        return true;

    }

    /**
     * Description:
     * Saves a snapshot of tracked files in the current commit and staging area so
     * they can be restored at a later time, creating a new commit. The commit is
     * said to be tracking the saved files. By default, each commit’s snapshot of
     * files will be exactly the same as its parent commit’s snapshot of files; it
     * will keep versions of files exactly as they are, and not update them. A commit
     * will only update the contents of files it is tracking that have been staged for
     * addition at the time of commit, in which case the commit will now include the
     * version of the file that was staged instead of the version it got from its parent.
     * A commit will save and start tracking any files that were staged for addition but
     * weren’t tracked by its parent. Finally, files tracked in the current commit may
     * be untracked in the new commit as a result being staged for removal by the rm command (below).
     *
     * Failure cases: If no files have been staged, abort. Print the message No changes
     * added to the commit. Every commit must have a non-blank message. If it doesn’t,
     * print the error message Please enter a commit message. It is not a failure for
     * tracked files to be missing from the working directory or changed in the working
     * directory. Just ignore everything outside the .gitlet directory entirely.
     */
    public static void makeNewCommit(String message) throws IOException {
        Staging_Area stageState = Staging_Area.readStageState();                //读取stageState
        if (stageState.isStageEmpty()) {                                        //检查暂存情况，若无文件被暂存则报错推出
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (!validMessage(message)) {                                           //检查message是否为空白字符串，若为空白字符串则报错推出
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit headCommit = readHeadCommit();                                   //读取HEAD所指的Commit
        String headSha = Branch.headBranch();                                   //读取headCommit的sha
        Commit newCommit = new Commit(message, headSha, headCommit);            //创建新的Commit
        newCommit.updateAddition(stageState.getAddition());                     //遍历stageState.addition, 更新newCommit.blobs
        newCommit.updateRemoval(stageState.getRemoval());                       //遍历stageState.removal, 更新newCommit.blobs
        stageState.clearStage();                                                //清空暂存区
        stageState.saveStageState();                                            //保存stageState
        String newCommitSha = newCommit.saveCommit();                           //保存newCommit
        Branch.updateBranch(newCommitSha);                                      //更新当前Branch指向的提交
    }

    public static void makeNewCommit(String message, List<String> parent) throws IOException {
        Staging_Area stageState = Staging_Area.readStageState();                //读取stageState
        if (stageState.isStageEmpty()) {                                        //检查暂存情况，若无文件被暂存则报错推出
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (!validMessage(message)) {                                           //检查message是否为空白字符串，若为空白字符串则报错推出
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit headCommit = readHeadCommit();                                   //读取HEAD所指的Commit
        String headSha = Branch.headBranch();                                   //读取headCommit的sha
        Commit newCommit = new Commit(message, parent, headCommit);            //创建新的Commit
        newCommit.updateAddition(stageState.getAddition());                     //遍历stageState.addition, 更新newCommit.blobs
        newCommit.updateRemoval(stageState.getRemoval());                       //遍历stageState.removal, 更新newCommit.blobs
        stageState.clearStage();                                                //清空暂存区
        stageState.saveStageState();                                            //保存stageState
        String newCommitSha = newCommit.saveCommit();                           //保存newCommit
        Branch.updateBranch(newCommitSha);                                      //更新当前Branch指向的提交
    }
    /**
     * message 正确性验证
     * @param message
     * @return
     */
    public static boolean validMessage(String message) {
        for (int i = 0; i < message.length(); i += 1) {
            if (message.charAt(i) != ' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * 遍历stageState.addition 完成更新
     * @param map
     */
    public void updateAddition(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            blobs.put(key, value);
        }
    }
    /**
     * 遍历stageState.removal 完成更新
     * @param map
     */
    public void updateRemoval(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //String value = entry.getValue();
            blobs.remove(key);
        }
    }

    /**
     * Description:
     * Starting at the current head commit, display information about
     * each commit backwards along the commit tree until the initial commit, following
     * the first parent commit links, ignoring any second parents found in merge commits.
     * (In regular Git, this is what you get with git log --first-parent). This set of
     * commit nodes is called the commit’s history. For every node in this history,
     * the information it should display is the commit id, the time the commit was made,
     * and the commit message.
     *
     */
    public static void log() {
        Commit headCommit = readHeadCommit();                   // 获取headCommit
        String currentCommitSha = Branch.headBranch();          // 获取headCommit的Sha
        while (headCommit != null) {                            // 遍历直至当前Commit无父节点
            headCommit.printLogInfo(currentCommitSha);          // 打印当前Commit节点信息
            if (headCommit.parent == null) {
                break;
            }
            currentCommitSha = headCommit.parent.get(0);        // 获取前一节点Sha
            headCommit = getCommitBySha(currentCommitSha);      // 获取前一节点
        }
    }

    /**
     * 通过sha获取指定的Commit
     * @param sha
     * @return
     */
    public static Commit getCommitBySha(String sha) {
        if (sha == null) {
            return null;
        }
        if (sha.length() < 40 && sha.length() >= 6) {
            Commit commit = getCommitByPrefixSha(sha);
            if (commit != null) {
                return commit;
            }
            System.out.println("Please enter a longer id.");
            return null;
        }
        for (File file : COMMITS_DIR.listFiles()) {
            if (file.getName().equals(sha)) {
                return readObject(file, Commit.class);
            }
        }
        return null;
    }

    private static Commit getCommitByPrefixSha(String commitPrefix) {
        for (File file : COMMITS_DIR.listFiles()) {
            boolean fix = true;
            for (int i = 0; i < commitPrefix.length(); i++) {
                if (file.getName().charAt(i) != commitPrefix.charAt(i)) {
                    fix = false;
                    break;
                }
            }
            if (fix) {
                return readObject(file, Commit.class);
            }
        }
        return null;
    }
    /**
     * 打印当前Commit信息
     * @param currentCommitSha
     */
    public void printLogInfo(String currentCommitSha) {
        if (this.parent == null || this.parent.size() == 1) {            // 非merge节点
            System.out.println("===");
            System.out.println("commit " + currentCommitSha);
            System.out.println("Date: " + this.getTimestamp());
            System.out.println(this.message);
            System.out.println();
        } else {                                                        // merge节点
            System.out.println("===");
            System.out.println("commit " + currentCommitSha);
            System.out.print("Merge:");
            for (String s : this.parent) {
                System.out.print(" " + s.substring(0, 7));
            }
            System.out.println();
            System.out.println("Date: " + this.getTimestamp());
            System.out.println(this.message);
            System.out.println();
        }
    }

    /**
     * Description:
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter. Hint: there is a useful method in
     * gitlet.Utils that will help you iterate over files within a directory.
     */
    public static void global_log() {
        List<String> commitsSha = plainFilenamesIn(COMMITS_DIR);
        for (String commitSha : commitsSha) {
            Commit commit = getCommitBySha(commitSha);
            commit.printLogInfo(commitSha);
        }
    }

    /**
     * Description:
     * Prints out the ids of all commits that have the given commit
     * message, one per line. If there are multiple such commits, it prints the
     * ids out on separate lines. The commit message is a single operand; to
     * indicate a multiword message, put the operand in quotation marks, as for
     * the commit command below. Hint: the hint for this command is the same as
     * the one for global-log.
     *
     * Failure cases:
     * If no such commit exists, prints the error message Found no commit with that
     * message.
     */
    public static void find(String message) {
        List<String> commitsSha = plainFilenamesIn(COMMITS_DIR);
        boolean commitExit = false;
        for (String commitSha : commitsSha) {
            Commit commit = getCommitBySha(commitSha);
            if (commit.getMessage().equals(message)) {
                System.out.println(commitSha);
                commitExit = true;
            }
        }
        if (!commitExit) {
            System.out.println("Found no commit with that message.");
        }
    }

    public String getMessage() {
        return message;
    }

    /**
     * 查询commit中是否有此file的映射
     * @param fileName
     * @return
     */
    public boolean fileExitInCommit(String fileName) {
        return blobs.containsKey(fileName);
    }

    /**
     * 返回文件名为fileName的Blob
     * @param fileName
     * @return
     */
    public File blobInCommit(String fileName) {
        String blobSha = blobs.get(fileName);
        if (blobSha == null) {
            return null;
        }
        for (File file : Blobs.BLOBS_DIR.listFiles()) {
            if (file.getName().equals(blobSha)) {
                return file;
            }
        }
        return null;
    }

    public Set<Map.Entry<String, String>> blobMap() {
        return blobs.entrySet();
    }

    /**
     * 返回Blobs中sha的Set视图
     * @return
     */
    public Set<String> fileNameInBlob() {
        return blobs.keySet();
    }

    public static boolean validateCommitId(String commitId) {
        for (File file : Commit.COMMITS_DIR.listFiles()) {
            if (file.getName().equals(commitId)) {
                return true;
            }
        }
        return false;
    }

    public String fileShaInBlobs(String fileName) {
        return blobs.get(fileName);
    }

    public String getBlobShaByName(String fileName) {
        return blobs.get(fileName);
    }
}
