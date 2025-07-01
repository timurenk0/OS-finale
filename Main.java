import operatingSystem.*;
import operatingSystem.Process;

public class Main {
    public static void main(String[] args) {
        Process p = new Process(1, 250);
        DiskManager disk = new DiskManager(1024);
        RAMManger ram = new RAMManger(256, disk);

        System.out.println(disk.getOccupiedSpace() + " / " + disk.getTotalSpace());
        System.out.println(ram.getOccupiedSpace() + " / " + ram.getTotalSpace());

        disk.storeProcess(p);

        for (Page page : p.getPages().values()) {
            ram.addPage(page);
        }

        System.out.println(disk.getProcessById(p.getId()));
        System.out.println(disk.getOccupiedSpace() + " / " + disk.getTotalSpace());
        System.out.println(ram.getOccupiedSpace() + " / " + ram.getTotalSpace());
    }
}
