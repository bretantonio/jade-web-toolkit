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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

//#APIDOC_EXCLUDE_FILE

public final class CompilerUtils {
	
	public static void compileJavaSrc(String classPath, List<File> srcList, String dest) throws Exception {
		String[] javacCommand = new String[srcList.size() + 7];
		javacCommand[0] = "javac";
		javacCommand[1] = "-classpath";
		javacCommand[2] = classPath;        
		javacCommand[3] = "-d";
		javacCommand[4] = dest;
		javacCommand[5] = "-target";
		javacCommand[6] = "1.5";

		int i = 7;
		for (File f : srcList) {
			javacCommand[i++] = f.getAbsolutePath();            
		}
		internalCompile(javacCommand, 7); 
	}
    
	public static void setupClasspath(StringBuilder classPath, ClassLoader classLoader) throws URISyntaxException, IOException {

		ClassLoader scl = ClassLoader.getSystemClassLoader();        
		ClassLoader tcl = classLoader;
		do {
			if (tcl instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader)tcl).getURLs();
				if (urls == null) {
					urls = new URL[0];
				}
				for (URL url : urls) {
					if (url.getProtocol().startsWith("file")) {
						File file; 
						try { 
							file = new File(url.toURI().getPath()); 
						} catch (URISyntaxException urise) { 
							file = new File(url.getPath()); 
						} 

						if (file.exists()) { 
							classPath.append(file.getAbsolutePath()) 
							.append(System 
									.getProperty("path.separator")); 

							if (file.getName().endsWith(".jar")) { 
								addClasspathFromManifest(classPath, file); 
							}                         
						}     
					}
				}
			}
			tcl = tcl.getParent();
			if (null == tcl) {
				break;
			}
		} while(!tcl.equals(scl.getParent()));
	}

	private static void internalCompile(String[] args, int sourceFileIndex) throws Exception {
        Process p = null;
        String cmdArray[] = null;
        File tmpFile = null;
        try {
            if (isLongCommandLines(args) && sourceFileIndex >= 0) {
                PrintWriter out = null;
                tmpFile = FileUtils.createTempFile("wsdc-compiler", null);
                out = new PrintWriter(new FileWriter(tmpFile));
                for (int i = sourceFileIndex; i < args.length; i++) {
                    if (args[i].indexOf(" ") > -1) {
                        args[i] = args[i].replace(File.separatorChar, '/');
                        //
                        // javac gives an error if you use forward slashes
                        // with package-info.java.  Refer to:
                        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6198196
                        //
                        if (args[i].indexOf("package-info.java") > -1
                            && System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
                            out.println("\"" + args[i].replaceAll("/", "\\\\\\\\") + "\"");
                        } else {
                            out.println("\"" + args[i] + "\"");
                        }
                    } else {
                        out.println(args[i]);
                    }
                }
                out.flush();
                out.close();
                cmdArray = new String[sourceFileIndex + 1];
                System.arraycopy(args, 0, cmdArray, 0, sourceFileIndex);
                cmdArray[sourceFileIndex] = "@" + tmpFile;
            } else {
                cmdArray = new String[args.length];
                System.arraycopy(args, 0, cmdArray, 0, args.length);
            }
            
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
                for (int i = 0; i < cmdArray.length; i++) {
                    if (cmdArray[i].indexOf("package-info") == -1) {
                        cmdArray[i] = cmdArray[i].replace('\\', '/');
                    }
                }
            }
            
            p = Runtime.getRuntime().exec(cmdArray);
            if (p != null) {
            	// Start thread to monitor the compilation
    			Thread t = new CompilerProcessManager(p);
    			t.start();
            	
            	if (p.waitFor() != 0) {
            		throw new Exception("Compiling error (see log for more detail)");
            	}
            }
        } catch (SecurityException se) {
        	throw se;
        } catch (InterruptedException ie) {
            // ignore
        } catch (IOException ioe) {
        	throw ioe;
        } finally {
            if (tmpFile != null
                && tmpFile.exists()) {
                FileUtils.delete(tmpFile);
            }
        }
    }

    private static boolean isLongCommandLines(String args[]) {
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            strBuffer.append(args[i]);
        }
        return strBuffer.toString().length() > 4096 ? true : false;
    }
    
	private static void addClasspathFromManifest(StringBuilder classPath, File file) throws URISyntaxException, IOException {

		JarFile jar = new JarFile(file);
		Attributes attr = null;
		if (jar.getManifest() != null) {
			attr = jar.getManifest().getMainAttributes();
		}
		if (attr != null) {
			String cp = attr.getValue("Class-Path");
			while (cp != null) {
				String fileName = cp;
				int idx = fileName.indexOf(' ');
				if (idx != -1) {
					fileName = fileName.substring(0, idx);
					cp =  cp.substring(idx + 1).trim();
				} else {
					cp = null;
				}
				
				URI uri = new URI(fileName);

				File f2;
				if (uri.isAbsolute()) {
					f2 = new File(uri);
				} else {
					f2 = new File(file.getParentFile(), fileName);
				}
				if (f2.exists()) {
					classPath.append(f2.getAbsolutePath());
					classPath.append(System.getProperty("path.separator"));
				}
			}
		}         
	}
	
}
