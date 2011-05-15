/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package jade.webservice.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//#APIDOC_EXCLUDE_FILE

public final class FileUtils {
	
    private static final int RETRY_SLEEP_MILLIS = 10;
    private static File defaultTempDir;
    
    private static synchronized File getDefaultTempDir() {
        if (defaultTempDir != null
            && defaultTempDir.exists()) {
            return defaultTempDir;
        }
        
        String s = null;
        try {
            s = System.getProperty(FileUtils.class.getName() + ".TempDirectory");
        } catch (SecurityException e) {
            //Ignorable, we'll use the default
        }
        if (s == null) {
            int x = (int)(Math.random() * 1000000);
            s = System.getProperty("java.io.tmpdir");
            File f = new File(s, "wsdc-tmp-" + x);
            while (!f.mkdir()) {
                x = (int)(Math.random() * 1000000);
                f = new File(s, "wsdc-tmp-" + x);
            }
            defaultTempDir = f;
            Thread hook = new Thread() {
                @Override
                public void run() {
                    removeDir(defaultTempDir);
                }
            };
            Runtime.getRuntime().addShutdownHook(hook);            
        } else {
            //assume someone outside of us will manage the directory
            File f = new File(s);
            f.mkdirs();
            defaultTempDir = f;
        }
        return defaultTempDir;
    }

    public static void removeDir(File d) {
        String[] list = d.list();
        if (list == null) {
            list = new String[0];
        }
        for (int i = 0; i < list.length; i++) {
            String s = list[i];
            File f = new File(d, s);
            if (f.isDirectory()) {
                removeDir(f);
            } else {
                delete(f);
            }
        }
        delete(d);
    }

    public static void delete(File f) {
        if (!f.delete()) {
            if (isWindows()) {
                System.gc();
            }
            try {
                Thread.sleep(RETRY_SLEEP_MILLIS);
            } catch (InterruptedException ex) {
                // Ignore Exception
            }
            if (!f.delete()) {
                f.deleteOnExit();
            }
        }
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        return osName.indexOf("windows") > -1;
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, null, false);
    }
    
    public static File createTempFile(String prefix, String suffix, File parentDir,
                               boolean deleteOnExit) throws IOException {
        File result = null;
        File parent = (parentDir == null)
            ? getDefaultTempDir()
            : parentDir;
            
        if (suffix == null) {
            suffix = ".tmp";
        }
        if (prefix == null) {
            prefix = "wsdc";
        } else if (prefix.length() < 3) {
            prefix = prefix + "wsdc";
        }
        result = File.createTempFile(prefix, suffix, parent);

        //if parentDir is null, we're in our default dir
        //which will get completely wiped on exit from our exit
        //hook.  No need to set deleteOnExit() which leaks memory.
        if (deleteOnExit && parentDir != null) {
            result.deleteOnExit();
        }
        return result;
    }
    
    public static List<File> getFilesRecurse(File dir, final String pattern) {
        return getFilesRecurse(dir, pattern, null);
    }

    public static List<File> getFilesRecurse(File dir, final String pattern, File exclude) {
        return getFilesRecurse(dir, Pattern.compile(pattern), exclude, true, new ArrayList<File>());    
    }
    private static List<File> getFilesRecurse(File dir, 
                                              Pattern pattern,
                                              File exclude, boolean rec,
                                              List<File> fileList) {
        for (File file : dir.listFiles()) {
            if (file.equals(exclude)) {
                continue;
            }
            if (file.isDirectory() && rec) {
                getFilesRecurse(file, pattern, exclude, rec, fileList);
            } else {
                Matcher m = pattern.matcher(file.getName());
                if (m.matches()) {
                    fileList.add(file);                                
                }
            }
        }
        return fileList;
    }

}
