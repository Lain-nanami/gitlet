package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static gitlet.Utils.*;

public class Staging_Area implements Serializable{
    public static final File STAGING_AREA_DIR = join(Repository.GITLET_DIR, "staging_area");
    private TreeMap<String, String> addition;
    private TreeMap<String, String> removal;

    Staging_Area() {
        addition = new TreeMap<>();
        removal = new TreeMap<>();
    }

    /**
     * 保存stageState
     * @throws IOException
     */
    public  void saveStageState() throws IOException {
        File[] files = STAGING_AREA_DIR.listFiles();
        File tmp = null;
        for (File file : files) {
            if (file.getName().equals("stageState")) {
                tmp = file;
            }
        }
        if (tmp != null) {
            writeObject(tmp, this);
            return;
        }
        File stageState = join(STAGING_AREA_DIR, "stageState");
        writeObject(stageState, this);
        stageState.createNewFile();
    }

    /**
     * 读取stageState
     * @return
     */
    public static Staging_Area readStageState() {
        File[] files = STAGING_AREA_DIR.listFiles();
        for (File file : files) {
            if (file.getName().equals("stageState")) {
                Staging_Area stageState = readObject(file, Staging_Area.class);
                return stageState;
            }
        }
        return null;
    }
    /**
     * Description:
     * Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command). For this reason, adding a file is
     * also called staging the file for addition. Staging an already-staged file overwrites
     * the previous entry in the staging area with the new contents. The staging area should
     * be somewhere in .gitlet. If the current working version of the file is identical to
     * the version in the current commit, do not stage it to be added, and remove it from
     * the staging area if it is already there (as can happen when a file is changed, added,
     * and then changed back to it’s original version). The file will no longer be staged
     * for removal (see gitlet rm), if it was at the time of the command.
     *
     * Failure cases:
     * If the file does not exist, print the error message File does not exist. and exit without changing anything.
     */
    public static void add(String fileName) throws IOException {
        File file = Repository.weatherFileExit(fileName);                   //是否存在此文件
        if (file == null) {                                                 //若不存在，则报错退出
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String sha = sha1(file);                                            //获取文件夹中文件的sha1
        Staging_Area stageState = Staging_Area.readStageState();            //读取stageState
        String removalSha =  stageState.removal.get(fileName);              //查询removal中是否存在fileName的映射，若存在则删除
        if (removalSha != null ) {
            stageState.removal.remove(fileName);
        }
        Commit headCommit = Commit.readHeadCommit();                        //获取HEAD所指的Commit
        if (!headCommit.sameFileExitInCommit(fileName, sha)) {              //当前Commit中不存在<fileName, sha>的映射
            Blobs.creatNewBlob(file, sha);                                  //创建新的Blob
            stageState.addition.put(fileName, sha);                         //向stageState添加<fileName, sha>映射
        }
        stageState.saveStageState();                                        //保存stageState
    }

    /**
     * 判断addition和removal是否都为空
     * @return
     */
    public boolean isStageEmpty() {
        if (addition.isEmpty() && removal.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     *Description:
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for removal
     * and remove the file from the working directory if the user has not
     * already done so (do not remove it unless it is tracked in the current
     * commit).
     *
     * Failure cases:
     * If the file is neither staged nor tracked by the head commit, print
     * the error message No reason to remove the file.
     */
    public static void rm(String fileName) throws IOException {
        Staging_Area stageState = readStageState();                 // 读取stageState
        Commit headCommit = Commit.readHeadCommit();                // 读取HEAD所指的Commit
        if (!stageState.addition.containsKey(fileName) && !headCommit.fileExitInCommit(fileName)) {
            System.out.println("No reason to remove the file.");    // 此文件不存在暂存添加区并且在当前的Commit中未被跟踪，报错并退出
            System.exit(0);
        }
        File deleteFile = join(Repository.CWD, fileName);
        String deleteFileSha;
        if (deleteFile.exists()) {
            deleteFileSha = sha1(deleteFile);
        } else {
            deleteFileSha = headCommit.fileShaInBlobs(fileName);
        }
        stageState.addition.remove(fileName);                       // 将此文件从暂存添加区删除
        if (headCommit.fileExitInCommit(fileName)) {                // 若此文件在Commit中被跟踪，则将其添加到暂存删除区在当前目录下删除此文件
            stageState.removal.put(fileName, deleteFileSha);
            restrictedDelete(fileName);
        }
        stageState.saveStageState();
    }

    public TreeMap<String, String> getAddition() {
        return addition;
    }

    public TreeMap<String, String> getRemoval() {
        return removal;
    }

    public void clearStage() {
        addition.clear();
        removal.clear();
    }

    public boolean fileExitInAddition(String fileName) {
        return addition.containsKey(fileName);
    }

}
