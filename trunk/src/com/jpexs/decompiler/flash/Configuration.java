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
package com.jpexs.decompiler.flash;

import com.jpexs.proxy.Replacement;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Configuration {

    public static final boolean DISPLAY_FILENAME = true;
    public static boolean DEBUG_COPY = false;
    public static boolean dump_tags = false;
    /**
     * Debug mode = throwing an error when comparing original file and
     * recompiled
     */
    public static boolean debugMode = false;
    /**
     * Turn off reading unsafe tags (tags which can cause problems with
     * recompiling)
     */
    public static boolean DISABLE_DANGEROUS = false;
    /**
     * Turn off resolving constants in ActionScript 2
     */
    public static final boolean RESOLVE_CONSTANTS = true;
    /**
     * Find latest constant pool in the code
     */
    public static final boolean LATEST_CONSTANTPOOL_HACK = false;
    /**
     * Limit of code subs (for obfuscated code)
     */
    public static final int SUBLIMITER = 500;
    /**
     * Decompilation timeout in seconds
     */
    public static final int DECOMPILATION_TIMEOUT = 30 * 60;
    /**
     * Decompilation timeout for a single method in AS3 or single action in
     * AS1/2 in seconds
     */
    public static final int DECOMPILATION_TIMEOUT_SINGLE_METHOD = 5;
    //using parameter names in decompiling may cause problems because official programs like Flash CS 5.5 inserts wrong parameter names indices
    public static final boolean PARAM_NAMES_ENABLE = false;
    private static HashMap<String, Object> config = new HashMap<>();
    /**
     * List of replacements
     */
    public static java.util.List<Replacement> replacements = new ArrayList<>();

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

    public static boolean containsConfig(String cfg) {
        return config.containsKey(cfg);
    }

    public static Object getConfig(String cfg) {
        return getConfig(cfg, null);
    }

    public static Object getConfig(String cfg, Object defaultValue) {
        if (!config.containsKey(cfg)) {
            return defaultValue;
        }
        return config.get(cfg);
    }

    public static Object setConfig(String cfg, Object value) {
        if (cfg.equals("paralelSpeedUp")) {
            cfg = "parallelSpeedUp";
        }
        return config.put(cfg, value);
    }

    public static void unsetConfig(String cfg) {
        config.remove(cfg);
    }

    public static void loadFromMap(Map<String, Object> map) {
        config.clear();
        config.putAll(map);
    }

    @SuppressWarnings("unchecked")
    public static void loadFromFile(String file, String replacementsFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

            config = (HashMap<String, Object>) ois.readObject();
        } catch (FileNotFoundException ex) {
        } catch (ClassNotFoundException cnf) {
        } catch (IOException ex) {
        }
        if (replacementsFile != null) {
            loadReplacements(replacementsFile);
        }
        if (containsConfig("paralelSpeedUp")) {
            setConfig("parallelSpeedUp", getConfig("paralelSpeedUp"));
            unsetConfig("paralelSpeedUp");
        }
    }

    public static void saveToFile(String file, String replacementsFile) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(config);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot save configuration.", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(SWFInputStream.class.getName()).severe("Configuration directory is read only.");
        }
        if (replacementsFile != null) {
            saveReplacements(replacementsFile);
        }
    }

    public static List<Replacement> getReplacements() {
        return replacements;
    }
}
