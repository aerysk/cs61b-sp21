package gitlet;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import static gitlet.Utils.*;

public class Branch implements Serializable {

    private String branchName;
    /** Linked list of all commits in the branch, starting with most recent. */
    private LinkedList<Commit> branchLL;

    public Branch(String name, Branch branchFrom) {
        branchName = name;
        if (branchFrom == null) {
            branchLL = new LinkedList<>();
        } else {
            branchLL = branchFrom.getCommitsLL();
        }
    }

    public void newCommit(Commit commit) {
        branchLL.addFirst(commit);
    }

    public LinkedList<Commit> getCommitsLL() {
        return branchLL;
    }


    public Commit getHeadCommit() {
        return branchLL.getFirst();
    }

    public void removeHeadCommit() {
        branchLL.removeFirst();
    }

    /** Returns the LCA commit given the name of the branch to compare with.
     *  If no LCA found, returns initial commit. */
    public Commit getLCA(String compareWith) {
        LinkedList<Commit> queue = new LinkedList<>();
        String initialCommitSHA = readContentsAsString(join(Repository.GITLET_DIR,
                "initial commit"));
        Commit initialCommit = readObject(join(Repository.COMMITS, initialCommitSHA),
                Commit.class);
        Branch given = readObject(join(Repository.BRANCHES, compareWith), Branch.class);
        queue.add(given.getHeadCommit());
        while (!queue.isEmpty()) {
            Commit c = queue.removeFirst();
            Commit parent1 = c.getParent1();
            Commit parent2 = c.getParent2();
            if (allCommitSHAs().contains(c.getSHA())) {
                return c;
            }
            if (parent1 != null) {
                queue.addLast(parent1);
            }
            if (parent2 != null) {
                queue.addLast(parent2);
            }
        }
        return initialCommit;
    }

    private HashSet<String> allCommitSHAs() {
        HashSet<String> allSHAs = new HashSet<>();
        LinkedList<Commit> queue = new LinkedList<>();
        queue.add(getHeadCommit());
        while (!queue.isEmpty()) {
            Commit c = queue.removeFirst();
            allSHAs.add(c.getSHA());
            Commit parent1 = c.getParent1();
            Commit parent2 = c.getParent2();
            if (parent1 != null) {
                queue.addLast(parent1);
            }
            if (parent2 != null) {
                queue.addLast(parent2);
            }
        }
        return allSHAs;
    }
}
