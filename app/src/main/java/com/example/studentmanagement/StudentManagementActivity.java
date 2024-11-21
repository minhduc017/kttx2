package com.example.studentmanagement;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;

public class StudentManagementActivity extends AppCompatActivity {
    private ListView lvStudents;
    private ArrayAdapter<String> studentAdapter;
    private ArrayList<String> studentList;
    private ArrayList<String> filteredStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        EditText edtSearchStudent = findViewById(R.id.edtSearchStudent);
        Button btnAddStudent = findViewById(R.id.btnAddStudent);
        Button btnEditStudent = findViewById(R.id.btnEditStudent);
        Button btnDeleteStudent = findViewById(R.id.btnDeleteStudent);
        lvStudents = findViewById(R.id.lvStudents);

        // Khởi tạo danh sách sinh viên (demo)
        studentList = new ArrayList<>();
        studentList.add("SV001 - Nguyễn Văn A");
        studentList.add("SV002 - Trần Thị B");
        // Tạo một danh sách sinh viên đã lọc
        filteredStudentList = new ArrayList<>(studentList);

        // Adapter cho ListView
        studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredStudentList);
        lvStudents.setAdapter(studentAdapter);

        // Xử lý sự kiện thêm sinh viên
        btnAddStudent.setOnClickListener(v -> showAddStudentDialog());

        // Tìm kiếm sinh viên
        edtSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lọc danh sách sinh viên khi người dùng nhập từ khóa
                studentAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sửa và Xóa sinh viên
        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStudent = filteredStudentList.get(position);
            Toast.makeText(this, "Chọn: " + selectedStudent, Toast.LENGTH_SHORT).show();

            // Sửa sinh viên
            btnEditStudent.setOnClickListener(v -> showEditStudentDialog(position));

            // Xóa sinh viên
            btnDeleteStudent.setOnClickListener(v -> showDeleteStudentDialog(position));
        });
    }

    // Hiển thị dialog thêm sinh viên
    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Sinh Viên");

        // Tạo giao diện nhập thông tin sinh viên
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputId = new EditText(this);
        inputId.setHint("Nhập mã sinh viên (ID)");
        layout.addView(inputId);

        final EditText inputName = new EditText(this);
        inputName.setHint("Nhập tên sinh viên");
        layout.addView(inputName);

        final EditText inputYearOfBirth = new EditText(this);
        inputYearOfBirth.setHint("Nhập năm sinh (YYYY)");
        inputYearOfBirth.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputYearOfBirth);

        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("Nhập số điện thoại");
        inputPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(inputPhone);

        final EditText inputAddress = new EditText(this);
        inputAddress.setHint("Nhập địa chỉ");
        layout.addView(inputAddress);

        final EditText inputClassId = new EditText(this);
        inputClassId.setHint("Nhập mã lớp (Class ID)");
        layout.addView(inputClassId);

        builder.setView(layout);

        // Xử lý khi người dùng nhấn nút "Thêm"
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String studentId = inputId.getText().toString().trim();
            String name = inputName.getText().toString().trim();
            String yearOfBirthStr = inputYearOfBirth.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String classId = inputClassId.getText().toString().trim();

            // Kiểm tra dữ liệu hợp lệ
            if (studentId.isEmpty() || name.isEmpty() || yearOfBirthStr.isEmpty() || phone.isEmpty() || address.isEmpty() || classId.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int yearOfBirth = Integer.parseInt(yearOfBirthStr);

                    // Gọi DatabaseHelper để thêm sinh viên
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    boolean isAdded = dbHelper.addStudent(studentId, name, yearOfBirth, phone, address, classId);

                    if (isAdded) {
                        // Tạo thông tin sinh viên đầy đủ
                        String studentInfo = studentId + " - " + name + " - " + yearOfBirth + " - " + phone + " - " + address + " - " + classId;

                        // Thêm thông tin vào danh sách
                        studentList.add(studentInfo);              // Thêm vào danh sách hiển thị
                        filteredStudentList.add(studentInfo);      // Thêm vào danh sách lọc
                        studentAdapter.notifyDataSetChanged();     // Cập nhật Adapter

                        Toast.makeText(this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Thêm sinh viên thất bại. Kiểm tra dữ liệu nhập!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Năm sinh phải là số!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }



    // Hiển thị dialog sửa sinh viên
    private void showEditStudentDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Sinh Viên");

        // Lấy thông tin sinh viên đã chọn từ danh sách filteredStudentList
        String selectedStudent = filteredStudentList.get(position);
        String[] studentDetails = selectedStudent.split(" - "); // Chia thông tin sinh viên theo dấu "-"
        String studentId = studentDetails[0];
        String studentName = studentDetails[1];
        // Giả sử bạn có một cách lấy các thông tin khác như năm sinh, số điện thoại, địa chỉ và mã lớp
        String yearOfBirth = "2000"; // Ví dụ: lấy từ danh sách sinh viên
        String phone = "0909123456"; // Ví dụ: lấy từ danh sách sinh viên
        String address = "TP.HCM"; // Ví dụ: lấy từ danh sách sinh viên
        String classId = "L001"; // Ví dụ: lấy từ danh sách sinh viên

        // Giao diện nhập thông tin mới
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(this);
        inputName.setText(studentName);
        inputName.setHint("Tên sinh viên");
        layout.addView(inputName);

        final EditText inputYearOfBirth = new EditText(this);
        inputYearOfBirth.setText(yearOfBirth);
        inputYearOfBirth.setHint("Năm sinh");
        layout.addView(inputYearOfBirth);

        final EditText inputPhone = new EditText(this);
        inputPhone.setText(phone);
        inputPhone.setHint("Số điện thoại");
        layout.addView(inputPhone);

        final EditText inputAddress = new EditText(this);
        inputAddress.setText(address);
        inputAddress.setHint("Địa chỉ");
        layout.addView(inputAddress);

        final EditText inputClassId = new EditText(this);
        inputClassId.setText(classId);
        inputClassId.setHint("Mã lớp");
        layout.addView(inputClassId);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String updatedName = inputName.getText().toString().trim();
            String updatedYearOfBirth = inputYearOfBirth.getText().toString().trim();
            String updatedPhone = inputPhone.getText().toString().trim();
            String updatedAddress = inputAddress.getText().toString().trim();
            String updatedClassId = inputClassId.getText().toString().trim();

            if (!updatedName.isEmpty() && !updatedYearOfBirth.isEmpty() && !updatedPhone.isEmpty() &&
                    !updatedAddress.isEmpty() && !updatedClassId.isEmpty()) {

                // Cập nhật thông tin sinh viên
                String updatedStudent = studentId + " - " + updatedName + " - " + updatedYearOfBirth + " - "
                        + updatedPhone + " - " + updatedAddress + " - " + updatedClassId;

                studentList.set(position, updatedStudent);            // Cập nhật studentList
                filteredStudentList.set(position, updatedStudent);   // Cập nhật filteredStudentList
                studentAdapter.notifyDataSetChanged();                // Thông báo cho Adapter về sự thay đổi

                Toast.makeText(this, "Sửa sinh viên thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tất cả các trường thông tin phải được điền!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    // Hiển thị dialog xóa sinh viên
    private void showDeleteStudentDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa Sinh Viên");
        builder.setMessage("Bạn có chắc chắn muốn xóa sinh viên này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            studentList.remove(position);                    // Xóa khỏi studentList
            filteredStudentList.remove(position);            // Xóa khỏi filteredStudentList
            studentAdapter.notifyDataSetChanged();           // Thông báo cho Adapter về sự thay đổi
            Toast.makeText(this, "Đã xóa sinh viên!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }}