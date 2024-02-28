package com.example.taskwise.Calendar;

import static android.app.ProgressDialog.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.taskmanager.R;
import com.example.taskwise.Main.Adapter.EventAdapter;
import com.example.taskwise.ContextWrapper;
import com.example.taskwise.DataBase.AppDataBase;
import com.example.taskwise.EventDatail.EventDetailActivity;
import com.example.taskwise.Model.Event;
import com.example.taskwise.SharedPreferences.AppSettingContainer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements EventAdapter.changeListener , CalendarContract.view {

    CalendarView calendar;
    RecyclerView eventsRv;
    RelativeLayout backBtn;
    LinearLayout emptyState , taskList;

    AppSettingContainer settingContainer;
    EventAdapter adapter;
    CalendarContract.presentor presentor;
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy" , Locale.US);

    public void cast(){
        calendar = findViewById(R.id.calendar);
        eventsRv = findViewById(R.id.eventsRv);
        backBtn = findViewById(R.id.backBtn);
        emptyState = findViewById(R.id.emptyState);
        taskList = findViewById(R.id.taskList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingContainer = new AppSettingContainer(this);
        ContextWrapper.setTheme(this , settingContainer.getAppTheme());
        setContentView(R.layout.activity_calendar);
        cast();

        presentor = new CalendarPresentor(AppDataBase.getAppDataBase(this).getDataBaseDao() , sdf.format(calendar.getDate()));
        adapter = new EventAdapter(this , this );
        presentor.onAttach(this);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar getDateCalendar = Calendar.getInstance();
                getDateCalendar.set(year , month , dayOfMonth);
                long date = getDateCalendar.getTimeInMillis();
                presentor.onSearch(sdf.format(date));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presentor = new CalendarPresentor(AppDataBase.getAppDataBase(CalendarActivity.this).getDataBaseDao() , sdf.format(calendar.getDate()));
        calendar.setDate(Calendar.getInstance().getTime().getTime());
        presentor.onAttach(this);
    }

    @Override
    public void onDelete(Event event) {
        presentor.deleteEvent(event , sdf.format(calendar.getDate()));
        adapter.deleteEvent(event);
    }

    @Override
    public void onClick(Event event) {
        Intent onClickIntent = new Intent(CalendarActivity.this , EventDetailActivity.class);
        onClickIntent.putExtra("event" , event);
        startActivity(onClickIntent);
    }

    @Override
    public void showEvents(List<Event> events) {
        eventsRv.setLayoutManager(new LinearLayoutManager(CalendarActivity.this , RecyclerView.VERTICAL , false));
        adapter.setEvents(events);
        eventsRv.setAdapter(adapter);
    }

    @Override
    public void setEmptyStateVisibility(boolean visible) {
        emptyState.setVisibility(visible ? View.VISIBLE : View.GONE);
        taskList.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}