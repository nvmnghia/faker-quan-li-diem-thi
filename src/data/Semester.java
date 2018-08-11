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

public class Semester {
    public static final String TABLE_NAME = "hockys";
    static {
        try {
            DataUtl.truncate(Config.DB.NAME, TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Semester> ID2Semester = new HashMap<>();

    private static PreparedStatement pstmInsertSemester = null;
    public static void createSemesters() throws SQLException {
        List<Semester> semesters = new ArrayList<>();

        if (pstmInsertSemester == null) {
            pstmInsertSemester = DataUtl.getDBConnection().prepareStatement("INSERT INTO `" + Config.DB.NAME + "`." + Semester.TABLE_NAME + " (`Học kỳ`, namhoc_id) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
        }

        for (Map.Entry<Integer, Year> entry : Year.ID2Year.entrySet()) {
            Semester semester = new Semester();
            semester.semester = "Học kỳ I năm học " + entry.getValue().year;
            semester.yearID = entry.getKey();
            semesters.add(semester);

            pstmInsertSemester.setString(1, semester.semester);
            pstmInsertSemester.setInt(2, semester.yearID);
            pstmInsertSemester.addBatch();

            semester = new Semester();
            semester.semester = "Học kỳ II năm học " + entry.getValue().year;
            semester.yearID = entry.getKey();
            semesters.add(semester);

            pstmInsertSemester.setString(1, semester.semester);
            pstmInsertSemester.setInt(2, semester.yearID);
            pstmInsertSemester.addBatch();
        }
        pstmInsertSemester.executeBatch();
        List<Integer> IDs = DataUtl.getAutoIncrementIDs(pstmInsertSemester);

        for (int i = 0; i < IDs.size(); ++i) {
            semesters.get(i).ID = IDs.get(i);
            Semester.addSemester(semesters.get(i));
        }

        // Create courses
        Course.createCourses();
    }

    public static void addSemester(Semester semester) {
        ID2Semester.put(semester.ID, semester);
    }

    public static List<Integer> getSemesters(int yearID) {
        List<Integer> semesters = new ArrayList<>();

        for (Map.Entry<Integer, Semester> entry : ID2Semester.entrySet()) {
            if (entry.getValue().yearID == yearID) {
                semesters.add(entry.getKey());
            }
        }

        return semesters;
    }

    public int ID;
    public String semester;
    public int yearID;
}
