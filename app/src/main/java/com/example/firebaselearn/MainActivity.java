package com.example.firebaselearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button logout;
    private Button add;
    private Button addPicture;
    private EditText edit;
    private ListView listView;

    private Uri imageUri;

    private static final int IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);
        add = findViewById(R.id.add);
        addPicture = findViewById(R.id.addPicture);
        edit = findViewById(R.id.edit);
        listView = findViewById(R.id.listView);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,StartActivity.class));
            }
        });


        // Firebase Realtime Database

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Values")
                .child("Values Added Manually")
                .setValue("Test1");

        HashMap<String,Object>map= new HashMap<>();
        map.put("Name","Aadi");
        map.put("Email","aadipoddarmail@gmail.com");

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Values")
                .child("Values With Hash Map")
                .updateChildren(map);


        HashMap<String,Object>map1 = new HashMap<>();
        map1.put("n1","Java");
        map1.put("n2","Kotlin");
        map1.put("n3","Flutter");
        map1.put("n4","React Native");

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Languages")
                .updateChildren(map1);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_name = edit.getText().toString();
                if(txt_name.isEmpty()){
                    Toast.makeText(MainActivity.this, "No Name Entered", Toast.LENGTH_SHORT).show();
                }else {
                    /*
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Values From Text Field")
                            .child("Values With Unique ID")
                            .push() // This generates the Unique ID
                            .child("Name")
                            .setValue(txt_name);

                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Values From Text Field")
                            .child("Values Without Unique ID")
                            .child("Name")
                            .setValue(txt_name);
                     */

                    // Input Language name and show in List View
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Languages")
                            .child("Name")
                            .setValue(txt_name);
                }
            }
        });


        // To get The List of Languages
        /*
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_item,list);
        listView.setAdapter(adapter);

        DatabaseReference reference =
                FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Languages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         */


        // To get The List of Information
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_item,list);
        listView.setAdapter(adapter);

        DatabaseReference reference =
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Information");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Information info = snapshot.getValue(Information.class);
                    String txt = info.getName() + " : " + info.getEmail();
                    list.add(txt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // Firebase Cloud Firestore


        /*

        // Add data to Specific Field

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String , Object> city = new HashMap<>();
        city.put("City","Kolkata");
        city.put("State","West Bengal");
        city.put("Country","India");

        db
                .collection("places")
                .document("JSR")
                .set(city)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Values Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        // Add data to existing document
        Map<String,Object>data = new HashMap<>();
        data.put("Capital",false);

        db
                .collection("places")
                .document("JSR")
                .set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Merge Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        // Add Data with Unique ID
        data.put("Country","Japan");
        data.put("Capital","Tokyo");

        db
                .collection("places")
                .add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Values Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Update Data

        DocumentReference ref =
                FirebaseFirestore
                        .getInstance()
                        .collection("places")
                        .document("JSR");

        ref.update("Capital",true);

         */




        // Retrieve Data From Firestore

        DocumentReference docRef =
                FirebaseFirestore
                        .getInstance()
                        .collection("cities")
                        .document("SF");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()){
                    Log.d("Document",doc.getData().toString());
                }else {
                    Log.d("Document","no Data");
                }
            }
        });



        // Retrieve Data on a condition
        FirebaseFirestore
                .getInstance()
                .collection("cities")
                .whereEqualTo("capital",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                Log.d("Document",doc.getId() + "=>" + doc.getData());
                            }
                        }
                    }
                });


        // Firebase Storage (Images)

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();

            uploadImage();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            StorageReference fileRef =
                    FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child("uploads")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("DownloadUrl",url);
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "Image uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

}