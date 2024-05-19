package com.jp.dino_ar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
      Manifest.permission.INTERNET
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
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setAllowFileAccess(true);

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
      loadWebView();
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

  private void loadWebView() {
    webView.loadUrl("file:///android_asset/index.html");
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        loadWebView();
      } else {
        Toast.makeText(this, "Permissions are required for the app to function", Toast.LENGTH_LONG).show();
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

  private static class WebViewClientHandler extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      return false;
    }
  }
}
