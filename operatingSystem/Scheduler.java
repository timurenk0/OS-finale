package operatingSystem;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
    private final Queue<Process> processQueue;
    private final CPUManager cpu;
    private final RAMManager ram;

    // Constructor
    public Scheduler(CPUManager cpu, RAMManager ram) {
        this.processQueue = new LinkedList<>();
        this.cpu = cpu;
        this.ram = ram;
    }

    // Getters
    public String getProcessQueue() {
        StringBuilder sb = new StringBuilder();

        for (Process p : processQueue) {
            sb.append(p.toString());
        }

        if (!processQueue.isEmpty()) {
            sb.setLength(sb.length() - 1); // remove last comma
        }

        return sb.toString();
    }

    // Methods
    public void addProcess(Process process) {
        if (process.getState() == Process.State.NEW) {
            process.setState(Process.State.READY);
            processQueue.add(process);
            System.out.println("[SCHEDULER] Process " + process.getId() + " is READY");
        } else {
            System.out.println("[ERROR_SCHEDULER] Failed to set process state. Process " + process.getId() + " state is not NEW");
        }
    }

    public void schedule() {
        for (Process p : processQueue) {
            if (p.getState() != Process.State.RUNNING) {
                new Thread(() -> {
                    try {
                        runProcess(p);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }
    }

    private void runProcess(Process process) throws InterruptedException {
        while (process.getState() == Process.State.READY || process.getState() == Process.State.RUNNING) {
            Page nextPage = findNextPage(process);

            if (nextPage == null) {
                // if all pages of a process are executed
                if (process.isAllPagesExecuted()) {
                    process.setState(Process.State.TERMINATED);
                    System.out.println("[SCHEDULER] Process " + process.getId() + " is TERMINATED");

                    ram.removeProcess(process.getId()); // kick process from RAM
                    processQueue.remove(process); // kick process from process queue
                    process.setState(Process.State.NEW); // set process state NEW so it can be executed again
                    process.clearExecutedPages(); // set all pages of the process to non-executed
                }
                break;
            }

            process.setState(Process.State.RUNNING);
            System.out.println("[SCHEDULER] Process " + process.getId() + " is RUNNING");

            Page calledPage = cpu.callPage(process.getId(), nextPage.getId());

            if (calledPage == null) {
                System.out.println("[ERROR_SCHEDULER] Failed to call a page. Retrying...");
                int retries = 0;
                final int MAX_RETRIES = 3;

                while (calledPage == null && retries < MAX_RETRIES) {
                    calledPage = ram.handlePageFault(process.getId(), nextPage.getId());
                    retries++;

                    if (calledPage == null) {
                        System.out.println("[ERROR_SCHEDULER] Retry number " + retries + " timed out");
                        Thread.sleep(100);
                    }
                }
                if (calledPage == null) {
                    System.out.println("[ERROR_SCHEDULER] Failed to call a page. Maximum number of retries reached (" + MAX_RETRIES + ")");
                    break;
                }
            }
            if (cpu.executePage(calledPage)) {
                process.setPageExecuted(calledPage.getId());
            } else {
                System.out.println("[ERROR_SCHEDULER] Failed to execute a page " + nextPage.getId());
                break;
            }
        }
    }

    private Page findNextPage(Process process) {
        for (Page page : process.getPages().values()) {
            // if there is an unexecuted page in RAM
            if (!process.getExecutedPages().contains(page.getId()) && !ram.getMemory().containsKey(page.getId())) {
                return page;
            }
        }
        System.out.println("[ERROR_SCHEDULER] Failed to find next page. No unexecuted pages for process " + process.getId() + " found in RAM");
        return null;
    }
}
