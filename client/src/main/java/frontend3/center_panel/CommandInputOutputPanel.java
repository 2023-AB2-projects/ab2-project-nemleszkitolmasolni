package frontend3.center_panel;

import lombok.Setter;

public class CommandInputOutputPanel extends javax.swing.JPanel {
    @Setter
    private CenterClientPanel centerClientPanel;

    public CommandInputOutputPanel() {
        initComponents();
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
        inputScrollPanel = new javax.swing.JScrollPane();
        inputArea = new javax.swing.JTextArea();
        outputScrollPanel = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(900, 1000));
        setPreferredSize(new java.awt.Dimension(900, 900));

        commandSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        inputArea.setColumns(20);
        inputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        inputArea.setRows(5);
        inputScrollPanel.setViewportView(inputArea);

        commandSplitPane.setTopComponent(inputScrollPanel);

        outputArea.setEditable(false);
        outputArea.setColumns(20);
        outputArea.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        outputArea.setRows(5);
        outputArea.setText("COMMAND OUTPUT");
        outputScrollPanel.setViewportView(outputArea);

        commandSplitPane.setRightComponent(outputScrollPanel);

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
    public void setOutputAreaString(String string) {
        this.outputArea.setText(string);
    }

    /* Getters */
    public String getInputAreaText() { return this.inputArea.getText(); }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane commandSplitPane;
    private javax.swing.JTextArea inputArea;
    private javax.swing.JScrollPane inputScrollPanel;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JScrollPane outputScrollPanel;
    // End of variables declaration//GEN-END:variables
}
