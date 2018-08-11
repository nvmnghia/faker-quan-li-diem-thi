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

public class Course {
    public static final String TABLE_NAME = "lopmonhocs";
    static {
        try {
            DataUtl.truncate(Config.DB.NAME, TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Course> ID2Course = new HashMap<>();

    private static PreparedStatement pstmInsertCourse = null;
    public static void createCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();

        if (pstmInsertCourse == null) {
            pstmInsertCourse = DataUtl.getDBConnection().prepareStatement("INSERT INTO `" + Config.DB.NAME + "`." + Course.TABLE_NAME + " (`Mã lớp môn học`, `Trạng thái điểm`, `Tên lớp môn học`, hocky_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
        }

        for (Map.Entry<Integer, Semester> entry : Semester.ID2Semester.entrySet()) {
            for (int i = 0; i < 15; ++i) {
                Course course = new Course();
                course.code = "INT" + i;
                course.semesterID = entry.getKey();
                courses.add(course);

                pstmInsertCourse.setString(1, course.code);
                pstmInsertCourse.setInt(2, 1);
                pstmInsertCourse.setString(3, course.code);
                pstmInsertCourse.setInt(4, course.semesterID);
                pstmInsertCourse.addBatch();
            }
        }
        pstmInsertCourse.executeBatch();
        List<Integer> IDs = DataUtl.getAutoIncrementIDs(pstmInsertCourse);

        for (int i = 0; i < IDs.size(); ++i) {
            courses.get(i).ID = IDs.get(i);
            Course.addCourse(courses.get(i));
        }
    }

    public static void addCourse(Course course) {
        ID2Course.put(course.ID, course);
    }

    public static List<Integer> getCourses(int semesterID) {
        List<Integer> courses = new ArrayList<>();

        for (Map.Entry<Integer, Course> entry : ID2Course.entrySet()) {
            if (entry.getValue().semesterID == semesterID) {
                courses.add(entry.getKey());
            }
        }

        return courses;
    }

    public int ID;
    public String code;
    public int semesterID;
}
