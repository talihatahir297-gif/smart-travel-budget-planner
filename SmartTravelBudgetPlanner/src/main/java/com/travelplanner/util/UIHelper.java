package com.travelplanner.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {

    // Rounded button with hover effect
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(UIConstants.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        Color hoverColor = bg.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); btn.repaint(); }
        });
        return btn;
    }

    // Styled text field
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(UIConstants.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(200, UIConstants.FIELD_HEIGHT));
        return field;
    }

    // Styled password field
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UIConstants.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(200, UIConstants.FIELD_HEIGHT));
        return field;
    }

    // Styled combo box
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(UIConstants.FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        combo.setPreferredSize(new Dimension(200, UIConstants.FIELD_HEIGHT));
        return combo;
    }

    // Card panel with shadow-like border
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(UIConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    // Label factory
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    // Progress bar for budget
    public static JProgressBar createProgressBar(double percent, boolean exceeded) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.min(percent, 100));
        bar.setStringPainted(false);
        bar.setForeground(exceeded ? UIConstants.DANGER : (percent > 75 ? UIConstants.WARNING : UIConstants.ACCENT));
        bar.setBackground(UIConstants.BORDER);
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(200, 10));
        return bar;
    }
}
