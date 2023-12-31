package com.example.taskwise.TaskDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskwise.ContextWrapper;
import com.example.taskwise.DataBase.AppDataBase;
import com.example.taskwise.Model.Task;
import com.example.taskmanager.R;
import com.example.taskwise.SharedPreferences.AppSettingContainer;
import com.example.taskwise.TaskDetail.WorkManager.TaskListStatusUpdater;

import java.util.concurrent.TimeUnit;


public class TaskDetailActivity extends AppCompatActivity implements TaskDetailContract.view {

    EditText taskTitleEt , descriptionEt;
    AppCompatButton submitTaskBtn;
    TextView spinnerTv , headerTv;
    Spinner timePeriodSpinner , importanceSpinner ;
    RelativeLayout deleteTaskBtn , backBtn;

    int time_period , importance = 1;
    AppSettingContainer settingContainer;

    TaskDetailPresentor presentor;

    public void cast(){

        taskTitleEt = findViewById(R.id.taskTitleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        timePeriodSpinner = findViewById(R.id.timePeriodSpinner);
        spinnerTv = findViewById(R.id.spinnerTv);
        submitTaskBtn = findViewById(R.id.submitTaskBtn);
        importanceSpinner = findViewById(R.id.importanceSpinner);
        headerTv = findViewById(R.id.headerTv);
        deleteTaskBtn = findViewById(R.id.deleteTaskBtn);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingContainer = new AppSettingContainer(this);
        ContextWrapper.setTheme(this , settingContainer.getAppTheme());
        setContentView(R.layout.activity_task_detail);
        cast();
        presentor = new TaskDetailPresentor(AppDataBase.getAppDataBase(this).getDataBaseDao() , getIntent().getParcelableExtra("task"));
        presentor.onAttach(this);

        setSpinners();

        submitTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskTitle = taskTitleEt.getText().toString();
                String taskDescription = descriptionEt.getText().toString();

                if (taskTitle.length() > 0 && taskDescription.length() > 0){
                    presentor.saveButtonClicked(taskTitle , taskDescription , time_period , importance);
                    finish();
                }
                else if (taskTitleEt.length() == 0)
                    taskTitleEt.setError(R.string.titleError + "");

                else if(descriptionEt.length() == 0)
                    descriptionEt.setError(R.string.descriptionError + "");

            }
        });

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentor.deleteButtonClicked();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ContextWrapper.wrap(newBase));
    }

    public void setSpinners(){
        String[] period_spinner_items = {getString(R.string.inDayAhead) , getString(R.string.inThreeDays) , getString(R.string.inThisWeek) , getString(R.string.inThisMonth)};
        ArrayAdapter<String> periodSpinnerAdapter = new ArrayAdapter<String>(this , R.layout.item_period_spinner , R.id.spinnerTv , period_spinner_items);
        periodSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        timePeriodSpinner.setAdapter(periodSpinnerAdapter);
        timePeriodSpinner.setSelection(time_period);

        String[] importance_spinner_items = {getString(R.string.highImportance) , getString(R.string.normalPriority) , getString(R.string.lowPriority)};
        ArrayAdapter<String> importanceSpinnerAdapter = new ArrayAdapter<String>(this , R.layout.item_importance_spinner , R.id.spinnerTv , importance_spinner_items);
        importanceSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        importanceSpinner.setAdapter(importanceSpinnerAdapter);
        importanceSpinner.setSelection(importance);


        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_period = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        importanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                importance = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void setTexts(int headerText, int buttonTv) {
        headerTv.setText(headerText);
        submitTaskBtn.setText(buttonTv);
    }

    @Override
    public void showTask(Task task) {
        taskTitleEt.setText(task.getTitle());
        descriptionEt.setText(task.getDescription());
        time_period = task.getTime_period();
        importance = task.getImportance();
        timePeriodSpinner.setSelection(task.getTime_period());
        importanceSpinner.setSelection(task.getImportance());

    }

    @Override
    public void setDeleteButtonVisibility(boolean visible) {
        deleteTaskBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setWorkManager(String taskTitle , int expiredDate) {
        Data data = new Data.Builder().putString("taskInfo" , taskTitle ).build();

        WorkManager manager = WorkManager.getInstance(TaskDetailActivity.this);
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(TaskListStatusUpdater.class)
                .setInputData(data)
                .setInitialDelay(expiredDate , TimeUnit.DAYS)
                .build();
        manager.enqueue(request);

        finish();
    }

    @Override
    public void updateTask() {
        Toast.makeText(this, "Successfuly Edited.!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void deleteTask() {
        Toast.makeText(this, "Seccessfuly Deleted.!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presentor.onDetach();
    }
}