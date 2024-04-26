package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.Serializable;

public class Blob implements Serializable {

    private String SHA;
    private byte[] contents;
    private String fileName;

    public Blob(String name, File path) {
        if (!path.exists()) {
            contents = null;
        } else {
            contents = readContents(path);
            SHA = generateSHA(path);
            fileName = name;
        }
    }

    private String generateSHA(File f) {
        String representation = readContentsAsString(f);
        return sha1(representation);
    }

    public String getSHA() {
        return SHA;
    }

    public byte[] getContents() {
        return contents;
    }
}
