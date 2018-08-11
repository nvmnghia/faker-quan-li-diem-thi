import config.Config;
import data.Course;
import data.Semester;
import data.Student;
import data.Year;
import util.DataUtl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Main {

    public static Random random = new Random();

    public static void main(String[] args) throws SQLException {
        Year.createYears();
        Student.createStudents();
        fixActivation();
    }

    private static void fixActivation() throws SQLException {
        ResultSet rs = DataUtl.queryDB(Config.DB.NAME, "SELECT MAX(id) FROM " + Year.TABLE_NAME);
        rs.next();
        int thisYearID = rs.getInt(1);
        System.out.println(thisYearID);

        // Set Trang thai diem = 0
        // 50% of the latest courses haven't been graded
        List<Integer> semesters = Semester.getSemesters(thisYearID);
        System.out.println(Semester.ID2Semester.get(semesters.get(0)).semester);
        List<Integer> courses = Course.getCourses(semesters.get(semesters.size() - 1));

        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < courses.size(); ++i) {
            if (random.nextFloat() < 0.5f) {
                temp.append(courses.get(i));

                if (i != courses.size() - 1) {
                    temp.append(", ");
                }
            }
        }
        String courseToDeactivate = temp.toString();
        if (courseToDeactivate.endsWith(", ")) {
            courseToDeactivate = courseToDeactivate.substring(0, courseToDeactivate.length() - 2);
        }

        String update = "UPDATE " + Course.TABLE_NAME + " SET `Trạng thái điểm` = 0 WHERE id IN (" + courseToDeactivate + ")";
        DataUtl.updateDB(Config.DB.NAME, update);

        update = "UPDATE sinhvien_lopmonhoc SET score = NULL WHERE lopmonhoc_id IN (" + courseToDeactivate + ")";
        DataUtl.updateDB(Config.DB.NAME, update);
    }
}
