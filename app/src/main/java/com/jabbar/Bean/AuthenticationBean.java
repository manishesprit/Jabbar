package com.jabbar.Bean;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/19/17.
 */

public class AuthenticationBean extends ResponseBean {
    public int userid;
    public String name;
    public String avatar;
    public String status;
    public String privacy;
    public ArrayList<ContactsBean> buddies_list;
}
