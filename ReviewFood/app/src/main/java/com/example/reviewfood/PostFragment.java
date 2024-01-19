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

public class PostFragment extends Fragment {

    RecyclerView postRecycle;
    HomeAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private String UserID;

    public PostFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View postFragment = inflater.inflate(R.layout.fragment_my_post, container, false);

        postList = new ArrayList<>();
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();

        UserID = fireAuth.getCurrentUser().getUid();

        postRecycle = postFragment.findViewById(R.id.frame_my_post);
        postAdapter = new HomeAdapter(getContext(), postList, UserID);
        postRecycle.setAdapter(postAdapter);
        postRecycle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listenDataChanged();

        return postFragment;
    }

    public void listenDataChanged() {
        fireStore.collection("Post").whereEqualTo("userID", UserID)
                .orderBy("postTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                for (DocumentChange doc : value.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String postId = doc.getDocument().getId();
                                        Post reviewPost = doc.getDocument().toObject(Post.class).withId(postId);
                                        postList.add(reviewPost);
                                        postAdapter.notifyDataSetChanged();
                                    } else
                                        postAdapter.notifyDataSetChanged();
                                }
                            } else
                                Toast.makeText(getContext(), "You haven't posted any reviews yet!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
