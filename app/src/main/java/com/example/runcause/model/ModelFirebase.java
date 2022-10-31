package com.example.runcause.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.runcause.model.intefaces.AddRunListener;
import com.example.runcause.model.intefaces.AddUserListener;
import com.example.runcause.model.intefaces.GetAllProjectListener;
import com.example.runcause.model.intefaces.GetAllRunsListener;
import com.example.runcause.model.intefaces.GetUserByEmailListener;
import com.example.runcause.model.intefaces.UploadImageListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

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
                LinkedList<Project> prjectList = new LinkedList<Project>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Project p = Project.fromJson(doc.getData());
                        p.setId_key(doc.getId());
                        if (p != null) {
                            prjectList.add(p);
                        }
                    }
                }else{

                }
                listener.onComplete(prjectList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });
    }
    public void getAllRuns(Long since, GetAllRunsListener listener) {
        db.collection("runs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Run> postList = new LinkedList<Run>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Run r = Run.fromJson(doc.getData());
                        r.setId_key(doc.getId());
                        if (r != null) {
                            postList.add(r);
                        }
                    }
                }else{

                }
                listener.onComplete(postList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        });

    }

    public void addRun(@NonNull Run run, AddRunListener listener) {
        if(run.getId_key()==null){
            db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION)
                .document().set(run.toJson())
                .addOnSuccessListener((successListener)-> {
                    listener.onComplete();
                })
                .addOnFailureListener((e)-> {
                });
        }
        else{
            db.collection(Constants.MODEL_FIRE_BASE_RUNS_COLLECTION)
                    .document(run.getId_key()).set(run.toJson())
                    .addOnSuccessListener((successListener)-> {
                        listener.onComplete();
                    })
                    .addOnFailureListener((e)-> {
                    });
        }
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
}
