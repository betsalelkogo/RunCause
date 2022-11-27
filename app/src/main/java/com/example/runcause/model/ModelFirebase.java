package com.example.runcause.model;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.runcause.LocationHelper;
import com.example.runcause.model.intefaces.AddLocationListener;
import com.example.runcause.model.intefaces.AddProjectListener;
import com.example.runcause.model.intefaces.AddRunListener;
import com.example.runcause.model.intefaces.AddUserListener;
import com.example.runcause.model.intefaces.GetAllProjectListener;
import com.example.runcause.model.intefaces.GetAllRunsListener;
import com.example.runcause.model.intefaces.GetLocationListener;
import com.example.runcause.model.intefaces.GetProjectByNameListener;
import com.example.runcause.model.intefaces.GetUserByEmailListener;
import com.example.runcause.model.intefaces.UploadImageListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static int counter=0;
    public ModelFirebase(){
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }
    public void getAllProject(Long since, GetAllProjectListener listener){
        db.collection("projects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Project> projectList = new LinkedList<Project>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Project p = Project.fromJson(doc.getData());
                        p.setId_key(doc.getId());
                        if (p != null) {
                            projectList.add(p);
                        }
                    }
                }else{

                }
                listener.onComplete(projectList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }
    public void getAllRuns(Long since, GetAllRunsListener listener) {
        db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Run> runList = new LinkedList<Run>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Run r = Run.fromJson(doc.getData());
                        r.setId_key(doc.getId());
                        if (r != null) {
                            runList.add(r);
                        }
                    }
                }else{

                }
                listener.onComplete(runList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });

    }
    public void getLocationForRun(Run run, GetLocationListener listener){
        ArrayList<Location> list = new ArrayList<>();
        DocumentReference bulletinRef = db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION).document(run.getId_key()).collection("location").document();
        bulletinRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            for(int i=0;i<document.getData().size();i++){
                            list.add(Location.fromJson((Map<String, Object>) document.getData().get(i)));
                        }}
                    }
                });
    }
    public void addRun(@NonNull Run run,AddRunListener listener) {
        db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION)
                .document(run.getId_key()).set(run.toJson())
                .addOnSuccessListener((successListener)-> {
                    listener.onComplete();
                })
                .addOnFailureListener((e)-> {
                });
    }

    public void getUserByEmail(String userEmail, GetUserByEmailListener listener) {
        DocumentReference docRef = db.collection(Constants.MODEL_FIRE_BASE_USER_COLLECTION).document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = User.fromJson(document.getData());
                        listener.onComplete(user);
                    } else {
                        listener.onComplete(null);
                    }
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public void uploadImage(Bitmap bitmap, String id_key, final UploadImageListener listener){
        FirebaseStorage storage=FirebaseStorage.getInstance();
        final StorageReference imageRef;
        if(id_key==null){
            imageRef=storage.getReference().child("image"+counter);
            counter++;}
        else
            imageRef=storage.getReference().child(Constants.MODEL_FIRE_BASE_IMAGE_COLLECTION).child(id_key);
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data= baos.toByteArray();
        UploadTask uploadTask=imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onComplete(uri.toString());
                    }
                });
            }
        });

    }

    public void addUser(@NonNull User user, AddUserListener listener) {
        db.collection(Constants.MODEL_FIRE_BASE_USER_COLLECTION)
                .document(user.getEmail()).set(user.toJson())
                .addOnSuccessListener((successListener)-> {
                    listener.onComplete();
                })
                .addOnFailureListener((e)-> { });
    }

    public void addProject(@NonNull Project project, AddProjectListener listener) {
        if(project.getId_key()==null){
            db.collection(Constants.MODEL_FIRE_BASE_PROJECTS_COLLECTION)
                    .document().set(project.toJson())
                    .addOnSuccessListener((successListener)-> {
                        listener.onComplete();
                    })
                    .addOnFailureListener((e)-> {
                    });
        }
        else{
            db.collection(Constants.MODEL_FIRE_BASE_PROJECTS_COLLECTION)
                    .document(project.getId_key()).set(project.toJson())
                    .addOnSuccessListener((successListener)-> {
                        listener.onComplete();
                    })
                    .addOnFailureListener((e)-> {
                    });
        }
    }

    public void getProjectByName(String projectName, GetProjectByNameListener listener) {
        DocumentReference docRef = db.collection(Constants.MODEL_FIRE_BASE_PROJECTS_COLLECTION).document(projectName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Project project = Project.fromJson(document.getData());
                        listener.onComplete(project);
                    } else {
                        listener.onComplete(null);
                    }
                } else {
                    listener.onComplete(null);
                }
            }
        });

    }



    public void getLocationByRunId(String id, GetLocationListener listener) {
        db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION).document(id).collection("location").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Location> arrLocation = new ArrayList<>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Location l = Location.fromJson(doc.getData());
                        l.setId_key(doc.getId());
                        if (l != null) {
                            arrLocation.add(l);
                        }
                    }
                }else{

                }
                listener.onComplete(arrLocation);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });

    }

    public void saveLocation(String id_key, ArrayList<Location> arrLocations, AddLocationListener listener) {
        Map<String, Object> json = new HashMap<>();
        for(int i=0;i<arrLocations.size();i++)
        {
            json.put(""+i,arrLocations.get(i).toJson());
        }
        db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION)
                .document(id_key).collection("location").document().set(json)
                .addOnSuccessListener((successListener)-> {
                    listener.onComplete();
                })
                .addOnFailureListener((e)-> {
                });
    }
}
