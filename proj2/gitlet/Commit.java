package gitlet;

import static gitlet.Utils.*;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

/** Represents a gitlet commit object.
 *  Commit objects are serialized and identified by SHA-1 id's. Each also includes
 *  a HashMap of Blob references of its files, a parent reference, log message, and commit
 *  date/time.
 *  @author Emily Nguyen
 */
public class Commit implements Serializable {
    /** The SHA-1 hash of the current commit. */
    private String SHA;

    /** The parent commit(s) of the current commit. */
    private Commit parent1;
    private Commit parent2;

    /** The date and time of the current commit. */
    private Date timestamp;

    /** The message of this commit. */
    private String message;

    /** HashMap of all files in the commit, with file name as the key and the Blob
     *  SHA as the value. */
    private HashMap<String, String> files = new HashMap<>();

    /** Creates the new Commit object given a parent Commit and String message.
     *  Generates a SHA for the Commit, commits all files in STAGING_AREA, removes
     *  files indicated in REMOVE, and clears staging area. If parent is null,
     *  creates initial commit. */
    public Commit(String m, Commit p1, Commit p2) {
        message = m;
        parent1 = p1;
        parent2 = p2;
        SHA = generateSHA();
        if (p1 == null) {
            timestamp = new Date(0);
        } else {
            timestamp = new Date();
            HashMap<String, String> parentFiles = p1.getFilesMap();
            List<String> toRemove = plainFilenamesIn(Repository.REMOVE);
            if (toRemove != null) {
                for (String name : toRemove) {
                    join(Repository.REMOVE, name).delete();
                    parentFiles.remove(name);
                }
            }
            List<String> toAdd = plainFilenamesIn(Repository.STAGING_AREA);
            if (toAdd != null) {
                for (String name : toAdd) {
                    File file = join(Repository.STAGING_AREA, name);
                    Blob blobby = readObject(file, Blob.class);
                    File commitBlob = join(Repository.BLOBS, blobby.getSHA());
                    if (!commitBlob.exists()) {
                        writeObject(commitBlob, blobby);
                    }
                    file.delete();
                    parentFiles.put(name, blobby.getSHA());
                }
            }
            files = parentFiles;
        }
    }

    /** Returns the SHA-1 string of the current commit. */
    public String getSHA() {
        return this.SHA;
    }

    /** Generates the SHA-1 of the current commit. */
    private String generateSHA() {
        byte[] representation = serialize(this);
        return sha1(representation);
    }

    /** Returns deep copy of files HashMap. */
    public HashMap<String, String> getFilesMap() {
        HashMap<String, String> filesCopy = new HashMap<>();
        for (String fileName : this.files.keySet()) {
            filesCopy.put(fileName, getFileSHA(fileName));
        }
        return filesCopy;
    }

    /** Returns the SHA id associated with fileName from the files HashMap.
     *  If it doesn't exist, returns null. */
    public String getFileSHA(String fileName) {
        return this.files.get(fileName);
    }

    /** Returns the string message of the current commit. */
    public String getMessage() {
        return this.message;
    }

    /** Returns the Date of the current commit as a string. */
    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy");
        return dateFormat.format(this.timestamp) + " -0800";
    }

    public HashSet<String> getFileNames() {
        HashSet<String> fileSet = new HashSet<>();
        for (String fileName : this.files.keySet()) {
            fileSet.add(fileName);
        }
        return fileSet;
    }

    public Commit getParent1() {
        return this.parent1;
    }

    public Commit getParent2() {
        return this.parent2;
    }
}
