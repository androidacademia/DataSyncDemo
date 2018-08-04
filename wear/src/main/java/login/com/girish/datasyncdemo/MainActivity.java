package login.com.girish.datasyncdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;

public class MainActivity extends WearableActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,DataApi.DataListener {

    private TextView mTextView;
    private ImageView imageView;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        imageView = (ImageView)findViewById(R.id.imageViewW);

        // Enables Always-on
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
        mGoogleApiClient.connect();
        setAmbientEnabled();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer){
            if (dataEvent.getType() ==  DataEvent.TYPE_CHANGED){
                //collect the dataitems
                DataItem item = dataEvent.getDataItem();
                if (item.getUri().getPath().equals("/send")){
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    String str = map.getString("message");
                    Asset asset = map.getAsset("image");
                    Bitmap bitmap = getBitmapFromAsset(asset);
                    if (bitmap !=null){
                        imageView.setImageBitmap(bitmap);
                    }
                    mTextView.setText(str);
                }
                /////////////sync location
                if (item.getUri().getPath().equals("/location")){
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    String str = map.getString("message");
                    mTextView.setText(str);
                }

            }
        }
    }



    public Bitmap getBitmapFromAsset(Asset asset){
        ConnectionResult result = mGoogleApiClient.blockingConnect();
        if ((!result.isSuccess())){
            return  null;
        }

        InputStream inputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient,asset)
                .await().getInputStream();
        mGoogleApiClient.disconnect();
        return BitmapFactory.decodeStream(inputStream);
    }
}
















