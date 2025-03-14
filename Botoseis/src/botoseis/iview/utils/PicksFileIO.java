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
import java.util.List;
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

    public void writePicksToFile(String file, List<SVPoint2D> picksList, SUSection section) {
        
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
        float XlenghtHI = section.getF2();  // = H(1).sx / scalco

        float distanciax = section.getD2();
        float delrt = section.getF1();  // primeiro tempo do matlab = 0

        float dt = section.getD1(); // = dt do matlab = 0.004

        int XMatr = Math.round((mouseLocation.fx - XlenghtHI) / distanciax) + 1;
        int YMatr = Math.round((mouseLocation.fy - delrt) / dt) + 1;

        int n_linhamaximo = section.getN1();
        int n = YMatr - 1 + (XMatr - 1) * (n_linhamaximo);

        int trace = n / section.getN1();

        return trace;
    }

}
