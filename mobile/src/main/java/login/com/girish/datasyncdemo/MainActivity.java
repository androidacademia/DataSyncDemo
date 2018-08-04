package login.com.girish.datasyncdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private EditText editTextSend;
    private Button btnSend;
    private GoogleApiClient mGoogleApiClient;
    private ImageView imageView;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String loc = location.getLatitude() +","+location.getLongitude();
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/location");
            putDataMapRequest.getDataMap().putString("message",loc);
            //putDataMapRequest.getDataMap().putAsset("image", creteAssetFromCurrentImage());
            putDataMapRequest.setUrgent();
            PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextSend = findViewById(R.id.editTextSend);
        btnSend = findViewById(R.id.buttonSend);
        imageView = findViewById(R.id.imageView);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String send = editTextSend.getText().toString();
                ////////////Component to sync this string///////////////
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/send");
                putDataMapRequest.getDataMap().putString("message",send);
                putDataMapRequest.getDataMap().putAsset("image", creteAssetFromCurrentImage());
                putDataMapRequest.setUrgent();
                PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest);

                ///////////////////////////////////////////////////////
                ////////////Syncing Location//////////////////////////
                LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);

            }
        });
    }

    private Asset creteAssetFromCurrentImage() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap mImage  = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        mImage.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return Asset.createFromBytes(byteArrayOutputStream.toByteArray());
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
