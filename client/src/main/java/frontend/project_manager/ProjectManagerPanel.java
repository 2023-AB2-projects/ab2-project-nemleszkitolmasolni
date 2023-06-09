package frontend.project_manager;

import control.ClientController;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import service.Config;
import service.Utility;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.*;

@Slf4j
public class ProjectManagerPanel extends javax.swing.JPanel {
    // References
    @Setter
    private ClientController clientController;

    // OK and warning symbol icons
    private final ImageIcon okSymbolIcon = Utility.resizeIcon(
            new ImageIcon(Config.getImagesPath() + File.separator + "ok_symbol.png"),
            30, 30
    );
    private final ImageIcon warningSymbolIcon = Utility.resizeIcon(
            new ImageIcon(Config.getImagesPath() + File.separator + "warning_symbol.png"),
            30, 30
    );

    private static final ImageIcon sqlIcon = Utility.resizeIcon(
            new ImageIcon(Config.getImagesPath() + File.separator + "sql_file_icon.png"),
            22, 22
    );

    private static final ImageIcon folderIcon = Utility.resizeIcon(
            new ImageIcon(Config.getImagesPath() + File.separator + "sql_simple_folder_icon.png"),
            22, 22
    );

    // File system logic
    private final DefaultMutableTreeNode projectsRootNode;
    private final DefaultTreeModel documentJTreeMutable;

    // File logic
    private String currentFileName;
    private File currentFile;

    public ProjectManagerPanel() {
        initComponents();

        // Init root node
        this.projectsRootNode = new DefaultMutableTreeNode("User Projects");
        this.documentJTreeMutable = new DefaultTreeModel(this.projectsRootNode);
        this.documentJTree.setModel(this.documentJTreeMutable);

        this.initLabelIcons();
        this.createRootFolderIfNotExists();

        // Init default file
        this.initDefaultFile();
        this.currentFileField.setText(this.currentFileName);

        // Set JTree cell renderer
        this.documentJTree.setCellRenderer(new CustomTreeCellRendererTable());

        // Update file system
        this.update();
    }

    private void initLabelIcons() {
        // Set SQL Query Label Icon (resize it to 32x32)
        sqlQueryLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "sql_file_icon.png"),
                32, 32
        ));

        // Set Project Label Icon (resize it to 32x32)
        projectLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "sql_folder_icon.png"),
                32, 32
        ));

        // Save file label
        saveFileLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "save_file_icon.png"),
                32, 32
        ));

        // Delete label
        deleteLabel.setIcon(Utility.resizeIcon(
                new ImageIcon(Config.getImagesPath() + File.separator + "delete_file_icon.png"),
                32, 32
        ));

        // File indicator label
        fileIndicatorLabel.setIcon(okSymbolIcon);
    }

    private void createRootFolderIfNotExists() {
        // Create root folder if it doesn't exist
        File rootFolder = new File(Config.getUserScriptsPath());

        // If the root folder doesn't exist, create it
        if (!rootFolder.exists()) {
            if (!rootFolder.mkdir()) {
                log.error("Failed to create user scripts directory");
            }
        }
    }

    private void initDefaultFile() {
        this.currentFileName = "New query.sql";

        // Check if file already exists in root folder
        this.currentFile = new File(Config.getUserScriptsPath() + File.separator + this.currentFileName);
        if (!this.currentFile.exists()) {
            // If it doesn't exist -> Create a new file
            try {
                if (this.currentFile.createNewFile()) {
                    log.info("Created new file: " + this.currentFileName);
                }
            } catch (IOException e) {
                log.error("Failed to create new file");
            }
        }
    }

    private void update() {
        // Clear out the current JTree
        this.projectsRootNode.removeAllChildren();

        // Read all the files and directories in the user scripts directory recursively
        File rootFolder = new File(Config.getUserScriptsPath());

        // If the root folder doesn't exist, create it
        if (!rootFolder.exists()) {
            if (!rootFolder.mkdir()) {
                log.error("Failed to create user scripts directory");
            }
        } else {
            // If the directory exists -> Parse it recursively (After skipping the root folder)
            File[] files = rootFolder.listFiles();
            if (files != null) {
                for (final File subFile : files) {
                    this.parseUserScripts(subFile, this.projectsRootNode);
                }
            }
        }

        // Update the JTree
        this.documentJTreeMutable.reload();
    }

    private void parseUserScripts(File file, DefaultMutableTreeNode parentNode) {
        // If it's directory -> Recursive case
        if (file.isDirectory()) {
            // Create a TreeNode for the directory
            DefaultMutableTreeNode directoryNode = new DefaultMutableTreeNode(file.getName());

            // Add the directory node to the parent node
            parentNode.add(directoryNode);

            // Parse every file in the directory
            File[] files = file.listFiles();
            if (files != null) {
                for (final File subFile : files) {
                    this.parseUserScripts(subFile, directoryNode);
                }
            }
        } else {
            // It's a file -> Base case
            // Create a TreeNode for the file
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());

            // Add the file node to the parent node
            parentNode.add(fileNode);
        }
    }

    private String pathToSelectedFile() {
        // Find the currently selected Tree node
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) documentJTree.getLastSelectedPathComponent();

        // If nothing is selected -> Use the root node
        if (selectedNode == null) selectedNode = this.projectsRootNode;

        // Reconstruct path to current node (In file system)
        StringBuilder pathBuilder = new StringBuilder();
        for (final TreeNode pathNode : selectedNode.getPath()) {
            String pathElementString = pathNode.toString();
            if (pathElementString.equals("User Projects")) continue;
            pathBuilder.append(File.separator).append(pathElementString);
        }

        // Path to the file
        return Config.getUserScriptsPath() + pathBuilder.toString();
    }

    /* Setters */
    public void readCurrentFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.currentFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // Set the text area text
            this.clientController.setInputTextAreaString(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCurrentFile() {
        // Get the text from the editor
        String currentSQLText = this.clientController.getInputTextAreaString();

        // Save SQL text to file
        try {
            // Check if file exists
            boolean fileExists = this.currentFile.exists();

            FileWriter fileWriter = new FileWriter(this.currentFile);
            fileWriter.write(currentSQLText);
            fileWriter.close();

            // If the file didn't exist before -> Update the JTree
            if (!fileExists) {
                this.update();
            }
        } catch (IOException e) {
            log.error("Error saving file: " + this.currentFileName);
            e.printStackTrace();
        }

        // File saved -> Switch to OK icon
        this.fileIndicatorLabel.setIcon(okSymbolIcon);
    }

    public void inputAreaChanged() {
        // File changed -> Switch to warning icon
        this.fileIndicatorLabel.setIcon(warningSymbolIcon);
    }

    public void setLightMode() {
        this.projectManagerTag.setBackground(Color.LIGHT_GRAY);
    }

    public void setDarkMode() {
        this.projectManagerTag.setBackground(Color.DARK_GRAY);
    }

    // Custom cell renderer class
    static class CustomTreeCellRendererTable extends DefaultTreeCellRenderer {
        // Override the getTreeCellRendererComponent method
        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            // Invoke the default implementation
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            // Check if the node's value is equal to "Tables"
            String valueName = value.toString();

            // If it's a sql file (ends with .sql) -> Set the icon to the sql file icon
            if (valueName.contains(".sql")) {
                setIcon(sqlIcon);
            } else {
                setIcon(folderIcon);
            }

            return this;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectManagerTag = new javax.swing.JLabel();
        sqlQueryLabel = new javax.swing.JLabel();
        newProjectButton = new javax.swing.JButton();
        projectLabel = new javax.swing.JLabel();
        newQueryButton = new javax.swing.JButton();
        documentTreeScrollPanel = new javax.swing.JScrollPane();
        documentJTree = new javax.swing.JTree();
        saveFileButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        saveFileLabel = new javax.swing.JLabel();
        deleteLabel = new javax.swing.JLabel();
        currentFileLabel = new javax.swing.JLabel();
        currentFileField = new javax.swing.JTextField();
        fileIndicatorLabel = new javax.swing.JLabel();

        projectManagerTag.setBackground(java.awt.Color.darkGray);
        projectManagerTag.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        projectManagerTag.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        projectManagerTag.setText("Project Manager");
        projectManagerTag.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        projectManagerTag.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        projectManagerTag.setOpaque(true);

        newProjectButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        newProjectButton.setText("New Project");
        newProjectButton.setMargin(new java.awt.Insets(2, 7, 3, 7));
        newProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectButtonActionPerformed(evt);
            }
        });

        newQueryButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        newQueryButton.setText("New Query");
        newQueryButton.setMargin(new java.awt.Insets(2, 7, 3, 7));
        newQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newQueryButtonActionPerformed(evt);
            }
        });

        documentJTree.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        documentJTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                documentJTreeMousePressed(evt);
            }
        });
        documentTreeScrollPanel.setViewportView(documentJTree);

        saveFileButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        saveFileButton.setText("Save");
        saveFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        currentFileLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        currentFileLabel.setText("Current file:");
        currentFileLabel.setFocusable(false);

        currentFileField.setEditable(false);
        currentFileField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        currentFileField.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectManagerTag, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(documentTreeScrollPanel)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sqlQueryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(saveFileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newQueryButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(currentFileLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentFileField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileIndicatorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(projectManagerTag, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentFileLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(currentFileField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fileIndicatorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sqlQueryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(projectLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(newQueryButton)
                        .addComponent(newProjectButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveFileButton)
                    .addComponent(deleteButton)
                    .addComponent(deleteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(documentTreeScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void newProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectButtonActionPerformed
        File selectedFile = new File(this.pathToSelectedFile());    // Selected file (or directory)

        // Display the input dialog and get the user's input
        String userInput = JOptionPane.showInputDialog(this.clientController.getClientFrame(), "New project name:");

        // Display the input provided by the user
        if (userInput != null) { // User clicked OK or entered a value
            // Remove any extensions from file name
            userInput = userInput.replaceAll("\\.sql$", "");

            // Check if selected file is a directory
            File folder;
            if (selectedFile.isDirectory()) {
                folder = selectedFile;
            } else {
                folder = selectedFile.getParentFile();
            }

            // Create new directory with given name
            File newDirectory = new File(folder, userInput);
            if (!newDirectory.mkdir()) {
                log.error("Failed to create new project directory: " + userInput);
                JOptionPane.showMessageDialog(this.clientController.getClientFrame(), "Failed to create new project directory: " + userInput, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Refresh the file tree
                this.update();
            }
        }
    }//GEN-LAST:event_newProjectButtonActionPerformed

    private void newQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newQueryButtonActionPerformed
        File selectedFile = new File(this.pathToSelectedFile());

        // Display the input dialog and get the user's input
        String userInput = JOptionPane.showInputDialog("New SQL query name:");

        // Display the input provided by the user
        if (userInput != null) { // User clicked OK or entered a value
            // Remove any extensions from file name
            userInput = userInput.replaceAll("\\.sql$", "");

            // Check if selected file is a directory
            File folder;
            if (selectedFile.isDirectory()) {
                folder = selectedFile;
            } else {
                folder = selectedFile.getParentFile();
            }

            // Create a new file in the selected directory
            String fileName = userInput + ".sql";
            File newFile = new File(folder, fileName);
            try {
                // Create the new file
                if (!newFile.createNewFile()) {
                    log.error("Failed to create new file: " + newFile.getAbsolutePath());
                } else {
                    log.info("Created new file: " + fileName);
                }
            } catch (IOException ex) {
                log.error("Failed to create new file: " + newFile.getAbsolutePath());
            }

            // Save the current file
            this.saveCurrentFile();

            // Set current file to the newly created file
            this.currentFile = newFile;
            this.currentFileName = fileName;
            this.currentFileField.setText(fileName);

            // Load new current file
            this.readCurrentFile();

            // Refresh the file tree
            this.update();
        }
    }//GEN-LAST:event_newQueryButtonActionPerformed

    private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileButtonActionPerformed
        this.saveCurrentFile();
    }//GEN-LAST:event_saveFileButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.documentJTree.getLastSelectedPathComponent();
        // Remove currently selected node from JTree and it's children (if the user confirms)

        // Selected node value
        String selectedNodeValue = selectedNode.toString();

        // Ask use if they are sure they want to delete the selected node
        String question = "Are you sure you want to delete the ";
        question += (selectedNode.isLeaf()) ? "file " : "directory ";
        question += selectedNodeValue + "?";
        int dialogResult = JOptionPane.showConfirmDialog(this.clientController.getClientFrame(), question, "Delete", JOptionPane.YES_NO_OPTION);

        // Check if user clicked no
        if (dialogResult != JOptionPane.YES_OPTION) {
           return;
        }

        // Check if node is a file
        if (selectedNode.isLeaf()) {
            // Delete the file
            File fileToDelete = new File(this.pathToSelectedFile());
            if (!fileToDelete.delete()) {
                log.error("Failed to delete file: " + fileToDelete.getAbsolutePath());
                JOptionPane.showMessageDialog(this.clientController.getClientFrame(), "Failed to delete file: " + fileToDelete.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Refresh the file tree
                this.update();
            }
        } else {
            // Delete the directory
            File directoryToDelete = new File(this.pathToSelectedFile());
            if (!Utility.deleteDirectory(directoryToDelete)) {
                log.error("Failed to delete directory: " + directoryToDelete.getAbsolutePath());
                JOptionPane.showMessageDialog(this.clientController.getClientFrame(), "Failed to delete directory: " + directoryToDelete.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Refresh the file tree
                this.update();
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void documentJTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_documentJTreeMousePressed
        // Check if the user double-clicked
        if (evt.getClickCount() != 2) {
            return;
        }

        // Path to selected file
        String pathToSelectedFile = this.pathToSelectedFile();

        // Check if selected file is a directory
        File selectedFile = new File(pathToSelectedFile);
        if (!selectedFile.isDirectory()) {
            // Save current file
            this.saveCurrentFile();

            // Set the current file
            this.currentFile = selectedFile;
            this.currentFileName = selectedFile.getName();
            this.currentFileField.setText(selectedFile.getName());

            // Read the current file
            this.readCurrentFile();
        }
    }//GEN-LAST:event_documentJTreeMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField currentFileField;
    private javax.swing.JLabel currentFileLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel deleteLabel;
    private javax.swing.JTree documentJTree;
    private javax.swing.JScrollPane documentTreeScrollPanel;
    private javax.swing.JLabel fileIndicatorLabel;
    private javax.swing.JButton newProjectButton;
    private javax.swing.JButton newQueryButton;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JLabel projectManagerTag;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JLabel saveFileLabel;
    private javax.swing.JLabel sqlQueryLabel;
    // End of variables declaration//GEN-END:variables
}
