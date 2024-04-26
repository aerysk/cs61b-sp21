package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Emily Nguyen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *      init - Creates a new Gitlet version control system in the current directory.
     *      add [fileName] - Adds a file called fileName to the staging area.
     *      commit [message] - Commits all files in the staging area.
     *      log - Starting at the current head commit, displays information about each commit
     *              backwards along the commit tree until the initial commit.
     *      checkout -- [fileName] - Takes the version of the file as it exists in the head
     *              commit and puts it in the working directory.
     *      checkout [commitID] -- [fileName] - takes the version of the file as it exists in
     *              the commit with the given id and puts it in the working directory.
     */
    
    public static void main(String[] args) {
        String firstArg;
        try {
            firstArg = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please enter a command.");
            return;
        }
        String secondArg = null;
        String thirdArg = null;
        String fourthArg = null;
        int commandArgs = args.length - 1;
        if (commandArgs > 0) {
            secondArg = args[1];
        }
        if (commandArgs > 1) {
            thirdArg = args[2];
        }
        if (commandArgs > 2) {
            fourthArg = args[3];
        }
        if (firstArg.equals("init")) {
            if (commandArgs > 0) {
                System.out.println("Incorrect operands.");
            }
            Repository.init();
            return;
        }
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else {
            switch (firstArg) {
                case "add":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.add(secondArg);
                    break;
                case "commit":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.commit(secondArg);
                    break;
                case "rm":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.rm(secondArg);
                    break;
                case "log":
                    if (commandArgs != 0) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.log();
                    break;
                case "global-log":
                    if (commandArgs != 0) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.globalLog();
                    break;
                case "find":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.find(secondArg);
                    break;
                case "status":
                    Repository.status();
                    break;
                case "checkout":
                    if (commandArgs == 1) {
                        Repository.checkoutWithBranch(secondArg);
                    } else if (commandArgs == 2) {
                        if (secondArg.equals("--")) {
                            Repository.checkoutWithFile(thirdArg);
                        } else {
                            System.out.println("Incorrect operands.");
                        }
                    } else if (commandArgs == 3) {
                        if (thirdArg.equals("--")) {
                            Repository.checkoutWithCommit(secondArg, fourthArg);
                        } else {
                            System.out.println("Incorrect operands.");
                        }
                    }
                    break;
                case "branch":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.branch(secondArg);
                    break;
                case "rm-branch":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.rmBranch(secondArg);
                    break;
                case "reset":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.reset(secondArg);
                    break;
                case "merge":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.merge(secondArg);
                    break;
                case "add-remote":
                    if (commandArgs != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.addRemote(secondArg, thirdArg);
                    break;
                case "rm-remote":
                    if (commandArgs != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.rmRemote(secondArg);
                    break;
                case "push":
                    if (commandArgs != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.push(secondArg, thirdArg);
                    break;
                case "fetch":
                    if (commandArgs != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.fetch(secondArg, thirdArg);
                    break;
                case "pull":
                    if (commandArgs != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.pull(secondArg, thirdArg);
                    break;
                default:
                    System.out.println("No command with that name exists.");
            }
        }
    }
}
