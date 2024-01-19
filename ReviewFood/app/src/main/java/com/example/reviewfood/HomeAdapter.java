package com.example.reviewfood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.ContextMenu;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reviewfood.Fragment.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {


        SharedPreferences preferences = context.getSharedPreferences("AdminPreferences", Context.MODE_PRIVATE);
        boolean isAdmin = preferences.getBoolean("isAdmin", false);

        // Neu la admin thi khong cho like, dislike
        if (isAdmin){
            holder.like.setEnabled(false);
            holder.dislike.setEnabled(false);
            holder.save.setEnabled(false);
        }
        else {
            holder.like.setEnabled(true);
            holder.dislike.setEnabled(true);
            holder.save.setEnabled(true);
        }

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

        int currentCmtNumber = posts.get(position).getCommentList().size();
        posts.get(position).setCommentNumber(currentCmtNumber);

        int currentLikeNumber = posts.get(position).getLikeIDList().size();
        posts.get(position).setLikeNumber(currentLikeNumber);

        int currentDislikeNumber = posts.get(position).getDislikeIDList().size();
        posts.get(position).setDislikeNumber(currentDislikeNumber);

        holder.fullNameAuthor.setText(posts.get(position).getAuthor());
        holder.timePost.setText(TimestampConverter.getTime(posts.get(position).getPostTime()));
        holder.countLike.setText(String.valueOf(posts.get(position).getLikeNumber()));
        holder.countDislike.setText(String.valueOf(posts.get(position).getDislikeNumber()));
        holder.countCmt.setText(String.valueOf(posts.get(position).getCommentNumber()));


        holder.statusPost.setText(posts.get(position).getStatus());
        holder.statusPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean expanded = false;
                if (expanded) {
                    // Giảm số dòng hiển thị khi đã mở rộng
                    holder.statusPost.setMaxLines(3);
                    holder.statusPost.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    // Hiển thị toàn bộ nội dung khi chưa mở rộng
                    holder.statusPost.setMaxLines(Integer.MAX_VALUE);
                    holder.statusPost.setEllipsize(null);
                }
                expanded = !expanded;
            }
        });

        Glide.with(context).load(posts.get(position).getImagePost()).into(holder.imagePost);
        if (posts.get(position).getImagePost() == null || posts.get(position).getImagePost().length() <= 0){
            holder.imagePost.setVisibility(View.GONE);
        }
        else holder.imagePost.setVisibility(View.VISIBLE);


        //cac chuc nang khac cua post : like, cmt, save,...


        if (posts.get(position).getLikeIDList().contains(currentUserID)){
            posts.get(position).isLiked = true;
            posts.get(position).isDisLiked = false;
            holder.like.setImageResource(R.drawable.ic_up_vote_on);
            holder.dislike.setImageResource(R.drawable.ic_down_vote);
        } else if (posts.get(position).getDislikeIDList().contains(currentUserID)) {
            posts.get(position).isLiked = false;
            posts.get(position).isDisLiked = true;
            holder.like.setImageResource(R.drawable.ic_up_vote);
            holder.dislike.setImageResource(R.drawable.ic_down_vote_on);
        }
        else {
            posts.get(position).isLiked = false;
            posts.get(position).isDisLiked = false;
            holder.like.setImageResource(R.drawable.ic_up_vote);
            holder.dislike.setImageResource(R.drawable.ic_down_vote);
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posts.get(position).isLiked == false){
                    holder.like.setImageResource(R.drawable.ic_up_vote_on);
                    posts.get(position).isLiked = true;
                    posts.get(position).getLikeIDList().add(currentUserID);
                    int currentLikeNumber = posts.get(position).getLikeNumber();
                    posts.get(position).setLikeNumber(currentLikeNumber + 1);
                    holder.countLike.setText(String.valueOf(posts.get(position).getLikeNumber()));

                    if (posts.get(position).isDisLiked == true) {
                        holder.dislike.setImageResource(R.drawable.ic_down_vote);
                        posts.get(position).isDisLiked = false;
                        if (posts.get(position).getDislikeIDList().contains(currentUserID)) {
                            posts.get(position).getDislikeIDList().remove(currentUserID);
                        }
                        int currentDisLikeNumber = posts.get(position).getDislikeNumber();
                        posts.get(position).setDislikeNumber(currentDisLikeNumber - 1);
                        holder.countDislike.setText(String.valueOf(posts.get(position).getDislikeNumber()));
                    }
                }
                else {
                    holder.like.setImageResource(R.drawable.ic_up_vote);
                    posts.get(position).isLiked = false;
                    posts.get(position).getLikeIDList().remove(currentUserID);
                    int currentLikeNumber = posts.get(position).getLikeNumber();
                    posts.get(position).setLikeNumber(currentLikeNumber - 1);
                }

                updateLikeDisLikeToCloud(position, postId);
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posts.get(position).isDisLiked == false){
                    holder.dislike.setImageResource(R.drawable.ic_down_vote_on);
                    posts.get(position).isDisLiked = true;
                    posts.get(position).getDislikeIDList().add(currentUserID);
                    int currentDisLikeNumber = posts.get(position).getDislikeNumber();
                    posts.get(position).setDislikeNumber(currentDisLikeNumber + 1);
                    holder.countDislike.setText(String.valueOf(posts.get(position).getDislikeNumber()));

                    if (posts.get(position).isLiked == true) {
                        holder.like.setImageResource(R.drawable.ic_up_vote);
                        posts.get(position).isLiked = false;
                        if (posts.get(position).getLikeIDList().contains(currentUserID)) {
                            posts.get(position).getLikeIDList().remove(currentUserID);
                        }
                        int currentLikeNumber = posts.get(position).getLikeNumber();
                        posts.get(position).setLikeNumber(currentLikeNumber - 1);
                        holder.countLike.setText(String.valueOf(posts.get(position).getLikeNumber()));
                    }

                }
                else {
                    holder.dislike.setImageResource(R.drawable.ic_down_vote);
                    posts.get(position).isDisLiked = false;
                    posts.get(position).getDislikeIDList().remove(currentUserID);
                    int currentDisLikeNumber = posts.get(position).getDislikeNumber();
                    posts.get(position).setDislikeNumber(currentDisLikeNumber - 1);
                }

                updateLikeDisLikeToCloud(position, postId);
            }
        });



        // comment

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("postId", posts.get(position).postId);
                commentIntent.putStringArrayListExtra("commentIDList", (ArrayList<String>) posts.get(position).getCommentList());
                context.startActivity(commentIntent);
            }
        });


        holder.btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
            }
        });



        // Check save post before
        fireStore.collection("User/" + currentUserID + "/Favorite Post").document(postId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value.exists()) {
                        holder.save.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_skye), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        holder.save.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        });


        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireStore.collection("User/" + currentUserID + "/Favorite Post").document(postId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.getResult().exists()) {
                                    Map<String, Object> saveTime = new HashMap<>();
                                    saveTime.put("saveTime", FieldValue.serverTimestamp());
                                    fireStore.collection("User/" + currentUserID + "/Favorite Post").document(postId).set(saveTime);
                                    holder.save.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_skye), android.graphics.PorterDuff.Mode.SRC_IN);
                                } else
                                    Toast.makeText(context, "User saved this post before.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });





    }


    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.post_context_menu, popupMenu.getMenu());

        // Kiểm tra xem bài viết có phải của người dùng hiện tại không
        boolean isCurrentUserPost = posts.get(position).getUserID().equals(currentUserID);

        // Ẩn/hiện các mục menu tùy thuộc vào người dùng hiện tại có phải là tác giả của bài viết không
        popupMenu.getMenu().findItem(R.id.menu_delete_post).setVisible(isCurrentUserPost);
        popupMenu.getMenu().findItem(R.id.menu_edit_post).setVisible(isCurrentUserPost);

        // Đăng ký sự kiện cho các mục menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_delete_post){
                    handleDeletePost(position);
                    return true;
                }
                else if (id == R.id.menu_edit_post){
                    return true;
                }
                return false;
            }
        });

        // Hiển thị PopupMenu
        popupMenu.show();
    }

    private void handleDeletePost(int position) {

        // Lấy thông tin của bình luận từ danh sách
        Post deletedPost = posts.get(position);
        List<String> idCmtOfPost = new ArrayList<>();

        // Lấy DocumentReference của bình luận cần xóa
        DocumentReference postRef = fireStore.collection("Post").document(deletedPost.postId);

        // Lấy dữ liệu của bài viết
        postRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document tồn tại, bạn có thể lấy dữ liệu từ documentSnapshot
                        Post post = documentSnapshot.toObject(Post.class);
                        // Bây giờ biến post chứa dữ liệu của bài viết từ Firestore
                        for (String id:post.getCommentList()
                             ) {
                            idCmtOfPost.add(id);
                        }
                    } else {
                        // Document không tồn tại
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi có lỗi xảy ra trong quá trình lấy dữ liệu
                });

        // Xóa bình luận từ Firestore
        postRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (String cmtId : idCmtOfPost) {
                            deleteCommentOfPost(cmtId);
                        }
                        posts.remove(deletedPost);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Post đã được xóa", Toast.LENGTH_SHORT).show();
                        //updateCommentToCloud(postId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi xóa thất bại
                        Toast.makeText(context, "Xóa Post thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteCommentOfPost(String cmtId){
        DocumentReference commentRef = fireStore.collection("Comment").document(cmtId);

        // Xóa bình luận từ Firestore
        commentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //updateCommentToCloud(postId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateLikeDisLikeToCloud(int position, String postId){
        // Tạo một HashMap để chứa dữ liệu cần cập nhật
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("likeNumber", posts.get(position).getLikeNumber()); // newLikeNumber là giá trị mới của likeNumber
        updateData.put("dislikeNumber", posts.get(position).getDislikeNumber());
        updateData.put("likeIDList", posts.get(position).getLikeIDList()); // newListLikeID là danh sách mới của listLikeID
        updateData.put("dislikeIDList", posts.get(position).getDislikeIDList());

// Đường dẫn của bài viết cần cập nhật
        String postDocumentPath = "Post/" + postId; // postId là id của bài viết cần cập nhật

// Thực hiện cập nhật dữ liệu lên Firestore
        fireStore.document(postDocumentPath)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi cập nhật thành công
                    // Ví dụ: hiển thị thông báo hoặc thực hiện các thao tác khác
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                    // Ví dụ: hiển thị thông báo lỗi hoặc thực hiện các thao tác khác
                });
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
