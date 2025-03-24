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

//    public void foo(SUSection section, List<SVPoint2D> picksList) {
//        printt("foo()");
//        List<SUTrace> traces = section.getTraces();
//        printt("  section.getTraces().size(): " + section.getTraces().size());
//        float[] offsets = new float[traces.size()];
//        for (int i = 0; i < traces.size(); i++) {
//            offsets[i] = traces.get(i).getHeader().offset;
//            isXcontainedInPickCurve(offsets[i], picksList);
//        }
//    }
//    private float[] picksListToOffsetsList() {
//        float[] pickOffsets
//    }
//    private float isXcontainedInPickCurve(float x, List<SVPoint2D> picksList) {
//
//        // if outside pick curve's range, exit already
//        if (x < picksList.get(0).fx || x > picksList.get(picksList.size() - 1).fx) {
//            printt(String.format("x: %.2f", x));
//            return 0;
//        }
//
//        SVPoint2D xPoint = new SVPoint2D();
//        xPoint.fx = x;
//
//        int insertionPoint = Collections.binarySearch(picksList, xPoint, SVPoint2DComparator.getInstance());
//        if (insertionPoint < 0) {
//            insertionPoint = -insertionPoint - 1;
//        }
//        printt(String.format("x: %.2f | insertion point value: %.2f", x, picksList.get(insertionPoint)));
//
    ////        for (int i = 0; i < picksList.size() - 1; i++) {
////            SVPoint2D lineStart = picksList.get(i);
////            SVPoint2D lineEnd = picksList.get(i + 1);
////        }
//        return 0;
//    }
    
//    public static float computeIntersection_pickXcoordinate_tracesXcoordinates(float traceXcoordinate, float[] tracesXcoordinates) {
//
//        int insertionPoint = Collections.binarySearch(picksList, xPoint, SVPoint2DComparator.getInstance());
//        if (insertionPoint < 0) {
//            insertionPoint = -insertionPoint - 1;
//        }
//
//    }
        
    
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
        SVPoint2D enpointLeft = picksCoordinates.get(insertionIndex - 1);
        SVPoint2D enpointRight = picksCoordinates.get(insertionIndex);

        printt("insertionIndex: " + insertionIndex);
//        printt("insertionIndex: " + picksCoordinates.get(insertionIndex));
        return null;
    }
    
    private static float findIntersection_verticalLine_lineSegment(float x, SVPoint2D endpointLeft, SVPoint2D endpointRight) {
//        float lineSlope = 
    }

    public static void toTraceIntersectionPointsList(List<SVPoint2D> pickCoordinates, SUSection section) {
        printt("toTraceInterceptionPointsList()");
        float[] tracesXcoordinates = toTracesXcoordinates(section);

        List<SVPoint2D> traceIntersectionPoints = new ArrayList<>();
        for (int i = 0; i < tracesXcoordinates.length; i++) {
//            SVPoint2D intersectionPoint =
            findIntersection_traceXcoordinate_picksLineChart(tracesXcoordinates[i], pickCoordinates);
//            traceIntersectionPoints.add();

        }
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

//        foo(section, picksList);
        toTraceIntersectionPointsList(picksList, section);
        System.err.println("picksList.size(): " + picksList.size());

        try (CsvWriter csvWriter = CsvWriter.builder().build(Paths.get(file))) {
            // Write header
            csvWriter.writeRecord("FFID", "SLOC", "CHAN", "TIME");

            // Write columns
            for (int i = 0; i < picksList.size(); i++) {
                SVPoint2D pick = picksList.get(i);
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
