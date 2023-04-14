package frontend.center_panel;

import backend.MessageModes;
import control.ClientController;
import frontend.center_panel.command_templates.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandPanel extends javax.swing.JPanel {
    @Setter
    private ClientController clientController;

    public CommandPanel() {
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

        selectComboBox = new javax.swing.JComboBox<>();
        createAlterComboBox = new javax.swing.JComboBox<>();
        dropDeleteComboBox = new javax.swing.JComboBox<>();
        insertUpdateComboBox = new javax.swing.JComboBox<>();
        horizontalFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        runCommandButton = new javax.swing.JButton();
        useComboBox = new javax.swing.JComboBox<>();

        setPreferredSize(new java.awt.Dimension(1000, 42));

        selectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECT", "DEFAULT SELECT", "FULL SELECT" }));
        selectComboBox.setToolTipText("");
        selectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectComboBoxActionPerformed(evt);
            }
        });

        createAlterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CREATE/ALTER", "CREATE DATABASE", "CREATE TABLE", "ALTER TABLE" }));
        createAlterComboBox.setToolTipText("");
        createAlterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAlterComboBoxActionPerformed(evt);
            }
        });

        dropDeleteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DROP/DELETE", "DROP DATABASE", "DROP TABLE" }));
        dropDeleteComboBox.setToolTipText("");
        dropDeleteComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropDeleteComboBoxActionPerformed(evt);
            }
        });

        insertUpdateComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INSERT/UPDATE", "INSERT INTO TABLE", "UPDATE TABLE" }));
        insertUpdateComboBox.setToolTipText("");
        insertUpdateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertUpdateComboBoxActionPerformed(evt);
            }
        });

        runCommandButton.setText("RUN COMMAND");
        runCommandButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        runCommandButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runCommandButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                runCommandButtonMousePressed(evt);
            }
        });
        runCommandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runCommandButtonActionPerformed(evt);
            }
        });

        useComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "USE", "USE DATABASE" }));
        useComboBox.setToolTipText("");
        useComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useComboBoxItemStateChanged(evt);
            }
        });
        useComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useComboBox, 0, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(createAlterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(dropDeleteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(insertUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(306, 306, 306)
                .addComponent(horizontalFiller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(runCommandButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(useComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createAlterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dropDeleteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(insertUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(horizontalFiller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(runCommandButton)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void runCommandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runCommandButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_runCommandButtonActionPerformed

    private void runCommandButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_runCommandButtonMousePressed
        String command = this.clientController.getInputTextAreaString();
        log.info("Command to be sent to server:\n" + command);

        this.clientController.sendCommandToServer(command);
        this.clientController.receiveMessageAndPerformAction(MessageModes.refreshDatabases);
        this.clientController.receiveMessageAndPerformAction(MessageModes.setTextArea);
    }//GEN-LAST:event_runCommandButtonMousePressed

    private void insertUpdateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertUpdateComboBoxActionPerformed
        switch (this.insertUpdateComboBox.getSelectedIndex()) {
            case 1 -> this.clientController.setInputTextAreaString(new InsertIntoTableTemplate().toString());
            case 2 -> this.clientController.setInputTextAreaString(new UpdateTableTemplate().toString());
        }

        // Reset to basic index
        this.insertUpdateComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_insertUpdateComboBoxActionPerformed

    private void dropDeleteComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropDeleteComboBoxActionPerformed
        switch (this.dropDeleteComboBox.getSelectedIndex()) {
            case 1 -> this.clientController.setInputTextAreaString(new DropDatabaseTemplate().toString());
            case 2 -> this.clientController.setInputTextAreaString(new DropTableTemplate().toString());
        }

        // Reset to basic index
        this.dropDeleteComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_dropDeleteComboBoxActionPerformed

    private void createAlterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAlterComboBoxActionPerformed
        switch (this.createAlterComboBox.getSelectedIndex()) {
            case 1 -> this.clientController.setInputTextAreaString(new CreateDatabaseTemplate().toString());
            case 2 -> this.clientController.setInputTextAreaString(new CreateTableTemplate().toString());
            case 3 -> this.clientController.setInputTextAreaString(new AlterTableTemplate().toString());
        }

        // Reset to basic index
        this.createAlterComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_createAlterComboBoxActionPerformed

    private void selectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectComboBoxActionPerformed
        switch (this.selectComboBox.getSelectedIndex()) {
            case 1 -> this.clientController.setInputTextAreaString(new DefaultSelectTemplate().toString());
            case 2 -> this.clientController.setInputTextAreaString(new FullSelectTemplate().toString());
        }

        // Reset to basic index
        this.selectComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_selectComboBoxActionPerformed

    private void useComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useComboBoxActionPerformed
        int selected = this.useComboBox.getSelectedIndex();

        if (selected == 1) {
            this.clientController.setInputTextAreaString(new UseDatabaseTemplate().toString());
        }

        // Reset to basic index
        this.useComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_useComboBoxActionPerformed

    private void useComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_useComboBoxItemStateChanged
        // TODO add your handling code here:
        // Ignore this one
    }//GEN-LAST:event_useComboBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> createAlterComboBox;
    private javax.swing.JComboBox<String> dropDeleteComboBox;
    private javax.swing.Box.Filler horizontalFiller;
    private javax.swing.JComboBox<String> insertUpdateComboBox;
    private javax.swing.JButton runCommandButton;
    private javax.swing.JComboBox<String> selectComboBox;
    private javax.swing.JComboBox<String> useComboBox;
    // End of variables declaration//GEN-END:variables
}