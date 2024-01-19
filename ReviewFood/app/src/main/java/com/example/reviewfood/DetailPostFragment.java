package com.example.reviewfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailPostFragment extends Fragment {

    RecyclerView postRecycleDetail;
    HomeAdapter postAdapterDetail;
    private List<Post> postListDetail;
    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private String UserID;

    public static DetailPostFragment newInstance(Post post) {
        DetailPostFragment fragment = new DetailPostFragment();
        Bundle args = new Bundle();
        args.putSerializable("post_key", post); // Giả sử Post implements Serializable
        fragment.setArguments(args);
        return fragment;
    }

    private Post singlePost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            singlePost = (Post) getArguments().getSerializable("post_key");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View postFragment = inflater.inflate(R.layout.fragment_my_post, container, false);

        postRecycleDetail = postFragment.findViewById(R.id.frame_my_post);

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();

        UserID = fireAuth.getCurrentUser().getUid();

        if (singlePost != null) {
            postListDetail = new ArrayList<>();
            postListDetail.add(singlePost);
            postAdapterDetail = new HomeAdapter(getContext(), postListDetail, UserID);
            postRecycleDetail.setAdapter(postAdapterDetail);
            postRecycleDetail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        }
        return postFragment;
    }
}
