package io.github.manavpreet1809.sleeptracker.nights;

public class WorkspaceFullException extends RuntimeException {

    public WorkspaceFullException(int limit) {
        super("A workspace can hold at most " + limit + " nights");
    }
}
