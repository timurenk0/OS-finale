import GUI.*;

import fileSystem.FileSystem;
import operatingSystem.*;


public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        DiskManager disk = new DiskManager(16384);
        RAMManager ram = new RAMManager(2048, disk);
        CPUManager cpu = new CPUManager(ram, true);
        Scheduler scheduler = new Scheduler(cpu, ram);
        FileSystemGUI fsg = new FileSystemGUI(fs, disk, scheduler);
    }
}