package kr.co.company.ProjectA;

import android.location.Location;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GettingXML {
    private ArrayList<Pharmacy> pharmacyList;
    private String userAdminArea; // 도
    private String userLocality; // 구
    private String serviceKey;
    private String currentDay;
    private int currentTime;
    private String array; // 정렬 방법
    private double latitude;
    private double longitude;
    private int mode;
    private String startTime;
    private String endTime;

    public GettingXML(ArrayList<Pharmacy> pharmacyList, String userAdminArea, String userLocality, String serviceKey, String array, double latitude, double longitude, int mode) { // Mode 1 생성자
        this.pharmacyList = pharmacyList;
        this.userAdminArea = userAdminArea;
        this.userLocality = userLocality;
        this.serviceKey = serviceKey;
        this.array = array;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mode = mode;
    }

    public GettingXML(ArrayList<Pharmacy> pharmacyList, String userAdminArea, String userLocality, String serviceKey, String array, String currentDay, int currentTime, double latitude, double longitude, int mode) { // Mode 2 생성자
        this.pharmacyList = pharmacyList;
        this.userAdminArea = userAdminArea;
        this.userLocality = userLocality;
        this.serviceKey = serviceKey;
        this.array = array;
        this.currentDay = currentDay;
        this.currentTime = currentTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mode = mode;
    }

    ArrayList<Pharmacy> getXmlData() { // XML parser
        String pharmacy_name = "없음"; // 약국 이름
        String pharmacy_address = "없음"; // 약국 주소
        String pharmacy_tel = "없음"; // 약국 번호
        String pharmacy_time = "없음"; // 진료 시간
        String pharmacy_Info = ""; // 약국 정보
        String pharmacy_etc = ""; // 약국 특이사항
        String pharmacy_mapimg = ""; // 간이약도
        Double pharmacy_latitude = 0.0; // 약국 위도
        Double pharmacy_longitude = 0.0; // 약국 경도
        String tmp = null;
        float[] result = new float[1]; // Mode 1, 2 모두 거리를 구함
        pharmacyList.clear(); // clear 후 다시 add (팝업 후 재실행 때문)
        Log.e("GettingXML",userAdminArea);
        Log.e("GettingXML",userLocality);
        userAdminArea = URLEncoder.encode(userAdminArea); // mode 2 에서는 서울특별시
        userLocality = URLEncoder.encode(userLocality); // mode 2 에서는 setText로 받아옴(-구)
        String queryUrl = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire"
                + "?serviceKey=" + serviceKey // 서비스 키는 공통
                + "&Q0=" + userAdminArea
                + "&Q1=" + userLocality
                // + "&QT=1~6"
                + "&ORD=" + array
                + "&numOfRows=999";
        Log.e("123", queryUrl);
        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));  // inputstream 으로부터 xml 입력받기
            String tag;
            xpp.next();
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();    //테그 이름 얻어오기

                        if (tag.equals("item")); // 첫번째 검색결과
                        else if (tag.equals("dutyAddr")) {
                            xpp.next();
                            pharmacy_address = xpp.getText();
                        } else if (tag.equals("dutyEtc")) {
                            xpp.next();
                            pharmacy_etc = xpp.getText();
                        } else if (tag.equals("dutyInf")) {
                            xpp.next();
                            pharmacy_Info = xpp.getText();
                        } else if (tag.equals("dutyMapimg")) {
                            xpp.next();
                            pharmacy_mapimg = xpp.getText();
                        } else if (tag.equals("dutyName")) {
                            xpp.next();
                            pharmacy_name = xpp.getText();
                        } else if (tag.equals("dutyTel1")) {
                            xpp.next();
                            pharmacy_tel = xpp.getText();
                        } else if (tag.equals("dutyTime1c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime1s")) {
                            xpp.next();
                            pharmacy_time = "\n월요일 : " + xpp.getText() + " ~ " + tmp + "\n";
                        } else if (tag.equals("dutyTime2c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime2s")) {
                            xpp.next();
                            pharmacy_time += "화요일 : " + xpp.getText() + " ~ " + tmp + "\n";
                        } else if (tag.equals("dutyTime3c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime3s")) {
                            xpp.next();
                            pharmacy_time += "수요일 : " + xpp.getText() + " ~ " + tmp + "\n";
                        } else if (tag.equals("dutyTime4c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime4s")) {
                            xpp.next();
                            pharmacy_time += "목요일 : " + xpp.getText() + " ~ " + tmp + "\n";
                        } else if (tag.equals("dutyTime5c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime5s")) {
                            xpp.next();
                            pharmacy_time += "금요일 : " + xpp.getText() + " ~ " + tmp + "\n";
                        } else if (tag.equals("dutyTime6c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime6s")) {
                            xpp.next();
                            pharmacy_time += "토요일 : " + xpp.getText() + " ~ " + tmp;
                        } else if (tag.equals("dutyTime7c")) { // 일요일과 공휴일은 없을 수 있으니 있는 경우에만 처음에 개행문자
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime7s")) {
                            xpp.next();
                            pharmacy_time += "\n일요일 : " + xpp.getText() + " ~ " + tmp;
                        } else if (tag.equals("dutyTime8c")) {
                            xpp.next();
                            tmp = xpp.getText();
                        } else if (tag.equals("dutyTime8s")) {
                            xpp.next();
                            pharmacy_time += "\n공휴일 : " + xpp.getText() + " ~ " + tmp;
                        } else if (tag.equals("wgs84Lat")) {
                            xpp.next();
                            pharmacy_latitude = Double.parseDouble(xpp.getText());
                        } else if (tag.equals("wgs84Lon")) {
                            xpp.next();
                            pharmacy_longitude = Double.parseDouble(xpp.getText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();    // 태그 이름 얻어오기

                        if (tag.equals("item")) {
                            Location.distanceBetween(latitude, longitude, pharmacy_latitude, pharmacy_longitude, result);
                            int tmpInt = (int) result[0];
                            String distance = String.valueOf(tmpInt);
                            Pharmacy tmpPharmacy = new Pharmacy(pharmacy_name, pharmacy_address, pharmacy_tel, pharmacy_time, pharmacy_Info,
                                    pharmacy_etc, pharmacy_mapimg, pharmacy_latitude, pharmacy_longitude, distance);

                            /* 내 주변 (구) 약국 리스트 */
                            if (mode == 1) {
                                if (pharmacy_latitude != 0.0 && pharmacy_longitude != 0.0) {
                                    pharmacyList.add(tmpPharmacy);
                                }
                            }

                            /* 현재 운영 중인 약국 리스트 */
                            else if (mode == 2) {
                                String[] cutString = tmpPharmacy.data_pharmacy_time.split("\\n");
                                int i = 1;
                                boolean check = false;
                                for(;i<cutString.length;i++){
                                    if(cutString[i].contains(currentDay)){
                                        check = true;
                                        break;
                                    }
                                }
                                if (check) {
                                    startTime = cutString[i].substring(6, 10).replace(" ", "0"); // ex)0900
                                    endTime = cutString[i].substring(13, 17).replace(" ", "0"); // ex)1800
                                    if (Integer.parseInt(startTime) < currentTime && currentTime < Integer.parseInt(endTime)) { // ex)현재 시간이 0900 ~ 1800 사이면
                                        if (pharmacy_latitude != 0.0 && pharmacy_longitude != 0.0)
                                            pharmacyList.add(tmpPharmacy);
                                    }
                                }
                            }
                            pharmacy_name = "없음"; // 약국 이름
                            pharmacy_address = "없음"; // 약국 주소
                            pharmacy_tel = "없음"; // 약국 번호
                            pharmacy_time = "없음"; // 진료 시간
                            pharmacy_Info = ""; // 약국 정보
                            pharmacy_etc = ""; // 약국 특이사항
                            pharmacy_mapimg = ""; // 간이약도
                            pharmacy_latitude = 0.0; // 약국 위도
                            pharmacy_longitude = 0.0; // 약국 경도
                        }
                        break;
                }
                eventType = xpp.next();
            }
            return pharmacyList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
