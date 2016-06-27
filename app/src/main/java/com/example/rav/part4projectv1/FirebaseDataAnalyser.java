package com.example.rav.part4projectv1;

/**
 * Created by Rav on 30/04/2016.
 */
public class FirebaseDataAnalyser {
        private String status;
        private String time;

        public FirebaseDataAnalyser(String status, String time) {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
            this.status = status;
            this.time = time;
        }

        public String getStatus() {
            return status;
        }

        public String getTime() {
            return time;
        }


}
