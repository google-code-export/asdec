/*
 *  Copyright (C) 2010-2013 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.configuration;

import com.jpexs.decompiler.flash.ApplicationInfo;
import com.jpexs.helpers.Helper;
import com.jpexs.proxy.Replacement;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Configuration {

    private static final String CONFIG_NAME = "config.bin";
    private static final String REPLACEMENTS_NAME = "replacements.cfg";
    private static final File unspecifiedFile = new File("unspecified");
    private static File directory = unspecifiedFile;

    /**
     * List of replacements
     */
    private static List<Replacement> replacements = new ArrayList<>();

    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> decompile = null;
    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> parallelSpeedUp = null;
    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> autoDeobfuscate = null;
    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> cacheOnDisk = null;
    @ConfigurationDefaultBoolean(false)
    public static final ConfigurationItem<Boolean> internalFlashViewer = null;
    @ConfigurationDefaultBoolean(false)
    public static final ConfigurationItem<Boolean> gotoMainClassOnStartup = null;
    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> deobfuscateUsePrevTagOnly = null;
    @ConfigurationDefaultBoolean(false)
    public static final ConfigurationItem<Boolean> offeredAssociation = null;
    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> removeNops = null;
    @ConfigurationDefaultBoolean(false)
    public static final ConfigurationItem<Boolean> showHexOnlyButton = null;

    /**
     * Debug mode = throwing an error when comparing original file and
     * recompiled
     */
    @ConfigurationDefaultBoolean(false)
    @ConfigurationDescription("Debug mode = throwing an error when comparing original file and recompiled")
    public static final ConfigurationItem<Boolean> debugMode = null;
    /**
     * Turn off reading unsafe tags (tags which can cause problems with
     * recompiling)
     */
    @ConfigurationDefaultBoolean(false)
    @ConfigurationDescription("Turn off reading unsafe tags (tags which can cause problems with recompiling)")
    public static final ConfigurationItem<Boolean> disableDangerous = null;
    /**
     * Turn off resolving constants in ActionScript 2
     */
    @ConfigurationDefaultBoolean(true)
    @ConfigurationDescription("Turn off resolving constants in ActionScript 2")
    public static final ConfigurationItem<Boolean> resolveConstants = null;
    /**
     * Limit of code subs (for obfuscated code)
     */
    @ConfigurationDefaultInt(500)
    @ConfigurationDescription("Limit of code subs (for obfuscated code)")
    public static final ConfigurationItem<Integer> sublimiter = null;
    /**
     * Total export timeout in seconds
     */
    @ConfigurationDefaultInt(30 * 60)
    @ConfigurationDescription("Total export timeout in seconds")
    public static final ConfigurationItem<Integer> exportTimeout = null;
    /**
     * Decompilation timeout in seconds for a single file
     */
    @ConfigurationDefaultInt(5 * 60)
    @ConfigurationDescription("Decompilation timeout in seconds for a single file")
    public static final ConfigurationItem<Integer> decompilationTimeoutFile = null;
    /**
     * Using parameter names in decompiling may cause problems because official programs like Flash CS 5.5 inserts wrong parameter names indices
     */
    @ConfigurationDefaultBoolean(false)
    @ConfigurationDescription("Using parameter names in decompiling may cause problems because official programs like Flash CS 5.5 inserts wrong parameter names indices")
    public static final ConfigurationItem<Boolean> paramNamesEnable = null;

    @ConfigurationDefaultBoolean(true)
    public static final ConfigurationItem<Boolean> displayFileName = null;
    @ConfigurationDefaultBoolean(false)
    public static final ConfigurationItem<Boolean> debugCopy = null;
    @ConfigurationDefaultBoolean(false)
    public static final ConfigurationItem<Boolean> dumpTags = null;

    @ConfigurationDefaultInt(60)
    public static final ConfigurationItem<Integer> decompilationTimeoutSingleMethod = null;
    @ConfigurationDefaultInt(1)
    public static final ConfigurationItem<Integer> lastRenameType = null;

    @ConfigurationDefaultString(".")
    public static final ConfigurationItem<String> lastSaveDir = null;
    @ConfigurationDefaultString(".")
    public static final ConfigurationItem<String> lastOpenDir = null;
    @ConfigurationDefaultString(".")
    public static final ConfigurationItem<String> lastExportDir = null;
    @ConfigurationDefaultString("en")
    public static final ConfigurationItem<String> locale = null;
    @ConfigurationDefaultString("_loc%d_")
    public static final ConfigurationItem<String> registerNameFormat = null;
    @ConfigurationDefaultInt(8)
    public static final ConfigurationItem<Integer> maxRecentFileCount = null;
    public static final ConfigurationItem<String> recentFiles = null;
    
    public static final ConfigurationItem<Calendar> lastUpdatesCheckDate = null;

    @ConfigurationDefaultInt(1000)
    @ConfigurationName("gui.window.width")
    public static final ConfigurationItem<Integer> guiWindowWidth = null;
    @ConfigurationDefaultInt(700)
    @ConfigurationName("gui.window.height")
    public static final ConfigurationItem<Integer> guiWindowHeight = null;
    @ConfigurationDefaultBoolean(false)
    @ConfigurationName("gui.window.maximized.horizontal")
    public static final ConfigurationItem<Boolean> guiWindowMaximizedHorizontal = null;
    @ConfigurationDefaultBoolean(false)
    @ConfigurationName("gui.window.maximized.vertical")
    public static final ConfigurationItem<Boolean> guiWindowMaximizedVertical = null;
    @ConfigurationName("gui.avm2.splitPane.dividerLocation")
    public static final ConfigurationItem<Integer> guiAvm2SplitPaneDividerLocation = null;
    @ConfigurationName("guiActionSplitPaneDividerLocation")
    public static final ConfigurationItem<Integer> guiActionSplitPaneDividerLocation = null;
    @ConfigurationName("gui.splitPane1.dividerLocation")
    public static final ConfigurationItem<Integer> guiSplitPane1DividerLocation = null;
    @ConfigurationName("gui.splitPane2.dividerLocation")
    public static final ConfigurationItem<Integer> guiSplitPane2DividerLocation = null;
    
    private enum OSId {

        WINDOWS, OSX, UNIX
    }

    private static OSId getOSId() {
        PrivilegedAction<String> doGetOSName = new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("os.name");
            }
        };
        OSId id = OSId.UNIX;
        String osName = AccessController.doPrivileged(doGetOSName);
        if (osName != null) {
            if (osName.toLowerCase().startsWith("mac os x")) {
                id = OSId.OSX;
            } else if (osName.contains("Windows")) {
                id = OSId.WINDOWS;
            }
        }
        return id;
    }

    public static String getFFDecHome() throws IOException {
        if (directory == unspecifiedFile) {
            directory = null;
            String userHome = null;
            try {
                userHome = System.getProperty("user.home");
            } catch (SecurityException ignore) {
            }
            if (userHome != null) {
                String applicationId = ApplicationInfo.shortApplicationName;
                OSId osId = getOSId();
                if (osId == OSId.WINDOWS) {
                    File appDataDir = null;
                    try {
                        String appDataEV = System.getenv("APPDATA");
                        if ((appDataEV != null) && (appDataEV.length() > 0)) {
                            appDataDir = new File(appDataEV);
                        }
                    } catch (SecurityException ignore) {
                    }
                    String vendorId = ApplicationInfo.vendor;
                    if ((appDataDir != null) && appDataDir.isDirectory()) {
                        // ${APPDATA}\{vendorId}\${applicationId}
                        String path = vendorId + "\\" + applicationId + "\\";
                        directory = new File(appDataDir, path);
                    } else {
                        // ${userHome}\Application Data\${vendorId}\${applicationId}
                        String path = "Application Data\\" + vendorId + "\\" + applicationId + "\\";
                        directory = new File(userHome, path);
                    }
                } else if (osId == OSId.OSX) {
                    // ${userHome}/Library/Application Support/${applicationId}
                    String path = "Library/Application Support/" + applicationId + "/";
                    directory = new File(userHome, path);
                } else {
                    // ${userHome}/.${applicationId}/
                    String path = "." + applicationId + "/";
                    directory = new File(userHome, path);
                }
            }
        }
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                if (!directory.exists()) {
                    throw new IOException("cannot create directory " + directory);
                }
            }
        }
        String ret = directory.getAbsolutePath();
        if (!ret.endsWith(File.separator)) {
            ret += File.separator;
        }
        return ret;
    }

    public static List<String> getRecentFiles() {
        String files = recentFiles.get();
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(recentFiles.get().split("::"));
    }
    
    public static void addRecentFile(String path) {
        List<String> recentFilesArray = new ArrayList<>(getRecentFiles());
        int idx = recentFilesArray.indexOf(path);
        if (idx != -1) {
            recentFilesArray.remove(idx);
        }        
        recentFilesArray.add(path);
        while (recentFilesArray.size() >= maxRecentFileCount.get()) {
            recentFilesArray.remove(0);
        }
        recentFiles.set(Helper.joinStrings(recentFilesArray, "::"));
    }
    
    /**
     * Saves replacements to file for future use
     */
    private static void saveReplacements(String replacementsFile) {
        if (replacements.isEmpty()) {
            File rf = new File(replacementsFile);
            if (rf.exists()) {
                if (!rf.delete()) {
                    Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, "Cannot delete replacements file");
                }
            }
        } else {
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(replacementsFile), "utf-8"))) {
                for (Replacement r : replacements) {
                    pw.println(r.urlPattern);
                    pw.println(r.targetFile);
                }
            } catch (IOException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, "Exception during saving replacements", ex);
            }
        }
    }

    /**
     * Load replacements from file
     */
    private static void loadReplacements(String replacementsFile) {
        if (!(new File(replacementsFile)).exists()) {
            return;
        }
        replacements = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(replacementsFile), "utf-8"))) {
            String s;
            while ((s = br.readLine()) != null) {
                Replacement r = new Replacement(s, br.readLine());
                replacements.add(r);
            }
        } catch (IOException e) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, "Error during load replacements", e);
        }
    }

    private static String getReplacementsFile() throws IOException {
        return getFFDecHome() + REPLACEMENTS_NAME;
    }

    private static String getConfigFile() throws IOException {
        return getFFDecHome() + CONFIG_NAME;
    }

    private static HashMap<String, Object> loadFromFile(String file, String replacementsFile) {
        HashMap<String, Object> config = new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

            @SuppressWarnings("unchecked")
            HashMap<String, Object> cfg = (HashMap<String, Object>) ois.readObject();
            config = cfg;
        } catch (FileNotFoundException ex) {
        } catch (ClassNotFoundException cnf) {
        } catch (IOException ex) {
        }
        if (replacementsFile != null) {
            loadReplacements(replacementsFile);
        }
        if (config.containsKey("paralelSpeedUp")) {
            config.put("parallelSpeedUp", config.get("paralelSpeedUp"));
            config.remove("paralelSpeedUp");
        }
        return config;
    }

     private static void saveToFile(String file, String replacementsFile) {
        HashMap<String, Object> config = new HashMap<>();
        for (Entry<String, Field> entry : getConfigurationFields().entrySet()) {
            try {
                String name = entry.getKey();
                Field field = entry.getValue();
                ConfigurationItem item = (ConfigurationItem) field.get(null);
                if (item.hasValue) {
                    config.put(name, item.get());
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(config);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot save configuration.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Configuration.class.getName()).severe("Configuration directory is read only.");
        }
        if (replacementsFile != null) {
            saveReplacements(replacementsFile);
        }
    }

    public static List<Replacement> getReplacements() {
        return replacements;
    }

    public static void saveConfig() {
        try {
            saveToFile(getConfigFile(), getReplacementsFile());
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static {
        setConfigurationFields();
    }
    
    @SuppressWarnings("unchecked")
    public static void setConfigurationFields() {
        try {
            HashMap<String, Object> config = loadFromFile(getConfigFile(), getReplacementsFile());
            for (Entry<String, Field> entry : getConfigurationFields().entrySet()) {
                String name = entry.getKey();
                Field field = entry.getValue();
                // remove final modifier from field
                Field modifiersField = field.getClass().getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                Object defaultValue = getDefaultValue(field);
                if (config.containsKey(name)) {
                    field.set(null, new ConfigurationItem(name, defaultValue, config.get(name)));
                } else {
                    field.set(null, new ConfigurationItem(name, defaultValue));
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            // Reflection exceptions. This should never happen
            throw new Error(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Object getDefaultValue(Field field) {
        Object defaultValue = null;
        ConfigurationDefaultBoolean aBool = (ConfigurationDefaultBoolean) field.getAnnotation(ConfigurationDefaultBoolean.class);
        if (aBool != null) {
            defaultValue = aBool.value();
        }
        ConfigurationDefaultInt aInt = (ConfigurationDefaultInt) field.getAnnotation(ConfigurationDefaultInt.class);
        if (aInt != null) {
            defaultValue = aInt.value();
        }
        ConfigurationDefaultString aString = (ConfigurationDefaultString) field.getAnnotation(ConfigurationDefaultString.class);
        if (aString != null) {
            defaultValue = aString.value();
        }
        return defaultValue;
    }
    
    public static String getDescription(Field field) {
        ConfigurationDescription a = (ConfigurationDescription) field.getAnnotation(ConfigurationDescription.class);
        if (a != null) {
            return a.value();
        }
        return null;
    }
    
    public static Map<String, Field> getConfigurationFields() {
        Field[] fields = Configuration.class.getFields();
        Map<String, Field> result = new HashMap<>();
        for (Field field : fields) {
            if (ConfigurationItem.class.isAssignableFrom(field.getType())) {
                ConfigurationName annotation = (ConfigurationName) field.getAnnotation(ConfigurationName.class);
                String name = annotation == null ? field.getName() : annotation.value();
                result.put(name, field);
            }
        }
        return result;
    }
}
