package com.jabbar.Ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jabbar.API.ChangeFavoriteAPI;
import com.jabbar.API.GetContactAPI;
import com.jabbar.Adapter.BuddiesAdapter;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.MyClickListener;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import static com.jabbar.Ui.InputDataActivity.PERMISSION_CODE;

public class BuddiesFragment extends Fragment implements UpdateContact.ContactListener, ResponseListener, GetLocation.MyLocationListener {


    private View mView;
    public ArrayList<ContactsBean> contactsBeanArrayList;
    private RecyclerView rlFriendList;
    private BuddiesAdapter buddiesAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progress_refresh;
    private int Clickpos = -1;
    private GetLocation getLocation;
    private UpdateContact updateContact;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_myfriend, null);
        return mView;
    }

    public void OnUpdate() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, PERMISSION_CODE);
        } else {
            getLocation = new GetLocation(getContext(), this);
            getLocation.UpdateLocation();
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
        contactsBeanArrayList = new UserBll(getContext()).geBuddiestList(false);
        buddiesAdapter = new BuddiesAdapter(getContext(), contactsBeanArrayList, myClickListener);
        rlFriendList.setAdapter(buddiesAdapter);

        if (updateContact == null) {
            updateContact = new UpdateContact(getContext(), this, true);
            updateContact.execute();
        }

    }

    @Override
    public void OnSuccess(boolean b, ArrayList<ContactsBean> contactsBeanArrayList, boolean OnlySync) {
        updateContact = null;
        if (b && contactsBeanArrayList.size() > 0) {
            if (OnlySync) {
                new UserBll(getContext()).UpdateDirectContact(contactsBeanArrayList);
                contactsBeanArrayList.clear();
                contactsBeanArrayList.addAll(new UserBll(getContext()).geBuddiestList(false));
                buddiesAdapter.notifyDataSetChanged();
            } else {
                if (Utils.isOnline(getContext())) {
                    new GetContactAPI(getContext(), this, contactsBeanArrayList);
                } else {
                    progress_refresh.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No internet. Try again", Toast.LENGTH_LONG).show();
                }
            }
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
            HomeActivity.isFavoriteUpdate = true;
        } else if (tag.equalsIgnoreCase(Config.TAG_CHANGE_FAVORITE) && result == 0) {
            new UserBll(getContext()).updateFavoriteContact(contactsBeanArrayList.get(Clickpos).userid, (int) obj);
            contactsBeanArrayList.get(Clickpos).isFavorite = (int) obj;
            buddiesAdapter.notifyDataSetChanged();
            HomeActivity.isFavoriteUpdate = true;
        } else {
            Toast.makeText(getContext(), obj.toString(), Toast.LENGTH_LONG).show();
        }
        Clickpos = -1;
    }

    public MyClickListener myClickListener = new MyClickListener() {
        @Override
        public void onClick(int pos) {
            if (Utils.isOnline(getContext())) {
                if (progress_refresh.getVisibility() == View.GONE) {
                    progress_refresh.setVisibility(View.VISIBLE);
                    Clickpos = pos;
                    new ChangeFavoriteAPI(getContext(), BuddiesFragment.this, contactsBeanArrayList.get(pos).userid);
                }
            } else {
                Toast.makeText(getContext(), "No internet. Try again", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void getLoc(boolean isUpdate) {
        Log.print("=====UpdateContact====");
        if (progress_refresh.getVisibility() == View.GONE && updateContact == null) {
            progress_refresh.setVisibility(View.VISIBLE);
            updateContact = new UpdateContact(getContext(), this, false);
            updateContact.execute();
        }
    }
}
