/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package USBThermometerLib;

import java.io.File;

/**
 *
 * @author pawelkn
 */
public class LibraryLoader {

    public static String LIBRARY_NAME = null;

    public static void load() {
        boolean loaded = false;

        String libraryPaths = System.getProperty("java.library.path");
        String[] libraryPath = null;

        String os = System.getProperty("os.name");
        if ( os.toLowerCase().contains("win") ) {
            libraryPath = libraryPaths.split(";");
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            libraryPath = libraryPaths.split(":");
        }

        if( ( libraryPath != null ) && ( libraryPath.length > 0 ) ) {
            for(String path : libraryPath) {
                if( tryLoad(path) ) {
                    loaded = true;
                    break;
                }
            }
        }
        if( !loaded ) {
            System.err.println("Unable to load native library " + LIBRARY_NAME);
            System.exit(1);
        }
    }

    private static boolean tryLoad(String path) {
        boolean loaded = false;
        try {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("win")) {
                path = new File(path).getAbsolutePath() + "\\";
                if ("64".equals(System.getProperty("sun.arch.data.model", "?"))) {
                    System.load(path + LIBRARY_NAME + "-win-x64.dll");
                    System.out.println("Load library " + path + LIBRARY_NAME + "-win-x64.dll");
                } else {
                    System.load(path + LIBRARY_NAME + "-win-i386.dll");
                    System.out.println("Load library " + path + LIBRARY_NAME + "-win-i386.dll");
                }
            } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
                path = new File(path).getAbsolutePath() + "/";
                if ("64".equals(System.getProperty("sun.arch.data.model", "?"))) {
                    System.load(path + LIBRARY_NAME + "-linux-x64.so");
                    System.out.println("Load library " + path + LIBRARY_NAME + "-linux-x64.so");
                } else {
                    System.load(path + LIBRARY_NAME + "-linux-i386.so");
                    System.out.println("Load library " + path + LIBRARY_NAME + "-linux-i386.so");
                }
            }
            loaded = true;
        } catch (UnsatisfiedLinkError ex) {
        }
        return loaded;
    }
}
