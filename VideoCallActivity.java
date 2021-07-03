package com.example.livechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoCallActivity extends AppCompatActivity
        implements  Session.SessionListener,
        PublisherKit.PublisherListener
{

    private static String API_Key = "3****78";
    private static String SESSION_ID = "4************************************************************-92";
    private static String TOKEN = "1z*****************************************************************************************************************************************************************************************************************************************************************************************Nmyza873-012";
    private static final String LOG_TAG = VideoCallActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;

    private ImageView closeVideoChatBtn;
    private DatabaseReference userRef;
    private String userID= "";

    private FrameLayout mPubViewController;
    private FrameLayout mSubViewController;

    private Session mSession;
    private Publisher mpublisher;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);


        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(userID).hasChild("Ringing")){
                            userRef.child(userID).child("Ringing").removeValue();

                            if (mpublisher != null)
                            {
                                mpublisher.destroy();
                            }
                            if (mSubscriber != null)
                            {
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoCallActivity.this,RegistrationActivity.class));
                            finish();
                        }
                        if (snapshot.child(userID).hasChild("Calling")){
                            userRef.child(userID).child("Calling").removeValue();
                            if (mpublisher != null)
                            {
                                mpublisher.destroy();
                            }
                            if (mSubscriber != null)
                            {
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoCallActivity.this,RegistrationActivity.class));
                            finish();
                        }
                        else {
                            if (mpublisher != null)
                            {
                                mpublisher.destroy();
                            }
                            if (mSubscriber != null)
                            {
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoCallActivity.this,RegistrationActivity.class));
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        requestPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,VideoCallActivity.this);
    }
    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions(){

        String [] perms = {Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

        if (EasyPermissions.hasPermissions(this,perms))
        {

            mPubViewController = findViewById(R.id.pub_container);
            mSubViewController = findViewById(R.id.sub_container);


            mSession = new Session.Builder(this,API_Key,SESSION_ID).build();

            mSession.setSessionListener(VideoCallActivity.this);
            mSession.connect(TOKEN);
        }


        else {

            EasyPermissions.requestPermissions(this,"Need Camera and Mic Permissions....",RC_VIDEO_APP_PERM,perms);
        }

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG,"Session Connected");
        mpublisher =  new Publisher.Builder(this).build();
        mpublisher.setPublisherListener(VideoCallActivity.this);

        mPubViewController.addView(mpublisher.getView());

        if (mpublisher.getView()  instanceof GLSurfaceView)
        {

            ((GLSurfaceView) mpublisher.getView()).setZOrderOnTop(true);


        }

        mSession.publish(mpublisher);
    }

    @Override
    public void onDisconnected(Session session) {


        Log.i(LOG_TAG,"Stream Disconnected");


    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(LOG_TAG,"Stream Received");

        if (mSubscriber == null)
        {

            mSubscriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubViewController.addView(mSubscriber.getView());

        }


    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.i(LOG_TAG,"Stream Dropped");
        if (mSubscriber != null )
        {

            mSubscriber = null;

            mSubViewController.removeAllViews();
        }


    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.i(LOG_TAG,"Stream Error");


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}