package kr.co.company.ProjectA;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

public class GPSInfo extends Service implements LocationListener {

    private final Context mContext;
    boolean isGPSEnabled = false; // 현재 GPS 사용유무
    boolean isNetworkEnabled = false; // 네트워크 사용유무
    boolean isGetLocation = false; // GPS 상태값
    Location location;
    double lat; // 위도
    double lon; // 경도
    private static final long MIN_DISTANCE_UPDATES = 10; // GPS 정보 업데이트 거리 10미터
    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1; // GPS 정보 업데이트 시간 1/1000
    protected LocationManager locationManager;
    String AdminArea = null;
    String Locality = null;

    public GPSInfo(Context context) {
        this.mContext = context;
        //getLocation();
    }

    public Location getLocation(){
        try {
            Log.e("order", "7");
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && ! isNetworkEnabled) {
                Log.e("order", "8");
                this.isGetLocation = false;
            } else
            { //네트워크 정보로부터 위치값 가져오기
                Log.e("order", "9");
                this.isGetLocation = true;
                if(isNetworkEnabled){
                    Log.e("order", "10");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_DISTANCE_UPDATES, MIN_TIME_UPDATES, this);
                    if(locationManager != null){
                        Log.e("order", "15");
                        while (location == null) {
                            /* 시간이 오래 걸림 */
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Log.e("order", "16");
                        }
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
                if (isGPSEnabled) {
                    Log.e("order", "13");
                    if(location == null) { //GPS정보로 위치값 가져오기
                        Log.e("order", "14");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_UPDATES, MIN_TIME_UPDATES, this);
                        if(locationManager != null){
                            Log.e("order", "15");
                            while (location == null) {
                                /* 시간이 오래 걸림 */
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                Log.e("order", "16");
                            }
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

            }
        }catch(SecurityException e){
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS() { // GPS 종료
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(GPSInfo.this);
            } catch (SecurityException e) {
                Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
            }
        }
    }

    public double getLatitude() { // 위도값
        if (location != null) {
            Log.e("order", "17");
            lat = location.getLatitude();
        }
        return lat;
    }

    public double getLongitude() { // 경도값
        if (location != null) {
            Log.e("order", "18");
            lon = location.getLongitude();
        }
        return lon;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    public void showSettingsAlert() { //  GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창
        Log.e("order", "19");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                mContext);

        alertDialog.setTitle("GPS 기능 활성화");
        alertDialog
                .setMessage("GPS 기능이 활성화되지 않았습니다.\n 설정창으로 가시겠습니까?");
        alertDialog.setPositiveButton("설정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        Log.e("order", "20");
                    }
                });
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                        Toast.makeText(mContext, "위치 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.show();
    }

    public void getAddress(double lat, double lng, Geocoder geocoder) { // 내 위치 주소값 얻어오기
        List<Address> list = null;
        if (Geocoder.isPresent()) {
            try {
                list = geocoder.getFromLocation(lat, lng, 1);
            }
            catch (NullPointerException ne) {
                ne.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(list == null){
                Log.e("getAddress", "주소 데이터 얻기 실패");
                return;
            }
            if(list != null && list.size() > 0){
                Address addr = list.get(0);
                Log.e("12345", String.valueOf(Build.VERSION.SDK_INT));
                if (addr.getAdminArea() != null)
                    Log.e("12345", addr.getAdminArea());
                else
                    Log.e("12345", "null");
                if (addr.getSubAdminArea() != null)
                    Log.e("12345", addr.getSubAdminArea());
                else
                    Log.e("12345", "null");
                if (addr.getLocality() != null)
                    Log.e("12345", addr.getLocality());
                else
                    Log.e("12345", "null");
                if (addr.getSubLocality() != null)
                    Log.e("12345", addr.getSubLocality());
                else
                    Log.e("12345", "null");
                if (addr.getThoroughfare() != null)
                    Log.e("12345", addr.getThoroughfare());
                else
                    Log.e("12345", "null");
                if (addr.getSubThoroughfare() != null)
                    Log.e("12345", addr.getSubThoroughfare());
                else
                    Log.e("12345", "null");

                /* API 23 이상 */
                if (Build.VERSION.SDK_INT >= 23) {
                    AdminArea = addr.getLocality(); // 시도
                    Locality = addr.getSubLocality(); // 시군구
                }
                /* API 23 미만 */
                else {
                    AdminArea = addr.getAdminArea(); // 시도
                    Locality = addr.getLocality(); // 시군구
                }
            }
        }
        return;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // TODO Auto-generated method stub
    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
}
