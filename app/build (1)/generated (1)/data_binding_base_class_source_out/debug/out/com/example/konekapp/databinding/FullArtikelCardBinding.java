// Generated by view binder compiler. Do not edit!
package com.example.konekapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.konekapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FullArtikelCardBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final TextView fullDateArtikel;

  @NonNull
  public final ImageView fullImageArtikel;

  @NonNull
  public final TextView fullSourceArtikel;

  @NonNull
  public final TextView fullTitleArtikel;

  private FullArtikelCardBinding(@NonNull CardView rootView, @NonNull TextView fullDateArtikel,
      @NonNull ImageView fullImageArtikel, @NonNull TextView fullSourceArtikel,
      @NonNull TextView fullTitleArtikel) {
    this.rootView = rootView;
    this.fullDateArtikel = fullDateArtikel;
    this.fullImageArtikel = fullImageArtikel;
    this.fullSourceArtikel = fullSourceArtikel;
    this.fullTitleArtikel = fullTitleArtikel;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static FullArtikelCardBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FullArtikelCardBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.full_artikel_card, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FullArtikelCardBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fullDateArtikel;
      TextView fullDateArtikel = ViewBindings.findChildViewById(rootView, id);
      if (fullDateArtikel == null) {
        break missingId;
      }

      id = R.id.fullImageArtikel;
      ImageView fullImageArtikel = ViewBindings.findChildViewById(rootView, id);
      if (fullImageArtikel == null) {
        break missingId;
      }

      id = R.id.fullSourceArtikel;
      TextView fullSourceArtikel = ViewBindings.findChildViewById(rootView, id);
      if (fullSourceArtikel == null) {
        break missingId;
      }

      id = R.id.fullTitleArtikel;
      TextView fullTitleArtikel = ViewBindings.findChildViewById(rootView, id);
      if (fullTitleArtikel == null) {
        break missingId;
      }

      return new FullArtikelCardBinding((CardView) rootView, fullDateArtikel, fullImageArtikel,
          fullSourceArtikel, fullTitleArtikel);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}