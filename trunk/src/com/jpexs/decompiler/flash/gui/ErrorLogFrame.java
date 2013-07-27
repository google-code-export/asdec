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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author JPEXS
 */
public class ErrorLogFrame extends AppFrame {

    private JPanel logView = new JPanel();
    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    public ErrorLogFrame() {
        setTitle(translate("dialog.title"));
        setSize(700, 400);
        setBackground(Color.white);
        View.centerScreen(this);
        View.setWindowIcon(this);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Container cnt = getContentPane();
        cnt.setLayout(new BorderLayout());
        logView.setBackground(Color.white);
        logView.setLayout(new ListLayout());
        cnt.setBackground(Color.white);

        cnt.add(new JScrollPane(logView));
        handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                log(record.getLevel(), record.getMessage(), record.getThrown());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
    }

    private void log(final Level level, final String msg, final String detail) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel pan = new JPanel();
                pan.setBackground(Color.white);
                pan.setLayout(new ListLayout());

                JComponent detailComponent;
                if (detail == null) {
                    detailComponent = null;
                } else {
                    final JTextArea detailTextArea = new JTextArea(detail);
                    detailTextArea.setEditable(false);
                    detailTextArea.setOpaque(false);
                    detailTextArea.setFont(new JLabel().getFont());
                    detailTextArea.setBackground(Color.white);
                    detailComponent = detailTextArea;
                }
                JPanel header = new JPanel();
                header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
                header.setBackground(Color.white);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                final String dateStr = format.format(new Date());

                JToggleButton copyButton = new JToggleButton(View.getIcon("copy16"));
                copyButton.setFocusPainted(false);
                copyButton.setBorderPainted(false);
                copyButton.setFocusable(false);
                copyButton.setContentAreaFilled(false);
                copyButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                copyButton.setMargin(new Insets(2, 2, 2, 2));
                copyButton.setToolTipText(translate("copy"));
                copyButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        StringSelection stringSelection = new StringSelection(dateStr + " " + level.toString() + " " + msg + "\r\n" + detail);
                        clipboard.setContents(stringSelection, null);
                    }
                });

                final JToggleButton expandButton = new JToggleButton(View.getIcon("expand16"));
                expandButton.setFocusPainted(false);
                expandButton.setBorderPainted(false);
                expandButton.setFocusable(false);
                expandButton.setContentAreaFilled(false);
                expandButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                expandButton.setMargin(new Insets(2, 2, 2, 2));
                expandButton.setToolTipText(translate("details"));

                final JScrollPane scrollPane;
                if (detailComponent != null) {
                    scrollPane = new JScrollPane(detailComponent);
                    scrollPane.setAlignmentX(0f);
                    scrollPane.setMinimumSize(new Dimension(getWidth(), 500));
                } else {
                    scrollPane = null;
                }


                if (detailComponent != null) {
                    expandButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            scrollPane.setVisible(expandButton.isSelected());
                            revalidate();
                            repaint();
                        }
                    });
                }



                JLabel dateLabel = new JLabel(dateStr);
                dateLabel.setPreferredSize(new Dimension(140, 25));
                dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                header.add(dateLabel);



                JLabel levelLabel = new JLabel(level.getName());
                levelLabel.setPreferredSize(new Dimension(75, 25));
                levelLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                header.add(levelLabel);
                JTextArea msgLabel = new JTextArea(msg);
                msgLabel.setEditable(false);
                msgLabel.setOpaque(false);
                msgLabel.setFont(levelLabel.getFont());

                msgLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                header.add(msgLabel);
                header.setAlignmentX(0f);
                if (detailComponent != null) {
                    header.add(expandButton);
                }
                header.add(copyButton);
                pan.add(header);
                if (detailComponent != null) {
                    pan.add(scrollPane);
                    scrollPane.setVisible(false);
                }
                pan.setAlignmentX(0f);
                logView.add(pan);
                revalidate();
                repaint();
            }
        });


    }

    public void log(Level level, String msg) {
        log(level, msg, (String) null);
    }

    public void log(Level level, String msg, Throwable ex) {
        StringWriter sw = new StringWriter();
        if (ex != null) {
            ex.printStackTrace(new PrintWriter(sw));
        }
        log(level, msg, sw.toString());
    }
}
