package net.hui.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.net.URLEncoder;
//import  org.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject ;
//import org.json.JSONArray;



/**
 * 测试登录Servlet
 *
 * @author Implementist
 */
public class LoginServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        // 设置响应内容类型
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        try (PrintWriter out = response.getWriter()) {

            //获得请求中传来的用户名和密码
            String accountNumber = request.getParameter("AccountNumber").trim();
            String password = request.getParameter("Password").trim();

            System.out.println("*******************************");
            //根据用户名等信息，把user搞过来
            User user = verifyLogin(accountNumber, password);
            //密码验证结果
            boolean verifyResult = (user.getPassword().equals(password));

            //System.out.printf("get:****** %s   ", user.getAdr_id());

            Map<String, String> params = new HashMap<>();

            JSONObject jsonObject=new JSONObject();

            //在返回json中表明登录状态
            if (verifyResult) {
                params.put("Result", "success");
               // params.put("id", user.getAdr_id());
                // detailed_info();
            } else {
                params.put("Result", "failed");
            }
           jsonObject.put("params", params);


            //connect to the gaode API, and get the data
            //String org="116.481028,39.989643";
            //String dest="116.434446,39.90816";
            //将得到的起点终点数据包装，发送请求给高德
            double org1 = user.info.get("lon1");
            double org2 = user.info.get("lat1");
            double dest1 = user.info.get("lon2");
            double dest2 = user.info.get("lat2");
            String org= String.format("%f,%f",org1,org2);
            String dest=String.format("%f,%f",dest1,dest2);
            System.out.printf("org is: %s \n",org);
            System.out.printf("dest is: %s \n",dest);

            String key = "b84753eb6ca9b6a6b81048da46aa6d9b";
            //System.out.println("print line 109" );
            String url = String.format
                    ("https://restapi.amap.com/v3/direction/driving?origin=%s&destination=%s&key=%s",
                            org, dest, key);
            URL obj = new URL(url);
            //System.out.println("print 114" );

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            //System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            //得到高德的返回数据，并加以处理
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            String res = "";
            while ((inputLine = in.readLine()) != null) {
                res=res+inputLine;
            }
            res=new String(res.getBytes("GBK"), "UTF-8");
            System.out.println(res);

            //process the received data，装入json_object
            JSONObject jso;
            jso=JSONObject.fromObject(res);

           // System.out.println(jso);
            String status = jso.getString("status");
            //System.out.println(status);

            //解析json数据
            JSONObject jso1 = jso.getJSONObject("route");
            JSONArray jso_array = jso1.getJSONArray("paths");
            JSONObject jso3 = jso_array.getJSONObject(0);
            JSONArray jso_step = jso3.getJSONArray("steps");
            JSONObject tmp;


            //将point数据包装在point-array中
            ArrayList<String[]> adr_array = new ArrayList<>();
            JSONArray point_array= new JSONArray();

            //处理第一个
            adr_array.add(org.split(";"));

            for (int i = 0; i < jso_step.size(); i++) {

                tmp = jso_step.getJSONObject(i);
                String long_line = tmp.getString("polyline");
                //System.out.printf("The step %d is : %s \n",i,long_line);
                adr_array.add(long_line.split(";"));
            }

            //处理终点：
            adr_array.add(dest.split(";"));

            int i=0;
            for (String[] s : adr_array) {
                for (String point_str: s) {
                    //解析得到每一个polyline的经纬度
                    String longitude=point_str.split(",")[0];
                    String latitude=point_str.split(",")[1];
                    JSONObject point_array_tmp= new JSONObject();
                    double lon,lat;
                    lon= Double.parseDouble(longitude);
                    lat=Double.parseDouble(latitude);
                    //得到每一个点的基站信息
                    Map<String,String> bs= getbs(user,lon,lat);

                    point_array_tmp.put("MCC",bs.get("MCC"));
                    point_array_tmp.put("LAC",bs.get("LAC"));
                    point_array_tmp.put("CID",bs.get("CID"));
                    point_array_tmp.put("MNC",bs.get("MNC"));
                    point_array_tmp.put("BSSS","100");
                    point_array_tmp.put("longitude",longitude);
                    point_array_tmp.put("latitude",latitude);
                    point_array.element(i,point_array_tmp);
                    i++;
                }
            }


            jsonObject.put("points",point_array);


            String result= String.format("%s",jsonObject.toString());
            System.out.println(result);
            out.write(result);

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private  Map<String,String> getbs(User user,double lon, double lat){
        Map<String,String> bs =new HashMap<>();
        //某两点之间的距离正比于sin^2(a/2)+cos(lat1)*cos(lat2)*sin^2(b/2)
        double min=1e10;

        for (Map<String,String> mp: user.bs)
        {
                double bs_lon=Double.parseDouble(mp.get("lon"));
                double bs_lat=Double.parseDouble(mp.get("lat"));
                //double b= (lon-bs_lon)/180.0 * Math.PI;
                //double a= lat-bs_lat/180.0 *Math.PI;
                double mlata=(90-lat)/180*Math.PI;
                double mlatb=(90-bs_lat)/180*Math.PI;
                double mlona=lon/180*Math.PI;
                double mlonb=bs_lon/180*Math.PI;
                double tmp;

                //tmp=Math.sin(mlata)*Math.sin(mlatb)*Math.cos(mlona-mlonb)+Math.cos(mlata)*Math.cos(mlatb);

                tmp=(bs_lat-lat)*(bs_lat-lat)+(lon-bs_lon)*(lon-bs_lon);
                if (tmp<min)
                {min=tmp;
                bs=mp;}
                //System.out.println(bs.get("lon")+','+bs.get("lat")+':'+tmp);
        }
        System.out.println(bs.get("lon")+','+bs.get("lat")+':'+min);
        return bs;
    }



    private User verifyLogin(String userName, String password) {

        return UserDAO.queryUser(userName);
        //账户密码验证
        //return null != user && password.equals(user.getPassword());
    }

}

