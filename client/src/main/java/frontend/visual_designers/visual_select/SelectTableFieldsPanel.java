package frontend.visual_designers.visual_select;

import service.CatalogManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SelectTableFieldsPanel extends javax.swing.JPanel {
    private final String databaseName, tableName;
    private final List<String> tableFieldsNames;

    // Checkboxes for each field
    private List<JCheckBox> fieldCheckBoxes;

    // References
    private SelectMainPanel mainPanel;

    public SelectTableFieldsPanel(String databaseName, String tableName) {
        // Table and database names
        this.databaseName = databaseName;
        this.tableName = tableName;

        // Get table details
        this.tableFieldsNames = CatalogManager.getFieldNames(this.databaseName, this.tableName);

        initComponents();

        // Set table label name
        this.tableNameLabel.setText(tableName);

        // Init variables used
        this.initVariables();

        // Initialize selectors
        this.initSelectors();
    }

    private void initVariables() {
        this.fieldCheckBoxes = new ArrayList<>();
    }

    private void initSelectors() {
        // Add a selector for each field
        Font font = this.selectedAllBox.getFont();
        for (final String fieldName : this.tableFieldsNames) {
            JCheckBox checkBox = new JCheckBox(fieldName);
            checkBox.setFont(font);

            // Add action listeners to each
            checkBox.addItemListener(event -> {
                JCheckBox checkbox = (JCheckBox) event.getSource();
                // Update selected all box
                if (checkbox.isSelected()) selectedAllBox.setSelected(false);

                // Try to add to table selection area if it's selected
                if (checkbox.isSelected()) mainPanel.fieldIsSelected(tableName, checkbox.getText());
                else mainPanel.fieldIsDeselected(tableName, checkbox.getText());
            });

            // Add to list and panel
            this.fieldCheckBoxes.add(checkBox);
            this.selectorPanel.add(checkBox);
        }
    }

    /* Setters */
    public void setMainPanel(SelectMainPanel mainPanel) { this.mainPanel = mainPanel; }

    /* Getters */
    public List<String> getSelectedFields() {
        // Check if all are selected
        if (this.selectedAllBox.isSelected()) {
            return this.tableFieldsNames;       // Return full list of field names
        }

        // Iterate over each check box and see if they are selected
        List<String> selectedFieldNames = new ArrayList<>();
        int ind = 0;
        for (final JCheckBox checkBox : this.fieldCheckBoxes) {
            // Add to list if is selected
            if (checkBox.isSelected()) selectedFieldNames.add(this.tableFieldsNames.get(ind));
            ind++;
        }

        return selectedFieldNames;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableNameLabel = new javax.swing.JLabel();
        selectorPanel = new javax.swing.JPanel();
        selectedAllBox = new javax.swing.JCheckBox();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableNameLabel.setBackground(new java.awt.Color(51, 51, 51));
        tableNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tableNameLabel.setText("table_name");
        tableNameLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tableNameLabel.setOpaque(true);

        selectorPanel.setLayout(new java.awt.GridLayout(6, 2));

        selectedAllBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        selectedAllBox.setText("* (All Fields)");
        selectedAllBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectedAllBoxItemStateChanged(evt);
            }
        });
        selectorPanel.add(selectedAllBox);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(tableNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectedAllBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectedAllBoxItemStateChanged
        // If all is selected then check of every other
        if (this.selectedAllBox.isSelected()) {
            this.fieldCheckBoxes.forEach((checkbox) -> checkbox.setSelected(false));
        }

        // Removed/add data to JTable
        if (this.selectedAllBox.isSelected()) {
            // We 'select' each field
            this.fieldCheckBoxes.forEach((checkbox) -> mainPanel.fieldIsSelected(this.tableName, checkbox.getText()));
        } else {
            // We 'deselect' each field
            this.fieldCheckBoxes.forEach((checkbox) -> mainPanel.fieldIsDeselected(this.tableName, checkbox.getText()));
        }
    }//GEN-LAST:event_selectedAllBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox selectedAllBox;
    private javax.swing.JPanel selectorPanel;
    private javax.swing.JLabel tableNameLabel;
    // End of variables declaration//GEN-END:variables
}
