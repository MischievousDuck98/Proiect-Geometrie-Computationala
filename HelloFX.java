import javafx.application.*;
import java.awt.Point;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.shape.*;
import java.util.*;
import java.io.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
public class HelloFX extends Application {
    
    public static void main(String[] args) {
        launch();
    }
    
    @Override
    public void start(final Stage stage) {
        try{
            Scanner file = new Scanner(new File("input.txt"));
            String point = null;
            List<Point> pts = new ArrayList<>();
            while (file.hasNextLine()){
                  point = file.nextLine();
                  String[] addedPoint = point.split(" +");
                  Point convertedPoint = new Point(Integer.parseInt(addedPoint[0]), Integer.parseInt(addedPoint[1]));
                  pts.add(convertedPoint);
                  
            }
            file.close();
            List<Point> hull = ConvexHull(pts);
            Point withMaxX = Collections.max(hull, Comparator.comparingDouble(Point::getX));
            Point withMaxY = Collections.max(hull, Comparator.comparingDouble(Point::getY));
            Axes axes = new Axes(500, 500, -1*withMaxX.getX() - 2, withMaxX.getX() + 2, 1, -1*withMaxY.getY() - 2, withMaxY.getY() + 2, 1);
            Plot plot = new Plot(hull, axes);
            StackPane layout = new StackPane(plot);
            stage.setTitle("Convex Hull");
            layout.setPadding(new Insets(50));
            layout.setStyle("-fx-background-color: rgb(35, 39, 50)");
            stage.setScene(new Scene(layout, Color.rgb(35, 39, 50)));
            stage.show();
        }catch (FileNotFoundException e) { e.printStackTrace(); }
    }

      class Axes extends Pane {
        private NumberAxis xAxis;
        private NumberAxis yAxis;

        public Axes(
                int width, int height,
                double xLow, double xHi, double xTickUnit,
                double yLow, double yHi, double yTickUnit
        ) {
            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            setPrefSize(width, height);
            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

            xAxis = new NumberAxis(xLow, xHi, xTickUnit);
            xAxis.setSide(Side.BOTTOM);
            xAxis.setMinorTickVisible(false);
            xAxis.setPrefWidth(width);
            xAxis.setLayoutY(height / 2);

            yAxis = new NumberAxis(yLow, yHi, yTickUnit);
            yAxis.setSide(Side.LEFT);
            yAxis.setMinorTickVisible(false);
            yAxis.setPrefHeight(height);
            yAxis.layoutXProperty().bind(
                Bindings.subtract(
                    (width / 2) + 1,
                    yAxis.widthProperty()
                )
            );

            getChildren().setAll(xAxis, yAxis);
        }

        public NumberAxis getXAxis() {
            return xAxis;
        }

        public NumberAxis getYAxis() {
            return yAxis;
        }
    }

     class Plot extends Pane {
        public Plot(
                List<Point> poly, Axes axes
        ) {
            double x1, y1, x2, y2;
            Group group = new Group();
            for (int i = 0; i < poly.size() - 1; i ++) {
			x1 = poly.get(i).getX();
			y1 = poly.get(i).getY();
                        x2 = poly.get(i + 1).getX();
			y2 = poly.get(i + 1).getY();
                        Line line = new Line(mapX(x1, axes), mapY(y1, axes), mapX(x2, axes), mapY(y2, axes));
                        line.setStroke(Color.GREEN);
                        line.setStrokeWidth(5);
                        group.getChildren().add(line);
                        
            }
            x1 = poly.get(poly.size() - 1).getX();
	    y1 = poly.get(poly.size() - 1).getY();
            x2 = poly.get(0).getX();
	    y2 = poly.get(0).getY();
            Line line = new Line(mapX(x1, axes), mapY(y1, axes), mapX(x2, axes), mapY(y2, axes));
            line.setStroke(Color.GREEN);
            line.setStrokeWidth(5);
            group.getChildren().add(line);
	    setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            getChildren().setAll(axes, group);
        }

        private double mapX(double x, Axes axes) {
            double tx = axes.getPrefWidth() / 2;
            double sx = axes.getPrefWidth() / 
               (axes.getXAxis().getUpperBound() - 
                axes.getXAxis().getLowerBound());

            return x * sx + tx;
        }

        private double mapY(double y, Axes axes) {
            double ty = axes.getPrefHeight() / 2;
            double sy = axes.getPrefHeight() / 
                (axes.getYAxis().getUpperBound() - 
                 axes.getYAxis().getLowerBound());

            return -y * sy + ty;
        }
    }

    public static List<Point> ConvexHull(List<Point> p) {
        if (p.isEmpty()) return null;
        p.sort(Comparator.comparing(Point::getX));
        List<Point> h = new ArrayList<>();
 
        for (Point pt : p) {
            while (h.size() >= 2 && !ccw(h.get(h.size() - 2), h.get(h.size() - 1), pt)) {
                h.remove(h.size() - 1);
            }
            h.add(pt);
        }
 
        int t = h.size() + 1;
        for (int i = p.size() - 1; i >= 0; i--) {
            Point pt = p.get(i);
            while (h.size() >= t && !ccw(h.get(h.size() - 2), h.get(h.size() - 1), pt)) {
                h.remove(h.size() - 1);
            }
            h.add(pt);
        }
 
        h.remove(h.size() - 1);
        return h;
    }
 
    
    private static boolean ccw(Point a, Point b, Point c) {
        return ((b.x - a.x) * (c.y - a.y)) > ((b.y - a.y) * (c.x - a.x));
    }
}
