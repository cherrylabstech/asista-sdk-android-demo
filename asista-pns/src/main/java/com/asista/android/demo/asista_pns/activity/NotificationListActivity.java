package com.asista.android.demo.asista_pns.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asista.android.demo.asista_pns.R;
import com.asista.android.demo.asista_pns.db.DBHelper;
import com.asista.android.demo.asista_pns.model.Message;
import com.asista.android.pns.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin J on 02-06-2019.
 */
public class NotificationListActivity extends AppCompatActivity {
    private static final String TAG = NotificationListActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private Adapter adapter;

    private List<Message> messageList = new ArrayList<>();

    private NotificationListActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        context = this;

        setToolbar();

        recyclerView = findViewById(R.id.notification_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setToolbar(){
        if (null != getSupportActionBar()){
            getSupportActionBar().setTitle(getResources().getString(R.string.activity_notification_list_toolbar_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_wht);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Refresh");
        item.setIcon(R.drawable.ic_action_refresh_wht);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                populateViews();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void populateViews(){
        Log.i(TAG, ":populateViews ");
        messageList.clear();
        messageList = DBHelper.getInstance(context).fetchMessages();

        if (!CommonUtil.checkIsEmpty(messageList)) {
            adapter = new Adapter();
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else
            Log.e(TAG, "initViews: messageList is empty" );
    }

    private class Adapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Adapter.ViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Adapter.ViewHolder viewHolder = (Adapter.ViewHolder) holder;
            final Message message = messageList.get(position);
            if (null != message) {
                viewHolder.title.setText(message.getTitle());
                viewHolder.body.setText(message.getBody());
            }
        }

        @Override
        public int getItemCount() {
            if (null != messageList)
                return messageList.size();
            return 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView body;

            public ViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false));
                title = itemView.findViewById(R.id.title);
                body = itemView.findViewById(R.id.body);

            }
        }
    }
}
