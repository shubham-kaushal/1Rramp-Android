package com.hapramp.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hapramp.R;
import com.hapramp.notification.FirebaseNotificationStore;
import com.hapramp.notification.NotificationParser;
import com.hapramp.notification.model.BaseNotificationModel;
import com.hapramp.ui.adapters.NotificationAdapter;
import com.hapramp.utils.NotificationSortUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hapramp.notification.FirebaseNotificationStore.NODE_IS_READ;

public class NotificationActivity extends AppCompatActivity {
  @BindView(R.id.backBtn)
  ImageView backBtn;
  @BindView(R.id.toolbar_container)
  RelativeLayout toolbarContainer;
  @BindView(R.id.no_notification_message)
  TextView noNotificationMessage;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.listView)
  RecyclerView recyclerView;
  @BindView(R.id.markAsReadBtn)
  TextView markAsReadBtn;
  private Handler mHandler;
  private NotificationAdapter notificationAdapter;
  private ArrayList<BaseNotificationModel> mNotifications;

  //todo: Mark all notifications when page opens
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification);
    ButterKnife.bind(this);
    mNotifications = new ArrayList<>();
    DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    Drawable drawable = getResources().getDrawable(R.drawable.notification_divider);
    itemDecor.setDrawable(drawable);
    mHandler = new Handler();
    notificationAdapter = new NotificationAdapter();
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(itemDecor);
    recyclerView.setAdapter(notificationAdapter);
    backBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    listenToNotifications();
  }

  private void listenToNotifications() {
    try {
      FirebaseNotificationStore.getNotificationsListNode().addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(final @NonNull DataSnapshot dataSnapshot) {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              if (dataSnapshot.exists()) {
                retrieveNotifications((Map<String, Object>) dataSnapshot.getValue());
              } else {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                markAsReadBtn.setVisibility(View.GONE);
                noNotificationMessage.setVisibility(View.VISIBLE);
              }
            }
          });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }
    catch (Exception e) {
      recyclerView.setVisibility(View.GONE);
      progressBar.setVisibility(View.GONE);
      noNotificationMessage.setVisibility(View.VISIBLE);
    }
  }

  private void retrieveNotifications(Map<String, Object> notifs) {
    ArrayList<BaseNotificationModel> notifications = new ArrayList<>();
    for (Map.Entry<String, Object> entry : notifs.entrySet()) {
      Map map = (Map) entry.getValue();
      BaseNotificationModel baseNotificationModel = NotificationParser.parseNotification(map);
      if (baseNotificationModel != null) {
        baseNotificationModel.setNotificationId(entry.getKey());
        if (map.containsKey(NODE_IS_READ)) {
          if (map.get(NODE_IS_READ) instanceof Boolean) {
            boolean isRead = (Boolean) map.get(NODE_IS_READ);
            if (!isRead) {
              enableReadMarkButton();
            }
            baseNotificationModel.setRead(isRead);
          }
        }
        notifications.add(0, baseNotificationModel);
      }
    }
    if (recyclerView != null) {
      if (notifications.size() > 0) {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        NotificationSortUtils.sortNotification(notifications);
        Collections.reverse(notifications);
        mNotifications = notifications;
        noNotificationMessage.setVisibility(View.GONE);
        notificationAdapter.setNotificationModels(notifications);
      } else {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        noNotificationMessage.setVisibility(View.VISIBLE);
        disableReadMarkButton();
      }
    }
  }

  private void enableReadMarkButton() {
    try {
      markAsReadBtn.setVisibility(View.VISIBLE);
      markAsReadBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          markAllAsRead();
        }
      });
    }
    catch (Exception e) {

    }
  }

  private void disableReadMarkButton() {
    try {
      markAsReadBtn.setVisibility(View.GONE);
    }
    catch (Exception e) {

    }
  }

  private void markAllAsRead() {
    for (int i = 0; i < mNotifications.size(); i++) {
      FirebaseNotificationStore.markAsRead(mNotifications.get(i).getNotificationId());
    }
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        disableReadMarkButton();
      }
    }, 200);
  }
}
