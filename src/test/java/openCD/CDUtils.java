package openCD;

import java.io.File;  
import java.io.FileWriter;  
import java.util.ArrayList;  
import javax.swing.JOptionPane;  
import javax.swing.filechooser.FileSystemView;  
public class CDUtils {  
    private CDUtils() {  
    }      
    public static void open(String drive) {  
        try {  
            File file = File.createTempFile("realhowto", ".vbs");  
            file.deleteOnExit();  
            FileWriter fw = new FileWriter(file);
  
            String vbs = "Set wmp = CreateObject(\"WMPlayer.OCX\") \n"  
                    + "Set cd = wmp.cdromCollection.getByDriveSpecifier(\""  
                    + drive + "\") \n cd.Eject";  
  
            fw.write(vbs);  
            fw.close();  
  
            Runtime.getRuntime().exec("wscript " + file.getPath()).waitFor();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }   
    public static void close(String drive) {  
        try {  
            File file = File.createTempFile("realhowto", ".vbs");  
            file.deleteOnExit();  
            FileWriter fw = new FileWriter(file);         
            String vbs = "Set wmp = CreateObject(\"WMPlayer.OCX\") \n"  
                    + "Set cd = wmp.cdromCollection.getByDriveSpecifier(\""  
                    + drive + "\") \n cd.Eject \n cd.Eject ";  
  
            fw.write(vbs);  
            fw.close();  
            Runtime.getRuntime().exec("wscript " + file.getPath()).waitFor();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public static ArrayList<File> findCDWin32() {  
        FileSystemView fsv = FileSystemView.getFileSystemView();  
  
        File[] roots = fsv.getRoots();  
        if (roots.length == 1) {  
            roots = roots[0].listFiles()[0].listFiles();  
        } else {  
            System.out.println("I guess you're not on Windows");  
            return null;  
        }  
  
        ArrayList<File> foundDrives = new ArrayList<File>();  
        for (int i = 0; i < roots.length; i++) {  
            if (fsv.isDrive(roots[i])) {  
                if (fsv.getSystemTypeDescription(roots[i]).indexOf("CD") != -1) {  
                    foundDrives.add(roots[i]);  
                }  
            }  
        }  
  
        return foundDrives;  
    }  
      
    public static void main(String[] args) {  
  
        String cdDrive = "";  
          
        if(findCDWin32().size() > 0) {  
            File file = findCDWin32().toArray(new File[0])[0];  
            cdDrive = file.getPath();  
        } else {
            System.out.println("该电脑内有CD驱动器");
            return;  
        }  
        JOptionPane.showConfirmDialog((java.awt.Component) null, "Press OK to open CD", "CDUtils",   
                JOptionPane.DEFAULT_OPTION);
  
        CDUtils.open(cdDrive);  
    }  
  
}  
