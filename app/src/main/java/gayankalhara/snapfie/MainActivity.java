package gayankalhara.snapfie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView welcome;
    private Profile profile;
    private TextView selfieCount;
    public final static String EXTRA_MESSAGE = "gayankalhara.snapfie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();
        welcome = (TextView) findViewById(R.id.welcome);
        selfieCount = (TextView) findViewById(R.id.selfieCount);
        if(welcome != null)
        {
            welcome.setText(getString(R.string.hello_user) + " " + profile.getFirstName() + "!");
        }
        else
        {
            welcome.setText(getString(R.string.hello_user));
        }

        selfieCount.setText("You have taken " + countImgs(0) + " no of selfies so far...");


    }

    public int countImgs(int number) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Snapfie");
        File[] dirs = file.listFiles();
        String name = "";
        if (dirs != null) { // Sanity check
            for (File dir : dirs) {
                if (dir.isFile()) { // Check file or directory
                    name = dir.getName().toLowerCase();
                    // Add or delete extensions as needed
                    if (name.endsWith(".png") || name.endsWith(".jpg")
                            || name.endsWith(".jpeg")) {
                        number++;
                    }
                } else number = countImgs(number);
            }
        }

        return number;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final Intent intentLogin = new Intent(this, LoginActivity.class);

        switch (item.getItemId()) {
            case R.id.about_us:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("About Us")
                        .setMessage("This app is created for MAD Module\n" +
                                "\n" +
                                "App Developers:\n" +
                                "Gayan Kalhara (IT14062148)\n" +
                                "Udesh Hewagama (IT14048838)")
                        .setCancelable(false)
                        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), "Thanks for checking about us", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.action_logout:
                AccessToken.setCurrentAccessToken(null);
                startActivity(intentLogin);
                break;
            case R.id.exit:
                finish();
            default:
                //default intent
        }

        return super.onOptionsItemSelected(item);
    }

    public void takeSelfie(View view) {
        Intent camera = new Intent(this, Picture.class);
        String message = "camera";
        camera.putExtra(EXTRA_MESSAGE, message);
        startActivity(camera);
    }

    public void browseGallery(View view) {
        Intent gallery = new Intent(this, Picture.class);
        String message = "gallery";
        gallery.putExtra(EXTRA_MESSAGE, message);
        startActivity(gallery);
    }

    public void generateAnimatedGIF(View view) {
        Intent animatedGIF = new Intent(this, GIF_Generator.class);
        startActivity(animatedGIF);
    }

    public void generateVideo(View view) {

    }
}
