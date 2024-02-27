import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JCalendar;

public class Main extends JFrame {
    private DeadlineDAO deadlineDAO = new DeadlineDAO();
    private DefaultListModel<Deadline> deadlineListModel = new DefaultListModel<>();
    private JList<Deadline> deadlineList = new JList<>(deadlineListModel);
    private JTextField titleField = new JTextField(20);
    private JTextField descriptionField = new JTextField(30);
    private JDateChooser dueDateChooser = new JDateChooser();
    private JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Urgent", "Important", "Not Important"});
    private JCalendar calendar = new JCalendar();

    public Main() {
        setTitle("Deadline Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(55, 155, 255));
        JLabel titleLabel = new JLabel("Deadline Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.GRAY, 1)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridy++;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridy++;
        inputPanel.add(new JLabel("Due Date:"), gbc);
        gbc.gridy++;
        inputPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(titleField, gbc);
        gbc.gridy++;
        inputPanel.add(descriptionField, gbc);
        dueDateChooser.setPreferredSize(new Dimension(150, 25));
        gbc.gridy++;
        inputPanel.add(dueDateChooser, gbc);
        gbc.gridy++;
        inputPanel.add(statusComboBox, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton addButton = createButton("Add Deadline");
        addButton.addActionListener(e -> addDeadline());
        buttonPanel.add(addButton);

        JButton deleteButton = createButton("Delete Deadline");
        deleteButton.addActionListener(e -> deleteDeadline());
        buttonPanel.add(deleteButton);

        JButton viewButton = createButton("View Details");
        viewButton.addActionListener(e -> viewDeadlineDetails());
        buttonPanel.add(viewButton);

        JButton viewAllButton = createButton("View All Deadlines");
        viewAllButton.addActionListener(e -> viewAllDeadlines());
        buttonPanel.add(viewAllButton);

        JButton organizeButton = createButton("Organize Deadlines");
        organizeButton.addActionListener(e -> organizeDeadlines());
        buttonPanel.add(organizeButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.add(new JScrollPane(deadlineList), BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.GRAY, 1)));
        rightPanel.add(calendar, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.EAST);

        calendar.getDayChooser().addPropertyChangeListener("day", e -> {
            LocalDate selectedDate = calendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            updateDeadlineList(selectedDate);
        });

        deadlineList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Deadline selectedDeadline = deadlineList.getSelectedValue();
                if (selectedDeadline != null) {
                    calendar.setDate(java.util.Date.from(selectedDeadline.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            }
        });

        deadlineList.setCellRenderer(new DeadlineCellRenderer());

        refreshDeadlineList();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(55, 155, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Adjust padding
        button.setFocusPainted(false);
        return button;
    }
    

    private void refreshDeadlineList() {
        deadlineListModel.clear();
        List<Deadline> deadlines = deadlineDAO.getAllDeadlines();
        for (Deadline deadline : deadlines) {
            deadlineListModel.addElement(deadline);
        }
    }

    private void clearFields() {
        titleField.setText("");
        descriptionField.setText("");
        dueDateChooser.setDate(null);
        statusComboBox.setSelectedIndex(0);
    }

    private void addDeadline() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        LocalDate dueDate = null;
        if (dueDateChooser.getDate() != null) {
            dueDate = dueDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String status = (String) statusComboBox.getSelectedItem();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input text in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Deadline deadline = new Deadline(title, description, dueDate, status);
        deadlineDAO.addDeadline(deadline);
        refreshDeadlineList();
        clearFields();
    }

    private void deleteDeadline() {
        Deadline selectedDeadline = deadlineList.getSelectedValue();
        if (selectedDeadline != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this deadline?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = deadlineDAO.deleteDeadline(selectedDeadline);
                if (success) {
                    refreshDeadlineList();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete the deadline.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a deadline to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDeadlineDetails() {
        Deadline selectedDeadline = deadlineList.getSelectedValue();
        if (selectedDeadline != null) {
            JFrame viewDeadlineFrame = new JFrame("View Deadline Details");
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
    
            panel.add(new JLabel("Title:"));
            JTextField titleTextField = new JTextField(selectedDeadline.getTitle());
            panel.add(titleTextField);
    
            panel.add(new JLabel("Description:"));
            JTextField descriptionTextField = new JTextField(selectedDeadline.getDescription());
            panel.add(descriptionTextField);
    
            panel.add(new JLabel("Due Date:"));
            JDateChooser dueDateChooser = new JDateChooser();
            dueDateChooser.setDate(java.util.Date.from(selectedDeadline.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            panel.add(dueDateChooser);
    
            panel.add(new JLabel("Status:"));
            JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Urgent", "Important", "Not Important", "Expired", "Finished"});
            statusComboBox.setSelectedItem(selectedDeadline.getStatus());
            panel.add(statusComboBox);
    
            panel.add(new JLabel("Time Left:"));
            panel.add(new JLabel(selectedDeadline.getTimeLeft()));
    
            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(e -> {
                selectedDeadline.setTitle(titleTextField.getText());
                selectedDeadline.setDescription(descriptionTextField.getText());
                selectedDeadline.setDueDate(dueDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                selectedDeadline.setStatus((String) statusComboBox.getSelectedItem());
                deadlineDAO.updateDeadline(selectedDeadline);
                refreshDeadlineList();
                viewDeadlineFrame.dispose();
            });
            panel.add(updateButton);
    
            viewDeadlineFrame.add(panel);
            viewDeadlineFrame.pack();
            viewDeadlineFrame.setLocationRelativeTo(null);
            viewDeadlineFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a deadline to view its details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void updateDeadlineList(LocalDate selectedDate) {
        deadlineListModel.clear();
        List<Deadline> deadlines = deadlineDAO.getDeadlinesByDate(selectedDate);
        for (Deadline deadline : deadlines) {
            deadlineListModel.addElement(deadline);
        }
    }

    private void viewAllDeadlines() {
        List<Deadline> allDeadlines = deadlineDAO.getAllDeadlines();
        deadlineListModel.clear();
        for (Deadline deadline : allDeadlines) {
            deadlineListModel.addElement(deadline);
        }
    }

    private void organizeDeadlines() {
        List<Deadline> deadlines = new ArrayList<>();
        for (int i = 0; i < deadlineListModel.size(); i++) {
            deadlines.add(deadlineListModel.getElementAt(i));
        }

        // Sort deadlines based on the time left
        deadlines.sort(Comparator.comparingInt(deadline -> {
            String timeLeft = deadline.getTimeLeft();
            if (timeLeft.contains("years")) {
                return Integer.parseInt(timeLeft.split(" ")[0]) * 365; // Convert years to days
            } else if (timeLeft.contains("months")) {
                return Integer.parseInt(timeLeft.split(" ")[0]) * 30; // Convert months to days
            } else if (timeLeft.contains("days")) {
                return Integer.parseInt(timeLeft.split(" ")[0]);
            } else {
                return Integer.MAX_VALUE; // Handle other cases (e.g., "No due date specified")
            }
        }));

        // Update the deadline list model
        deadlineListModel.clear();
        for (Deadline deadline : deadlines) {
            deadlineListModel.addElement(deadline);
        }
    }

    private class DeadlineCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Deadline deadline = (Deadline) value;
            LocalDate currentDate = LocalDate.now();
            LocalDate dueDate = deadline.getDueDate();
            long daysLeft = ChronoUnit.DAYS.between(currentDate, dueDate);
            String status = deadline.getStatus();
            String labelText = deadline.getTitle() + " - Days Left: " + daysLeft + " - Status: " + status;
            setText(labelText);
    
            if (daysLeft <= 0) {
                setBackground(Color.GRAY); // Expired
            } else if (daysLeft <= 5) {
                setBackground(Color.RED); // Less than or equal to 5 days left
            } else if (daysLeft <= 10) {
                setBackground(Color.ORANGE); // Less than or equal to 10 days left
            } else if (daysLeft <= 15) {
                setBackground(Color.CYAN); // Less than or equal to 15 days left
            } else {
                setBackground(Color.GREEN); // More than 15 days left
            }
            return this;
        }
    }
    


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
