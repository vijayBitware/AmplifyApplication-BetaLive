/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amplify.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.amplify.R;
import com.amplify.utils.Constant;
import com.amplify.utils.SharedPref;
import com.amplify.view.Activity.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static java.lang.System.in;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    Context mContext;
    SharedPref shared_pref = new SharedPref(MyApplication.getContext());
    //static Context context;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload:" + remoteMessage.getData().get("message"));

            sendNotification(remoteMessage.getData().get("message").toString());
           /* String mesage = remoteMessage.getData().get("message").toString();
            String[] chat_notification_message = null;
            if (mesage.contains("www")){
                chat_notification_message = mesage.split("www");
                String title = "Amplify";
                String msg = chat_notification_message[0];
                String image = chat_notification_message[2];
                shared_pref.writeString(SharedPref.NOTIFICATION_COUNTER,  chat_notification_message[1]);
//                new generatePictureStyleNotification(this,title, msg, image).execute();
            }*/
          //  new sendNotification(MyApplication.getContext()).execute(remoteMessage.getData().get("message").toString());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        } else {
            Log.d(TAG, "Message Notification Body: remoteMessage null");
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     *
     */

    @SuppressLint("NewApi")
    private void sendNotification(String message) {

        int count;
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        Context context = MyApplication.getContext();
        String PREFS = "MyPrefs";
        NotificationManager notificationManager;
        Notification notification;
        String title;
        SharedPref shared_pref = new SharedPref(context);
        int icon = R.drawable.icon_logo;
        long when = System.currentTimeMillis();
        String[] chat_notification_message = null;

        if (message.contains("www")) {

            chat_notification_message = message.split("www");
            Log.e("Tag","Notification array 0 "+ chat_notification_message[0]);
            Log.e("Tag","Notification array 1 "+ chat_notification_message[1]);
            Log.e("Tag","Notification array 2 "+ chat_notification_message[2]);

            shared_pref.writeString(SharedPref.NOTIFICATION_COUNTER,  chat_notification_message[1]);

            notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            title = context.getString(R.string.app_name);

            if (!SharedPref.getPreferences().getString(SharedPref.USER_ID, "")
                    .equals("")) {
                Intent notificationIntent = null;
                notificationIntent = new Intent(context, HomeActivity.class);
                notificationIntent.putExtra("from","notification");
                Constant.fromLikeNotification = 1;
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                Notification.Builder builder = new Notification.Builder(context);
                if (Build.VERSION.SDK_INT > 26){
                    Log.d("test","######### verssion is greater than 26"+chat_notification_message[2]);
                    try {
                        URL url = new URL(chat_notification_message[2]);
                        Bitmap image = null,roudedImageBitmap=null;
                        if (chat_notification_message[2].contains("facebook")){
                            //System.out.println("image url >> " +chat_notification_message[2]);
                            System.out.println("image url >> " +chat_notification_message[2]);
                            String[] facebookImegUrl = chat_notification_message[2].split("://");
                            System.out.println("imageUrl[0] >> " +facebookImegUrl[0]);
                            System.out.println("imageUrl[1] >> " +facebookImegUrl[1]);
                            System.out.println("Appended >> " +"https://"+facebookImegUrl[1]);
                            image=getFacebookProfilePicture("https://"+facebookImegUrl[1]);
                            roudedImageBitmap = getCircleBitmap(image);

                        }else {
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            roudedImageBitmap = getCircleBitmap(image);
                        }
                        builder.setSmallIcon(R.drawable.noti_icon)
                                .setShowWhen(true)
                                .setContentTitle(title)
                                .setColor(getResources().getColor(R.color.white))
                                .setContentText(chat_notification_message[0])
                                .setLargeIcon(roudedImageBitmap)
                                .setContentIntent(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("######### verssion is smaller than 26");
                    try {
                        URL url = new URL(chat_notification_message[2]);
                        Bitmap image = null,roudedImageBitmap=null;
                        if (chat_notification_message[2].contains("facebook")){

                            System.out.println("image url >> " +chat_notification_message[2]);
                            String[] facebookImegUrl = chat_notification_message[2].split("://");
                            System.out.println("imageUrl[0] >> " +facebookImegUrl[0]);
                            System.out.println("imageUrl[1] >> " +facebookImegUrl[1]);
                            System.out.println("Appended >> " +"https://"+facebookImegUrl[1]);
                            image=getFacebookProfilePicture("https://"+facebookImegUrl[1]);
                            roudedImageBitmap = getCircleBitmap(image);

                        }else {
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            roudedImageBitmap = getCircleBitmap(image);
                        }
                        builder.setSmallIcon(R.drawable.noti_icon)
                                .setShowWhen(true)
                                .setContentTitle(title)
                                .setColor(getResources().getColor(R.color.blue))
                                .setContentText(chat_notification_message[0])
                                .setLargeIcon(roudedImageBitmap)
                                .setContentIntent(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                notification = builder.getNotification();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(m, notification);

            }
            updateCounter(context);
        }

    }

    public static Bitmap getFacebookProfilePicture(String userID){
        URL imageURL = null;
        Bitmap bitmap=null;
        try {
//            "https://graph.facebook.com/" + userID + "/picture?type=large"
            imageURL = new URL(userID);
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            System.out.println("Facebook image url >> " +userID);

        } catch (Exception e) {
            Log.d("test","Expection at facebook : "+e.toString());
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String  message, imageUrl;
        int count;
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        Context context = MyApplication.getContext();
        String PREFS = "MyPrefs";
        NotificationManager notificationManager;
        Notification notification;
        String title;
        SharedPref shared_pref = new SharedPref(context);
        int icon = R.drawable.icon_logo;
        long when = System.currentTimeMillis();
        String[] chat_notification_message = null;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (!SharedPref.getPreferences().getString(SharedPref.USER_ID, "")
                        .equals("")) {
                    Intent notificationIntent = null;
                    notificationIntent = new Intent(context, HomeActivity.class);
                    notificationIntent.putExtra("from", "notification");
                    Constant.fromLikeNotification = 1;
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                    Notification.Builder builder = new Notification.Builder(context);
                    if (Build.VERSION.SDK_INT > 26) {
                        System.out.println("######### verssion is greater than 26");
                        builder.setSmallIcon(R.drawable.icon_logo)
                                .setShowWhen(true)
                                .setContentTitle(title)
                                .setColor(getResources().getColor(R.color.white))
                                .setContentText(message)
                                .setLargeIcon(result)
                                .setContentIntent(intent);
                    } else {
                        System.out.println("######### verssion is smaller than 26");
                        builder.setSmallIcon(R.drawable.icon_logo)
                                .setShowWhen(true)
                                .setContentTitle(title)
                                .setColor(getResources().getColor(R.color.blue))
                                .setContentText(message)
                                .setLargeIcon(result)
                                .setContentIntent(intent);
                    }
                    notification = builder.getNotification();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(m, notification);

                }
            updateCounter(context);
            /*Intent intent = new Intent(mContext, HomeActivity.class);
            intent.putExtra("key", "value");
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = new Notification.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(result)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                    .build();
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notif);*/
        }
    }

    static void updateCounter(Context context) {
        Intent intent = new Intent("homeScreen");
        //send broadcast
        context.sendBroadcast(intent);
    }

    public static void cancelNotification() {
        Context context = MyApplication.getContext();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
