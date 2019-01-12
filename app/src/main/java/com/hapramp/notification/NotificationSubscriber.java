package com.hapramp.notification;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hapramp.preferences.HaprampPreferenceManager;
import com.hapramp.utils.HashGenerator;

public class NotificationSubscriber {
  /**
   * subscribe with `username` topic
   */
  public static void subscribeForUserTopic() {
    String username = HaprampPreferenceManager.getInstance().getCurrentSteemUsername();
    String topic = HashGenerator.getMD5Hash(username);
    FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        HaprampPreferenceManager.getInstance().setUserTopicSubscribed(true);
      }
    });

    unsubscribeToUnrequiredTopic();
  }

  /**
   * subscribe to listening to new competitions.
   */
  public static void subscribeForNewCompetition() {
    final String topic = "competitionstest";
    FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
      }
    });
  }

  /**
   * subscribe to listen for particular competition.
   *
   * @param competition_id is the topic to target participants
   */
  public static void subscribeForParticularCompetition(String competition_id) {
    competition_id = "test_comp";
    FirebaseMessaging.getInstance().subscribeToTopic(competition_id).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {

      }
    });
  }

  /**
   * unsubscribe from listening for particular competition.
   *
   * @param competition_id is the topic to target participants
   */
  public static void unsubscribeForParticularCompetition(String competition_id) {
    FirebaseMessaging.getInstance().unsubscribeFromTopic(competition_id).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {

      }
    });
  }

  /**
   * unsubscribe from listening to new competitions.
   */
  public static void unsubscribeForNewCompetition() {
    String topic = "competition";
    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {

      }
    });
  }

  /**
   * unsubscribe with `username` topic
   */
  public static void unsubscribeForUserTopic() {
    String username = HaprampPreferenceManager.getInstance().getCurrentSteemUsername();
    String topic = HashGenerator.getMD5Hash(username);
    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        HaprampPreferenceManager.getInstance().setUserTopicSubscribed(false);
      }
    });
  }

  public static void unsubscribeToUnrequiredTopic(){
    String username = HaprampPreferenceManager.getInstance().getCurrentSteemUsername();
    FirebaseMessaging.getInstance().unsubscribeFromTopic(username).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        HaprampPreferenceManager.getInstance().setUserTopicSubscribed(false);
      }
    });
  }
}
