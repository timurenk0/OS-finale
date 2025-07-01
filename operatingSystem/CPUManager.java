package operatingSystem;

public class CPUManager {
    private final boolean useLock;
    private final MySemaphore lock;
    private final RAMManager ram;
    private int sharedResource;

    // Constructor
    public CPUManager(RAMManager ram, boolean useLock) {
        this.useLock = useLock;
        this.lock = new MySemaphore();
        this.ram = ram;
        this.sharedResource = 0;
    }

    // Getters
    public int getSharedResource() { return sharedResource; }

    // Setters
    public void resetSharedResource() { sharedResource = 0; }

    // Methods
    public Page callPage(int processId, int pageId) {
        if (useLock) {
            try {
                lock.acquire();
                Page page = ram.handlePageFault(processId, pageId);

                if (page == null) {
                    System.out.println("[ERROR_CPU] Failed to fetch page. Page " + pageId + " not found");
                    return null;
                }

                System.out.println("[CPU] Page " + pageId + " fetched successfully");
                return page;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[ERROR_CPU] Interrupted while calling page " + pageId);
                return null;
            } finally {
                lock.release();
            }
        } else {
            Page page = ram.handlePageFault(processId, pageId);
            if (page == null) {
                System.out.println("[ERROR_CPU] Failed to fetch page. Page " + pageId + " not found");
                return null;
            }

            System.out.println("[CPU] Page " + pageId + " fetched successfully");
            return page;
        }
    }

    public boolean executePage(Page page) {
        if (page == null) {
            System.out.println("[ERROR_CPU] Failed to execute page. Page is null");
            return false;
        }

        if (useLock) {
            try {
                lock.acquire();
                return executeInstructions(page);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[ERROR_CPU] Interrupted while executing page " + page.getId());
                return false;
            } finally {
                lock.release();
            }
        } else {
            return executeInstructions(page);
        }
    }

    private boolean executeInstructions(Page page) {
        Process process = page.getParentProcess();

        try {
            Thread.sleep(page.getExecTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERROR_CPU] Interrupted while executing page " + page.getId());
            return false;
        }

        sharedResource++;
        System.out.println("[CPU] Page " + page.getId() + "executed successfully. Shared resource: " + sharedResource);

        process.setPageExecuted(page.getId());

        return true;
    }
}
