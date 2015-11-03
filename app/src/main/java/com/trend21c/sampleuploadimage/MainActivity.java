package com.trend21c.sampleuploadimage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    WebView wv;
    private final static int PICK_IMAGE_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv = (WebView) findViewById(R.id.webview);
        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient());
        WebSettings set = wv.getSettings();
        set.setJavaScriptEnabled(true);
        wv.loadUrl("http://test.com/upload.php");
        wv.addJavascriptInterface(new JavaScriptInterface(this), "Android");
    }

    public class JavaScriptInterface {
        private Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void selectImage() {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();

                    new AsyncTask<Uri, Void, String>() {
                        @Override
                        protected String doInBackground(Uri... params) {
                            String mimeType = getMimeType(params[0]);

                            File file = uriToFile(params[0]);
                            String base64EncodedImage = fileToString(file);

                            return "javascript:updateImage('" + mimeType + "', '" + base64EncodedImage + "');";
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            wv.loadUrl(result);
                        }
                    }.execute(uri);
                }
            }
        }
    }

    public String getMimeType(Uri uri) {
        ContentResolver cR = getContentResolver();
        String type = cR.getType(uri);
        return type;
    }

    private File uriToFile(Uri uri) {

        String filePath = "";

        final String[] imageColumns = {MediaStore.Images.Media.DATA };

        String scheme = uri.getScheme();

        if ( scheme.equalsIgnoreCase("content") ) {
            Cursor imageCursor = getContentResolver().query(uri, imageColumns, null, null, null);

            if (imageCursor.moveToFirst()) {
                filePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        } else {
            filePath = uri.getPath();
        }

        File file = new File( filePath );

        return file;
    }

    public String fileToString(File file) {

        String fileString = "";

        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                byteOutStream.write(buf, 0, len);
            }

            byte[] fileArray = byteOutStream.toByteArray();
            fileString = new String(Base64.encodeToString(fileArray, 0));

            inputStream.close();
            byteOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
