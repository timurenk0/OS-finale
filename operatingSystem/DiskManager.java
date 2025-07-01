package operatingSystem;

import java.util.HashMap;
import java.util.Map;

public class DiskManager {
    private final Map<Integer, Process> storage;
    private final int totalSpace;
    private int occupiedSpace;

    // Constructor
    public DiskManager(int totalSpace) {
        this.storage = new HashMap<>();
        this.occupiedSpace = 2048; // assume OS is stored in disk
        if (totalSpace > this.occupiedSpace) {
            this.totalSpace = totalSpace;
        } else {
            System.out.println("[WARNING_HDD] Total space cannot be less than occupied space (2048 bytes). Automatically assigned 16384 bytes");
            this.totalSpace = 16384;
        }
    }

    // Getters
    public int getTotalSpace() { return totalSpace; }
    public int getOccupiedSpace() { return occupiedSpace; }
    public Process getProcessById(int id) { return storage.get(id); }
    public boolean hasSpace(int size) { return occupiedSpace + size <= totalSpace; }

    // Methods
    public boolean storeProcess(Process process) {
        // true process size is the total size of all pages
        int processSize = process.getPages().size() * Page.getPageSize();
        if (occupiedSpace + processSize > totalSpace) {
            System.out.println("[ERROR_HDD] Failed to store process. Not enough space");
            return false;
        }

        storage.put(process.getId(), process);
        occupiedSpace += processSize;
        System.out.println("[HDD] Process " + process.getId() + " stored successfully (" + processSize + " bytes)");
        return true;
    }

    public boolean removeProcess(int processId) {
        Process process = storage.remove(processId);
        if (process == null) {
            System.out.println("[ERROR_HDD] Failed to remove process. Process " + processId + " not found");
            return false;
        }

        int processSize = process.getPages().size() * Page.getPageSize();
        occupiedSpace -= processSize;
        System.out.println("[HDD] Process " + processId + " removed successfully ( -" + processSize + " bytes)");
        return true;
    }

    public boolean storePage(Page page) {
        if (occupiedSpace + Page.getPageSize() > totalSpace) {
            System.out.println("[ERROR_HDD] Failed to store page. Not enough space");
            return false;
        }

        Map<Integer, Page> pages = storage.get(page.getParentProcess().getId()).getPages(); // retrieve process from storage to check if it is stored in disk
        if (pages.containsKey(page.getId())) {
            System.out.println("[ERROR_HDD] Page " + page.getId() + " already exists");
            return false;
        }

        pages.put(page.getId(), page);
        occupiedSpace += Page.getPageSize();
        System.out.println("[HDD] Page " + page.getId() + " stored successfully");
        return true;
    }

    public Page getPageById(int processId, int pageId) {
        Map<Integer, Page> pages = storage.get(processId).getPages();
        if (pages == null) {
            System.out.println("[ERROR_HDD] Failed to get process. Process " + processId + " not found");
            return null;
        }

        Page page = pages.get(pageId);
        if (page == null) {
            System.out.println("[ERROR_HDD] Failed to get page. Page " + pageId + " not found");
            return null;
        }

        return page;
    }

}
