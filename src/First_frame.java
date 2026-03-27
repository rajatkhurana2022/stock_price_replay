import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

class First_frame extends JFrame {

    JPanel master_panel;
    JPanel panel1;
    DrawingPanel panel2;

    Timer timer;

    boolean time_coordinate_updated;
    boolean timer_on;

    public First_frame()
    {
        this.setLayout(new BorderLayout());

        // master_panel
        master_panel=new JPanel();

        master_panel.setLayout(new BorderLayout());
        this.getContentPane().add(master_panel, BorderLayout.CENTER);

        // panel1 will have some buttons
        panel1 = new JPanel();
        master_panel.add(panel1,BorderLayout.WEST);

        //panel1.setLayout(new BorderLayout());
        JButton test_button = new JButton("start/stop");
        //panel1.add(test_button,BorderLayout.EAST);
        panel1.add(test_button);

        JButton resetButton = new JButton("Reset");
        panel1.add(resetButton);

        JButton drawLineBtn = new JButton("Draw line");
        panel1.add(drawLineBtn);

        // panel2 is for drawing graphs

        panel2 = new DrawingPanel();
        master_panel.add(panel2, BorderLayout.CENTER);

        timer_on = false;
        panel2.timer_on = false;

        panel1.setPreferredSize(new Dimension(100,panel2.dp_y_size));
        panel2.setPreferredSize(new Dimension(panel2.dp_x_size,panel2.dp_y_size));

        this.pack();
        //upload graph data
        this.upload_data();
        this.process_data();
        //panel2.repaint();
        // convert data and update panel2(drawing panel)

        test_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(panel1,"click");
                time_coordinate_updated = true;
                panel2.repaint();

                if(timer_on)
                {
                    timer.stop();
                    timer_on = false;
                    panel2.timer_on = false;
                }
                else
                {
                    timer = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // draw the lines connecting points


                            panel2.visible_range_min++;
                            panel2.visible_range_max++;

                            if(panel2.visible_range_max == panel2.dates.size())
                            {
                                panel2.visible_range_max--;
                                panel2.visible_range_min--;
                                timer.stop();
                                timer_on = false;
                                panel2.timer_on = false;
                            }


                            upload_data();
                            process_data();

                            panel2.repaint();
                        }
                    });
                    timer.start();
                    timer_on = true;
                    panel2.timer_on = true;
                }

                //TimerTask task = new TimerRelatedUpdates();

            }
        });

        resetButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel2.visible_range_min = -1;
                panel2.repaint();
            }
        });

        drawLineBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!panel2.lineDrawingOn)
                {
                    panel2.lineDrawingOn = true;
                    drawLineBtn.setText("turn off");
                }
                else
                {
                    panel2.lineDrawingOn = false;
                    drawLineBtn.setText("Draw Line");
                }
            }
        });

        panel2.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                panel2.pointerx = (float) e.getX();
                panel2.pointery = (float) e.getY();
                panel2.repaint();
            }
        });

        panel2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel2.requestFocus();
                if(panel2.lineDrawingOn)
                {
                    if(panel2.lineStartDrawn)
                    {
                        panel2.updateTrendLineData(panel2.tempx1,panel2.tempy1,(float) e.getX(),(float)e.getY());
                        panel2.repaint();
                        panel2.lineStartDrawn = false;
                    }
                    else
                    {
                        panel2.tempx1 = (float) e.getX();
                        panel2.tempy1 = (float) e.getY();
                        panel2.repaint();
                        panel2.lineStartDrawn = true;

                    }
                }
                else
                {
                    // check distance from point
                    for(int i = 0;i<panel2.trendlinesx1.size();i++)
                    {
                        Float x1 = panel2.data_to_point_x(panel2.trendlinesx1.get(i));
                        Float x2 = panel2.data_to_point_x(panel2.trendlinesx2.get(i));
                        Float y1 = panel2.data_to_point_y(panel2.trendlinesy1.get(i));
                        Float y2 = panel2.data_to_point_y(panel2.trendlinesy2.get(i));

                        Float dist = (float) Line2D.ptSegDist(x1,y1,x2,y2,e.getX(),e.getY());
                        if(dist<4f)
                        {
                            System.out.println("line no "+i);

                            if(panel2.highlighted_trendlines.indexOf(i)!= -1)
                            {
                                panel2.highlighted_trendlines.remove(panel2.highlighted_trendlines.indexOf(i));
                            }
                            else {
                                panel2.highlighted_trendlines.add(i);
                                panel2.requestFocus();
                            }
                            System.out.println(panel2.highlighted_trendlines);
                            panel2.repaint();
                        }

                    }

                    //Line2D.ptSegDist();
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        panel2.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE && !panel2.lineStartDrawn)
                {
                    for(int i=0;i<panel2.highlighted_trendlines.size();i++)
                    {
                        int trendline_number = panel2.highlighted_trendlines.get(i);
                        panel2.trendlinesx1.remove(trendline_number);
                        panel2.trendlinesx2.remove(trendline_number);
                        panel2.trendlinesy1.remove(trendline_number);
                        panel2.trendlinesy2.remove(trendline_number);
                        panel2.highlighted_trendlines.remove((Integer) trendline_number);

                        panel2.repaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void process_data()
    {

    }

    public void upload_data()
    {
        panel2.dates = new ArrayList<>();
        panel2.y_coordinates = new ArrayList<>();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader("C:/Users/rajat/Downloads/NIFTY 50-13-02-2026-to-13-03-2026.csv"));
            String line = br.readLine();
            line = br.readLine();
            while(line!=null) {
                String[] data_per_line = line.split(",");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH);
                LocalDate ld = LocalDate.parse(data_per_line[0],formatter);
                panel2.dates.add(ld);
                //System.out.println(ld.toString());

                float y_coordinate = Float.parseFloat(data_per_line[1]);
                panel2.y_coordinates.add(y_coordinate);
                //System.out.println(y_coordinate);

                line = br.readLine();
            }
            panel2.data_uploaded = true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }
}