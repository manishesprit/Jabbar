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
import android.widget.Toast;

import com.jabbar.API.GetContactAPI;
import com.jabbar.Adapter.BuddiesAdapter;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.ResponseListener;

import java.util.ArrayList;

public class BuddiesFragment extends Fragment implements UpdateContact.ContactListener, ResponseListener {


    private View mView;
    public ArrayList<ContactsBean> contactsBeanArrayList;
    private RecyclerView rlFriendList;
    private BuddiesAdapter buddiesAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progress_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myfriend, null);
        return mView;
    }

    public void OnUpdate() {
        Toast.makeText(getContext(), "OnUpdate MyFriendFragment", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progress_refresh = (ProgressBar) mView.findViewById(R.id.progress_refresh);
        rlFriendList = (RecyclerView) mView.findViewById(R.id.rlFriendList);
        rlFriendList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        rlFriendList.setLayoutManager(mLayoutManager);
        contactsBeanArrayList = new UserBll(getContext()).geBuddiestList(false);
        buddiesAdapter = new BuddiesAdapter(getContext(), contactsBeanArrayList);
        rlFriendList.setAdapter(buddiesAdapter);

    }


    public void UpdateContact() {
        Log.print("=====UpdateContact====");
        if (progress_refresh.getVisibility() == View.GONE) {

            progress_refresh.setVisibility(View.VISIBLE);
            new UpdateContact(getContext(), this);
        }
    }

    @Override
    public void OnSuccess(boolean b, ArrayList<ContactsBean> contactsBeanArrayList) {
        if (b && contactsBeanArrayList.size() > 0) {
            new GetContactAPI(getContext(), this, contactsBeanArrayList);
        } else {
            progress_refresh.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sync fail. Try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponce(String tag, int result, Object obj) {
        progress_refresh.setVisibility(View.GONE);
        if (tag.equalsIgnoreCase(Config.TAG_GET_CONTACT_LIST) && result == 0) {
            contactsBeanArrayList.clear();
            contactsBeanArrayList.addAll(new UserBll(getContext()).geBuddiestList(false));
            buddiesAdapter.notifyDataSetChanged();
        }
    }
}
