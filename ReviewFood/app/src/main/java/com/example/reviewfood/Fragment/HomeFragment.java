package com.example.reviewfood.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reviewfood.CreatePostActivity;
import com.example.reviewfood.HomeAdapter;
import com.example.reviewfood.Post;
import com.example.reviewfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private View mView;
    private EditText searchBar;
    private ImageButton btnCreatePost;
    private RecyclerView viewPostRecycle;
    HomeAdapter viewPostAdapter;
    Context context;

    private List<Post> postList;
    private List<Post> tempPost;

    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private String currentUserId;

    ImageButton btnClear;

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

        tempPost = new ArrayList<>();
        postList = new ArrayList<>();

        listenDataChange();

        searchBar = mView.findViewById(R.id.search_Bar);
        btnClear = mView.findViewById(R.id.btn_clear);

        viewPostRecycle = mView.findViewById(R.id.post_RecyclerView);
        btnCreatePost = mView.findViewById(R.id.btn_CreatePost);
        currentUserId = fireAuth.getCurrentUser().getUid();

        ClickToCreatePost();
        handleSearchBar();
        clickToClear();

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

                                        tempPost.clear();
                                        tempPost.addAll(postList);
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

    private void handleSearchBar(){
        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && searchBar.getText().toString().length() > 0) {

                    //Lấy chuỗi từ search_bar
                    String tempValue =  searchBar.getText().toString();
                    int longest = lengthLongest(tempValue);
                    Log.d("SB_longest_word", String.valueOf(longest));
                    String keyValue = tempValue.toLowerCase().replace(" ", "");
                    Log.d("SB_key", keyValue);

                    List<Integer> relativeLevel = new ArrayList<>();
                    final String[] valueFromDB = new String[1];
                    List<Post> postsFromDB = new ArrayList<>();
                    final int[] countPost = {0};
                    countPost[0] = 0;

                    //lấy data từ firebase
                    fireStore.collection("Post")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Lấy chuỗi status
                                            valueFromDB[0] = document.getString("status").toLowerCase().replace(" ", "");

                                            if(maxLengthSubString(keyValue, valueFromDB[0]) >= longest - 1){
                                                //lấy mảng Post
                                                String postId = document.getId();
                                                Post rvPost = document.toObject(Post.class).withId(postId);
                                                postsFromDB.add(rvPost);
                                                //Lấy độ dài liên quan dài nhất
                                                relativeLevel.add(maxLengthSubString(keyValue, valueFromDB[0]));

                                                Log.d("Search Bar", document.getId() + " => " + document.getData());
                                                Log.d("SB_info", valueFromDB[0]);
                                                Log.d("SB_max", String.valueOf(relativeLevel.get(countPost[0])));

                                                countPost[0]++;
                                            }
                                        }
                                        //Sort mảng Post lấy được theo độ liên quan
                                        qsort(postsFromDB, relativeLevel,0,relativeLevel.size() - 1);

                                        //Xóa Post không liên quan (có độ liên quan < longest - 1.
                                        for (int i = 0; i < relativeLevel.size(); i++){
                                            Log.d("Sort", postsFromDB.get(i).getStatus() + " - " + relativeLevel.get(i));
                                        }

                                        //Cập nhật màn hình chính
                                        postList.clear();
                                        postList.addAll(postsFromDB);
                                        viewPostAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("Search Bar", "Error getting documents to search bar from: ", task.getException());
                                    }
                                }
                            });

                    return true;
                }
                return false;
            }
        });
    }

    private void clickToClear(){
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postList.clear();
                postList.addAll(tempPost);
                viewPostAdapter.notifyDataSetChanged();
            }
        });
    }

    //hàm tìm độ dài của chuỗi con chung lớn nhất
    public static int maxLengthSubString(String s, String t) {
        int n = s.length();
        int m = t.length();
        int[][] dp = new int[n + 1][m + 1];
        int maxLength = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    maxLength = Math.max(maxLength, dp[i][j]);
                }
            }
        }

        return maxLength;
    }

    //tìm độ dài chữ dài nhất trong xâu.
    public static int lengthLongest(String s){
        int length = 0;
        int longest = 0;

        for (int i = 0; i < s.length(); i++){
            if (s.charAt(i) == ' '){
                length = 0;
            } else {
                length = length + 1;
                if (length > longest) longest = length;
            }
        }

        return longest;
    }

    //sort data tải về theo độ liên quan
    void qsort(List<Post> b, List<Integer> a, int lo, int hi)
    {
        if(lo >= hi) return;

        int pivot = a.get(lo + (hi - lo) / 2);
        int i = lo - 1;
        int j = hi + 1;
        while(true)
        {
            while (a.get(++i) > pivot);
            while (a.get(--j) < pivot);
            if (i >= j) break;

            Collections.swap(a, i, j);
            Collections.swap(b, i, j);
        }

        qsort(b, a, lo, j);
        qsort(b, a, j + 1, hi);
    }

}
