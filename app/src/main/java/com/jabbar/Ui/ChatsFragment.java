package com.jabbar.Ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jabbar.API.GetStoryAPI;
import com.jabbar.Adapter.ChatsAdapter;
import com.jabbar.Adapter.StoryAdapter;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.StoryBean;
import com.jabbar.Bll.StoryBll;
import com.jabbar.Bll.UserBll;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

public class ChatsFragment extends Fragment implements ResponseListener {


    private View mView;
    public ArrayList<ContactsBean> contactsBeanArrayList;

    private RecyclerView rlFriendList;
    private ChatsAdapter chatsAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView txtNoChats;
    private RecyclerView rcvStory;
    private StoryAdapter storyAdapter;
    private LinearLayoutManager mLayoutManagerStory;
    private ProgressBar progress_refresh;
    public ArrayList<StoryBean> storyBeanArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myfriend, null);
        return mView;
    }

    public void ListUpdate() {
        if (contactsBeanArrayList != null) {
            contactsBeanArrayList.clear();
            contactsBeanArrayList.addAll(new UserBll(getContext()).getChatList());
            chatsAdapter.notifyDataSetChanged();

            if (contactsBeanArrayList.size() == 0) {
                txtNoChats.setVisibility(View.VISIBLE);
            } else {
                txtNoChats.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progress_refresh = (ProgressBar) mView.findViewById(R.id.progress_refresh);
        rlFriendList = (RecyclerView) mView.findViewById(R.id.rlFriendList);
        rlFriendList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        rlFriendList.setLayoutManager(mLayoutManager);
        contactsBeanArrayList = new UserBll(getContext()).getChatList();
        chatsAdapter = new ChatsAdapter(getContext(), contactsBeanArrayList);
        rlFriendList.setAdapter(chatsAdapter);
        txtNoChats = (TextView) mView.findViewById(R.id.txtNoChats);
        rcvStory = (RecyclerView) mView.findViewById(R.id.rcvStory);
        mLayoutManagerStory = new LinearLayoutManager(getContext());
        mLayoutManagerStory.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvStory.setLayoutManager(mLayoutManagerStory);
        storyBeanArrayList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyBeanArrayList);
        rcvStory.setAdapter(storyAdapter);

        if (contactsBeanArrayList.size() == 0) {
            txtNoChats.setVisibility(View.VISIBLE);
        } else {
            txtNoChats.setVisibility(View.GONE);
        }

        if (Utils.isOnline(getContext())) {
            new GetStoryAPI(getContext(), ChatsFragment.this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        storyBeanArrayList.clear();
        storyBeanArrayList.addAll(new StoryBll(getContext()).getStoryListWithGroup());
        storyAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResponce(String tag, int result, Object obj) {
        progress_refresh.setVisibility(View.GONE);

        if (tag.equalsIgnoreCase(Config.TAG_GET_STORY_LIST) && result == 0) {
            storyBeanArrayList.clear();
            storyBeanArrayList.addAll(new StoryBll(getContext()).getStoryListWithGroup());
            storyAdapter.notifyDataSetChanged();
        } else {
            if (getContext() != null)
                new JabbarDialog(getContext(), obj.toString()).show();
        }
    }

}
