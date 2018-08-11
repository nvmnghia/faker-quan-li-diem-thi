package data;

import com.github.javafaker.Faker;
import config.Config;
import util.DataUtl;
import util.Utl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Student {
    public static final String TABLE_NAME = "sinhviens";
    static {
        try {
            DataUtl.truncate(Config.DB.NAME, TABLE_NAME);
            DataUtl.truncate(Config.DB.NAME, "sinhvien_lopmonhoc");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Random random = new Random();
    private static Faker faker = new Faker();

    private static PreparedStatement pstmInsertStudent = null;
    public static void createStudents() throws SQLException {

        if (pstmInsertStudent == null) {
            pstmInsertStudent = DataUtl.getDBConnection().prepareStatement("INSERT INTO `" + Config.DB.NAME + "`." + Student.TABLE_NAME + " (`Họ tên`, `Kích hoạt`, `Lớp khóa học`, `Ngày sinh`, id, username) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
        }

        int numOfStudent = 200;
        int year = Year.START;
        for (Map.Entry<Integer, Year> entry : Year.ID2Year.entrySet()) {
            int yearID = entry.getKey();

            // For faking DOB
            long from = (new Date(year - 18, 0, 1)).getTime();
            long to = (new Date(year - 17, 0, 30)).getTime();
            long diff = to - from;

            for (int i = 0; i < numOfStudent; ++i) {
                Student student = new Student();
                student.ID = (year - 2000) * 1000000 + i;
                student.name = faker.name().fullName();
                student.classOf = entry.getValue().year;

                pstmInsertStudent.setString(1, student.name);
                pstmInsertStudent.setInt(2, yearID > Year.CURRENT - 4 ? 1 : 0);
                pstmInsertStudent.setString(3, student.classOf);
                pstmInsertStudent.setDate(4, new java.sql.Date((long) (from + diff * random.nextFloat())));
                pstmInsertStudent.setInt(5, student.ID);
                pstmInsertStudent.setInt(6, student.ID);

                pstmInsertStudent.executeUpdate();

                // 4 year of pain
                for (int j = 0;  j < 4; ++j) {
                    List<Integer> semesters = Semester.getSemesters(yearID + j);
                    List<Integer> coursesFirstSemester = semesters.size() == 0 ? new ArrayList<>() : Course.getCourses(semesters.get(0));
                    List<Integer> coursesSecondSemester = semesters.size() == 2 ? Course.getCourses(semesters.get(1)) : new ArrayList<>();

                    // Learn semester I, if any
                    for (Integer course : coursesFirstSemester) {
                        if (random.nextFloat() < 0.2f) {
                            student.learn(course);
                        }
                    }

                    // Learn semester II, if any
                    for (Integer course : coursesSecondSemester) {
                        if (random.nextFloat() < 0.2f) {
                            student.learn(course);
                        }
                    }
                }
            }

            numOfStudent += numOfStudent / 8;
            ++year;
            ++yearID;
        }

        pstmInsertStudentCourse.executeBatch();
    }

    public int ID;
    public String name, classOf;
    public Date DOB;

    private static PreparedStatement pstmInsertStudentCourse = null;
    public void learn(int courseID) throws SQLException {
        if (pstmInsertStudentCourse == null) {
            pstmInsertStudentCourse = DataUtl.getDBConnection().prepareStatement("INSERT INTO `" + Config.DB.NAME + "`.sinhvien_lopmonhoc (lopmonhoc_id, sinhvien_id, score) VALUES (?, ?, ?)");
        }

        pstmInsertStudentCourse.setInt(1, courseID);
        pstmInsertStudentCourse.setInt(2, this.ID);
        pstmInsertStudentCourse.setFloat(3, Utl.round(10 * random.nextFloat(), 1));
        pstmInsertStudentCourse.addBatch();
    }
}
