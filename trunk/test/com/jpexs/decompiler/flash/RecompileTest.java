package com.jpexs.decompiler.flash;

import com.jpexs.decompiler.flash.abc.NotSameException;
import java.io.*;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author JPEXS
 */
public class RecompileTest {

    public static final String TESTDATADIR = "testdata/recompile";

    private void testRecompileOne(String filename) {
        try {
            SWF swf = new SWF(new FileInputStream(TESTDATADIR + File.separator + filename), false);
            Configuration.DEBUG_COPY = true;
            swf.saveTo(new ByteArrayOutputStream());
        } catch (IOException ex) {
            fail();
        } catch (NotSameException ex) {
            fail("File is different after recompiling: " + filename);
        }
    }

    @Test
    public void testRecompile() {
        File dir = new File(TESTDATADIR);
        if (!dir.exists()) {
            return;
        }
        File files[] = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".swf");
            }
        });
        for (File f : files) {
            testRecompileOne(f.getName());
        }
    }
}
