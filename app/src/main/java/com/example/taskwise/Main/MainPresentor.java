package com.example.taskwise.Main;

import com.example.taskwise.DataBase.TaskDao;
import com.example.taskwise.Model.Task;
import com.example.taskmanager.R;
import com.example.taskwise.SharedPreferences.AppSettingContainer;
import com.example.taskwise.SharedPreferences.UserInfoContainer;

import java.util.List;

public class MainPresentor implements MainContract.presentor {

    TaskDao dao;
    UserInfoContainer userInfoContainer;
    AppSettingContainer settingContainer;
    List<Task> todaysTasks;
    List<Task> futureTasks;
    MainContract.view view;

    int appTheme;
    String userName;
    String fullName;
    String expertise;

    public MainPresentor(TaskDao dao , UserInfoContainer userInfoContainer , AppSettingContainer settingContainer) {
        this.dao = dao;
        this.userInfoContainer = userInfoContainer;
        this.settingContainer = settingContainer;
        todaysTasks = dao.getTodayTaskList();
        futureTasks = dao.getTaskList();
        appTheme = settingContainer.getAppTheme();
        userName = userInfoContainer.getName().toString();
        fullName = userInfoContainer.getName() + " " + userInfoContainer.getFamily();
        expertise = userInfoContainer.getExpertise();
    }

    @Override
    public void onAttach(MainContract.view view) {
        this.view = view;
        view.setDate();
        view.setNavigationDrawerText(fullName , expertise);
        if (!todaysTasks.isEmpty() && !futureTasks.isEmpty()){
            view.setHeaderTexts(userName , R.string.todayTasks , todaysTasks.size());
            view.showTasks(todaysTasks );
            view.setEmptyStateVisibility(false , appTheme);
            view.setListEmptyStateVisibility(false , appTheme);
        }
        else if (!todaysTasks.isEmpty() && futureTasks.isEmpty()) {
            view.setHeaderTexts(userName , R.string.todayTasks , todaysTasks.size());
            view.showTasks(todaysTasks );
            view.setEmptyStateVisibility(false , appTheme);
        }
        else if (todaysTasks.isEmpty() && !futureTasks.isEmpty()){
            view.setHeaderTexts(userName , R.string.futureTasks , futureTasks.size());
            view.setListEmptyStateVisibility(true , appTheme);
            view.setEmptyStateVisibility(false , appTheme);
        }
        else if (todaysTasks.isEmpty() && futureTasks.isEmpty()){
            view.setEmptyStateVisibility(true, appTheme);
            view.setHeaderTexts(userName, R.string.taskManager, todaysTasks.size());
        }
    }

    @Override
    public void onDetach() {
        this.view = null;
    }

    @Override
    public void validatingUserInfo() {
        if (userName.equals("")){
            view.goToWelcomeActivity();
        }
    }

    @Override
    public void listSwitch(boolean b) {
        if (b == false){
            if (!futureTasks.isEmpty()){
                view.setListEmptyStateVisibility(false , settingContainer.getAppTheme());
                view.showTasks(futureTasks);
            } else {
                view.setListEmptyStateVisibility(true , settingContainer.getAppTheme());
            }
        }
        //This is the first one
        else{
            if (!todaysTasks.isEmpty()){
                view.setListEmptyStateVisibility(false , settingContainer.getAppTheme());
                view.showTasks(todaysTasks);
            } else {
                view.setListEmptyStateVisibility(true , appTheme);
            }
        }
    }

    @Override
    public void clearListClicked() {
        dao.deleteAll();
        view.setEmptyStateVisibility(true , appTheme);
    }

    @Override
    public void updateTask(Task task) {
        dao.update(task);
    }

}
