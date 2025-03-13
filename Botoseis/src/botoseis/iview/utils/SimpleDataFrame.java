package botoseis.iview.utils;

import java.util.*;

public class SimpleDataFrame {

    private final List<String> headers;
    private final Map<String, List<Float>> columns;
    private int rowCount;

    public SimpleDataFrame() {
        headers = new ArrayList<>();
        columns = new LinkedHashMap<>();
        rowCount = 0;
    }

    public void addColumn(String header, List<Float> data) {
        if (headers.contains(header)) {
            throw new IllegalArgumentException("Column already exists: " + header);
        }
        headers.add(header);
        columns.put(header, new ArrayList<>(data));

        if (rowCount == 0) {
            rowCount = data.size();
        } else if (data.size() != rowCount) {
            throw new IllegalArgumentException("Column length mismatch");
        }
    }

    public List<Float> getColumn(String header) {
        return columns.get(header);
    }

    public List<Float> getColumn(int index) {
        if (index < 0 || index >= headers.size()) {
            throw new IndexOutOfBoundsException("Invalid column index");
        }
        return columns.get(headers.get(index));
    }

    public List<Float> getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rowCount) {
            throw new IndexOutOfBoundsException("Invalid row index: " + rowIndex);
        }

        List<Float> row = new ArrayList<>();
        for (String header : headers) {
            List<Float> columnData = columns.get(header);
            row.add(columnData.get(rowIndex));
        }
        return row;
    }

    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    public int getRowCount() {
        return rowCount;
    }
}
