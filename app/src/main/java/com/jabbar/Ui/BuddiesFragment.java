package com.jabbar.Ui;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.jabbar.Bean.ContactBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by hardikjani on 6/13/17.
 */

public class BuddiesFragment extends Fragment {


    private View mView;
    private ArrayList<ContactBean> contactBeanArrayList;
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
        contactBeanArrayList = new UserBll(getContext()).geBuddiestList(false);
        buddiesAdapter = new BuddiesAdapter(getContext(), contactBeanArrayList);
        rlFriendList.setAdapter(buddiesAdapter);


    }

    public class ContactList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_refresh.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            Cursor contactsCursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            if (contactsCursor.moveToFirst()) {

                do {
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Cursor dataCursor = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + contactId, null, null);

                    String displayName = "";
                    String mobilePhone = "";

                    if (dataCursor.moveToFirst()) {
                        displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        do {
                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                if (dataCursor.getInt(dataCursor.getColumnIndex("data2")) == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                    mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                }
                            }

                        } while (dataCursor.moveToNext());

                        if (mobilePhone != null && mobilePhone.toString().length() > 9) {
                            Log.print("===displayName, mobilePhone===" + displayName + "----" + mobilePhone);
                            checkduplicate(contactBeanArrayList, new ContactBean((int) contactId, displayName, mobilePhone.length() < 11 ? mobilePhone : (mobilePhone.substring((mobilePhone.length() - 10), mobilePhone.length()))));
                        }
                    }

                } while (contactsCursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Collections.sort(contactBeanArrayList, new Comparator<ContactBean>() {
                @Override
                public int compare(ContactBean o1, ContactBean o2) {
                    return o1.name.compareToIgnoreCase(o2.name);
                }
            });

            buddiesAdapter.notifyDataSetChanged();
            progress_refresh.setVisibility(View.GONE);
        }
    }

    public void UpdateContact() {
        Log.print("=====UpdateContact====");
        if (progress_refresh.getVisibility() == View.GONE) {
            new ContactList().execute();
        }
    }

    public void checkduplicate(ArrayList<ContactBean> contactBeanArrayList, ContactBean contactBean) {
        boolean isMatch = false;
        if (contactBeanArrayList.size() == 0) {
            isMatch = false;
        } else {
            for (ContactBean contactBean1 : contactBeanArrayList) {
                if (contactBean1.number.equalsIgnoreCase(contactBean.number)) {
                    isMatch = true;
                    break;
                }
            }
        }

        if (!isMatch) {
            contactBeanArrayList.add(contactBean);
        }
    }
}
