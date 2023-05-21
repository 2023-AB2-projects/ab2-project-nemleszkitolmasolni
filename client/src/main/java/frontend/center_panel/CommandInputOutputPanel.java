package frontend.center_panel;

import frontend.other_elements.SQLDocument;
import frontend.other_elements.TabConfig;

import javax.swing.text.*;
import java.awt.*;

public class CommandInputOutputPanel extends javax.swing.JPanel {
    public CommandInputOutputPanel() {
        initComponents();

        // Setting the number of tabs and their length
        this.inputArea.setParagraphAttributes(TabConfig.getTabAttributeSet(), false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        commandSplitPane = new javax.swing.JSplitPane();
        input = new javax.swing.JScrollPane();
        inputArea = new javax.swing.JTextPane();
        outputTabbedPane = new javax.swing.JTabbedPane();
        outputAreaScrollPanel = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextPane();
        outputTableScrollPanel = new javax.swing.JScrollPane();
        outputTable = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(900, 1000));
        setPreferredSize(new java.awt.Dimension(900, 900));

        commandSplitPane.setDividerLocation(500);
        commandSplitPane.setDividerSize(3);
        commandSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        inputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        inputArea.setStyledDocument(new SQLDocument());
        input.setViewportView(inputArea);

        commandSplitPane.setLeftComponent(input);

        outputTabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        outputArea.setEditable(false);
        outputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        outputAreaScrollPanel.setViewportView(outputArea);

        outputTabbedPane.addTab("Command Output", outputAreaScrollPanel);

        outputTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        outputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        outputTable.setGridColor(new java.awt.Color(102, 102, 102));
        outputTable.setRowHeight(25);
        outputTable.setShowGrid(true);
        outputTable.getTableHeader().setReorderingAllowed(false);
        outputTableScrollPanel.setViewportView(outputTable);

        outputTabbedPane.addTab("Table Output", outputTableScrollPanel);

        commandSplitPane.setRightComponent(outputTabbedPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /* Setters */
    public void setInputAreaFont(Font font) { this.inputArea.setFont(font); }

    public void setInputTextAreaString(String string) { this.inputArea.setText(string); }

    public void setOutputAreaString(String string) { this.outputArea.setText(string); }

    public void increaseFont() {
        Font font = this.inputArea.getFont();
        Font newFont = new Font("Segoe", Font.PLAIN, font.getSize() + 1);

        // Set size
        this.inputArea.setFont(newFont);
        this.outputArea.setFont(newFont);
    }

    public void decreaseFont() {
        Font font = this.inputArea.getFont();
        if (font.getSize() > 0) {
            Font newFont = new Font("Segoe", Font.PLAIN, font.getSize() - 1);

            // Set size
            this.inputArea.setFont(newFont);
            this.outputArea.setFont(newFont);
        }
    }

    /* Getters */
    public String getInputAreaText() { return this.inputArea.getText(); }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane commandSplitPane;
    private javax.swing.JScrollPane input;
    private javax.swing.JTextPane inputArea;
    private javax.swing.JTextPane outputArea;
    private javax.swing.JScrollPane outputAreaScrollPanel;
    private javax.swing.JTabbedPane outputTabbedPane;
    private javax.swing.JTable outputTable;
    private javax.swing.JScrollPane outputTableScrollPanel;
    // End of variables declaration//GEN-END:variables
}
