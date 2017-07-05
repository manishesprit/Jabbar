package com.jabbar.Ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/17/17.
 */

public class UpdateContact extends AsyncTask<String, String, Boolean> {


    public Context context;
    public ContactListener contactListener;
    public ArrayList<ContactsBean> contactBeanArrayList;
    public boolean OnlySync = true;

    public UpdateContact(Context context, ContactListener contactListener, boolean OnlySync) {
        this.context = context;
        this.contactListener = contactListener;
        this.OnlySync = OnlySync;
    }


    @Override
    protected Boolean doInBackground(String... params) {

        Cursor contactsCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

        if (contactsCursor != null && contactsCursor.moveToFirst()) {
            contactBeanArrayList = new ArrayList<>();

            do {
                long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                Cursor dataCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + contactId, null, null);

                String displayName = "";
                String mobilePhone = "";

                if (dataCursor != null) {
                    if (dataCursor.moveToFirst()) {
                        displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        do {
                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                if (dataCursor.getInt(dataCursor.getColumnIndex("data2")) == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                    mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                }
                            }

                        } while (dataCursor.moveToNext());


                        if (mobilePhone != null && mobilePhone.toString().length() > 9 && mobilePhone.replace("+", "").matches("\\d+(?:\\.\\d+)?")) {
                            Log.print("===displayName, mobilePhone===" + displayName + "----" + mobilePhone);
                            ContactsBean contactsBean = new ContactsBean();
                            contactsBean.mobile_number = mobilePhone.length() <= 10 ? mobilePhone : (mobilePhone.substring((mobilePhone.length() - 10), mobilePhone.length()));
                            contactsBean.name = displayName;

                            if (!contactsBean.mobile_number.equalsIgnoreCase(Pref.getValue(context, Config.PREF_MOBILE_NUMBER, ""))) {
                                checkduplicate(contactBeanArrayList, contactsBean);
                            }
                        }
                    }
                }

            } while (contactsCursor.moveToNext());

            contactsCursor.close();
            contactsCursor = null;
            return true;
        }
        contactsCursor.close();
        contactsCursor = null;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);

        if (contactListener != null) {
            if (contactBeanArrayList != null) {
                contactListener.OnSuccess(s, contactBeanArrayList, OnlySync);
            } else {
                contactListener.OnSuccess(s, null, OnlySync);
            }
        } else {
            if (contactBeanArrayList != null) {
                new UserBll(context).UpdateDirectContact(contactBeanArrayList);
            }
        }
    }


    public interface ContactListener {
        public void OnSuccess(boolean b, ArrayList<ContactsBean> contactsBeanArrayList, boolean OnlySync);
    }

    public void checkduplicate(ArrayList<ContactsBean> contactBeanArrayList, ContactsBean contactBean) {
        boolean isMatch = false;
        if (contactBeanArrayList.size() == 0) {
            isMatch = false;
        } else {
            for (ContactsBean contactBean1 : contactBeanArrayList) {
                if (contactBean1.mobile_number.equalsIgnoreCase(contactBean.mobile_number)) {
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
