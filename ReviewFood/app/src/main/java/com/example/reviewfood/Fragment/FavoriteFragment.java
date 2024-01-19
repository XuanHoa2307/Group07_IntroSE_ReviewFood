package com.example.reviewfood.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reviewfood.DetailPostFragment;
import com.example.reviewfood.FavoriteAdapter;
import com.example.reviewfood.Post;
import com.example.reviewfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    Context context;
    String currentUserId;
    FirebaseFirestore fireStore;
    RecyclerView favoritePostRecycle;
    FavoriteAdapter favoritePostAdapter;
    FavoriteAdapter detailPostAdapter;
    List<Post> favoriteList;

    public FavoriteFragment(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        fireStore = FirebaseFirestore.getInstance();
        favoriteList = new ArrayList<>();

        favoritePostRecycle = view.findViewById(R.id.favorite_ListPost);

        //favoritePostAdapter = new FavoriteAdapter(favoriteList, context, currentUserId, this);

        favoritePostAdapter = new FavoriteAdapter(favoriteList, context, currentUserId, this, new FavoriteAdapter.OnFavoritePostClickListener() {
            @Override
            public void onPostDetailClicked(Post post) {
                showDetailFragment(post);
            }
        });

        favoritePostRecycle.setAdapter(favoritePostAdapter);
        favoritePostRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        listenDataChanged();
        return view;
    }

    private void showDetailFragment(Post post) {
        // Tạo một instance mới của Fragment chi tiết
        DetailPostFragment detailFragment = DetailPostFragment.newInstance(post);

        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void listenDataChanged() {
        favoriteList.clear();
        favoritePostAdapter.notifyDataSetChanged();
        fireStore.collection("User/" + currentUserId + "/Favorite Post").orderBy("saveTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                for (DocumentChange doc : value.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String id = doc.getDocument().getId();
                                        fireStore.collection("Post").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Post reviewPost = task.getResult().toObject(Post.class).withId(id);
                                                    if (reviewPost != null)
                                                    {
                                                        favoriteList.add(reviewPost);
                                                        favoritePostAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    else
                                        favoritePostAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }


}
