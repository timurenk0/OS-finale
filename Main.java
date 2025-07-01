import operatingSystem.*;
import operatingSystem.Process;

public class Main {
    public static void main(String[] args) {
        DiskManager disk = new DiskManager(1024);
        RAMManager ram = new RAMManager(256, disk);
        CPUManager cpu = new CPUManager(ram,true);

        System.out.println(disk.getOccupiedSpace() + " / " + disk.getTotalSpace());
        System.out.println(ram.getOccupiedSpace() + " / " + ram.getTotalSpace());


        for (int i = 1; i < 4; i++) {
            Process p = new Process(i, 200);
            disk.storeProcess(p);

            for (Page page : p.getPages().values()) {
                ram.addPage(page);
                cpu.executePage(page);
            }
        }




        System.out.println(disk.getOccupiedSpace() + " / " + disk.getTotalSpace());
        System.out.println(ram.getOccupiedSpace() + " / " + ram.getTotalSpace());
    }
}
