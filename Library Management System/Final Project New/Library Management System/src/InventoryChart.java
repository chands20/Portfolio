import javax.swing.*;
import java.awt.*;
public class InventoryChart extends JFrame {

    //Instance variables
    private int[] stats;
    private String title;

    //Constructor
    public InventoryChart(int[] stats, String title) {
        this.stats = stats;
        this.title = title;
    }

    //Getters and Setters

    public int[] getStats() {
        return stats;
    }

    public void setStats(int[] stats) {
        this.stats = stats;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //Method
    public void displayGraph(){
        //Create frame
        JFrame frame = new JFrame(title);

        //Create Panel with BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Set dimensions
        int maxVal = Math.max(stats[0], stats[1]);
        int width = 500;
        int height = 400;
        int border = 50;
        int spacing = 50;

        //Create bars and add to panel
        for (int i = 1; i >= 0; i--) {
            int value = stats[i];

            String label;
            if (i == 0)
                label = "fiction: " + stats[0];
            else
                label = "nonfiction: " + stats[1];

            int barWidth = (int) (((double) value) / maxVal * (width - 2 * border));
            int barHeight = height / 2 - border;
            JLabel bar = new JLabel(label);
            bar.setPreferredSize(new Dimension(barWidth, barHeight));

            if (i == 0)
                bar.setBackground(Color.RED);
            else
                bar.setBackground(Color.BLUE);

            bar.setOpaque(true);
            JPanel barPanel = new JPanel();
            barPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            barPanel.add(bar);
            panel.add(barPanel);
        }

        //Add panel to frame, format, and display
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(width, height));
    }
}
