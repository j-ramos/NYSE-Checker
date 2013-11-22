package feup.cmov.finance.connection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class Network {
    private String host;
    private DefaultHttpClient httpClient;

    public Network() {
        this.host = "http://192.168.1.67";
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);
        httpClient = new DefaultHttpClient(httpParameters);
    }


    public String get(String path, ArrayList<NameValuePair> params)
    {
        String res = new String();
        String paramString = URLEncodedUtils.format(params, "utf-8");
        HttpGet httpGet = new HttpGet(host+path+"?"+paramString);
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            res = EntityUtils.toString(response.getEntity());
        } catch (ConnectTimeoutException c)
        {
            c.printStackTrace();
        } catch (SocketTimeoutException s)
        {
            s.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
