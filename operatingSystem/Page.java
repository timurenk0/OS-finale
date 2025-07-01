package operatingSystem;

import java.util.Random;

public class Page {
    private static final int PAGE_SIZE = 4; // bytes
    private final int EXEC_TIME; // ms
    private final int id;
    private final Process parentProcess;
    private final byte[] payload; // not real data, exists simply for the sake of simulation
    private boolean inRAM;

    // Constructor
    public Page(int id, byte[] payload, Process parentProcess) {
        this.EXEC_TIME = new Random().nextInt(16) + 2;
        this.id = id;
        this.parentProcess = parentProcess;
        this.payload = payload;
        this.inRAM = false;
    }

    // Getters
    public static int getPageSize() { return PAGE_SIZE; }
    public int getExecTime() { return this.EXEC_TIME; }
    public int getId() { return id; }
    public byte[] getPayload() { return payload; }
    public Process getParentProcess() { return parentProcess; }
    public boolean isInRAM() { return inRAM; }

    // Setters
    public void setInRam(boolean inRAM) { this.inRAM = inRAM; }
}
