package com.jabbar.Ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jabbar.API.GetContactAPI;
import com.jabbar.API.GetStoryAPI;
import com.jabbar.Adapter.BuddiesAdapter;
import com.jabbar.Adapter.StoryAdapter;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.ExitsContactBean;
import com.jabbar.Bean.StoryBean;
import com.jabbar.Bll.StoryBll;
import com.jabbar.Bll.UserBll;
import com.jabbar.Listener.MyClickListener;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Pref;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.Utils.UpdateContact1;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import static com.jabbar.Ui.InputDataActivity.PERMISSION_CODE;

public class BuddiesFragment extends Fragment implements ResponseListener, GetLocation.MyLocationListener {


    private View mView;
    public ArrayList<ContactsBean> contactsBeanArrayList;

    private RecyclerView rlFriendList;
    private BuddiesAdapter buddiesAdapter;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView rcvStory;
    private StoryAdapter storyAdapter;
    private LinearLayoutManager mLayoutManagerStory;

    private ProgressBar progress_refresh;
    private GetLocation getLocation;

    private Handler handler;
    private Runnable runnable;
    int Attempt = 0;

    public ArrayList<StoryBean> storyBeanArrayList;

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

    public void ListUpdate() {
        if (contactsBeanArrayList != null) {
            contactsBeanArrayList.clear();
            contactsBeanArrayList.addAll(new UserBll(getContext()).geBuddiestList(false));
            buddiesAdapter.notifyDataSetChanged();
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

        rcvStory = (RecyclerView) mView.findViewById(R.id.rcvStory);
        mLayoutManagerStory = new LinearLayoutManager(getContext());
        mLayoutManagerStory.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvStory.setLayoutManager(mLayoutManagerStory);
        storyBeanArrayList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyBeanArrayList);
        rcvStory.setAdapter(storyAdapter);

        if (Utils.isOnline(getContext())) {
            new GetStoryAPI(getContext(), BuddiesFragment.this);
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

        if (tag.equalsIgnoreCase(Config.TAG_GET_CONTACT_LIST) && result == 0) {
            contactsBeanArrayList.clear();
            contactsBeanArrayList.addAll(new UserBll(getContext()).geBuddiestList(false));
            buddiesAdapter.notifyDataSetChanged();
            HomeActivity.isFavoriteUpdate = true;

            storyBeanArrayList.clear();
            storyBeanArrayList.addAll(new StoryBll(getContext()).getStoryListWithGroup());
            storyAdapter.notifyDataSetChanged();

        } else if (tag.equalsIgnoreCase(Config.TAG_GET_STORY_LIST) && result == 0) {
            storyBeanArrayList.clear();
            storyBeanArrayList.addAll(new StoryBll(getContext()).getStoryListWithGroup());
            storyAdapter.notifyDataSetChanged();
        } else {
            new JabbarDialog(getContext(), obj.toString()).show();
        }
    }

    public MyClickListener myClickListener = new MyClickListener() {
        @Override
        public void onClick(int pos) {

            new UserBll(getContext()).updateFavoriteContact(contactsBeanArrayList.get(pos).userid, contactsBeanArrayList.get(pos).isFavorite == 0 ? 1 : 0);
            contactsBeanArrayList.get(pos).isFavorite = contactsBeanArrayList.get(pos).isFavorite == 0 ? 1 : 0;
            buddiesAdapter.notifyDataSetChanged();
            HomeActivity.isFavoriteUpdate = true;
        }
    };

    @Override
    public void getLoc(boolean isUpdate) {
        if (progress_refresh.getVisibility() == View.GONE) {
            progress_refresh.setVisibility(View.VISIBLE);
            if (UpdateContact1.SyncOn == false) {
                if (Utils.isOnline(getContext())) {
                    ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(getContext(), Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                    if (exitsContactBeanArrayList != null && exitsContactBeanArrayList.size() > 0) {
                        new GetContactAPI(getContext(), BuddiesFragment.this, exitsContactBeanArrayList);
                    } else {
                        progress_refresh.setVisibility(View.GONE);
                        new JabbarDialog(getContext(), getString(R.string.no_contact)).show();
                    }

                } else {
                    progress_refresh.setVisibility(View.GONE);
                    new JabbarDialog(getContext(), getString(R.string.no_internet)).show();
                }
            } else {
                Attempt = 0;
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (UpdateContact1.SyncOn == false) {
                            handler.removeCallbacks(runnable);
                            Attempt = 0;
                            if (Utils.isOnline(getContext())) {
                                ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(getContext(), Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                                if (exitsContactBeanArrayList != null && exitsContactBeanArrayList.size() > 0) {
                                    new GetContactAPI(getContext(), BuddiesFragment.this, exitsContactBeanArrayList);
                                } else {
                                    progress_refresh.setVisibility(View.GONE);
                                    new JabbarDialog(getContext(), getString(R.string.no_contact)).show();
                                }
                            } else {
                                progress_refresh.setVisibility(View.GONE);
                                new JabbarDialog(getContext(), getString(R.string.no_internet)).show();
                            }

                        } else {
                            if (Attempt < 20) {
                                Attempt++;
                                handler.postDelayed(runnable, 1000);
                            } else {
                                handler.removeCallbacks(runnable);
                                Attempt = 0;
                                new JabbarDialog(getContext(), getString(R.string.sync_fail)).show();
                                progress_refresh.setVisibility(View.GONE);
                            }
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }
}
