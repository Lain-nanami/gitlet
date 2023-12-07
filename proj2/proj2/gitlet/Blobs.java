package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blobs implements Serializable {
    public static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs");

    /**
     * 将文件保存为Blob,文件名为file的sah, 内容为file的byte[]
     * @param file
     * @param sha
     * @throws IOException
     */
    public static void creatNewBlob(File file, String sha) throws IOException {
        File newBlob = join(BLOBS_DIR, sha);
        byte[] fileByte = readContents(file);
        writeContents(newBlob, fileByte);
        newBlob.createNewFile();
    }

    /**
     * 将指定的Blob读取到CWD
     * @param blob
     * @param fileName
     * @throws IOException
     */
    public static void readBlobToCWD(File blob, String fileName) throws IOException {
        File file = join(Repository.CWD, fileName);
        byte[] fileByte = readContents(blob);
        writeContents(file, fileByte);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
