package com.example.runcause.model;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.runcause.MyApplication;
import com.example.runcause.model.intefaces.AddProjectListener;
import com.example.runcause.model.intefaces.AddRunListener;
import com.example.runcause.model.intefaces.AddUserListener;
import com.example.runcause.model.intefaces.DeleteRunListener;
import com.example.runcause.model.intefaces.GetUserByEmailListener;
import com.example.runcause.model.intefaces.UploadImageListener;

import java.util.List;

public class Model {
    static final public Model instance = new Model();
    MutableLiveData<LoadingState> loadingState= new MutableLiveData<LoadingState>();
    public LiveData<LoadingState> getLoadingState(){return loadingState;}
    MutableLiveData<List<Run>> runsListLd = new MutableLiveData<List<Run>>();
    MutableLiveData<List<Project>> projectListLd = new MutableLiveData<List<Project>>();
    ModelFirebase modelFirebase = new ModelFirebase();
    private  Model(){
        loadingState.setValue(LoadingState.loaded);
        reloadProjectList();
        reloadRunsList();
    }
    public LiveData<List<Run>> getAllRun() {
        return runsListLd;
    }
    public LiveData<List<Project>> getAllProject() {
        return projectListLd;
    }
    public void reloadRunsList() {
        loadingState.setValue(LoadingState.loading);
        //1. get local last update
        Long localLastUpdate = Run.getLocalLastUpdated();
        //2. get all students record since local last update from firebase
        modelFirebase.getAllRuns(localLastUpdate,(list)->{
            if(list !=null){
                MyApplication.executorService.execute(()->{
                    //3. update local last update date
                    //4. add new records to the local db
                    Long lLastUpdate = new Long(0);
                    for(Run r : list){
                        if(!r.isDeleted()) {
                            AppLocalDB.db.runDao().insertAll(r);
                        }
                        else {
                            AppLocalDB.db.runDao().delete(r);
                        }
                        if (r.getLastUpdated() > lLastUpdate){
                            lLastUpdate = r.getLastUpdated();
                        }
                    }
                    Run.setLocalLastUpdated(lLastUpdate);
                    //5. return all records to the caller
                    List<Run> stList = AppLocalDB.db.runDao().getAll();
                    for (Run r: stList){
                        if(r.isDeleted()){
                            AppLocalDB.db.runDao().delete(r);
                        }
                    }
                    runsListLd.postValue(stList);
                    loadingState.postValue(LoadingState.loaded);
                });
            }
        });
    }

    public void addRun(Run run, AddRunListener listener){
        modelFirebase.addRun(run,()->{
            reloadRunsList();
            listener.onComplete();
        });
    }

    public void addProject(Project project, AddProjectListener listener){
        modelFirebase.addProject(project,()->{
            reloadProjectList();
            listener.onComplete();
        });
    }

    public void addUser(User user, AddUserListener listener){
        modelFirebase.addUser(user, () ->{
            listener.onComplete();
        });

    }
    public LiveData<List<Run>> getRunByName(String name) {
        return AppLocalDB.db.runDao().getRunByProject(name);
    }

    public LiveData<List<Project>> getProjectByName(String project_id) {
        return AppLocalDB.db.projectDao().getProjectById(project_id);
    }
    public void DeleteRun(Run run, DeleteRunListener listener){
        run.setDeleted(true);
        addRun(run,()->{
            listener.onComplete();
        });
    }

    public void getUserByEmail(String userEmail, GetUserByEmailListener listener) {
        modelFirebase.getUserByEmail(userEmail,listener);
    }
    public void uploadImage(Bitmap bitmap, String name, final UploadImageListener listener){
        modelFirebase.uploadImage(bitmap,name,listener);
    }

    public void reloadProjectList() {
        loadingState.setValue(LoadingState.loading);
        //1. get local last update
        Long localLastUpdate = Project.getLocalLastUpdated();
        //2. get all students record since local last update from firebase
        modelFirebase.getAllProject(localLastUpdate,(list)->{
            if(list !=null){
                MyApplication.executorService.execute(()->{
                    //3. update local last update date
                    //4. add new records to the local db
                    Long lLastUpdate = new Long(0);
                    for(Project p : list){
                        if(p.isPublic()) {
                            AppLocalDB.db.projectDao().insertAll(p);
                        }
                        else {
                            AppLocalDB.db.projectDao().delete(p);
                        }
                        if (p.getLastUpdated() > lLastUpdate){
                            lLastUpdate = p.getLastUpdated();
                        }
                    }
                    Project.setLocalLastUpdated(lLastUpdate);
                    //5. return all records to the caller
                    List<Project> projectList = AppLocalDB.db.projectDao().getAll();
                    for (Project p: projectList){
                        if(!p.isPublic()){
                            AppLocalDB.db.projectDao().delete(p);
                        }
                    }
                    projectListLd.postValue(projectList);
                    loadingState.postValue(LoadingState.loaded);
                });
            }
        });
    }
}
