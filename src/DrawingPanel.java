import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

class DrawingPanel extends JPanel
{
    Float min_y_coordinate;
    Float max_y_coordinate;
    LocalDate min_date;
    LocalDate max_date;
    float origin_distance_from_top;
    float origin_distance_from_left;

    Float pointerx;
    Float pointery;
    public boolean lineStartDrawn;
    ArrayList<LocalDate> trendlinesx1 = new ArrayList<>();
    ArrayList<Float> trendlinesy1 = new ArrayList<>();
    ArrayList<LocalDate> trendlinesx2 = new ArrayList<>();
    ArrayList<Float> trendlinesy2 = new ArrayList<>();

    // highlighted trendline numbers
    ArrayList<Integer>highlighted_trendlines = new ArrayList<>();

    Float tempx1;
    Float tempy1;
    
    public boolean lineDrawingOn = false;
    boolean data_uploaded;

    int visible_range_min = -1;
    int visible_range_max = -1;

    boolean timer_on;

    Integer dp_x_size = 500;
    Integer dp_y_size = 500;

    ArrayList<LocalDate> dates;
    ArrayList<Float> y_coordinates;

    ArrayList<LocalDate> visible_dates;
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
            this.visible_dates = new ArrayList<>(this.dates.subList(this.visible_range_min,this.visible_range_max+1));
            this.plottable_y_coordinates = new ArrayList<>(this.y_coordinates.subList(this.visible_range_min,this.visible_range_max+1));

            max_date = Collections.max(this.visible_dates);
            min_date = Collections.min(this.visible_dates);

            max_y_coordinate = Collections.max(this.plottable_y_coordinates);
            min_y_coordinate = Collections.min(this.plottable_y_coordinates);

            this.dp_x_size = this.getWidth();
            this.dp_y_size = this.getHeight();

            this.drawing_area_x_length = (0.8F*this.dp_x_size.floatValue());
            this.drawing_area_y_length = (0.8F*this.dp_y_size.floatValue());

            this.distances_from_left = new ArrayList<>();

            for(LocalDate date : this.visible_dates)
            {
                this.distances_from_left.add(this.drawing_area_x_length*(ChronoUnit.DAYS.between(min_date,date))/(ChronoUnit.DAYS.between(min_date,max_date)));
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

            //System.out.println(distances_from_left.size());
            //System.out.println(distances_from_top.size());

            for(int i = 0; i<distances_from_left.size()-1;i++)
            {
                Line2D line1 = new Line2D.Float(origin_distance_from_left+distances_from_left.get(i), distances_from_top.get(i),origin_distance_from_left+distances_from_left.get(i+1),distances_from_top.get(i+1));
                g2d.draw(line1);
            }

            // draw x axis points - 10 total

            float x_points_interval = (x_axis_end_point_from_left-origin_distance_from_left)/9;

            LocalDate a = Collections.min(visible_dates);
            LocalDate b = Collections.max(visible_dates);

            int x_labels_interval = (int) ChronoUnit.DAYS.between(a,b)/9;

            for(int i = 0; i<9; i++)
            {
                LocalDate d = a.plusDays(i*x_labels_interval);
                g2d.drawString(d.toString(), (int) (origin_distance_from_left+i*x_points_interval), (int) (this.getHeight()*0.9));
            }

            float y_points_interval = origin_distance_from_top/9;

            float c = Collections.min(plottable_y_coordinates);
            float d = Collections.max(plottable_y_coordinates);

            float y_labels_interval = (d-c)/9;

            for(int i=0;i<9;i++)
            {
                Float f = c+i*y_labels_interval;
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
                //float point_x1 = origin_distance_from_left+this.drawing_area_x_length*(trendlinesx1.get(i)-min_x_coordinate)/(max_x_coordinate-min_x_coordinate);
                float point_x1 = data_to_point_x(trendlinesx1.get(i));
                //float point_x2 = origin_distance_from_left+this.drawing_area_x_length*(trendlinesx2.get(i)-min_x_coordinate)/(max_x_coordinate-min_x_coordinate);
                float point_x2 = data_to_point_x(trendlinesx2.get(i));

                float distance_from_bottom = this.drawing_area_y_length*(trendlinesy1.get(i)-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
                float point_y1 = this.drawing_area_y_length-distance_from_bottom;

                distance_from_bottom = this.drawing_area_y_length*(trendlinesy2.get(i)-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
                float point_y2 = this.drawing_area_y_length-distance_from_bottom;


                Line2D l = new Line2D.Float(point_x1,point_y1,point_x2,point_y2);

                if(highlighted_trendlines.contains(i)) {
                    g2d.setColor(Color.RED);
                }
                g2d.draw(l);
                g2d.setColor(Color.BLACK);
            }
        }

    }

    public Float data_to_point_y(Float data_y)
    {
        float distance_from_bottom = this.drawing_area_y_length*(data_y-min_y_coordinate)/(max_y_coordinate-min_y_coordinate);
        float point_y1 = this.drawing_area_y_length-distance_from_bottom;
        return point_y1;
    }

    public Float data_to_point_x(LocalDate data_x)
    {
        float point_x = origin_distance_from_left+this.drawing_area_x_length*(ChronoUnit.DAYS.between(min_date,data_x))/(ChronoUnit.DAYS.between(min_date,max_date));
        return point_x;
    }

    public void setInitialVisibleRange()
    {
        if(this.data_uploaded) {
            if (this.visible_range_min == -1) {

                if (this.dates.size() > 15) {
                    // this.visible_range_max = this.x_coordinates.size() - 1;
                    // this.visible_range_min = this.x_coordinates.size() - 10;

                    this.visible_range_max = 14;
                    this.visible_range_min = 0;

                    //System.out.println(this.visible_range_min);
                } else {
                    this.visible_range_min = 0;
                    this.visible_range_max = this.dates.size() - 1;
                }

            }
        }
    }

    public void updateTrendLineData(Float tempx1, Float tempy1, Float x2, Float y2)
    {
        LocalDate max_x_coordinate = Collections.max(this.visible_dates);
        LocalDate min_x_coordinate = Collections.min(this.visible_dates);

        Float max_y_coordinate = Collections.max(this.plottable_y_coordinates);
        Float min_y_coordinate = Collections.min(this.plottable_y_coordinates);

        Float origin_distance_from_left = (float) (0.20*this.getWidth());
        Float origin_distance_from_top = (float) (0.80*this.getHeight());

        LocalDate data_x1 = min_x_coordinate.plusDays((long) (ChronoUnit.DAYS.between(min_x_coordinate,max_x_coordinate)*(tempx1-origin_distance_from_left)/(this.getWidth()-origin_distance_from_left)));
        this.trendlinesx1.add(data_x1);

        LocalDate data_x2 = min_x_coordinate.plusDays((long) (ChronoUnit.DAYS.between(min_x_coordinate,max_x_coordinate)*(x2-origin_distance_from_left)/(this.getWidth()-origin_distance_from_left)));
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
