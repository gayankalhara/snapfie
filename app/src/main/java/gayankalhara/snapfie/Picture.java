package gayankalhara.snapfie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.facebook.AccessToken;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Picture extends AppCompatActivity {

    ///used for the camera
    private static final int CAMERA_REQUEST = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int SELECT_PICTURE = 2;

    public static boolean imageLoaded = false;
    public static int imageLoadedCounter = 0;

    private Uri fileUri;
    public String picturePath;

    Bitmap newImage;
    public ArrayList<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Intent messagePassed = getIntent();
        String message = messagePassed.getStringExtra(MainActivity.EXTRA_MESSAGE);
        imageUrls = new ArrayList<>();

        imageLoaded = false;
        imageLoadedCounter = 0;
        if(message.equals("gallery")) {
            Intent upload = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(upload, SELECT_PICTURE);
        }
        else{
            // create Intent to take a picture and return control to the calling application
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera.putExtra("android.intent.extras.CAMERA_FACING", 1);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
            camera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            // start the image capture Intent
            startActivityForResult(camera, CAMERA_REQUEST);
        }
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Snapfie");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = null;

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            imageLoadedCounter++;
            imageView = (ImageView) findViewById(R.id.imageView);
            Log.d("Snapfie", fileUri.getPath());
            newImage = BitmapFactory.decodeFile(fileUri.getPath());
            imageView.setImageBitmap(newImage);
            Log.d("Snapfie", "activity result: getting picture from camera");
            imageLoaded = true;
        } else if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            imageLoadedCounter++;
            imageView = (ImageView) findViewById(R.id.imageView);
            Log.d("Snapfie", "activity result: uploading picture from gallery");
            //loads the picture user uploaded from the gallery into an image view
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            //this.imageView = (ImageView) findViewById(R.id.imageView);
            newImage = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(newImage);

            Log.d("Snapfie", picturePath);
            imageLoaded = true;

        } else {
            Log.d("Snapfie", "activity result: else");
            if(!imageLoaded || imageLoadedCounter < 1)
            {
                finish();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Intent intent_logIn = new Intent(this, LoginActivity.class);

        if (id == R.id.action_logout) {
            AccessToken.setCurrentAccessToken(null);
            startActivity(intent_logIn);
            finish();
            return true;
        }else if (id == R.id.about_us) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancelButtonClicked(View v)
    {
            finish();
    }

    public void uploadButtonClicked(View v)
    {
            uploadImageToFacebook(newImage);
    }

    public void uploadImageToFacebook(Bitmap image)
    {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
            SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
            ShareDialog.show(Picture.this, content);
        }
    }

}
