import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

class DrawingPanel extends JPanel
{
    Float min_y_coordinate;
    Float max_y_coordinate;
    Float min_x_coordinate;
    Float max_x_coordinate;
    float origin_distance_from_top;
    float origin_distance_from_left;

    Float pointerx;
    Float pointery;
    public boolean lineStartDrawn;
    ArrayList<Float> trendlinesx1 = new ArrayList<>();
    ArrayList<Float> trendlinesy1 = new ArrayList<>();
    ArrayList<Float> trendlinesx2 = new ArrayList<>();
    ArrayList<Float> trendlinesy2 = new ArrayList<>();

    Float tempx1;
    Float tempy1;
    
    public boolean lineDrawingOn = false;
    boolean data_uploaded;

    int visible_range_min = -1;
    int visible_range_max = -1;

    boolean timer_on;

    Integer dp_x_size = 500;
    Integer dp_y_size = 500;

    ArrayList<Float> x_coordinates;
    ArrayList<Float> y_coordinates;

    ArrayList<Float> plottable_x_coordinates;
    ArrayList<Float> plottable_y_coordinates;

    Float drawing_area_x_length;
    Float drawing_area_y_length;

    ArrayList<Float> distances_from_left;
    ArrayList<Float> distances_from_top;

    public DrawingPanel()
    {
        timer_on = false;
    }
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        setInitialVisibleRange();

        if(this.data_uploaded)
        {
            //System.out.println(visible_range_max);
            this.plottable_x_coordinates = new ArrayList<>(this.x_coordinates.subList(this.visible_range_min,this.visible_range_max+1));
            this.plottable_y_coordinates = new ArrayList<>(this.y_coordinates.subList(this.visible_range_min,this.visible_range_max+1));

            //System.out.println(this.plottable_x_coordinates);

            max_x_coordinate = Collections.max(this.plottable_x_coordinates);
            min_x_coordinate = Collections.min(this.plottable_x_coordinates);

            max_y_coordinate = Collections.max(this.plottable_y_coordinates);
            min_y_coordinate = Collections.min(this.plottable_y_coordinates);

            this.dp_x_size = this.getWidth();
            this.dp_y_size = this.getHeight();

            //System.out.println(this.getWidth());

            this.drawing_area_x_length = (0.8F*this.dp_x_size.floatValue());
            this.drawing_area_y_length = (0.8F*this.dp_y_size.floatValue());

            //System.out.println(this.drawing_area_x_length);

            this.distances_from_left = new ArrayList<>();

            //System.out.println(this.plottable_x_coordinates.size());

            for(Float x_coordinate : this.plottable_x_coordinates)
            {
                this.distances_from_left.add(this.drawing_area_x_length*(x_coordinate-min_x_coordinate)/(max_x_coordinate-min_x_coordinate));
                //System.out.println(this.distances_from_left.getLast());
            }

            this.distances_from_top = new ArrayList<>();

            for(Float y_coordinate : this.plottable_y_coordinates)
            {
                Float distance_from_bottom = this.drawing_area_y_length*(y_coordinate-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
                this.distances_from_top.add(this.drawing_area_y_length-distance_from_bottom);
                //System.out.println(this.distances_from_top.getLast());
            }

            this.setBackground(Color.WHITE);
            origin_distance_from_left = (float) (0.20*this.getWidth());
            origin_distance_from_top = (float) (0.80*this.getHeight());
            float x_axis_end_point_from_left = (float) this.getWidth();
            float x_axis_end_point_from_top = origin_distance_from_top;
            float y_axis_end_point_from_top = 0F;

            //System.out.println(origin_distance_from_top);

            Line2D x_axis = new Line2D.Float(origin_distance_from_left,origin_distance_from_top, x_axis_end_point_from_left, x_axis_end_point_from_top);
            g2d.draw(x_axis);

            Line2D y_axis = new Line2D.Float(origin_distance_from_left,origin_distance_from_top,origin_distance_from_left,y_axis_end_point_from_top);
            g2d.draw(y_axis);

            for(int i = 0; i<distances_from_left.size()-1;i++)
            {
                Line2D line1 = new Line2D.Float(origin_distance_from_left+distances_from_left.get(i), distances_from_top.get(i),origin_distance_from_left+distances_from_left.get(i+1),distances_from_top.get(i+1));
                g2d.draw(line1);
            }

            // draw x axis points - 10 total
            float x_points_interval = (x_axis_end_point_from_left-origin_distance_from_left)/9;

            Float a = Collections.min(plottable_x_coordinates);
            Float b = Collections.max(plottable_x_coordinates);

            float x_labels_interval = (b-a)/9;

            for(int i = 0; i<9; i++)
            {
                Float f = a+i*x_labels_interval;
                g2d.drawString(f.toString(), (int) (origin_distance_from_left+i*x_points_interval), (int) (this.getHeight()*0.9));
            }

            float y_points_interval = origin_distance_from_top/9;

            a = Collections.min(plottable_y_coordinates);
            b = Collections.max(plottable_y_coordinates);

            float y_labels_interval = (b-a)/9;

            for(int i=0;i<9;i++)
            {
                Float f = a+i*y_labels_interval;
                DecimalFormat df = new DecimalFormat("#.00");
                Float formatted_f = Float.valueOf((df.format(f))); // Output: 123.14
                g2d.drawString(formatted_f.toString(),origin_distance_from_left/2,origin_distance_from_top-i*y_points_interval);
            }

            if(lineDrawingOn && lineStartDrawn)
            {
                Line2D line1 = new Line2D.Float(tempx1,tempy1,pointerx,pointery);
                g2d.draw(line1);
            }

            // draw trend lines
            for(int i = 0;i<trendlinesx1.size();i++)
            {
                float point_x1 = origin_distance_from_left+this.drawing_area_x_length*(trendlinesx1.get(i)-min_x_coordinate)/(max_x_coordinate-min_x_coordinate);
                float point_x2 = origin_distance_from_left+this.drawing_area_x_length*(trendlinesx2.get(i)-min_x_coordinate)/(max_x_coordinate-min_x_coordinate);

                float distance_from_bottom = this.drawing_area_y_length*(trendlinesy1.get(i)-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
                float point_y1 = this.drawing_area_y_length-distance_from_bottom;

                distance_from_bottom = this.drawing_area_y_length*(trendlinesy2.get(i)-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
                float point_y2 = this.drawing_area_y_length-distance_from_bottom;

                Line2D l = new Line2D.Float(point_x1,point_y1,point_x2,point_y2);
                g2d.draw(l);
            }
        }

    }

    public Float data_to_point_y(Float data_y)
    {
        float distance_from_bottom = this.drawing_area_y_length*(data_y-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
        float point_y1 = this.drawing_area_y_length-distance_from_bottom;
        return point_y1;
    }

    public Float data_to_point_x(Float data_x)
    {
        float point_x = origin_distance_from_left+this.drawing_area_x_length*(data_x-min_x_coordinate)/(max_x_coordinate-min_x_coordinate);
        return point_x;
    }

    public void setInitialVisibleRange()
    {
        if(this.data_uploaded) {
            if (this.visible_range_min == -1) {

                if (this.x_coordinates.size() > 10) {
                    // this.visible_range_max = this.x_coordinates.size() - 1;
                    // this.visible_range_min = this.x_coordinates.size() - 10;

                    this.visible_range_max = 9;
                    this.visible_range_min = 0;

                    //System.out.println(this.visible_range_min);
                } else {
                    this.visible_range_min = 0;
                    this.visible_range_max = this.x_coordinates.size() - 1;
                }

            }
        }
    }

    public void updateTrendLineData(Float tempx1, Float tempy1, Float x2, Float y2)
    {
        Float max_x_coordinate = Collections.max(this.plottable_x_coordinates);
        Float min_x_coordinate = Collections.min(this.plottable_x_coordinates);

        Float max_y_coordinate = Collections.max(this.plottable_y_coordinates);
        Float min_y_coordinate = Collections.min(this.plottable_y_coordinates);

        Float origin_distance_from_left = (float) (0.20*this.getWidth());
        Float origin_distance_from_top = (float) (0.80*this.getHeight());

        Float data_x1 = min_x_coordinate+(max_x_coordinate-min_x_coordinate)*(tempx1-origin_distance_from_left)/(this.getWidth()-origin_distance_from_left);
        this.trendlinesx1.add(data_x1);

        Float data_x2 = min_x_coordinate+(max_x_coordinate-min_x_coordinate)*(x2-origin_distance_from_left)/(this.getWidth()-origin_distance_from_left);
        this.trendlinesx2.add(data_x2);

        Float data_y1 = min_y_coordinate+(max_y_coordinate-min_y_coordinate)*(origin_distance_from_top-tempy1)/(origin_distance_from_top);
        this.trendlinesy1.add(data_y1);

        Float data_y2 = min_y_coordinate+(max_y_coordinate-min_y_coordinate)*(origin_distance_from_top-y2)/(origin_distance_from_top);
        this.trendlinesy2.add(data_y2);


        //System.out.println(data_x1);
        //System.out.println(data_x2);
        //System.out.println(data_y1);
        //System.out.println(data_y2);

    }
}
