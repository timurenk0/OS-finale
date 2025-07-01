package operatingSystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class RAMManger {
    private final Map<Integer, Page> memory;
    private final Queue<Page> pageQueue;
    private final int totalSpace;
    private int occupiedSpace;
    private final DiskManager disk;

    // Constructor
    public RAMManger(int totalSpace, DiskManager disk) {
        this.memory = new HashMap<>();
        this.pageQueue = new LinkedList<>();
        this.occupiedSpace = 512; // assume OS is occupying RAM
        if (totalSpace > occupiedSpace) {
            this.totalSpace = totalSpace;
        } else {
            System.out.println("Total space cannot be less than occupied space (512 bytes). Automatically assigned 2048 bytes");
            this.totalSpace = 2048;
        }
        this.disk = disk;
    }

    // Getters
    public int getTotalSpace() {
        return totalSpace;
    }

    public int getOccupiedSpace() {
        return occupiedSpace;
    }

    // Methods
    public boolean addPage(Page page) {
        if (occupiedSpace + Page.getPageSize() > totalSpace) {
            if (!kickPage()) {
                System.out.println("[ERROR_RAM] Failed to kick page to make space for page " + page.getId());
                return false;
            }
        }

        if (memory.containsKey(page.getId())) {
            System.out.println("[RAM] Page " + page.getId() + " is already in the memory");
            return true;
        }

        try {
            Thread.sleep(page.getExecTime()); // simulate waiting time for RAM access
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        memory.put(page.getId(), page);
        pageQueue.add(page);
        occupiedSpace += Page.getPageSize();
        page.setInRam(true);
        System.out.println("[RAM] Page " + page.getId() + " added successfully (" + Page.getPageSize() + " bytes in " + page.getExecTime() + "ms)");
        return true;
    }

    public boolean kickPage() {
        if (pageQueue.isEmpty()) {
            System.out.println("[ERROR_RAM] No page to kick");
            return false;
        }

        Page kickedPage = pageQueue.poll();

        // Swap page to HDD
        if (!disk.storePage(kickedPage)) {
            System.out.println("[ERROR_RAM] Failed to swap page " + kickedPage.getId());
            return false;
        }

        memory.remove(kickedPage.getId());
        occupiedSpace -= Page.getPageSize();
        kickedPage.setInRam(false);
        System.out.println("[RAM] Page " + kickedPage.getId() + " kicked successfully");
        return true;
    }

    public Page handlePageFault(int processId, int pageId) {
        System.out.println("[RAM] Handling page fault for page " + pageId);
        Page page = memory.get(pageId);

        if (page != null) {
            System.out.println("[RAM] Page " + pageId + " is already in the RAM");
            return page;
        }

        page = disk.getPageById(processId, pageId);

        if (page == null) {
            System.out.println("[ERROR_RAM] Page fault: Page " + pageId + " not found on disk");
            return null;
        }

        if (!addPage(page)) {
            System.out.println("[ERROR_RAM] Page fault: Failed to add page " + page.getId() + " to the RAM");
            return null;
        }

        System.out.println("[RAM] Page " + pageId + " loaded into RAM successfully");
        return page;
    }

    public boolean removeProcess(int processId) {
        int pagesRemoved = 0;
        Map<Integer, Page> pagesToRemove = new HashMap<>();

        for (Map.Entry<Integer, Page> entry : memory.entrySet()) {
            Page page = entry.getValue();
            if (page.getParentProcess().getId() == processId) {
                pagesToRemove.put(entry.getKey(), page);
            }
        }

        for (Map.Entry<Integer, Page> entry : pagesToRemove.entrySet()) {
            int pageId = entry.getKey();
            Page page = entry.getValue();
            memory.remove(pageId);
            if (pageQueue.remove(page)) {
                occupiedSpace -= Page.getPageSize();
                page.setInRam(false);
                pagesRemoved++;
            }
        }

        if (pagesRemoved == 0) {
            System.out.println("[ERROR_RAM] Failed to remove process. No pages found");
        } else {
            System.out.println("[RAM] Process " + processId + " removed successfully (-" + pagesRemoved*Page.getPageSize() + " bytes)");
        }
        return true;
    }
}
