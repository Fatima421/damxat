package com.example.damxat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.damxat.Model.Xat;
import com.example.damxat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

// This is the recycler view that manages the xats in the app
public class RecyclerXatAdapter extends RecyclerView.Adapter<RecyclerXatAdapter.ViewHolder> {
    private ArrayList<Xat> arrayList;
    private Context context;

    private final int MSG_TYPE_RIGHT=0;
    private final int MSG_TYPE_LEFT=1;
    FirebaseUser firebaseUser;

    // Here we are passing the list of xats and the context
    public RecyclerXatAdapter(ArrayList<Xat> arrN, Context c){
        this.arrayList = arrN;
        this.context = c;
    }

    // Here we inflate the view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xat_right, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xat_left, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

    }

    // Here we set the text of the text message element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        //holder.txtUsername.setText(arrayList.get(i).getMessage());
        holder.txtXatMessage.setText(String.valueOf(arrayList.get(i).getMessage()));


    }

    // Here we set the size of the recyler view
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Here we find and set the elements that are in the layout
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtXatMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtXatMessage= itemView.findViewById(R.id.txtXatMessage);
        }
    }

    // Here we get the firebase user and depending if it's the sender or not the message is positioned right or left
    @Override
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(arrayList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}

