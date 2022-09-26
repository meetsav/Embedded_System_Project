package com.example.final_project_cs561.Retrofit;

public class SendNotificationData {
        private String to;
        private Data data;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    private Notification notification;


        // Getter Methods

        public String getTo() {
            return to;
        }

        public Data getData() {
            return data;
        }

        // Setter Methods

        public void setTo(String to) {
            this.to = to;
        }

        public void setData(Data data) {
            this.data = data;
        }

}
