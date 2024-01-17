package com.example.reviewfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Context context;
    private List<Post> posts;

    FirebaseFirestore fireStore;

    String currentUserID;

    public HomeAdapter(Context context, List<Post> posts, String currentUserID) {
        this.context = context;
        this.posts = posts;
        this.fireStore = FirebaseFirestore.getInstance();
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_layout, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {

        String postId = posts.get(position).postId;

        String userID = posts.get(position).getUserID();
        if(userID != null){
            fireStore.collection("User").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String avatarUri = documentSnapshot.getString("imageUri");
                    Glide.with(context).load(avatarUri).placeholder(R.drawable.avatar_default).into(holder.avatarAuthor);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Glide.with(context).load(R.drawable.avatar_default).into(holder.avatarAuthor);
                }
            });
        }

        holder.fullNameAuthor.setText(posts.get(position).getAuthor());
        holder.timePost.setText(TimestampConverter.getTime(posts.get(position).getPostTime()));

        holder.statusPost.setText(posts.get(position).getStatus());
        holder.statusPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    if (holder.statusPost.getMaxLines() == Integer.MAX_VALUE) {
                        holder.statusPost.setMaxLines(3);
                        holder.statusPost.setText(posts.get(clickedPosition).getStatus() + " .........");
                    } else {
                        holder.statusPost.setMaxLines(Integer.MAX_VALUE);
                        holder.statusPost.setText(posts.get(clickedPosition).getStatus() + "   ~ Thu gọn ~");
                    }
                }
            }
        });

        Glide.with(context).load(posts.get(position).getImagePost()).into(holder.imagePost);


        //cac chuc nang khac cua post : like, cmt, save,...
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarAuthor, btnReport;
        private TextView fullNameAuthor, timePost;
        private TextView statusPost;
        private ImageButton like, dislike, comment, save;
        private ImageView imagePost;
        private TextView countLike, countDislike, countCmt, txtSave;



        public HomeViewHolder(View view) {
            super(view);
            avatarAuthor = view.findViewById(R.id.avatar_Author);
            fullNameAuthor = view.findViewById(R.id.fullName_AuthorPost);
            timePost = view.findViewById(R.id.time_Post);
            btnReport = view.findViewById(R.id.report_Post);
            statusPost = view.findViewById(R.id.status_Post);
            imagePost = view.findViewById(R.id.post_ImageView);
            like = view.findViewById(R.id.like_Btn);
            countLike = view.findViewById(R.id.like_Count);
            dislike = view.findViewById(R.id.dislike_Btn);
            countDislike = view.findViewById(R.id.dislike_Count);
            comment = view.findViewById(R.id.comment_Btn);
            countCmt = view.findViewById(R.id.comment_Count);
            save = view.findViewById(R.id.save_Btn);
            txtSave = view.findViewById(R.id.save_Text);
        }
    }
}
