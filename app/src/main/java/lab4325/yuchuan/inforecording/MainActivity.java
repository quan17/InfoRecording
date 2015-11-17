package lab4325.yuchuan.inforecording;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textViewLat = null;
    TextView textViewLog = null;
    TextView textViewSpeed = null;
    Button switchButton = null;
    Boolean switchFlag = false;

    public float speed;
    public double lat;
    public double lng;

    File sdCardDir;
    File saveFile;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        textViewLat = (TextView) findViewById(R.id.textViewLat);
        textViewLog = (TextView) findViewById(R.id.textViewLong);
        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        switchButton = (Button) findViewById(R.id.buttonSwitch);

        sdCardDir = Environment.getExternalStorageDirectory();
        saveFile = new File(sdCardDir, "testing.txt");

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchFlag)
                    switchButton.setText("Stop");
                else
                    switchButton.setText("Resume");
                switchFlag = !switchFlag;
                System.out.println("work?"+switchFlag);

            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GetProvider();
        OpenGPS();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
//		UpdateWithNewLocation(location);
        locationManager.requestLocationUpdates(provider, 1000, (float) 0.05, locationListener);
    }


    @Override
    public void onStart(){
        super.onStart();

    }

    public void writeFile() throws IOException {
//        File sdCardDir = Environment.getExternalStorageDirectory();
//        File saveFile = new File(sdCardDir, filename);
        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }
        System.out.println(saveFile.getName());
        FileOutputStream phone_outStream =this.openFileOutput(saveFile.getName(), Context.MODE_WORLD_READABLE);
        String words = lat+","+lng+","+speed+"\r\n";
        phone_outStream.write(words.getBytes());
        phone_outStream.close();
    }

/*------------------ GPS module-----------------------  */

    public LocationManager locationManager;
    public String provider;
    public Location location;

    private void OpenGPS() {

        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "GPS already Set", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "GPS not set yet", Toast.LENGTH_SHORT).show();
//       	Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        	startActivityForResult(intent,0);
    }

    public void GetProvider(){

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        provider = locationManager.GPS_PROVIDER;
        provider = locationManager.getBestProvider(criteria,true);
    }


    public final LocationListener locationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated metfloathod stub
			UpdateWithNewLocation(location);
            speed=location.getSpeed();
            textViewSpeed.setText(""+speed*2.2369);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            UpdateWithNewLocation(null);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    };

    public void UpdateWithNewLocation(Location location) {
        String latLongString;
//        TextView myLocationText= (TextView)findViewById(R.id.BMWSpeedTitle);
        if (location != null) {
            lat =location.getLatitude();
            lng =location.getLongitude();
            textViewLat.setText(""+lat);
            textViewLog.setText(""+lng);
            latLongString = "Latitude:" + lat + "/nLongitude:" + lng;
            while(!switchFlag)
                try {
                    writeFile();
                }
                catch (Exception e) {
                    System.out.println("wrong");
                    e.printStackTrace();
                }
        }
        else {
            latLongString = "None GPS Info";
        }
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
