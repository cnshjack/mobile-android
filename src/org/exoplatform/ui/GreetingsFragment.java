package org.exoplatform.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.exoplatform.poc.tabletversion.R;

public class GreetingsFragment extends Fragment {

  private static final String TAG = "eXoGreetingsFragment";

  public GreetingsFragment() {}

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.greetings_panel, container, false);
  }
}
