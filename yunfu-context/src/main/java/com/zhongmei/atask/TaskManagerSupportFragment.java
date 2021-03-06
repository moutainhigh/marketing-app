package com.zhongmei.atask;

import android.support.v4.app.Fragment;

/**
 * Created by demo on 2018/12/15
 */
public class TaskManagerSupportFragment extends Fragment implements ITaskManagerFragment {

    private TaskManager taskManager;

    public TaskManagerSupportFragment() {
        //this(TaskContext, new ActivityFragmentLifecycle());
    }

    @Override
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (taskManager != null) {
            taskManager.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (taskManager != null) {
            taskManager.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (taskManager != null) {
            taskManager.onDestroy();
        }
    }
}
