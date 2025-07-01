package fileSystem;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSystemNode {
    private final String name;
    private final int id;
    private static final AtomicInteger counter = new AtomicInteger(1); // id counter so id is assigned automatically upon node creation
    private final boolean isDirectory;
    private final FileSystemNode parent;
    private Map<String, FileSystemNode> children;
    private final int size;
    private final long creationTime;


    // Constructor
    public FileSystemNode(String name, boolean isDirectory, FileSystemNode parent) {
        this.name = name;
        this.id = !isDirectory ? counter.getAndIncrement() : -1;
        this.isDirectory = isDirectory;
        this.parent = parent;
        this.children = isDirectory ? new HashMap<>() : null;
        this.size = isDirectory ? 0 : 300;
        this.creationTime = System.currentTimeMillis();
    }


    // Getters
    public boolean isDirectory() { return isDirectory; }
    public FileSystemNode getParent() { return parent; }
    public Map<String, FileSystemNode> getChildren() { return children; }
    public String getName() { return name; }
    public int getId() { return id; }
    public int getSize() { return size; }
    public long getCreationTime() { return creationTime; }
}