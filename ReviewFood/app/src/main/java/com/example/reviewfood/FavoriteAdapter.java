package com.example.reviewfood;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reviewfood.Fragment.FavoriteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    List<Post> favoritePostList;
    Context context;
    String currentUserId;
    FavoriteFragment favoritePostFragment;
    FirebaseFirestore fireStore;

    // Interface callback mới
    public interface OnFavoritePostClickListener {
        void onPostDetailClicked(Post post);
    }

    // Thành viên để giữ tham chiếu đến callback
    private OnFavoritePostClickListener listener;

    // Thêm listener vào constructor
    public FavoriteAdapter(List<Post> favoritePostList, Context context, String currentUserId, FavoriteFragment favoritePostFragment, OnFavoritePostClickListener listener) {
        this.listener = listener;
        this.favoritePostList = favoritePostList;
        this.context = context;
        this.currentUserId = currentUserId;
        this.favoritePostFragment = favoritePostFragment;
        this.fireStore = FirebaseFirestore.getInstance();
    }

    public FavoriteAdapter(List<Post> favoritePostList, Context context, String currentUserId, FavoriteFragment favoritePostFragment) {

        this.favoritePostList = favoritePostList;
        this.context = context;
        this.currentUserId = currentUserId;
        this.favoritePostFragment = favoritePostFragment;
        this.fireStore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_favorite_post_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String postId = favoritePostList.get(position).postId;
        holder.fullNameFavorite.setText(favoritePostList.get(position).getAuthor());
        holder.statusFavorite.setText(favoritePostList.get(position).getStatus());
        Glide.with(context).load(favoritePostList.get(position).getImagePost()).into(holder.imgFavoritePost);
        fireStore.collection("User/" + currentUserId + "/Favorite Post").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        holder.timeSaved.setText(TimestampConverter.getTime(doc.getTimestamp("saveTime")));
                    }
                }
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    listener.onPostDetailClicked(favoritePostList.get(position));
                }
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder unsaveDialog = new AlertDialog.Builder(context);
                unsaveDialog.setTitle("Do you want to unsave this post?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fireStore.collection("User/" + currentUserId + "/Favorite Post").document(postId).delete();
                                favoritePostFragment.listenDataChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                unsaveDialog.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritePostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameFavorite, statusFavorite, timeSaved;
        ImageView imgFavoritePost;
        Button btnDetail, btnRemove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameFavorite = itemView.findViewById(R.id.fullName_FavoritePost);
            statusFavorite = itemView.findViewById(R.id.status_FavoritePost);
            imgFavoritePost = itemView.findViewById(R.id.imgFavorite_Post);
            timeSaved = itemView.findViewById(R.id.time_Saved);
            btnDetail = itemView.findViewById(R.id.btn_Detail);
            btnRemove = itemView.findViewById(R.id.btn_Remove);
        }
    }
}


























































































//