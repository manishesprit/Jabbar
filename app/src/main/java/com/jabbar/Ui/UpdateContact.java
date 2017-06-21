package com.jabbar.Ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hardikjani on 6/17/17.
 */

public class UpdateContact extends AsyncTask<String, String, Boolean> {


    public Context context;
    public ContactListener contactListener;
    public ArrayList<ContactsBean> contactBeanArrayList;

    public UpdateContact(Context context, ContactListener contactListener) {
        this.context = context;
        this.contactListener = contactListener;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        Cursor contactsCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

        if (contactsCursor.moveToFirst()) {
            contactBeanArrayList = new ArrayList<>();

            do {
                long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                Cursor dataCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + contactId, null, null);

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
                        ContactsBean contactsBean = new ContactsBean();
                        contactsBean.mobile_number = mobilePhone.length() < 11 ? mobilePhone : (mobilePhone.substring((mobilePhone.length() - 10), mobilePhone.length()));
                        contactsBean.name = displayName;

                        if (!contactsBean.mobile_number.equalsIgnoreCase(Pref.getValue(context, Config.PREF_MOBILE_NUMBER, ""))) {
                            checkduplicate(contactBeanArrayList, contactsBean);
                        }
                    }
                }


            } while (contactsCursor.moveToNext());
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);

        if (contactListener != null) {
            if (contactBeanArrayList != null) {
                Collections.sort(contactBeanArrayList, new Comparator<ContactsBean>() {
                    @Override
                    public int compare(ContactsBean o1, ContactsBean o2) {
                        return o1.name.compareToIgnoreCase(o2.name);
                    }
                });

                contactListener.OnSuccess(s, contactBeanArrayList);
            } else {
                contactListener.OnSuccess(s, null);
            }
        }
    }


    public interface ContactListener {
        public void OnSuccess(boolean b, ArrayList<ContactsBean> contactsBeanArrayList);
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
