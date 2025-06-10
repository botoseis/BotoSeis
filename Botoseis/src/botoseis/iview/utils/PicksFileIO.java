/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package botoseis.iview.utils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import de.siegmar.fastcsv.writer.CsvWriter;
import gfx.SVPoint2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import usrdata.SUHeader;
import usrdata.SUSection;

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

    public static void printt(Object... x) {
        if (x.length == 1) {
            System.err.println(x[0]);
        } else {
            System.err.println(Arrays.toString(x));
        }
    }

    public static SVPoint2D findIntersection_traceXcoordinate_picksLineChart(float traceXcoordinate, List<SVPoint2D> picksCoordinates) {

        // if outside pick curve's range, exit already
        if (traceXcoordinate < picksCoordinates.get(0).fx || traceXcoordinate > picksCoordinates.get(picksCoordinates.size() - 1).fx) {
            return null;
        }
        SVPoint2D intersectionPoint = new SVPoint2D();
        // else, we already know that the X coordinate of the interception point
        // will be traceXcoordinate
        intersectionPoint.fx = traceXcoordinate;

        SVPoint2D traceXcoordinatePoint2D = new SVPoint2D();
        traceXcoordinatePoint2D.fx = traceXcoordinate;

        int result = Collections.binarySearch(picksCoordinates, traceXcoordinatePoint2D, SVPoint2DComparator.getInstance());

        // Collections.binarySearch(...)
        // the return value will be >= 0 if and only if the key is found
        if (result < 0) {
            // if the search key is not contained in the list
            //   returns (-(insertion point) - 1). The insertion point is defined as the point at
            //   which the key would be inserted into the list: the index of the first element
            //   greater than the key, or list.size() if all elements in the list are less than the
            //   specified key.
            int insertionPoint = -result - 1;
            SVPoint2D lineSegmentStart = picksCoordinates.get(insertionPoint - 1);
            SVPoint2D lineSegmentEnd = picksCoordinates.get(insertionPoint);

            intersectionPoint.fy = findIntersectionY_verticalLine_lineSegment(traceXcoordinate, lineSegmentStart, lineSegmentEnd);

            return intersectionPoint;
        } else {
            // if the search key is contained in the list
            //   returns the index of the search key

            int searchKeyIndex = result;
            // if the pick that was found is the last pick (searchKey is the last index)
            if (searchKeyIndex == picksCoordinates.size() - 1) {
                SVPoint2D lineSegmentStart = picksCoordinates.get(searchKeyIndex - 1);
                SVPoint2D lineSegmentEnd = picksCoordinates.get(searchKeyIndex);

                intersectionPoint.fy = findIntersectionY_verticalLine_lineSegment(traceXcoordinate, lineSegmentStart, lineSegmentEnd);

                return intersectionPoint;
            } else {
                SVPoint2D lineSegmentStart = picksCoordinates.get(searchKeyIndex);
                SVPoint2D lineSegmentEnd = picksCoordinates.get(searchKeyIndex + 1);

                intersectionPoint.fy = findIntersectionY_verticalLine_lineSegment(traceXcoordinate, lineSegmentStart, lineSegmentEnd);

                return intersectionPoint;
            }
        }
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

        float[] tracesXcoordinates = toTracesXcoordinates(section);

        List<SVPoint2D> traceIntersectionPoints = new ArrayList<>();

        // Extend left if not intercepting trace at leftmost pick
        // ------------------------------------------------------
        if (Arrays.binarySearch(tracesXcoordinates, pickCoordinates.get(0).fx) < 0) {
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
        }

        // Standard operation
        // ------------------
        for (int i = 0; i < tracesXcoordinates.length; i++) {
            SVPoint2D intersection = findIntersection_traceXcoordinate_picksLineChart(tracesXcoordinates[i], pickCoordinates);
            if (intersection != null) {
                traceIntersectionPoints.add(intersection);
            }
        }

        // Extend right if not intercepting trace at rightmost pick
        // --------------------------------------------------------
        if (Arrays.binarySearch(tracesXcoordinates, pickCoordinates.get(pickCoordinates.size() - 1).fx) < 0) {
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
        }

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

//    public static void writePicksToFile(String file, List<SVPoint2D> pickList, SUSection section) {
//        int currentGatherIndex = section.getTraces().get(0).getHeader().tracf;
//        
//        
//        
//    }
    public static void writePicksFromAllGathersToFile(String file, Map<Integer, ArrayList<SVPoint2D>> mapOfPickLists, SUSection section) {
        printt("writePicksFromAllGathersToFile");

        try (CsvWriter csvWriter = CsvWriter.builder().build(Paths.get(file))) {
            // Write header
            csvWriter.writeRecord("tracl", "tracr", "fldr", "tracf", "ep", "offset", "time");

            // Write content
            printt("  mapOfPickLists.keySet(): " + mapOfPickLists.keySet().toString());

            for (ArrayList<SVPoint2D> pickList : mapOfPickLists.values()) {
                printt("  pickList.size(): ", pickList.size());
                List<SVPoint2D> picksAtTraces = computeTraceIntersectionPoints(pickList, section);
                for (int i = 0; i < picksAtTraces.size(); i++) {
                    SVPoint2D pick = picksAtTraces.get(i);
                    int traceIndex = computeNearestTraceIndex(pick, section);
                    SUHeader header = section.getTraces().get(traceIndex).getHeader();
                    printt("  header.ep: " + header.ep);
                    csvWriter.writeRecord(
                            String.valueOf(header.tracl),
                            String.valueOf(header.tracr),
                            String.valueOf(header.fldr),
                            String.valueOf(header.tracf),
                            String.valueOf(header.ep),
                            String.valueOf(header.offset),
                            String.valueOf(pick.fy)
                    );
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PicksFileIO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public static void 
    public static void writePicksToFileDeprecated(String file, List<SVPoint2D> pickList, SUSection section) {

        List<SVPoint2D> picksAtTraces = computeTraceIntersectionPoints(pickList, section);

        try (CsvWriter csvWriter = CsvWriter.builder().build(Paths.get(file))) {
            // Write header
            csvWriter.writeRecord("tracl", "tracr", "fldr", "tracf", "ep", "offset", "time");

            // Write columns
            for (int i = 0; i < picksAtTraces.size(); i++) {
                SVPoint2D pick = picksAtTraces.get(i);
                int traceIndex = computeNearestTraceIndex(pick, section);
                SUHeader header = section.getTraces().get(traceIndex).getHeader();

                // fldr é sequencial, mas não obrigatoriamente sem falhas na sequencia
                // ep
                //   número da estação
                //   não obrigatoriamente sem faltas na sequencia
                // tracf resetou quando avançamos de ep
                csvWriter.writeRecord(
                        String.valueOf(header.tracl),
                        String.valueOf(header.tracr),
                        String.valueOf(header.fldr),
                        String.valueOf(header.tracf),
                        String.valueOf(header.ep),
                        String.valueOf(header.offset),
                        String.valueOf(pick.fy)
                );
            }
        } catch (IOException ex) {
            Logger.getLogger(PicksFileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static float traceIndexToXcoordinate(int traceIndex, SUSection section) {
//        int numTraces = section.getN2();
        float firstTraceLocationX = section.getF2();
        float spacingBetweenTraces = section.getD2();
        return firstTraceLocationX + traceIndex * spacingBetweenTraces;
    }

//    public static void appendPicksToCsv(String pathName, List<SVPoint2D> pickList) {
//        try (CsvWriter csvWriter = CsvWriter.builder().build(new BufferedWriter(new FileWriter(pathName, true)))) {
//            csvWriter.writeRecord("nada", "tudo", "algo");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    ////        try(CsvWriter.builder().b) {
////
////        }
//    }


    public static ArrayList<SVPoint2D> readPicksFromFile(String filename, SUSection section) {

        ArrayList<SVPoint2D> picksList = new ArrayList<>();
        Path file = Paths.get(filename);
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(file)) {
            csvReader.forEach(record -> {
                SVPoint2D pick = new SVPoint2D();
                pick.fy = Float.parseFloat(record.getField("time"));
                int traceIndex = Integer.parseInt(record.getField("tracf"));
                pick.fx = traceIndexToXcoordinate(traceIndex - 1, section);
                picksList.add(pick);
            }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return picksList;
    }

    public void writeCSV(SimpleDataFrame df) throws IOException {
        try (CsvWriter csvWriter = CsvWriter.builder().build(Paths.get("/home/cadu/Documents/output.csv"))) {
            csvWriter.writeRecord(df.getHeaders());

            for (int i = 0; i < df.getRowCount(); i++) {
                csvWriter.writeRecord(df.getRow(i).stream().map(String::valueOf).collect(Collectors.toList()));
            }
        }
    }

    public static int computeNearestTraceIndex(SVPoint2D mouseLocation, SUSection section) {

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

    private static final String CSV_HEADER = "tracl,tracr,fldr,tracf,ep,offset,time\n";

    public static void savePicksFromCurrentGather(Path path, List<SVPoint2D> pickList, SUSection currentGather) {
        if (!Files.exists(path) || !isFileValid(path)) {
            truncateAndWriteHeader(path);
        }

        String gatherKey = "fldr";
        String currentValueOfGatherKey = String.valueOf(currentGather.getTraces().get(0).getHeader().fldr);

        filterInplaceNotEqualTo(path, gatherKey, currentValueOfGatherKey);

        appendPicks(path, pickList, currentGather);
    }

    public static void truncateAndWriteHeader(Path path) {
        try {
            Files.writeString(path, CSV_HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void filterInplaceNotEqualTo(Path path, String targetColumn, String targetValue) {

        List<String> header;

        List<NamedCsvRecord> recordsToKeep = new ArrayList<>();
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(path)) {

            Iterator<NamedCsvRecord> iterator = csvReader.iterator();

            // First csv record
            if (iterator.hasNext()) {
                NamedCsvRecord firstRecord = iterator.next();
                header = firstRecord.getHeader();
                if (!firstRecord.getField(targetColumn).equals(targetValue)) {
                    recordsToKeep.add(firstRecord);
                }
            } else {
                return;
            }

            // Remaining csv records
            recordsToKeep.addAll(csvReader.stream()
                    .filter(record -> !record.getField(targetColumn).equals(targetValue))
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (CsvWriter csvWriter = CsvWriter.builder().build(path)) {
            csvWriter.writeRecord(header);
            recordsToKeep.forEach(recordToKeep -> {
                csvWriter.writeRecord(recordToKeep.getFields());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void appendPicks(Path path, List<SVPoint2D> pickList, SUSection currentSection) {
        List<SVPoint2D> picksAtTraces = computeTraceIntersectionPoints(pickList, currentSection);
        try (CsvWriter csvWriter = CsvWriter.builder().build(new BufferedWriter(new FileWriter(path.toFile(), true)))) {
            for (SVPoint2D pick : picksAtTraces) {
                int traceIndex = computeNearestTraceIndex(pick, currentSection);
                SUHeader header = currentSection.getTraces().get(traceIndex).getHeader();
                csvWriter.writeRecord(
                        String.valueOf(header.tracl),
                        String.valueOf(header.tracr),
                        String.valueOf(header.fldr),
                        String.valueOf(header.tracf),
                        String.valueOf(header.ep),
                        String.valueOf(header.offset),
                        String.valueOf(pick.fy)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<SVPoint2D> loadPicksFromGather(Path path, int currentValueOfGatherKey, SUSection currentSection) {
        if (!Files.exists(path) || !isFileValid(path)) {
            truncateAndWriteHeader(path);
        }
        return selectPicksCommonColumnValue(path, "fldr", String.valueOf(currentValueOfGatherKey), currentSection);
    }

    private static String readFirstLine(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isFileValid(Path path) {
        String firstLine = readFirstLine(path);
        if (firstLine == null) {
            return false;
        }
        return firstLine.concat("\n").equals(CSV_HEADER);
    }

    private static ArrayList<SVPoint2D> selectPicksCommonColumnValue(Path path, String column, String value, SUSection currentSection) {
        ArrayList<SVPoint2D> pickList = new ArrayList<>();
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder().ofNamedCsvRecord(path)) {
            csvReader.stream()
                    .filter(record -> record.getField(column).equals(value))
                    .forEach(record -> {
                        SVPoint2D pick = new SVPoint2D();
                        pick.fy = Float.parseFloat(record.getField("time"));
                        int traceIndex = Integer.parseInt(record.getField("tracf"));
                        pick.fx = traceIndexToXcoordinate(traceIndex - 1, currentSection);
                        pickList.add(pick);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pickList;
    }
}
