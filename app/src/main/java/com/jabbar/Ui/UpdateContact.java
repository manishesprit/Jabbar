package com.jabbar.Ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.jabbar.Bean.ContactBean;
import com.jabbar.Utils.Log;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/17/17.
 */

public class UpdateContact extends AsyncTask<String, String, Boolean> {


    public Context context;
    public ContactListener contactListener;
    public ArrayList<ContactBean> contactBeanArrayList;

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
                        checkduplicate(contactBeanArrayList, new ContactBean((int) contactId, displayName, mobilePhone.length() < 11 ? mobilePhone : (mobilePhone.substring((mobilePhone.length() - 10), mobilePhone.length()))));
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
        if (s) {

        }
        contactListener.OnSuccess(s);
    }


    public interface ContactListener {
        public void OnSuccess(boolean b);
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
