/*
 * Copyright (C) 2013 JPEXS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author JPEXS
 */
public class SearchDialog extends JDialog implements ActionListener {

    public JTextField searchField = new JTextField();
    public JCheckBox ignoreCaseCheckBox = new JCheckBox("Ignore case");
    public JCheckBox regexpCheckBox = new JCheckBox("Regular expression");
    public boolean result = false;

    public SearchDialog() {
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        Container cnt = getContentPane();
        setSize(400, 150);
        cnt.setLayout(new BoxLayout(cnt, BoxLayout.PAGE_AXIS));
        JPanel panButtons = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("CANCEL");
        cancelButton.addActionListener(this);
        panButtons.add(okButton);
        panButtons.add(cancelButton);
        JPanel panField = new JPanel(new FlowLayout());
        searchField.setPreferredSize(new Dimension(250, 25));
        panField.add(new JLabel("Search text:"));
        panField.add(searchField);
        cnt.add(panField);
        JPanel checkPanel = new JPanel(new FlowLayout());
        checkPanel.add(ignoreCaseCheckBox);
        checkPanel.add(regexpCheckBox);
        cnt.add(checkPanel);
        cnt.add(panButtons);
        getRootPane().setDefaultButton(okButton);
        View.centerScreen(this);
        View.setWindowIcon(this);
        setTitle("ActionScript search");
        setModalityType(ModalityType.APPLICATION_MODAL);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            result = false;
            searchField.requestFocusInWindow();
        }
        super.setVisible(b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            if (regexpCheckBox.isSelected()) {
                try {
                    Pattern pat = Pattern.compile(searchField.getText());
                } catch (PatternSyntaxException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid pattern", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            result = true;
        } else {
            result = false;
        }
        setVisible(false);
    }
}
