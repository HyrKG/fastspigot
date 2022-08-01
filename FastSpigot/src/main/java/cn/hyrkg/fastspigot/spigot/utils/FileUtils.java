package cn.hyrkg.fastspigot.spigot.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FileUtils {

    public static void saveResources(JavaPlugin plugin, File path, boolean replace) {
        try {
            plugin.saveResource(path.getAbsolutePath().substring(plugin.getDataFolder().getAbsolutePath().length() + 1), replace);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Resource Can not found : " + path.getAbsolutePath());
        }
    }


    /**
     * Read Field
     *
     * @param filePath The path of file
     * @return line by line of file
     */
    public static List<String> readFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return readFile(file);
        }
        return null;
    }

    /**
     * Read Field
     *
     * @param file File
     * @return line by line of file
     */
    public static List<String> readFile(File file) {
        List<String> returned = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                returned.add(line);
            }
            br.close();
            return returned;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Copy file
     *
     * @param fromFile The file from.
     * @param toFile   The fild to.
     */
    public static void copyFileWithFileChannel(File fromFile, File toFile) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannelInput = null;
        FileChannel fileChannelOutput = null;
        try {
            fileInputStream = new FileInputStream(fromFile);
            fileOutputStream = new FileOutputStream(toFile);
            fileChannelInput = fileInputStream.getChannel();
            fileChannelOutput = fileOutputStream.getChannel();
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (fileChannelInput != null)
                    fileChannelInput.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (fileChannelOutput != null)
                    fileChannelOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copy dir.
     *
     * @param fromFile The Dir from.
     * @param toFile   The Dir to.
     */
    public static void copyDirWithFileChannel(File fromFile, File toFile) {
        if (!fromFile.isDirectory()) {
            copyFileWithFileChannel(fromFile, toFile);
            return;
        }

        if (!toFile.exists())
            toFile.mkdir();

        File[] fs = fromFile.listFiles();

        for (File f : fs) {
            if (f.isFile()) {
                copyFileWithFileChannel(f, new File(toFile, f.getName()));
            } else if (f.isDirectory()) {
                copyDirWithFileChannel(f, new File(toFile, f.getName()));
            }
        }
    }

    /**
     * Delete Dir
     */
    public static void deleteDir(File dir) {
        if (!dir.isDirectory()) {
            dir.delete();
            return;
        }
        File[] fs = dir.listFiles();
        for (File f : fs) {
            if (f.isDirectory())
                deleteDir(f);
            else if (!f.delete())
                f.delete();
        }
        dir.delete();
    }

    /**
     * Read Parmeters Of A String.
     * <p/>
     * Format: -parm1_cmd parm1 -parm2_cmd parm2 <BR/>
     * Warm: THIS METHOD WILL READ EVERY FIELD CONTAIN "-" AND ITS FOLLWER
     * <p/>
     * eg. -parm1_cmd parm1 --parm2_cmd -parm2 <BR/>
     * return: <BR/>
     * - parm[0] -> cmd = parm1_cmd, parm = parm1 <BR/>
     * - parm[1] -> cmd = -parm2_cmd, parm = -parm2<BR/>
     *
     * @return A Arraylist of Parmeters whitch contains parm_cmd and parm.
     */
    public static ArrayList<Parameter> readStrParameters(String str) {
        ArrayList<Parameter> parms = new ArrayList<>();

        String[] args = str.split("\\s+");

        ArrayList<Integer> cmdPos = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].substring(0, 1).equalsIgnoreCase("-")) {
                args[i] = args[i].substring(1);
                cmdPos.add(i++);
                continue;
            }
        }
        for (int i : cmdPos)
            parms.add(new Parameter(args[i], (i + 1 < args.length ? args[i + 1] : "")));

        return parms;
    }

    public static class Parameter {
        public final String cmd;
        public final String parm;

        public Parameter(String str1, String str2) {
            cmd = str1;
            parm = str2;
        }
    }
}
