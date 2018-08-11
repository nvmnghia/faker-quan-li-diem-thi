package data;

import config.Config;
import util.DataUtl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year {
    public static final String TABLE_NAME = "namhocs";
    static {
        try {
            DataUtl.truncate(Config.DB.NAME, TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final int START = 2008;
    public static final int CURRENT = 2017;    // (2017-2018, second semester. 2018 to be exact, but just ignore it for now...)

    public static Map<Integer, Year> ID2Year = new HashMap<>();

    private static PreparedStatement pstmInsertYear = null;
    public static void createYears() throws SQLException {
        List<Year> years = new ArrayList<>();

        if (pstmInsertYear == null) {
            pstmInsertYear = DataUtl.getDBConnection().prepareStatement("INSERT INTO `" + Config.DB.NAME + "`." + Year.TABLE_NAME + " (`Kích hoạt`, `Năm học`) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
        }

        int start = START;
        int DURATION = CURRENT - START + 1;
        for (int i = 0; i < DURATION; ++i) {
            Year year = new Year();
            year.year = start + "-" + ++start;
            years.add(year);

            pstmInsertYear.setInt(1, start > CURRENT - 4 ? 1 : 0);
            pstmInsertYear.setString(2, year.year);
            pstmInsertYear.addBatch();
        }
        pstmInsertYear.executeBatch();
        List<Integer> IDs = DataUtl.getAutoIncrementIDs(pstmInsertYear);

        for (int i = 0; i < IDs.size(); ++i) {
            years.get(i).ID = IDs.get(i);
            Year.addYear(years.get(i));
        }

        // Create semesters
        Semester.createSemesters();
    }

    public static void addYear(Year year) {
        ID2Year.put(year.ID, year);
    }

    public int ID;
    public String year;
}
