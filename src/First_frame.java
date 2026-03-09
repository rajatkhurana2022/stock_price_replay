import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

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

                            if(panel2.visible_range_max == panel2.x_coordinates.size())
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
        panel2.x_coordinates = new ArrayList<>();
        panel2.y_coordinates = new ArrayList<>();

        panel2.x_coordinates.add(1F);
        panel2.x_coordinates.add(2F);
        panel2.x_coordinates.add(3F);
        panel2.x_coordinates.add(4F);
        panel2.x_coordinates.add(5F);
        panel2.x_coordinates.add(6F);
        panel2.x_coordinates.add(7F);
        panel2.x_coordinates.add(8F);
        panel2.x_coordinates.add(9F);
        panel2.x_coordinates.add(10F);
        panel2.x_coordinates.add(11F);
        panel2.x_coordinates.add(12F);
        panel2.x_coordinates.add(13F);
        panel2.x_coordinates.add(14F);
        panel2.x_coordinates.add(15F);
        panel2.x_coordinates.add(16F);
        panel2.x_coordinates.add(17F);
        panel2.x_coordinates.add(18F);
        panel2.x_coordinates.add(19F);
        panel2.x_coordinates.add(20F);
        panel2.x_coordinates.add(21F);
        panel2.x_coordinates.add(22F);
        panel2.x_coordinates.add(23F);
        panel2.x_coordinates.add(24F);
        panel2.x_coordinates.add(25F);

        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(3F);
        panel2.y_coordinates.add(7.8F);
        panel2.y_coordinates.add(13.2F);
        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(15F);
        panel2.y_coordinates.add(3F);
        panel2.y_coordinates.add(7.8F);
        panel2.y_coordinates.add(13.2F);
        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(20F);
        panel2.y_coordinates.add(7.8F);
        panel2.y_coordinates.add(13.2F);
        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(1F);
        panel2.y_coordinates.add(3F);
        panel2.y_coordinates.add(4F);
        panel2.y_coordinates.add(7F);
        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(6F);
        panel2.y_coordinates.add(3F);
        panel2.y_coordinates.add(4F);
        panel2.y_coordinates.add(5F);
        panel2.y_coordinates.add(2F);


        //System.out.println(panel2.x_coordinates.size());



        panel2.data_uploaded = true;
    }
}