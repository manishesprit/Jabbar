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

import com.jabbar.Adapter.BuddiesAdapter;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.Log;

import java.util.ArrayList;


/**
 * Created by hardikjani on 6/13/17.
 */

public class BuddiesFragment extends Fragment {


    private View mView;
    private ArrayList<ContactsBean> contactsBeanArrayList;
    private RecyclerView rlFriendList;
    private BuddiesAdapter buddiesAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progress_refresh;
    private UpdateContact updateContact;

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
            updateContact = new UpdateContact(getContext(), new UpdateContact.ContactListener() {
                @Override
                public void OnSuccess(boolean b, ArrayList<ContactsBean> contactsBeanArrayList) {
                    progress_refresh.setVisibility(View.GONE);
                    if (b) {

                    } else {
                        Toast.makeText(getContext(), "Sync fail. Try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
