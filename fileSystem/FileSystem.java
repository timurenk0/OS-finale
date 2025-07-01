package fileSystem;


import java.util.*;


public class FileSystem {
    private final FileSystemNode root;


    // Constructor
    public FileSystem() {
        root = new FileSystemNode("/", true, null);
    }


    // Methods
    private String normalizePath(String path) throws Exception {
        if (path == null || path.isEmpty()) return "/";

        if (!path.startsWith("/")) throw new Exception("Only absolute paths supported");

        String[] components = path.split("/");
        StringBuilder normalized = new StringBuilder("/");
        for (String comp : components) {
            if (!comp.isEmpty()) normalized.append(comp).append("/");
        }
        String result = normalized.toString();
        return result.equals("/") ? "/" : result.substring(0, result.length() - 1);
    }

    public FileSystemNode findNode(String path) throws Exception {
        path = normalizePath(path);
        if (path.equals("/")) return root;

        String[] components = path.split("/");
        FileSystemNode current = root;
        for (int i = 1; i < components.length; i++) {
            if (!current.isDirectory()) throw new Exception("Intermediate path is not a directory");
            current = current.getChildren().get(components[i]);

            if (current == null) throw new Exception("Path does not exist: " + path);
        }
        return current;
    }

    public FileSystemNode create(String name, String path, boolean isDir) throws Exception {
        FileSystemNode parent = findNode(path);
        if (!parent.isDirectory()) throw new Exception("Cannot create under non-directory");

        if (parent.getChildren().containsKey(name)) throw new Exception("Already exists");

        FileSystemNode newNode = new FileSystemNode(name, isDir, parent);
        parent.getChildren().put(name, newNode);
        return newNode;
    }

    public String delete(String name, String path) throws Exception {
        FileSystemNode parent = findNode(path);
        if (!parent.isDirectory()) return "Cannot delete non-directory";

        if (parent.getChildren().get(name).isDirectory() && !parent.getChildren().get(name).getChildren().isEmpty()) return "Cannot delete non-empty directories";

        parent.getChildren().remove(name);
        return "File deleted successfully";
    }

    public List<FileSystemNode> list(String path) throws Exception {
        FileSystemNode dir = findNode(path);
        if (!dir.isDirectory()) throw new Exception("Not a directory");

        return new ArrayList<>(dir.getChildren().values());
    }
}