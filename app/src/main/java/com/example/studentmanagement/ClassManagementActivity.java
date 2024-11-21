package com.example.studentmanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ClassManagementActivity extends AppCompatActivity {

    private ListView lvClasses;
    private ArrayAdapter<String> classAdapter;
    private ArrayList<String> classList;
    private DatabaseHelper dbHelper; // SQLite Helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);

        Button btnAddClass = findViewById(R.id.btnAddClass);
        Button btnEditClass = findViewById(R.id.btnEditClass);
        Button btnDeleteClass = findViewById(R.id.btnDeleteClass);
        lvClasses = findViewById(R.id.lvClasses);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Lấy danh sách lớp học từ SQLite
        classList = new ArrayList<>();
        loadClassesFromDatabase();

        // Adapter cho ListView
        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classList);
        lvClasses.setAdapter(classAdapter);
        lvClasses.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Thêm lớp học
        btnAddClass.setOnClickListener(v -> showAddClassDialog());

        // Sửa lớp học
        btnEditClass.setOnClickListener(v -> {
            int position = lvClasses.getCheckedItemPosition();
            if (position >= 0) {
                showEditClassDialog(position);
            } else {
                Toast.makeText(this, "Vui lòng chọn một lớp học để sửa!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xóa lớp học
        btnDeleteClass.setOnClickListener(v -> {
            int position = lvClasses.getCheckedItemPosition();
            if (position >= 0) {
                String selectedClass = classList.get(position);
                String classId = getClassIdFromString(selectedClass);

                if (dbHelper.deleteClass(classId)) {
                    classList.remove(position);
                    classAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Đã xóa lớp học!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không thể xóa lớp học!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn một lớp học để xóa!", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện khi chọn lớp học
        lvClasses.setOnItemClickListener((parent, view, position, id) -> lvClasses.setItemChecked(position, true));
    }

    // Hàm tải danh sách lớp học từ SQLite
    private void loadClassesFromDatabase() {
        Cursor cursor = dbHelper.getAllClasses();
        classList.clear();
        while (cursor.moveToNext()) {
            String classId = cursor.getString(0); // COLUMN_CLASS_ID
            String className = cursor.getString(1); // COLUMN_CLASS_NAME
            String academicYear = cursor.getString(2); // COLUMN_ACADEMIC_YEAR
            classList.add(classId + " - " + className + " (" + academicYear + ")");
        }
        cursor.close();
    }

    // Hiển thị dialog thêm lớp học
    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Lớp Học");

        // Giao diện nhập thông tin lớp học
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputId = new EditText(this);
        inputId.setHint("Nhập mã lớp");
        layout.addView(inputId);

        final EditText inputName = new EditText(this);
        inputName.setHint("Nhập tên lớp");
        layout.addView(inputName);

        final EditText inputYear = new EditText(this);
        inputYear.setHint("Nhập niên khóa");
        layout.addView(inputYear);

        builder.setView(layout);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String classId = inputId.getText().toString().trim();
            String className = inputName.getText().toString().trim();
            String academicYear = inputYear.getText().toString().trim();

            if (!classId.isEmpty() && !className.isEmpty() && !academicYear.isEmpty()) {
                if (dbHelper.addClass(classId, className, academicYear)) {
                    classList.add(classId + " - " + className + " (" + academicYear + ")");
                    classAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Thêm lớp học thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm lớp học. Mã lớp có thể đã tồn tại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Thông tin lớp học không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Hiển thị dialog sửa lớp học
    private void showEditClassDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Lớp Học");

        String selectedClass = classList.get(position);
        String classId = getClassIdFromString(selectedClass);

        // Lấy thông tin hiện tại của lớp học từ SQLite
        Cursor cursor = dbHelper.getAllClasses();
        String className = "";
        String academicYear = "";
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(classId)) {
                className = cursor.getString(1);
                academicYear = cursor.getString(2);
                break;
            }
        }
        cursor.close();

        // Giao diện nhập thông tin
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(this);
        inputName.setText(className);
        layout.addView(inputName);

        final EditText inputYear = new EditText(this);
        inputYear.setText(academicYear);
        layout.addView(inputYear);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String updatedName = inputName.getText().toString().trim();
            String updatedYear = inputYear.getText().toString().trim();

            if (!updatedName.isEmpty() && !updatedYear.isEmpty()) {
                if (dbHelper.updateClass(classId, updatedName, updatedYear)) {
                    classList.set(position, classId + " - " + updatedName + " (" + updatedYear + ")");
                    classAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Sửa lớp học thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi sửa lớp học!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Thông tin lớp học không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Tách mã lớp từ chuỗi hiển thị
    private String getClassIdFromString(String classString) {
        return classString.split(" - ")[0];
    }
}
