package com.example.reviewfood.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reviewfood.CreatePostActivity;
import com.example.reviewfood.HomeAdapter;
import com.example.reviewfood.Post;
import com.example.reviewfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View mView;
    private EditText searchBar;
    private ImageButton btnCreatePost;
    private RecyclerView viewPostRecycle;
    HomeAdapter viewPostAdapter;
    Context context;

    private List<Post> postList;

    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private String currentUserId;

    public HomeFragment(Context context){
        this.context = context;
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View mView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);

        postList = new ArrayList<>();

        listenDataChange();

        searchBar = mView.findViewById(R.id.search_Bar);
        viewPostRecycle = mView.findViewById(R.id.post_RecyclerView);
        btnCreatePost = mView.findViewById(R.id.btn_CreatePost);
        currentUserId = fireAuth.getCurrentUser().getUid();

        ClickToCreatePost();

        viewPostAdapter = new HomeAdapter(context, postList, currentUserId);
        viewPostRecycle.setAdapter(viewPostAdapter);
        viewPostRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

    }

    public void listenDataChange() {
        fireStore.collection("Post").orderBy("postTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                for (DocumentChange doc : value.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String postId = doc.getDocument().getId();
                                        Post rvPost = doc.getDocument().toObject(Post.class).withId(postId);
                                        postList.add(rvPost);
                                        viewPostAdapter.notifyDataSetChanged();
                                    } else
                                        viewPostAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }




    private void ClickToCreatePost(){
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });


    }
}
