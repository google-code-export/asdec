/*
 *  Copyright (C) 2010 JPEXS
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.jpexs.asdec.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Contains methods for GUI
 *
 * @author JPEXS
 */
public class View {
    /**
     * Sets windows Look and Feel
     */
    public static void setWinLookAndFeel() {
        try {

            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException ignored) {
        } catch (ClassNotFoundException ignored) {
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        }

    }

    /**
     * Loads image from resources
     *
     * @param path Path to image without starting slash
     * @return loaded Image
     */
    public static Image loadImage(String path) {
        ClassLoader cldr = (new Object()).getClass().getClassLoader();
        java.net.URL imageURL = (new Object()).getClass().getResource("/" + path);
        return Toolkit.getDefaultToolkit().createImage(imageURL);
    }

    /**
     * Sets icon of specified frame to ASDec icon
     *
     * @param f Frame to set icon in
     */
    public static void setWindowIcon(Frame f) {
        java.util.List<Image> images = new ArrayList<Image>();
        images.add(loadImage("com/jpexs/asdec/gui/graphics/icon16.png"));
        images.add(loadImage("com/jpexs/asdec/gui/graphics/icon32.png"));
        images.add(loadImage("com/jpexs/asdec/gui/graphics/icon48.png"));
        f.setIconImages(images);
    }

    /**
     * Centers specified frame on the screen
     *
     * @param f Frame to center on the screen
     */
    public static void centerScreen(Frame f) {
        Dimension dim = f.getToolkit().getScreenSize();
        Rectangle abounds = f.getBounds();
        f.setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
    }
}
