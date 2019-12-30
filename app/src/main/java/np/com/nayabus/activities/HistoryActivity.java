package np.com.nayabus.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import np.com.nayabus.R;
import np.com.nayabus.adapters.HistoryAdapter;
import np.com.nayabus.databinding.ActivityHistoryBinding;
import np.com.nayabus.listeners.MyChildEventListener;
import np.com.nayabus.models.History;

import static np.com.nayabus.utils.FirebaseUtils.auth;
import static np.com.nayabus.utils.FirebaseUtils.database;
import static np.com.nayabus.utils.FirebaseUtils.user;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding b;
    private List<History> historyList;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_history);

        b.toolbar.setTitle("History");
        b.toolbar.imgBack.setOnClickListener(v->super.onBackPressed());

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(this, historyList);
        b.recyclerView.setAdapter(adapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadHistory();
    }

    private void loadHistory() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        database.child("users/" + user.getUid()).child("history")
                .addChildEventListener(new MyChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                super.onChildAdded(dataSnapshot, s);
                History history = dataSnapshot.getValue(History.class);
                historyList.add(history);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }
}
