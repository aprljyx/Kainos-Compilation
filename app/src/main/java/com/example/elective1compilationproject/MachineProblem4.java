package com.example.elective1compilationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MachineProblem4 extends AppCompatActivity {
    private Spinner employeeID, positionCode, daysWorked;
    private TextView employeeName;
    private double positionValue, daysWorkedValue;
    private RadioGroup civilStatus;
    private Button compute, clear;

    private double BasicPay, taxRate, SSSRate, SSSContribution, WithholdingTax, NetPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_machine_problem4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        employeeID = findViewById(R.id.spinnerEmployeeID);
        employeeName = findViewById(R.id.tvName);

        HashMap<String, String> employeeData = new HashMap<>();
        employeeData.put("EMP01", "Asilito Caasi");
        employeeData.put("EMP02", "Gwyneth Landero");
        employeeData.put("EMP03", "April Faustino");
        employeeData.put("EMP04", "John Harvey Hingco");
        employeeData.put("EMP05", "Michael Bargabino");

        List<String> employeeIds = new ArrayList<>(employeeData.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeID.setAdapter(adapter);

        employeeID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedId = parent.getItemAtPosition(position).toString();
                employeeName.setText(employeeData.get(selectedId));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                employeeName.setText("");
            }
        });

        positionCode = findViewById(R.id.spinnerPosition);
        HashMap<String, Double> positionData = new HashMap<>();
        positionData.put("A", 500.0);
        positionData.put("B", 400.0);
        positionData.put("C", 300.0);

        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(positionData.keySet()));
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionCode.setAdapter(positionAdapter);

        positionCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionValue = positionData.get(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                positionValue = 0.0;
            }
        });

        civilStatus = findViewById(R.id.radioGroupTax);

        daysWorked = findViewById(R.id.spinnerDaysWorked);
        String[] daysArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18",
                "19", "20", "21", "22", "23", "24", "25", "26",
                "27", "28", "29", "30"};
        ArrayAdapter<String> daysWorkedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysArray);
        daysWorkedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysWorked.setAdapter(daysWorkedAdapter);

        daysWorked.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                daysWorkedValue = Double.parseDouble(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        compute = findViewById(R.id.btnCompute);
        clear = findViewById(R.id.btnClear);

        clear.setOnClickListener(v -> {
            employeeID.setSelection(0);
            employeeName.setText("");
            positionCode.setSelection(0);
            daysWorked.setSelection(0);
            civilStatus.clearCheck();
            Toast.makeText(this, "Fields cleared", Toast.LENGTH_SHORT).show();
        });

        compute.setOnClickListener(v -> {
            if (validateFields()) {
                computePayroll();
            }
        });
    }

    private boolean validateFields() {
        if (civilStatus.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a civil status.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void computePayroll() {
        BasicPay = daysWorkedValue * positionValue;
        SSSRate = BasicPay >= 10000 ? 0.07 : BasicPay >= 5000 ? 0.05 : BasicPay >= 1000 ? 0.03 : 0.01;
        SSSContribution = BasicPay * SSSRate;
        WithholdingTax = BasicPay * taxRate;
        NetPay = BasicPay - (SSSContribution + WithholdingTax);

        PayrollDatabaseHelper dbHelper = new PayrollDatabaseHelper(this);
        dbHelper.insertPayrollData(
                employeeID.getSelectedItem().toString(),
                employeeName.getText().toString(),
                positionCode.getSelectedItem().toString(),
                ((RadioButton) findViewById(civilStatus.getCheckedRadioButtonId())).getText().toString(),
                daysWorked.getSelectedItem().toString(),
                BasicPay,
                SSSContribution,
                WithholdingTax,
                NetPay
        );
        startActivity(new Intent(this, MachineProblem4Result.class).putExtra("EmployeeID", employeeID.getSelectedItem().toString()));
    }
}
