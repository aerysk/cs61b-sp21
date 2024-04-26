package gitlet;

import java.io.File;
import java.util.List;
import java.util.HashSet;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Includes all the commands used to manipulate files within the repository.
 *
 *  @author Emily Nguyen
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory, including folders for all blobs, commits, and branches. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File BRANCHES = join(GITLET_DIR, "branches");

    /** The .staging directory where files are added. */
    public static final File STAGING_AREA = join(CWD, ".staging");
    /** The .remove directory where files are staged for removal. */
    public static final File REMOVE = join(CWD, ".remove");

    /** A file containing the head branch. */
    public static final File HEAD = join(GITLET_DIR, "head");
    /** A file containing the name of the current Branch. */
    public static final File CURRENT_BRANCH = join(GITLET_DIR, "current branch");

    /** Initializes the repository with complete .gitlet and .staging directories. If the
     *  repo already exists, prints out an error message. */
    public static void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            BLOBS.mkdir();
            COMMITS.mkdir();
            BRANCHES.mkdir();
            STAGING_AREA.mkdir();
            REMOVE.mkdir();
            Commit initialCommit = new Commit("initial commit", null, null);
            writeObject(join(COMMITS, initialCommit.getSHA()), initialCommit);
            writeObject(HEAD, initialCommit);
            writeContents(join(GITLET_DIR, "initial commit"), initialCommit.getSHA());
            Branch currentBranch = new Branch("master", null);
            currentBranch.newCommit(initialCommit);
            writeObject(join(BRANCHES, "master"), currentBranch);
            writeContents(CURRENT_BRANCH, "master");
        } else {
            System.out.println("A Gitlet version-control system already exists "
                    + "in the current directory.");
        }
    }

    /** Adds the file named fileName to STAGING_AREA if the exact file was not already added
     *  and if the exact file is not already in our current HEAD commit. */
    public static void add(String fileName) {
        File f = join(CWD, fileName);
        if (f.exists()) {
            File inRemove = join(REMOVE, fileName);
            if (inRemove.exists()) {
                inRemove.delete();
            }
            Blob blobby = makeBlob(fileName, f);
            String blobbySHA = blobby.getSHA();
            Blob existingBlob = getBlobByName(fileName);
            // If blobby is already in the staging area
            if (existingBlob != null && blobbySHA.equals(existingBlob.getSHA())) {
                return;
            }
            Commit headCommit = readObject(HEAD, Commit.class);
            String headFileSHA = headCommit.getFileSHA(fileName);
            // If SHA of blobby is the same as that of corresponding file in the current commit
            if (headFileSHA != null && headFileSHA.equals(blobbySHA)) {
                removeBlobByName(fileName);
                return;
            }
            storeBlobByName(fileName, blobby);
        } else {
            System.out.println("File does not exist.");
        }
    }

    /** Creates a Commit object and writes it to a file in COMMITS. Updates HEAD
     *  and the current branch file in BRANCHES to reflect the new commit. */
    public static void commit(String message) {
        if (plainFilenamesIn(STAGING_AREA).isEmpty() && plainFilenamesIn(REMOVE).isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        } else if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        commitHelper(message, readObject(HEAD, Commit.class), null);
    }

    private static void commitHelper(String message, Commit p1, Commit p2) {
        Commit currentCommit = new Commit(message, p1, p2);
        writeObject(join(COMMITS, currentCommit.getSHA()), currentCommit);
        writeObject(HEAD, currentCommit);
        File currentBranchFile = join(BRANCHES, readContentsAsString(CURRENT_BRANCH));
        Branch currentBranch = readObject(currentBranchFile, Branch.class);
        currentBranch.newCommit(currentCommit);
        writeObject(currentBranchFile, currentBranch);
    }

    /** Removes the file named fileName from the staging area, or stages it to be
     *  untracked in the next commit and deletes it from CWD. If it doesn't exist
     *  in STAGING_AREA or in the current commit, prints an error message. */
    public static void rm(String fileName) {
        File file = join(STAGING_AREA, fileName);
        Commit currentCommit = readObject(HEAD, Commit.class);
        if (file.exists()) {
            file.delete();
        } else if (currentCommit.getFileSHA(fileName) == null) {
            System.out.println("No reason to remove the file.");
        } else {
            writeContents(join(REMOVE, fileName), "staged for removal");
            join(CWD, fileName).delete();
        }
    }

    /** Starting at the HEAD commit, displays information about each commit
     *  backwards along the commit tree until the initial commit. */
    public static void log() {
        Branch history = readObject(join(BRANCHES, readContentsAsString(CURRENT_BRANCH)),
                Branch.class);
        for (Commit c : history.getCommitsLL()) {
            System.out.println("===");
            System.out.println("commit " + c.getSHA());
            Commit p2 = c.getParent2();
            if (p2 != null) {
                System.out.println("Merge: " + c.getParent1().getSHA().substring(0, 7) + " "
                        + p2.getSHA().substring(0, 7));
            }
            System.out.println("Date: " + c.getDate());
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    /** Like log, but displays information about every commit ever made. */
    public static void globalLog() {
        List<String> allCommits = plainFilenamesIn(COMMITS);
        if (!allCommits.isEmpty()) {
            for (String id : allCommits) {
                Commit c = readObject(join(COMMITS, id), Commit.class);
                System.out.println("===");
                System.out.println("commit " + c.getSHA());
                Commit p2 = c.getParent2();
                if (p2 != null) {
                    System.out.println("Merge: " + c.getParent1().getSHA().substring(0, 7) + " "
                            + p2.getSHA().substring(0, 7));
                }
                System.out.println("Date: " + c.getDate());
                System.out.println(c.getMessage());
                System.out.println();
            }
        }
    }

    /** Prints out the ids of all commits with the input String message.
     *  If no such commit exists, prints an error message. */
    public static void find(String message) {
        List<String> allCommits = plainFilenamesIn(COMMITS);
        boolean messageExists = false;
        if (!allCommits.isEmpty()) {
            for (String id : allCommits) {
                Commit c = readObject(join(COMMITS, id), Commit.class);
                if (c.getMessage().equals(message)) {
                    System.out.println(id);
                    messageExists = true;
                }
            }
        }
        if (!messageExists) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Displays what branches currently exist, and marks the current branch with
     *  a *. Displays what files have been staged for addition or removal. */
    public static void status() {
        System.out.println("=== Branches ===");
        List<String> branchNames = plainFilenamesIn(BRANCHES);
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (!branchNames.isEmpty()) {
            for (String name : branchNames) {
                if (name.equals(currentBranch)) {
                    System.out.println("*" + name);
                } else {
                    System.out.println(name);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> stagedFiles = plainFilenamesIn(STAGING_AREA);
        if (!stagedFiles.isEmpty()) {
            for (String fileName : stagedFiles) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> removedFiles = plainFilenamesIn(REMOVE);
        if (!removedFiles.isEmpty()) {
            for (String fileName : removedFiles) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println();
        System.out.println("=== Untracked Files ===");

        System.out.println();
    }

    /** Given a String fileName, checks out the file from the HEAD commit. If file
     *  does not exist, returns an error message. */
    public static void checkoutWithFile(String fileName) {
        Commit headCommit = readObject(HEAD, Commit.class);
        String fileSHA = headCommit.getFileSHA(fileName);
        if (fileSHA == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blob blobby = readObject(join(BLOBS, fileSHA), Blob.class);
        writeContents(join(CWD, fileName), (Object) blobby.getContents());
    }

    /** Given a String commit id and String fileName, checks out the respective file
     *  from the indicated commit into CWD. If given commit id does not exist or the
     *  file does not exist in the indicated commit, returns an error message. */
    public static void checkoutWithCommit(String commitID, String fileName) {
        if (commitID.length() < 40) {
            commitID = returnFullID(commitID);
        }
        File f = join(COMMITS, commitID);
        if (commitID == null || !f.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(f, Commit.class);
        String fileSHA = commit.getFileSHA(fileName);
        if (fileSHA == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blob blobby = readObject(join(BLOBS, fileSHA), Blob.class);
        writeContents(join(CWD, fileName), (Object) blobby.getContents());
    }

    /** Takes all files in the commit at the head of the given branch and puts them
     *  in CWD, overwriting versions of existing files. The given branch becomes HEAD,
     *  other files are deleted, and STAGING_AREA is cleared. If no branch exists, the
     *  input is the current branch, or a working file is untracked in the current
     *  branch, prints an error message. */
    public static void checkoutWithBranch(String branchName) {
        File f = join(BRANCHES, branchName);
        if (untrackedFileExists()) {
            System.out.println("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
            return;
        } else if (!f.exists()) {
            System.out.println("No such branch exists.");
            return;
        } else if (readContentsAsString(CURRENT_BRANCH).equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch branch = readObject(f, Branch.class);
        Commit headCommit = branch.getHeadCommit();
        removeFilesNotInCheckoutAndRetrieve(headCommit.getSHA());
        writeObject(HEAD, headCommit);
        writeContents(CURRENT_BRANCH, branchName);
        clearStaging();
    }

    /** Creates a new branch with the given name and points it at the current
     *  HEAD commit. */
    public static void branch(String name) {
        List<String> branchNames = plainFilenamesIn(BRANCHES);
        if (branchNames != null && branchNames.contains(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        String currentBranchName = readContentsAsString(CURRENT_BRANCH);
        Branch newBranch = new Branch(name, readObject(join(BRANCHES, currentBranchName),
                Branch.class));
        writeObject(join(BRANCHES, name), newBranch);
    }

    /** Deletes the branch with the given name. If a branch of the name does
     *  not exist or user is asking to remove the current branch, prints an
     *  error message. */
    public static void rmBranch(String name) {
        File f = join(BRANCHES, name);
        if (!f.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (readContentsAsString(CURRENT_BRANCH).equals(name)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        f.delete();
    }

    /** Checks out all files tracked by given commit and removes files not
     *  present in that commit. Moves current branch's head to the commit. */
    public static void reset(String commitID) {
        if (commitID.length() < 40) {
            commitID = returnFullID(commitID);
        }
        File f = join(COMMITS, commitID);
        if (commitID == null || !f.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        } else if (untrackedFileExists()) {
            System.out.println("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
            return;
        }
        removeFilesNotInCheckoutAndRetrieve(commitID);
        Commit commit = readObject(f, Commit.class);
        String commitSHA = commit.getSHA();
        writeObject(HEAD, commit);
        File currentBranchFile = join(BRANCHES, readContentsAsString(CURRENT_BRANCH));
        Branch branch = readObject(currentBranchFile, Branch.class);
        boolean existsInBranch = false;
        for (Commit c : branch.getCommitsLL()) {
            if (c.getSHA().equals(commitSHA)) {
                existsInBranch = true;
                break;
            }
        }
        if (existsInBranch) {
            while (!branch.getHeadCommit().getSHA().equals(commitSHA)) {
                branch.removeHeadCommit();
            }
        } else {
            branch.newCommit(commit);
        }
        writeObject(currentBranchFile, branch);
        clearStaging();
    }

    /** Merges files from the given branch into the current branch. */
    public static void merge(String branchName) {
        if (untrackedFileExists()) {
            System.out.println("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
            return;
        } else if (!plainFilenamesIn(STAGING_AREA).isEmpty()
                || !plainFilenamesIn(REMOVE).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!plainFilenamesIn(BRANCHES).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (readContentsAsString(CURRENT_BRANCH).equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Branch currentBranch = readObject(join(BRANCHES, readContentsAsString(CURRENT_BRANCH)),
                Branch.class);
        Commit currentCommit = readObject(HEAD, Commit.class);
        Commit splitPoint = currentBranch.getLCA(branchName);
        String splitPointSHA = splitPoint.getSHA();
        Branch givenBranch = readObject(join(BRANCHES, branchName), Branch.class);
        Commit givenCommit = givenBranch.getHeadCommit();
        if (splitPointSHA.equals(givenCommit.getSHA())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (splitPointSHA.equals(currentCommit.getSHA())) {
            checkoutWithBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        processAllFiles(branchName, splitPoint, currentCommit, givenCommit);
    }

    /** Processes all files in a merge command. */
    private static void processAllFiles(String branchName, Commit split, Commit curr,
                                        Commit given) {
        HashSet<String> allFileNames = given.getFileNames();
        allFileNames.addAll(curr.getFileNames());
        allFileNames.addAll(split.getFileNames());
        boolean mergeConflictExists = false;
        boolean noChange = true;
        for (String fileName : allFileNames) {
            String currentFileSHA = curr.getFileSHA(fileName);
            String splitFileSHA = split.getFileSHA(fileName);
            String givenFileSHA = given.getFileSHA(fileName);
            if (currentFileSHA == null) {
                if (givenFileSHA == null) { // 3
                    continue;
                }
                Blob givenBlob = readObject(join(BLOBS, givenFileSHA), Blob.class);
                if (splitFileSHA == null) { // 5
                    writeContents(join(CWD, fileName), (Object) givenBlob.getContents());
                    storeBlobByName(fileName, givenBlob);
                    noChange = false;
                } else if (splitFileSHA.equals(givenFileSHA)) { // 7
                    continue;
                } else { // 8
                    mergeConflict(fileName, "", givenBlob.getContents());
                    mergeConflictExists = true;
                    noChange = false;
                }
            } else if (givenFileSHA == null) {
                Blob currentBlob = readObject(join(BLOBS, currentFileSHA), Blob.class);
                if (splitFileSHA == null) { // 4
                    continue;
                } else if (splitFileSHA.equals(currentFileSHA)) { // 6
                    rm(fileName);
                    noChange = false;
                } else { // 8
                    mergeConflict(fileName, currentBlob.getContents(), "");
                    mergeConflictExists = true;
                    noChange = false;
                }
            } else {
                Blob currentBlob = readObject(join(BLOBS, currentFileSHA), Blob.class);
                Blob givenBlob = readObject(join(BLOBS, givenFileSHA), Blob.class);
                if (currentFileSHA.equals(givenFileSHA)) { // 3
                    continue;
                } else if ((splitFileSHA == null)) { // 8
                    mergeConflict(fileName, currentBlob.getContents(), givenBlob.getContents());
                    mergeConflictExists = true;
                    noChange = false;
                } else if (splitFileSHA.equals(currentFileSHA)) { // 1
                    writeContents(join(CWD, fileName), (Object) givenBlob.getContents());
                    storeBlobByName(fileName, givenBlob);
                    noChange = false;
                } else if (splitFileSHA.equals(givenFileSHA)) { // 2
                    continue;
                } else { // 8
                    mergeConflict(fileName, currentBlob.getContents(), givenBlob.getContents());
                    mergeConflictExists = true;
                    noChange = false;
                }
            }
        }
        if (noChange) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (mergeConflictExists) {
            System.out.println("Encountered a merge conflict.");
        }
        commitHelper("Merged " + branchName + " into " + readContentsAsString(CURRENT_BRANCH)
                + ".", curr, given);
    }

    /** Creates a Blob object given a name and file. */
    private static Blob makeBlob(String name, File file) {
        return new Blob(name, file);
    }

    /** Stores a given Blob in the staging area as a file named fileName. */
    private static void storeBlobByName(String fileName, Blob blob) {
        File file = join(STAGING_AREA, fileName);
        writeObject(file, blob);
    }

    /** Retrieves a given Blob stored in the file named fileName from STAGING_AREA. */
    private static Blob getBlobByName(String fileName) {
        File file = join(STAGING_AREA, fileName);
        if (file.exists()) {
            return readObject(file, Blob.class);
        }
        return null;
    }

    /** Removes fileName from the staging area. */
    private static void removeBlobByName(String fileName) {
        join(STAGING_AREA, fileName).delete();
    }

    /** Returns full ID given a 6-character string, if it exists. Else, null. */
    private static String returnFullID(String commitID) {
        List<String> allCommitIDs = plainFilenamesIn(COMMITS);
        if (!allCommitIDs.isEmpty()) {
            for (String id : allCommitIDs) {
                if (id.contains(commitID)) {
                    return id;
                }
            }
        }
        return null;
    }

    /** Returns a boolean depending on whether there is an untracked file in
     *  the CWD. */
    private static boolean untrackedFileExists() {
        Commit currentHeadCommit = readObject(HEAD, Commit.class);
        List<String> filesInCWD = plainFilenamesIn(CWD);
        if (filesInCWD != null) {
            for (String fileName : filesInCWD) {
                if ((currentHeadCommit.getFileSHA(fileName) == null
                        && !join(STAGING_AREA, fileName).exists())
                        || join(REMOVE, fileName).exists()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Removes files in CWD that are not present in the given commit to be
     *  checked out and restores all files of the commit in the CWD. */
    private static void removeFilesNotInCheckoutAndRetrieve(String commitID) {
        List<String> filesInCWD = plainFilenamesIn(CWD);
        Commit commit = readObject(join(COMMITS, commitID), Commit.class);
        HashSet<String> commitFiles = commit.getFileNames();
        for (String fileName : commitFiles) {
            Blob blobby = readObject(join(BLOBS, commit.getFileSHA(fileName)), Blob.class);
            writeContents(join(CWD, fileName), (Object) blobby.getContents());
        }
        for (String fileName : filesInCWD) {
            if (!commitFiles.contains(fileName)) {
                join(CWD, fileName).delete();
            }
        }
    }

    /** Clears STAGING_AREA. */
    private static void clearStaging() {
        List<String> inStaging = plainFilenamesIn(STAGING_AREA);
        if (!inStaging.isEmpty()) {
            for (String fileName : inStaging) {
                join(STAGING_AREA, fileName).delete();
            }
        }
        List<String> inRemove = plainFilenamesIn(REMOVE);
        if (!inRemove.isEmpty()) {
            for (String fileName : inRemove) {
                join(REMOVE, fileName).delete();
            }
        }
    }

    /** Creates a file with the expected contents from a merge conflict. Stages the file. */
    private static void mergeConflict(String name, Object currContents, Object givenContents) {
        File f = join(CWD, name);
        writeContents(f, "<<<<<<< HEAD\n", currContents, "=======\n", givenContents,
                ">>>>>>>\n");
        storeBlobByName(name, makeBlob(name, f));
    }

    /** Saves the given login information under the given remote name. */
    public static void addRemote(String remoteName, String remoteDir) {
        System.out.println("A remote with that name already exists.");
    }

    public static void rmRemote(String remoteName) {
        System.out.println("A remote with that name already exists.");
    }

    public static void push(String remoteName, String remoteBranch) {
    }

    public static void fetch(String remoteName, String remoteBranch) {
    }

    public static void pull(String remoteName, String remoteBranch) {
    }
}
