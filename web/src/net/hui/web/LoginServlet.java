package net.hui.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 * 测试登录Servlet
 *
 * @author Implementist
 */
public class LoginServlet extends HttpServlet {



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user= new User();



        // 设置响应内容类型
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        try (PrintWriter out = response.getWriter()) {

            //获得请求中传来的用户名和密码
            String accountNumber = request.getParameter("AccountNumber").trim();
            String password = request.getParameter("Password").trim();

            //密码验证结果
            Boolean verifyResult = verifyLogin(accountNumber, password);

            Map<String, String> params = new HashMap<>();
            JSONObject jsonObject = new JSONObject();

            if (verifyResult) {
                params.put("Result", "success");
               // detailed_info();
            } else {
                params.put("Result", "failed");
            }
            jsonObject.put("params", params);

            Map<String, Double> address= new HashMap<>();
            Map<String,Integer> base_info=new HashMap<>();
            Map<String,Double> base_add=new HashMap<>();

            Double lon=12.0;
            Double lat=12.1;
            Integer MCC=460;
            Integer MNC1=1;
            Integer LAC1=123;
            Integer CID1=2345;
            Integer BSSS1=100;
            Double lon1=1.0;
            Double lat1=2.0;

            //if (verifyResult) {
               /* address.put("longitude",lon );
                address.put("latitude",lat);
                base_info.put("MCC1",MCC);
                base_info.put("MNC1",MNC1);
                base_info.put("LAC1",LAC1);
                base_info.put("CID1",CID1);
                base_info.put("BSSS1",BSSS1);

                base_add.put("lon1",lon1);
                base_add.put("lat1",lat1);
                jsonObject.put("address",address);
                jsonObject.put("bss_info",base_info);
                jsonObject.put("bss_address",base_add);
*/
              // jsonObject.put("address",user.info);
           // }

            out.write(jsonObject.toString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private Boolean verifyLogin(String userName, String password) {

        User user = UserDAO.queryUser(userName);

        //账户密码验证
        return null != user && password.equals(user.getPassword());
    }
    private void detailed_info(){

    }
}

