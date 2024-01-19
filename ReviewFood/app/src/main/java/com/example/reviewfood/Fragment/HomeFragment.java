package com.example.reviewfood.Fragment;

import android.app.Dialog;
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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
    ImageButton btnTagFilter;
    Dialog tagDialog;
    RadioButton radio_Monman, radio_Monchay, radio_Chayman, radio_Monbanh, radio_Anvat, radio_Monkho, radio_Monnuoc, radio_Haisan, radio_Donuong;
    ImageButton btnCloseFilter;
    AppCompatButton btnSearch;
    List<String> tagList;
    List<String> searchList;

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
        tempPost = new ArrayList<>();

        listenDataChange();

        searchBar = mView.findViewById(R.id.search_Bar);
        btnClear = mView.findViewById(R.id.btn_clear);
        btnClear.setVisibility(View.INVISIBLE);

        viewPostRecycle = mView.findViewById(R.id.post_RecyclerView);
        btnCreatePost = mView.findViewById(R.id.btn_CreatePost);
        currentUserId = fireAuth.getCurrentUser().getUid();

        ClickToCreatePost();
        handleSearchBar();
        clickToClear();

        //tag filter
        searchList = new ArrayList<>();
        tagList = new ArrayList<>();
        btnTagFilter = mView.findViewById(R.id.btn_TagFilter);
        tagDialog = new Dialog(requireActivity());
        tagDialog.setContentView(R.layout.layout_tag_filter);
        tagDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tagDialog.setCancelable(false);
        btnCloseFilter = tagDialog.findViewById(R.id.btn_closeFilter);
        btnSearch = tagDialog.findViewById(R.id.btn_Search);
        radio_Anvat = tagDialog.findViewById(R.id.radio_Anvat);
        radio_Chayman = tagDialog.findViewById(R.id.radio_Chayman);
        radio_Haisan = tagDialog.findViewById(R.id.radio_Haisan);
        radio_Donuong = tagDialog.findViewById(R.id.radio_Donuong);
        radio_Monchay = tagDialog.findViewById(R.id.radio_Monchay);
        radio_Monnuoc = tagDialog.findViewById(R.id.radio_Monnuoc);
        radio_Monkho = tagDialog.findViewById(R.id.radio_Monkho);
        radio_Monbanh = tagDialog.findViewById(R.id.radio_Monbanh);
        radio_Monman = tagDialog.findViewById(R.id.radio_Monman);

        initRadioButton();
        clickToTag();

        viewPostAdapter = new HomeAdapter(context, postList, currentUserId);
        viewPostRecycle.setAdapter(viewPostAdapter);
        viewPostRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

    }

/*    @Override
    public void onResume() {
        super.onResume();
        //listenDataChange();
        viewPostAdapter.notifyDataSetChanged();
    }*/


    public void listenDataChange() {
        fireStore.collection("Post").orderBy("postTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                for (DocumentChange doc : value.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String postId = doc.getDocument().getId();
                                        Post rvPost = doc.getDocument().toObject(Post.class).withId(postId);

                                        postList.add(rvPost);

                                        tempPost.clear();
                                        tempPost.addAll(postList);
                                    }
                                    viewPostAdapter.notifyDataSetChanged();

                                }
                            }
                        }
                    }
                });
    }

    // Kiểm tra xem post đã tồn tại trong danh sách chưa
    private boolean isPostExists(String postId) {
        for (Post post : postList) {
            if (post.postId.equals(postId)) {
                return true;
            }
        }
        return false;
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

                    String keyValue ="";

                    if(tempValue != null){
                        keyValue = tempValue.toLowerCase().replace(" ", "");
                    }


                    Log.d("SB_key", keyValue);

                    List<Integer> relativeLevel = new ArrayList<>();
                    final String[] valueFromDB = new String[1];
                    List<Post> postsFromDB = new ArrayList<>();
                    final int[] countPost = {0};
                    countPost[0] = 0;

                    //lấy data từ firebase
                    String finalKeyValue = keyValue;
                    fireStore.collection("Post")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Lấy chuỗi status
                                            if ( document.getString("status") != null) {
                                                valueFromDB[0] = document.getString("status").toLowerCase().replace(" ", "");

                                                if(maxLengthSubString(finalKeyValue, valueFromDB[0]) >= longest){
                                                    //lấy mảng Post
                                                    String postId = document.getId();
                                                    Post rvPost = document.toObject(Post.class).withId(postId);
                                                    postsFromDB.add(rvPost);
                                                    //Lấy độ dài liên quan dài nhất
                                                    relativeLevel.add(maxLengthSubString(finalKeyValue, valueFromDB[0]));

                                                    Log.d("Search Bar", document.getId() + " => " + document.getData());
                                                    Log.d("SB_info", valueFromDB[0]);
                                                    Log.d("SB_max", String.valueOf(relativeLevel.get(countPost[0])));

                                                    countPost[0]++;
                                                }

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

                                        btnClear.setVisibility(View.VISIBLE);
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

    private void initRadioButton(){
        radio_Monman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monman.isSelected()) {
                    radio_Monman.setChecked(true);
                    radio_Monman.setSelected(true);
                } else {
                    radio_Monman.setChecked(false);
                    radio_Monman.setSelected(false);
                }
            }
        });

        radio_Monchay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monchay.isSelected()) {
                    radio_Monchay.setChecked(true);
                    radio_Monchay.setSelected(true);
                } else {
                    radio_Monchay.setChecked(false);
                    radio_Monchay.setSelected(false);
                }
            }
        });

        radio_Chayman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Chayman.isSelected()) {
                    radio_Chayman.setChecked(true);
                    radio_Chayman.setSelected(true);
                } else {
                    radio_Chayman.setChecked(false);
                    radio_Chayman.setSelected(false);
                }
            }
        });

        radio_Monkho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monkho.isSelected()) {
                    radio_Monkho.setChecked(true);
                    radio_Monkho.setSelected(true);
                } else {
                    radio_Monkho.setChecked(false);
                    radio_Monkho.setSelected(false);
                }
            }
        });

        radio_Monnuoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monnuoc.isSelected()) {
                    radio_Monnuoc.setChecked(true);
                    radio_Monnuoc.setSelected(true);
                } else {
                    radio_Monnuoc.setChecked(false);
                    radio_Monnuoc.setSelected(false);
                }
            }
        });

        radio_Monbanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monbanh.isSelected()) {
                    radio_Monbanh.setChecked(true);
                    radio_Monbanh.setSelected(true);
                } else {
                    radio_Monbanh.setChecked(false);
                    radio_Monbanh.setSelected(false);
                }
            }
        });

        radio_Haisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Haisan.isSelected()) {
                    radio_Haisan.setChecked(true);
                    radio_Haisan.setSelected(true);
                } else {
                    radio_Haisan.setChecked(false);
                    radio_Haisan.setSelected(false);
                }
            }
        });

        radio_Donuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Donuong.isSelected()) {
                    radio_Donuong.setChecked(true);
                    radio_Donuong.setSelected(true);
                } else {
                    radio_Donuong.setChecked(false);
                    radio_Donuong.setSelected(false);
                }
            }
        });

        radio_Anvat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Anvat.isSelected()) {
                    radio_Anvat.setChecked(true);
                    radio_Anvat.setSelected(true);
                } else {
                    radio_Anvat.setChecked(false);
                    radio_Anvat.setSelected(false);
                }
            }
        });
    }
    private void clickToTag(){
        btnTagFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagDialog.show();
            }
        });

        btnCloseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagDialog.dismiss();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hiện nút xóa filter
                btnClear.setVisibility(View.VISIBLE);

                //xóa list check các radio button
                searchList.clear();

                String check = "";
                if (radio_Monman.isChecked()) {
                    check = check + radio_Monman.getText().toString();
                    searchList.add(radio_Monman.getText().toString());
                }
                if (radio_Monchay.isChecked()) {
                    check = check + radio_Monchay.getText().toString();
                    searchList.add(radio_Monchay.getText().toString());
                }
                if (radio_Chayman.isChecked()) {
                    check = check + radio_Chayman.getText().toString();
                    searchList.add(radio_Chayman.getText().toString());
                }
                if (radio_Monkho.isChecked()) {
                    check = check + radio_Monkho.getText().toString();
                    searchList.add(radio_Monkho.getText().toString());
                }
                if (radio_Monnuoc.isChecked()) {
                    check = check + radio_Monnuoc.getText().toString();
                    searchList.add(radio_Monnuoc.getText().toString());
                }
                if (radio_Monbanh.isChecked()) {
                    check = check + radio_Monbanh.getText().toString();
                    searchList.add(radio_Monbanh.getText().toString());
                }
                if (radio_Haisan.isChecked()) {
                    check = check + radio_Haisan.getText().toString();
                    searchList.add(radio_Haisan.getText().toString());
                }
                if (radio_Donuong.isChecked()) {
                    check = check + radio_Donuong.getText().toString();
                    searchList.add(radio_Donuong.getText().toString());
                }
                if (radio_Anvat.isChecked()) {
                    check = check + radio_Anvat.getText().toString();
                    searchList.add(radio_Anvat.getText().toString());
                }
                Log.d("CheckedFilter", check);

                List<Integer> relativeLevel = new ArrayList<>();
                List<Post> postsFromDB = new ArrayList<>();

                //lấy data từ firebase
                fireStore.collection("Post")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        relativeLevel.add(0);

                                        String postId = document.getId();
                                        Post rvPost = document.toObject(Post.class).withId(postId);

                                        if (rvPost.getTagList().size() > 0 && searchList.size() > 0){
                                            for (int i = 0; i < rvPost.getTagList().size(); i++){
                                                for (int j = 0; j < searchList.size(); j++){
                                                    if (rvPost.getTagList().get(i).compareTo(searchList.get(j)) == 0){
                                                        relativeLevel.set(relativeLevel.size() - 1, (relativeLevel.get(relativeLevel.size() - 1) + 1) );
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if (relativeLevel.get(relativeLevel.size() - 1) > 0){
                                            //lấy mảng Post
                                            postsFromDB.add(rvPost);

                                            Log.d("Tag Filter", document.getId() + " => " + document.getData());
                                            Log.d("Tag Filter relate", String.valueOf(relativeLevel.get(relativeLevel.size() - 1)));

                                        } else {
                                            relativeLevel.remove(relativeLevel.size() - 1);
                                        }

                                    }

                                    //Sort mảng Post lấy được theo độ liên quan
                                    qsort(postsFromDB, relativeLevel,0,relativeLevel.size() - 1);

                                    //Xóa Post không liên quan (có độ liên quan < 1).
                                    for (int i = 0; i < relativeLevel.size(); i++){
                                        Log.d("Sort", postsFromDB.get(i).getStatus() + " - " + relativeLevel.get(i));
                                    }

                                    //Cập nhật màn hình chính
                                    postList.clear();
                                    postList.addAll(postsFromDB);
                                    viewPostAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d("Tag Filter", "Error getting documents to search bar from: ", task.getException());
                                }
                            }
                        });

                tagDialog.dismiss();
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
                btnClear.setVisibility(View.INVISIBLE);
                searchBar.setText("");
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

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

}
