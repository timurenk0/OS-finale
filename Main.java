import GUI.*;

import fileSystem.FileSystem;
import operatingSystem.*;
import operatingSystem.Process;


public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        DiskManager disk = new DiskManager(16384);
        RAMManager ram = new RAMManager(2048, disk);
        CPUManager cpu = new CPUManager(ram, true);
        Scheduler scheduler = new Scheduler(cpu, ram);

        FileSystemGUI fsg = new FileSystemGUI(fs, disk, scheduler);
        OperatingSystemGUI osg = new OperatingSystemGUI(fs, disk, ram, cpu, scheduler);


        // COMMENT CODE ABOVE AND UNCOMMENT CODE BELOW TO TEST CPU WITHOUT SEMAPHORE LOCK (RACE CONDITION)
//        FileSystem fs = new FileSystem();
//        DiskManager disk = new DiskManager(16384);
//        RAMManager ram = new RAMManager(2048, disk);
//        CPUManager cpu = new CPUManager(ram, false);
//        Scheduler scheduler = new Scheduler(cpu, ram);
//
//        for (int i = 1; i < 4; i++) {
//            Process p = new Process(i, 300); // 75 pages (4 bytes each)
//            disk.storeProcess(p);
//            scheduler.addProcess(p);
//        }
//
//        scheduler.schedule(); // 75 * 3 = 225 (correct answer)
    }
}