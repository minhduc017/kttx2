package com.example.studentmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu
    private static final String DATABASE_NAME = "StudentManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_CLASSES = "classes";

    // Cột của bảng sinh viên
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_YEAR_OF_BIRTH = "year_of_birth";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_CLASS_ID = "class_id";

    // Cột của bảng lớp học
    private static final String COLUMN_CLASS_NAME = "class_name";
    private static final String COLUMN_ACADEMIC_YEAR = "academic_year";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng lớp học
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + " ("
                + COLUMN_CLASS_ID + " TEXT PRIMARY KEY, "
                + COLUMN_CLASS_NAME + " TEXT, "
                + COLUMN_ACADEMIC_YEAR + " TEXT)";
        db.execSQL(CREATE_CLASSES_TABLE);

        // Tạo bảng sinh viên với ràng buộc khóa ngoại
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + " ("
                + COLUMN_STUDENT_ID + " TEXT PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_YEAR_OF_BIRTH + " INTEGER, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_CLASS_ID + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + "))";
        db.execSQL(CREATE_STUDENTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu cần nâng cấp
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    // Chức năng thêm lớp học
    // Chức năng thêm lớp học
    public boolean addClass(String classId, String className, String academicYear) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_ID, classId);
        values.put(COLUMN_CLASS_NAME, className);
        values.put(COLUMN_ACADEMIC_YEAR, academicYear);

        long result = db.insert(TABLE_CLASSES, null, values);
        db.close();
        return result != -1;
    }

    // Kiểm tra xem mã lớp có tồn tại không
    public boolean isClassExists(String classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " WHERE " + COLUMN_CLASS_ID + " = ?", new String[]{classId});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Chức năng thêm sinh viên
    // Chức năng thêm sinh viên
    public boolean addStudent(String studentId, String name, int yearOfBirth, String phone, String address, String classId) {
        // Kiểm tra mã lớp trước khi thêm sinh viên
        if (!isClassExists(classId)) {
            // Nếu mã lớp không tồn tại
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, studentId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_YEAR_OF_BIRTH, yearOfBirth);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_CLASS_ID, classId);

        long result = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return result != -1;
    }


    // Lấy danh sách sinh viên
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);
    }

    // Lấy danh sách lớp học
    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
    }

    // Cập nhật lớp học
    public boolean updateClass(String classId, String className, String academicYear) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, className);
        values.put(COLUMN_ACADEMIC_YEAR, academicYear);

        int result = db.update(TABLE_CLASSES, values, COLUMN_CLASS_ID + "=?", new String[]{classId});
        db.close();
        return result > 0;
    }

    // Cập nhật sinh viên
    // Cập nhật sinh viên
    public boolean updateStudent(String studentId, String name, int yearOfBirth, String phone, String address, String classId) {
        // Kiểm tra mã lớp trước khi cập nhật
        if (!isClassExists(classId)) {
            // Nếu mã lớp không tồn tại
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_YEAR_OF_BIRTH, yearOfBirth);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_CLASS_ID, classId);

        int result = db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_ID + "=?", new String[]{studentId});
        db.close();
        return result > 0;
    }


    // Xóa lớp học
    public boolean deleteClass(String classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CLASSES, COLUMN_CLASS_ID + "=?", new String[]{classId});
        db.close();
        return result > 0;
    }

    // Xóa sinh viên
    public boolean deleteStudent(String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_STUDENTS, COLUMN_STUDENT_ID + "=?", new String[]{studentId});
        db.close();
        return result > 0;
    }
}
