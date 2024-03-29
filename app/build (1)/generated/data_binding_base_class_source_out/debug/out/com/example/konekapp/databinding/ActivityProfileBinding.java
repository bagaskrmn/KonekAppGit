// Generated by view binder compiler. Do not edit!
package com.example.konekapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.konekapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityProfileBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final Button btnUpdateProfile;

  @NonNull
  public final TextView profAddress;

  @NonNull
  public final ImageView profBackAction;

  @NonNull
  public final TextView profDateJoined;

  @NonNull
  public final TextView profDetailAddress;

  @NonNull
  public final CircleImageView profImage;

  @NonNull
  public final TextView profName;

  @NonNull
  public final TextView profNameTop;

  @NonNull
  public final TextView profPhoneNumber;

  private ActivityProfileBinding(@NonNull ScrollView rootView, @NonNull Button btnUpdateProfile,
      @NonNull TextView profAddress, @NonNull ImageView profBackAction,
      @NonNull TextView profDateJoined, @NonNull TextView profDetailAddress,
      @NonNull CircleImageView profImage, @NonNull TextView profName, @NonNull TextView profNameTop,
      @NonNull TextView profPhoneNumber) {
    this.rootView = rootView;
    this.btnUpdateProfile = btnUpdateProfile;
    this.profAddress = profAddress;
    this.profBackAction = profBackAction;
    this.profDateJoined = profDateJoined;
    this.profDetailAddress = profDetailAddress;
    this.profImage = profImage;
    this.profName = profName;
    this.profNameTop = profNameTop;
    this.profPhoneNumber = profPhoneNumber;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnUpdateProfile;
      Button btnUpdateProfile = ViewBindings.findChildViewById(rootView, id);
      if (btnUpdateProfile == null) {
        break missingId;
      }

      id = R.id.profAddress;
      TextView profAddress = ViewBindings.findChildViewById(rootView, id);
      if (profAddress == null) {
        break missingId;
      }

      id = R.id.profBackAction;
      ImageView profBackAction = ViewBindings.findChildViewById(rootView, id);
      if (profBackAction == null) {
        break missingId;
      }

      id = R.id.profDateJoined;
      TextView profDateJoined = ViewBindings.findChildViewById(rootView, id);
      if (profDateJoined == null) {
        break missingId;
      }

      id = R.id.profDetailAddress;
      TextView profDetailAddress = ViewBindings.findChildViewById(rootView, id);
      if (profDetailAddress == null) {
        break missingId;
      }

      id = R.id.profImage;
      CircleImageView profImage = ViewBindings.findChildViewById(rootView, id);
      if (profImage == null) {
        break missingId;
      }

      id = R.id.profName;
      TextView profName = ViewBindings.findChildViewById(rootView, id);
      if (profName == null) {
        break missingId;
      }

      id = R.id.profNameTop;
      TextView profNameTop = ViewBindings.findChildViewById(rootView, id);
      if (profNameTop == null) {
        break missingId;
      }

      id = R.id.profPhoneNumber;
      TextView profPhoneNumber = ViewBindings.findChildViewById(rootView, id);
      if (profPhoneNumber == null) {
        break missingId;
      }

      return new ActivityProfileBinding((ScrollView) rootView, btnUpdateProfile, profAddress,
          profBackAction, profDateJoined, profDetailAddress, profImage, profName, profNameTop,
          profPhoneNumber);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
