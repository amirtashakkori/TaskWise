package com.example.taskwise.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskwise.Model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    //Task DataBase
    @Insert
    long addTask(Task task);

    @Query("select * from table_task where time_period in (1 , 2 , 3) ORDER BY time_period ")
    List<Task> getTaskList();

    @Query("select * from table_task where time_period = 0")
    List<Task> getTodayTaskList();

    @Query("select * from table_task where time_period = 4 and is_completed = 0")
    List<Task> getOutDatedTasks();

    @Query("select * from table_task where is_completed = 1")
    List<Task> getCompletedTasks();

    @Query("SELECT * FROM table_task WHERE task_title LIKE :query")
    Task search(String query);

    @Delete
    int delete(Task task);

    @Query("DELETE FROM table_task")
    void deleteAll();

    @Query("Delete from table_task where is_completed = 1")
    void deleteCompletedTasks();

    @Query("delete from table_task where time_period = 4")
    void deleteOutDatedTasks();

    @Update
    int update(Task task);
}
