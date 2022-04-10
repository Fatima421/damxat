package com.example.damxat.Views.Fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.damxat.Api.Constants.retrofit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.damxat.Adapter.RecyclerXatAdapter;
import com.example.damxat.Api.ApiInterface;
import com.example.damxat.Model.NotificationModel;
import com.example.damxat.Model.PushNotification;
import com.example.damxat.Model.ResponseModel;
import com.example.damxat.Model.User;
import com.example.damxat.Model.Xat;
import com.example.damxat.Model.XatGroup;
import com.example.damxat.R;
import com.example.damxat.Views.Activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class XatFragment extends Fragment {

    DatabaseReference ref;
    View view;
    FirebaseUser firebaseUser;
    String userid;
    Bundle bundle;
    Boolean isXatUser;
    ArrayList<Xat> arrayXats;
    ArrayList<String> arrayUsers;
    private int RecordAudioRequestCode = 1;
    private EditText txtMessage;
    private String userToken;

    XatGroup group;
    String groupName;

    public XatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_xat, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // You retrieve the bundle arguments and see if there is a xatuser, in case there is you retrieve it and if there is not then you check if there is a group instead and the set the title according
        bundle = getArguments();

        if(bundle.getString("type").equals("xatuser")){
            isXatUser = true;
            getUserXat();
        }else{
            isXatUser = false;
            groupName = bundle.getString("group");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(groupName);
            readGroupMessages(groupName);
        }

        // The permission to record
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
            }
        }

        // Elements of the view
        ImageButton btnMessage = view.findViewById(R.id.btnMessage);
        txtMessage = view.findViewById(R.id.txtMessage);
        ImageButton btnVoice = view.findViewById(R.id.btnVoice);

        // This on click sends a message, it checks if it's empty or not and in case it's not it sets the message to the one written
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMessage.getText().toString();

                if(!msg.isEmpty()){
                    sendMessage(firebaseUser.getUid(), msg, isXatUser);
                    sendNotification(firebaseUser.getDisplayName(), msg, userToken);
                }else{
                    Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("");
            }
        });

        // This button allows you to send a voice message
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, digues quelcom!");
                startActivityForResult(speechRecognizerIntent, RecordAudioRequestCode);
            }
        });

        return view;
    }

    // Analize asked permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    // Analize the recorded information
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RecordAudioRequestCode && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result=data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            if(!result.get(0).isEmpty()){
                sendMessage(firebaseUser.getUid(), result.get(0), isXatUser);
                sendNotification(firebaseUser.getDisplayName(), result.get(0), userToken);
            }else{
                Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            txtMessage.setText("");
        }
    }

    // Gets the xat user and checks if there is a user, saves it and changes the action bar title
    public void getUserXat(){
        if(getArguments()!=null) {
            userid = bundle.getString("user");

            // This is a reference to the firebase, it references the Users collection specifically
            ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);

            // It sets a listener to the reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // When the data is changed it get's the user in Users and send it to User model
                    User user = dataSnapshot.getValue(User.class);

                    // You set the title of the action bar as the user's username
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(user.getUsername());
                    userToken = user.getToken();
                    // Calls the function read user messages
                    readUserMessages();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void sendNotification(String title, String missatge, String token) {
        NotificationModel notification = new NotificationModel(title, missatge, "");
        PushNotification pushNotification = new PushNotification(token, notification);
        ApiInterface apiCall = retrofit.create(ApiInterface.class);
        Call<ResponseModel> call = apiCall.postNotification(pushNotification);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });
    }

    public void sendMessage(String sender, String message, boolean isXatUser){
        // If there is a xat user then it creates a new xat and pushes the value in firebase
        if(isXatUser==true){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            String receiver = userid;
            Xat xat = new Xat(sender, receiver, message);
            ref.child("Xats").push().setValue(xat);
        }else{
            ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

            Xat xat = new Xat(sender, message);

            if(arrayXats==null) {
                arrayXats = new ArrayList<Xat>();
                arrayXats.add(xat);
            }else{
                arrayXats.add(xat);
            }

            if(group.getUsers()==null){
                arrayUsers = new ArrayList<String>();
                arrayUsers.add(firebaseUser.getUid());
            }else{
                if(!group.getUsers().contains(firebaseUser.getUid())){
                    arrayUsers.add(firebaseUser.getUid());
                }
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("xats", arrayXats);
            hashMap.put("users", arrayUsers);
            ref.updateChildren(hashMap);
        }
    }

    public void readUserMessages(){
        arrayXats = new ArrayList<>();

        // Get's the reference to the Xats collection in firebase
        ref = FirebaseDatabase.getInstance().getReference("Xats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayXats.clear();

                // For every item that the xat has it save's it in the Xat class
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Xat xat = postSnapshot.getValue(Xat.class);
                    // If the xat receiver is equal to the user id and the sender is equal to the uid or the receiver is equal to the uid and the sender is equal to user id it adds the xat to the list of xats
                    if(xat.getReceiver().equals(userid) && xat.getSender().equals(firebaseUser.getUid()) ||
                            xat.getReceiver().equals(firebaseUser.getUid()) && xat.getSender().equals(userid)){
                        arrayXats.add(xat);
                        Log.i("logTest",xat.getMessage());
                    }
                }

                // Once we have added the xat to the list we want to update the recycler so the new message is shown
                updateRecycler();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }


    public void readGroupMessages(String groupName){

        // Gets the reference to the groups collection in firebase
        ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(XatGroup.class);

                arrayXats = group.getXats();

                if(arrayXats!=null) {
                    updateRecycler();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }

    public void updateRecycler(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerXat);
        RecyclerXatAdapter adapter = new RecyclerXatAdapter(arrayXats, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}