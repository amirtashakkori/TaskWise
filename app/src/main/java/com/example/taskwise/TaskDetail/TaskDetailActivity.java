package com.example.taskwise.TaskDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskwise.BroadCastReceivers.Remiders;
import com.example.taskwise.ContextWrapper;
import com.example.taskwise.DataBase.AppDataBase;
import com.example.taskwise.DataBase.DBDao;
import com.example.taskwise.Model.Task;
import com.example.taskmanager.R;
import com.example.taskwise.SharedPreferences.AppSettingContainer;
import com.example.taskwise.BroadCastReceivers.ListStatusUpdater;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class TaskDetailActivity extends AppCompatActivity implements TaskDetailContract.view {

    EditText taskTitleEt , descriptionEt;
    AppCompatButton submitTaskBtn;
    TextView spinnerTv , headerTv , outDateTv;
    Spinner timePeriodSpinner , importanceSpinner ;
    RelativeLayout deleteTaskBtn , backBtn;

    int expiredDate , importance = 1;
    long notificationTime;

    AppSettingContainer settingContainer;
    TaskDetailPresentor presentor;
    DBDao dao;
    Task task;
    ArrayAdapter<String> periodSpinnerAdapter;
    ArrayAdapter<String> importanceSpinnerAdapter;

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
        outDateTv = findViewById(R.id.outDateTv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingContainer = new AppSettingContainer(this);
        ContextWrapper.setTheme(this , settingContainer.getAppTheme());
        setContentView(R.layout.activity_task_detail);
        cast();
        dao = AppDataBase.getAppDataBase(this).getDataBaseDao();
        task = getIntent().getParcelableExtra("task");
        presentor = new TaskDetailPresentor(dao , task , settingContainer);
        presentor.onAttach(this);

        setSpinners();

        submitTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskTitle = taskTitleEt.getText().toString();
                String taskDescription = descriptionEt.getText().toString();

                if (!taskTitle.isEmpty() && !taskDescription.isEmpty()){
                    presentor.saveButtonClicked(taskTitle , taskDescription , expiredDate , importance );
                    finish();
                }
                else if (taskTitle.isEmpty())
                    taskTitleEt.setError(R.string.titleError + "");

                else if(taskDescription.isEmpty())
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
        if (task != null && task.getTime_period() == 4){
            outDateTv.setVisibility(View.VISIBLE);
            timePeriodSpinner.setVisibility(View.GONE);
        } else {
            outDateTv.setVisibility(View.GONE);
            timePeriodSpinner.setVisibility(View.VISIBLE);
            String[] period_spinner_items = {getString(R.string.inDayAhead) , getString(R.string.inThreeDays) , getString(R.string.inThisWeek) , getString(R.string.inThisMonth)};
            periodSpinnerAdapter = new ArrayAdapter<String>(this , R.layout.item_period_spinner , R.id.spinnerTv , period_spinner_items);
            periodSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            timePeriodSpinner.setAdapter(periodSpinnerAdapter);
            timePeriodSpinner.setSelection(expiredDate);
        }

        String[] importance_spinner_items = {getString(R.string.highImportance) , getString(R.string.normalPriority) , getString(R.string.lowPriority)};
        importanceSpinnerAdapter = new ArrayAdapter<String>(this , R.layout.item_importance_spinner , R.id.spinnerTv , importance_spinner_items);
        importanceSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        importanceSpinner.setAdapter(importanceSpinnerAdapter);
        importanceSpinner.setSelection(importance);



        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expiredDate = position;
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
        expiredDate = task.getTime_period();
        importance = task.getImportance();
        timePeriodSpinner.setSelection(expiredDate);
        importanceSpinner.setSelection(importance);

    }

    @Override
    public void setDeleteButtonVisibility(boolean visible) {
        deleteTaskBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setWorkManager(long id) {
        Data data = new Data.Builder().putLong("taskId" , id ).build();
        Task task = dao.searchTask(id);

        WorkManager manager = WorkManager.getInstance(TaskDetailActivity.this);
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ListStatusUpdater.class)
                .setInputData(data)
                .setInitialDelay(getExpireDate(expiredDate) , TimeUnit.DAYS)
                .build();
        manager.enqueue(request);
        UUID workManagerId = request.getId();
        task.setWorkManagerId(workManagerId.toString());
        long res = dao.update(task);
        finish();
    }

    @Override
    public void cancelWorkManger(Task task) {
        WorkManager.getInstance(TaskDetailActivity.this).cancelWorkById(UUID.fromString(task.getWorkManagerId()));
    }

    @Override
    public void setAlarmManager(long id , Task task) {
        Intent intent = new Intent(TaskDetailActivity.this , Remiders.class);
        intent.putExtra("eventTitle" , task.getTitle());
        intent.putExtra("taskDescription" , task.getDescription());
        PendingIntent pi;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S){
            pi = PendingIntent.getBroadcast(getApplicationContext() , 0 , intent , PendingIntent.FLAG_IMMUTABLE );
        }
        else {
            pi = PendingIntent.getBroadcast(TaskDetailActivity.this , 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT );
        }

        switch (task.getTime_period()){
            case 0 :
                notificationTime = TimeUnit.DAYS.toMillis(1);
                break;
            case 1 :
                notificationTime = TimeUnit.DAYS.toMillis(3);
                break;
            case 2 :
                notificationTime = TimeUnit.DAYS.toMillis(7);
                break;
            case 3 :
                notificationTime =TimeUnit.DAYS.toMillis(30);
                break;
        }

        long date = System.currentTimeMillis() + notificationTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        manager.set(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis() , pi);
    }

    @Override
    public void cancelAlarmManager(long requestCode) {
        Intent intent = new Intent(this, Remiders.class);
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getBroadcast(this  , (int) requestCode , intent , PendingIntent.FLAG_IMMUTABLE );
        }
        else {
            pendingIntent = PendingIntent.getBroadcast(this , (int) requestCode , intent , PendingIntent.FLAG_UPDATE_CURRENT );
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager != null){
            manager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
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

    public int getExpireDate(int timePeriod){
        if(timePeriod == 0)
            return 1;
        else if (timePeriod == 1)
            return 3;
        else if (timePeriod == 2)
            return 7;
        else
            return 30;
    }
}