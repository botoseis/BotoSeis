/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package botoseis.iview.utils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import de.siegmar.fastcsv.writer.CsvWriter;
import gfx.SVPoint2D;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import usrdata.SUHeader;
import usrdata.SUSection;
import usrdata.SUTrace;

/**
 *
 * @author cadu
 */
public class PicksFileIO {

    private static class SVPoint2DComparator implements java.util.Comparator<gfx.SVPoint2D> {

        private static SVPoint2DComparator instance;

        private SVPoint2DComparator() {
        }

        public static SVPoint2DComparator getInstance() {
            if (instance == null) {
                instance = new SVPoint2DComparator();
            }
            return instance;
        }

        @Override
        public int compare(SVPoint2D o1, SVPoint2D o2) {
            return Float.compare(o1.fx, o2.fx);
        }
    }

    public static void printt(Object x) {
        System.err.println(x);
    }

    public static SVPoint2D findIntersection_traceXcoordinate_picksLineChart(float traceXcoordinate, List<SVPoint2D> picksCoordinates) {

        // if outside pick curve's range, exit already
        if (traceXcoordinate < picksCoordinates.get(0).fx || traceXcoordinate > picksCoordinates.get(picksCoordinates.size() - 1).fx) {
            printt(String.format("x: %.2f    OUTSIDE PICKS CHART", traceXcoordinate));
            return null;
        }
        SVPoint2D intersectionPoint = new SVPoint2D();
        // else, we already know that the X coordinate of the interception point
        // will be traceXcoordinate
        intersectionPoint.fx = traceXcoordinate;

        SVPoint2D traceXcoordinatePoint2D = new SVPoint2D();
        traceXcoordinatePoint2D.fx = traceXcoordinate;
        // Collections.binarySearch
        //   if the search key is not contained in the list, returns (insertion point) - 1),
        //   where insertion point is the index of the first element greater than the key
        int insertionIndex = Collections.binarySearch(picksCoordinates, traceXcoordinatePoint2D, SVPoint2DComparator.getInstance());
        if (insertionIndex < 0) {
            insertionIndex = -insertionIndex - 1;
        }

        SVPoint2D lineSegmentStart = picksCoordinates.get(insertionIndex - 1);
        SVPoint2D lineSegmentEnd = picksCoordinates.get(insertionIndex);

        intersectionPoint.fy = findIntersectionY_verticalLine_lineSegment(traceXcoordinate, lineSegmentStart, lineSegmentEnd);

        return intersectionPoint;
    }

    private static float findIntersectionY_verticalLine_lineSegment(float x, SVPoint2D endpoint1, SVPoint2D endpoint2) {
        // m = (y_2 - y_1) / (x_2 - x_1)
        float m = (endpoint2.fy - endpoint1.fy) / (endpoint2.fx - endpoint1.fx);
        //    m = (y - y_1) / (x - x_1)
        // => y - y_1 = m (x - x_1)
        // => y = m (x - x_1) + y_1
        return m * (x - endpoint1.fx) + endpoint1.fy;
    }

    public static List<SVPoint2D> computeTraceIntersectionPoints(List<SVPoint2D> pickCoordinates, SUSection section) {
        if (pickCoordinates.size() < 2) {
            throw new IllegalArgumentException("pickCoordinates must contain at least 2 elements.");
        }
        
        printt("toTraceInterceptionPointsList()");
        float[] tracesXcoordinates = toTracesXcoordinates(section);

        List<SVPoint2D> traceIntersectionPoints = new ArrayList<>();

        // Extend left
        // -----------
        SVPoint2D intersectionLeft = new SVPoint2D();
        int indexL = Arrays.binarySearch(tracesXcoordinates, pickCoordinates.get(0).fx);
        if (indexL >= 0) {
            // if it is contained in the array, returned index of the search key
            intersectionLeft.fx = tracesXcoordinates[indexL];
        } else {
            // otherwise, returned (-(insertion point) - 1)
            //   The insertion point is defined as the point at which the key would be inserted
            //   into the array: the index of the first element greater than the key
            int insertionPoint = -indexL - 1;
            intersectionLeft.fx = tracesXcoordinates[insertionPoint - 1];
        }
        intersectionLeft.fy = findIntersectionY_verticalLine_lineSegment(
                intersectionLeft.fx,
                pickCoordinates.get(0),
                pickCoordinates.get(1));
        traceIntersectionPoints.add(intersectionLeft);

        for (int i = 0; i < tracesXcoordinates.length; i++) {
            SVPoint2D intersection = findIntersection_traceXcoordinate_picksLineChart(tracesXcoordinates[i], pickCoordinates);
            if (intersection != null) {
                traceIntersectionPoints.add(intersection);
            }
        }
        
        // Extend right
        // ------------
        SVPoint2D intersectionRight = new SVPoint2D();
        int indexR = Arrays.binarySearch(tracesXcoordinates, pickCoordinates.get(pickCoordinates.size() - 1).fx);
        if (indexR >= 0) {
            intersectionRight.fx = tracesXcoordinates[indexR];
        } else {
            int insertionPoint = -indexR - 1;
            intersectionRight.fx = tracesXcoordinates[insertionPoint];
        }
        intersectionRight.fy = findIntersectionY_verticalLine_lineSegment(
                intersectionRight.fx,
                pickCoordinates.get(pickCoordinates.size() - 2),
                pickCoordinates.get(pickCoordinates.size() - 1));
        traceIntersectionPoints.add(intersectionRight);

        return traceIntersectionPoints;
    }

    private static float[] toTracesXcoordinates(SUSection section) {
        int numTraces = section.getN2();
        float firstTraceLocation = section.getF2();
        float spacingBetweenTraces = section.getD2();
        float[] tracesGraphicalXcoordinates = new float[numTraces];
        for (int i = 0; i < numTraces; i++) {
            tracesGraphicalXcoordinates[i] = spacingBetweenTraces * i + firstTraceLocation;
        }
        return tracesGraphicalXcoordinates;
    }

    public void writePicksToFile(String file, List<SVPoint2D> picksList, SUSection section) {

        List<SVPoint2D> picksAtTraces = computeTraceIntersectionPoints(picksList, section);
        printt("picksList.size(): " + picksList.size());
        printt("traceIntersectionPoints.size(): " + picksAtTraces.size());

        try (CsvWriter csvWriter = CsvWriter.builder().build(Paths.get(file))) {
            // Write header
            csvWriter.writeRecord("FFID", "SLOC", "CHAN", "TIME");

            // Write columns
            for (int i = 0; i < picksAtTraces.size(); i++) {
                SVPoint2D pick = picksAtTraces.get(i);
                int traceIndex = computeNearestTraceIndex(pick, section);
                SUHeader header = section.getTraces().get(traceIndex).getHeader();

                // fldr é sequencial, mas não obrigatoriamente sem falhas na sequencia
                // ep
                //   número da estação
                //   não obrigatoriamente sem faltas na sequencia
                csvWriter.writeRecord(
                        String.valueOf(header.fldr),
                        String.valueOf(header.ep),
                        String.valueOf(header.tracf),
                        String.valueOf(pick.fy)
                );

            }
        } catch (IOException ex) {
            Logger.getLogger(PicksFileIO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void readFileToPicks(String filename) {
        Path file = Paths.get(filename);
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(file)) {
            csvReader.forEach(
                    record -> System.out.println(record.getFields())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeCSV(SimpleDataFrame df) throws IOException {
        try (CsvWriter csvWriter = CsvWriter.builder().build(Paths.get("/home/cadu/Documents/output.csv"))) {
            csvWriter.writeRecord(df.getHeaders());

            for (int i = 0; i < df.getRowCount(); i++) {
                csvWriter.writeRecord(df.getRow(i).stream().map(String::valueOf).collect(Collectors.toList()));
            }
        }
    }

    public int computeNearestTraceIndex(SVPoint2D mouseLocation, SUSection section) {

        // SU: f2: first trace location
        float XlenghtHI = section.getF2();  // = H(1).sx / scalco

        // SU: d2: sample spacing between traces
        float distanciax = section.getD2();  // 0.025 km = 25 m
        float delrt = section.getF1();  // primeiro tempo do matlab = 0

        float dt = section.getD1(); // = dt do matlab = 0.004

        int XMatr = Math.round((mouseLocation.fx - XlenghtHI) / distanciax) + 1;
        // número de amostra temporal do mouse
        int YMatr = Math.round((mouseLocation.fy - delrt) / dt) + 1;

        int n_linhamaximo = section.getN1();
        int n = YMatr - 1 + (XMatr - 1) * (n_linhamaximo);

        int trace = n / section.getN1();

        return trace;
    }

}
