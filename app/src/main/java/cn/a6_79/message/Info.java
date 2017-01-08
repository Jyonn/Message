package cn.a6_79.message;

import java.util.ArrayList;

class Contact {
    String name;
    String phone;
    String email;
    int id;
    int checked;
    Contact(String name, String phone, String email, int id) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.id = id;
        this.checked = 0;
    }
}

class Message {
    private String notice;
    int message_id;
    Message(String notice, int message_id) {
        this.notice = notice;
        this.message_id = message_id;
    }
    @Override
    public String toString() {
        return notice;
    }
}

class Info {
    static String username;
    static String password;
    static String session = null;
    static boolean clock_set = false;
    static String clock_time = null;
    static int mid = -1;

    static int remaining;
    static int total;

    static ArrayList<Message> messages = new ArrayList<>();
    static ArrayList<Contact> contacts = new ArrayList<>();
    static final int CONTACT_ADD = 0;
    static final int CONTACT_MODIFY = 1;
    static final int CONTACT_NEW = -1;
    static final String CONTACT_TYPE = "CONTACT_TYPE";
    static final String CONTACT_INDEX = "CONTACT_INDEX";
    public static String content;
}
