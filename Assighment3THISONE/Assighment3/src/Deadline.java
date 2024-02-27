import java.awt.Color;
import java.time.LocalDate;
import java.time.Period;

public class Deadline {
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private Color color; // New field for color

    public Deadline(String title, String description, LocalDate dueDate, String status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.color = Color.BLACK; // Default color is black
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and setter for color
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Mark deadline as finished
    public void markAsFinished() {
        status = "Finished";
    }

    // Check if deadline is expired
    public boolean isExpired() {
        return LocalDate.now().isAfter(dueDate);
    }

    // Get time left until deadline
    public String getTimeLeft() {
        if (dueDate == null) {
            return "No due date specified";
        }
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(currentDate, dueDate);
        if (period.isNegative()) {
            return "Deadline expired";
        } else if (period.isZero()) {
            return "Deadline today";
        } else {
            return period.getYears() + " years, " + period.getMonths() + " months, " + period.getDays() + " days left";
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
