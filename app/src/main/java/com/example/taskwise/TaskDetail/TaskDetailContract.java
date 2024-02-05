package com.example.taskwise.TaskDetail;

import com.example.taskwise.BasePresentor;
import com.example.taskwise.BaseView;
import com.example.taskwise.Model.Task;

import java.util.UUID;

public interface TaskDetailContract {
    interface view extends BaseView{
        void setTexts(int headerText, int buttonTv);
        void showTask(Task task);
        void setDeleteButtonVisibility(boolean visible);
        void setWorkManager(long id);
        void cancelWorkManger(Task task);
        void setAlarmManager(long id , Task task);
        void cancelAlarmManager(long requestCode);
        void updateTask();
        void deleteTask();
    }

    interface presentor extends BasePresentor<view>{
        void deleteButtonClicked();
        void saveButtonClicked(String title , String description , int timePeriod , int priority);
    }
}
