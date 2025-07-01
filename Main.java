import operatingSystem.*;
import operatingSystem.Process;

public class Main {
    public static void main(String[] args) {
        Process p = new Process(1, 250);

        DiskManager disk = new DiskManager(1024);
        System.out.println(disk.getOccupiedSpace() + " / " + disk.getTotalSpace());
        disk.storeProcess(p);

        System.out.println(disk.getProcessById(p.getId()));
        System.out.println(disk.getOccupiedSpace() + " / " + disk.getTotalSpace());
    }
}
