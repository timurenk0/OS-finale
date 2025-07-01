package operatingSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED }

    private final int id;
    private final int size;
    private State state;
    private final HashMap<Integer, Page> pages;
    private final Set<Integer> executedPages; // needed for RAM management

    // Constructor
    public Process(int id, int size) {
        this.id = id;
        this.size = size;
        this.state = State.NEW;
        this.pages = new HashMap<>();
        this.executedPages = new HashSet<>();
        generatePages();
    }

    // Getters
    public int getId() { return id; }
    public int getSize() { return size; }
    public State getState() { return state; }
    public HashMap<Integer, Page> getPages() { return pages; }
    public Set<Integer> getExecutedPages() { return executedPages; }
    public boolean isAllPagesExecuted() { return executedPages.size() == pages.size(); }

    // Setters
    public void setState(State state) { this.state = state; }
    public void setPageExecuted(int pageId) { executedPages.add(pageId); }
    public void clearExecutedPages() { executedPages.clear(); }

    // Methods
    private void generatePages() {
        Random random = new Random();
        int numOfPages = (int) Math.ceil((double) size / Page.getPageSize());

        for (int i = 0; i < numOfPages; i++) {
            String payloadString = String.format("0x%02x", random.nextInt(256)); // enerates random payload for each page
            byte[] payload = payloadString.getBytes();

            pages.put(id*10000+i+1, new Page(id*10000+i+1, payload, this));
        }
        System.out.println("[PROCESS] Created process " + id + " with " + numOfPages + " pages (" + size + " bytes)");
    }


    // Helper
    @Override
    public String toString() {
        return "Process | PID " + id + " | STATE <" + state + "> | PAGES " + pages.size() + "\n";
    }
}
