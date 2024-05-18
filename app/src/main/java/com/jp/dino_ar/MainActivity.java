package com.jp.dino_ar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
  private static final int PERMISSIONS_REQUEST_CODE = 100;
  private static final String[] PERMISSIONS = {
      Manifest.permission.CAMERA,
      Manifest.permission.INTERNET,
      Manifest.permission.ACCESS_NETWORK_STATE
  };
  private WebView webView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    webView = findViewById(R.id.web_view);
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setMediaPlaybackRequiresUserGesture(false);
    webSettings.setPluginState(WebSettings.PluginState.ON);
    webSettings.setBuiltInZoomControls(true);

    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onPermissionRequest(final PermissionRequest request) {
        request.grant(request.getResources());
      }

      @Override
      public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("WebView", consoleMessage.message() + " -- From line "
            + consoleMessage.lineNumber() + " of "
            + consoleMessage.sourceId());
        return super.onConsoleMessage(consoleMessage);
      }
    });

    webView.setWebViewClient(new WebViewClientHandler());

    if (checkPermissions()) {
      if (isInternetAvailable()) {
        loadWebView();
      } else {
        showNoInternetMessage();
      }
    } else {
      requestPermissions();
    }
  }

  private boolean checkPermissions() {
    for (String permission : PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private void requestPermissions() {
    requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
  }

  private boolean isInternetAvailable() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnected();
  }

  private void showNoInternetMessage() {
    Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
    // Optionally, navigate to device settings to enable internet
    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
  }

  private void loadWebView() {
    webView.loadUrl("https://priyantha-it21021534.github.io/ctse-ar-dinowrhcbb/");
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (isInternetAvailable()) {
          loadWebView();
        } else {
          showNoInternetMessage();
        }
      } else {
        Toast.makeText(this, "Permissions are required for the app to function", Toast.LENGTH_LONG).show();
        // Optionally, close the app or navigate to settings
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.onBackPressed();
    }
  }

  private class WebViewClientHandler extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      return false;
    }
  }
}
