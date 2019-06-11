package com.asista.android.demo.asista_pns.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asista.android.demo.asista_pns.R;
import com.asista.android.pns.AsistaPNS;
import com.asista.android.pns.Result;
import com.asista.android.pns.exceptions.AsistaException;
import com.asista.android.pns.interfaces.Callback;
import com.asista.android.pns.model.SubscribedTopicsResponse;
import com.asista.android.pns.model.TopicsResponse;
import com.asista.android.pns.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin J on 05-06-2019.
 */
public class TopicsActivity extends AppCompatActivity {
    private static final String TAG = TopicsActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Adapter adapter;
    private List<SubscribedTopicsResponse.SubscribedTopic> subscribedTopics;
    private List<TopicsResponse.Topic> topics;
    private List<String> selectedTopics = new ArrayList<>();

    private boolean isSubscription;
    private TopicsActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        context = this;
        try {
            isSubscription = getIntent().getBooleanExtra(PNSActivity.IS_SUBSCRIPTION, false);
            Log.i(TAG, "onCreate: isSubscription: "+isSubscription);
            setToolbar();
            initViews();
        } catch (AsistaException e) {
            e.printStackTrace();
        }
    }

    private void setToolbar(){
        if (null != getSupportActionBar()){
            getSupportActionBar().setTitle(getResources().getString(R.string.activity_topic_list_toolbar_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_wht);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void initViews() throws AsistaException {
        recyclerView = findViewById(R.id.topic_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        progressBar = findViewById(R.id.progress_bar);

        selectedTopics.clear();

        if (isSubscription){
            ((TextView)findViewById(R.id.ok)).setText("Subscribe");
            findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: selectedTopics for subscription: "+selectedTopics);
                    if (CommonUtil.checkIsEmpty(selectedTopics)){
                        CommonUtil.displayDialog("Not Topics selected", context);
                    }else{
                        String[] topics = selectedTopics.toArray(new String[]{});
                        progressBar.setVisibility(View.VISIBLE);
                        AsistaPNS.subscribe(topics, new Callback<Void>() {
                            @Override
                            public void onSuccess(Result<Void> result) {
                                Log.i(TAG, "onSuccess: subscribed to topics successfully");
                                progressBar.setVisibility(View.GONE);
                                try {
                                    getSubscribedTopics();
                                } catch (AsistaException e) {
                                    e.printStackTrace();
                                }
                                CommonUtil.displayDialog("subscribed to topics successfully", context);
                            }

                            @Override
                            public void onFailed(AsistaException exception) {
                                Log.e(TAG, "onFailed: topics subscription failed" );
                                progressBar.setVisibility(View.GONE);
                                CommonUtil.displayDialog("subscription to topics failed. "+exception.getMessage(), context);
                                exception.printStackTrace();
                            }
                        });
                    }
                }
            });
        }else{
            ((TextView)findViewById(R.id.ok)).setText("Unubscribe");
            findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: selectedTopics for unsubscription: "+selectedTopics);
                    if (CommonUtil.checkIsEmpty(selectedTopics)){
                        CommonUtil.displayDialog("Not Topics selected", context);
                    }else{
                        String[] topics = selectedTopics.toArray(new String[]{});
                        progressBar.setVisibility(View.VISIBLE);
                        AsistaPNS.unsubscribe(topics, new Callback<Void>() {
                            @Override
                            public void onSuccess(Result<Void> result) {
                                Log.i(TAG, "onSuccess: unsubscribed from topics successfully");
                                progressBar.setVisibility(View.GONE);
                                try {
                                    getSubscribedTopics();
                                } catch (AsistaException e) {
                                    e.printStackTrace();
                                }
                                CommonUtil.displayDialog("unsubscribed from topics successfully", context);
                            }

                            @Override
                            public void onFailed(AsistaException exception) {
                                Log.e(TAG, "onFailed: topics subscription failed" );
                                progressBar.setVisibility(View.GONE);
                                CommonUtil.displayDialog("unsubscription from topics failed. "+exception.getMessage(), context);
                                exception.printStackTrace();
                            }
                        });
                    }
                }
            });
        }
        getSubscribedTopics();
    }


    private void getSubscribedTopics() throws AsistaException {
        subscribedTopics = null;
        progressBar.setVisibility(View.VISIBLE);
        AsistaPNS.getSubscribedTopics(new Callback<SubscribedTopicsResponse>() {
            @Override
            public void onSuccess(Result<SubscribedTopicsResponse> result) {
                Log.i(TAG, "onSuccess: subscribed topics fetch successful... ");
                progressBar.setVisibility(View.GONE);
                SubscribedTopicsResponse subscribedTopicsResponse = result.data;
                subscribedTopics = subscribedTopicsResponse.getList();

                if (isSubscription){
                    try {
                        getTopics();
                    } catch (AsistaException e) { e.printStackTrace(); }
                } else{
                    if (CommonUtil.checkIsEmpty(subscribedTopics)){
                        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.empty_view)).setText("No topics subscribed !!!");
                    }else {
                        findViewById(R.id.empty_view).setVisibility(View.GONE);

                    }
                    adapter = new Adapter(subscribedTopics);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(AsistaException exception) {
                Log.e(TAG, "onFailed: subscribed topics fetch failed... " );
                progressBar.setVisibility(View.GONE);
                exception.printStackTrace();
                CommonUtil.displayDialog("Subscribed topics fetch failed !!!", context);
            }
        });
    }

    private void getTopics() throws AsistaException {
        topics = null;
        progressBar.setVisibility(View.VISIBLE);
        final List<TopicsResponse.Topic> unsubscribedTopics = new ArrayList<>();
        AsistaPNS.getTopics(new Callback<TopicsResponse>() {
            @Override
            public void onSuccess(Result<TopicsResponse> result) {
                Log.i(TAG, "onSuccess: topics fetch successful... "+result.data.getList());
                progressBar.setVisibility(View.GONE);
                TopicsResponse topicsResponse = result.data;
                topics = topicsResponse.getList();
                if (CommonUtil.checkIsEmpty(topics)){
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.empty_view)).setText("No topics available !!!");
                }else {
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                    for (TopicsResponse.Topic topic: topics){
                        if (!checkIsSubscribed(topic.getTopicId()))
                            unsubscribedTopics.add(topic);
                    }
                    Log.i(TAG, "onSuccess: topics: "+topics);
                    Log.i(TAG, "onSuccess: unsubscribedtopics: "+unsubscribedTopics);
                }
                adapter = new Adapter(unsubscribedTopics, context);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(AsistaException exception) {
                Log.e(TAG, "onFailed: topics fetch failed... " );
                progressBar.setVisibility(View.GONE);
                exception.printStackTrace();
                CommonUtil.displayDialog("Topics fetch failed !!!", context);
            }
        });
    }

    private boolean checkIsSubscribed(String topic){
        for (SubscribedTopicsResponse.SubscribedTopic subscribedTopic: subscribedTopics) {
            if (subscribedTopic.getTopicId().equals(topic))
                return true;
        }
        return false;
    }

    private class Adapter extends RecyclerView.Adapter{
        private List<SubscribedTopicsResponse.SubscribedTopic> subscribedTopicList;
        private List<TopicsResponse.Topic> unsubscribedTopicList;

        public Adapter(List<SubscribedTopicsResponse.SubscribedTopic> subscribedTopicList) {
            this.subscribedTopicList = subscribedTopicList;
        }

        public Adapter(List<TopicsResponse.Topic> unsubscribedTopicList, Context context) {
            this.unsubscribedTopicList = unsubscribedTopicList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TopicViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final TopicViewHolder topicViewHolder = (TopicViewHolder)holder;

            if (isSubscription){
                final TopicsResponse.Topic topic = unsubscribedTopicList.get(position);
                topicViewHolder.checkBox.setText(topic.getTopicId());
                topicViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.i(TAG, "onCheckedChanged: subscribe checkbox click: "+isChecked);
                        if (isChecked)
                            selectedTopics.add(topic.getTopicId());
                        else
                            selectedTopics.remove(topic.getTopicId());
                    }
                });
            }else{
                final SubscribedTopicsResponse.SubscribedTopic topic = subscribedTopicList.get(position);
                topicViewHolder.checkBox.setText(topic.getTopicId());
                topicViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.i(TAG, "onCheckedChanged: unsubscribe checkbox click: "+isChecked);
                        if (isChecked)
                            selectedTopics.add(topic.getTopicId());
                        else
                            selectedTopics.remove(topic.getTopicId());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (isSubscription) {
                if (null != unsubscribedTopicList)
                    return unsubscribedTopicList.size();
            }else{
                if (null != subscribedTopicList)
                    return subscribedTopicList.size();
            }
            return 0;
        }

        private class TopicViewHolder extends RecyclerView.ViewHolder{
            private CheckBox checkBox;

            public TopicViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_row, parent, false));
                checkBox = itemView.findViewById(R.id.topic_cb);
            }
        }
    }
}
