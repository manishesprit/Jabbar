package com.jabbar.Ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.ExitsContactBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/17/17.
 */

public class UpdateContact1 extends AsyncTask<String, String, Boolean> {

    public static boolean SyncOn = false;
    public Context context;
    public ArrayList<ExitsContactBean> exitsContactBeanArrayList;
    public ArrayList<String> numberList;

    public UpdateContact1(Context context) {
        Log.print("====UpdateContact1======");
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        SyncOn = true;
        exitsContactBeanArrayList = Pref.getArrayValue(context, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
        if (exitsContactBeanArrayList == null) {
            exitsContactBeanArrayList = new ArrayList<ExitsContactBean>();
        }
        getNumberList();
    }

    public void getNumberList() {
        numberList = new ArrayList<>();
        Log.print("======exitsContactBeanArrayList.size=====" + exitsContactBeanArrayList.size());
        for (ExitsContactBean exitsContactBean : exitsContactBeanArrayList) {
            numberList.add(exitsContactBean.mobile_number);
            exitsContactBean.isDelate = true;
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {

        boolean result = false;
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String mobilePhone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.print("====ACCOUNT_TYPE_AND_DATA_SET=======" + pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET)));

                        if (mobilePhone != null && mobilePhone.toString().length() > 9 && mobilePhone.replace("+", "").matches("\\d+(?:\\.\\d+)?")) {
                            Log.print("===displayName, mobilePhone===" + displayName + "----" + mobilePhone);
                            ExitsContactBean exitsContactBean = new ExitsContactBean();
                            exitsContactBean.mobile_number = mobilePhone.length() <= 10 ? mobilePhone : (mobilePhone.substring((mobilePhone.length() - 10), mobilePhone.length()));
                            exitsContactBean.name = displayName;

                            if (!exitsContactBean.mobile_number.equalsIgnoreCase(Pref.getValue(context, Config.PREF_MOBILE_NUMBER, ""))) {

                                if (numberList.contains(exitsContactBean.mobile_number)) {
                                    Log.print("=========Exist=====" + numberList.indexOf(exitsContactBean.mobile_number));

                                    ExitsContactBean exitsContactBean1 = exitsContactBeanArrayList.get(numberList.indexOf(exitsContactBean.mobile_number));
                                    exitsContactBean1.name = exitsContactBean.name;
                                    exitsContactBean1.mobile_number = exitsContactBean.mobile_number;
                                    exitsContactBean1.isDelate = false;
                                    exitsContactBeanArrayList.set(numberList.indexOf(exitsContactBean.mobile_number), exitsContactBean1);

                                    // Update direct database
                                    ContactsBean contactsBean = new ContactsBean();
                                    contactsBean.name = exitsContactBean.name;
                                    contactsBean.mobile_number = exitsContactBean.mobile_number;
                                    new UserBll(context).updateContact(contactsBean);
                                } else {
                                    Log.print("=========NEW=====" + exitsContactBean.mobile_number);
                                    exitsContactBean.isDelate = false;
                                    exitsContactBeanArrayList.add(exitsContactBean);
                                    numberList.add(exitsContactBean.mobile_number);
                                    result = true;
                                }
                            }
                        }
                    }
                    pCur.close();
                }
            }
        }
        cur.close();
        return result;
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);

        String numbers = "";
        Log.print("======onPostExecute exitsContactBeanArrayList.size=====" + exitsContactBeanArrayList.size());

        for (int i = 0; i < exitsContactBeanArrayList.size(); i++) {
            Log.print("===IIII====" + exitsContactBeanArrayList.get(i).name);
            if (!exitsContactBeanArrayList.get(i).isDelate) {
                numbers += "," + exitsContactBeanArrayList.get(i).mobile_number;
            } else {
                Log.print("======Delete=====");
                exitsContactBeanArrayList.remove(i);
                i--;
            }
        }

        if (!numbers.equalsIgnoreCase("")) {
            numbers = numbers.substring(1, numbers.length());

        }
        Log.print("====NEW numbers=====" + numbers);

        Pref.setArrayValue(context, Config.PREF_CONTACT, exitsContactBeanArrayList);
        SyncOn = false;
    }

}
