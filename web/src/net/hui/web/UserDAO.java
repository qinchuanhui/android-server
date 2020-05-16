package net.hui.web;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Administrator
 */
public class UserDAO {
    /**
     * 查询给定用户名的用户的详细信息
     *
     * @param userName 给定的用户名
     * @return 查询到的封装了详细信息的User对象
     */
    public static User queryUser(String userName) {
        //获得数据库的连接对象
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        //PreparedStatement preparedStatement1;
        ResultSet resultSet = null;
        //ResultSet resultSet1;

        //生成SQL代码
        StringBuilder sqlStatement = new StringBuilder();
        StringBuilder sqlStatement1 = new StringBuilder();
        sqlStatement.append("SELECT * FROM user WHERE UserName=?");

        //设置数据库的字段值
        try {
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            preparedStatement.setString(1, userName);

            resultSet = preparedStatement.executeQuery();
            User user = new User();

            if (resultSet.next()) {
                //检测登录，并根据用户名分配地址序号
                user.setUserName(resultSet.getString("UserName"));
                user.setPassword(resultSet.getString("Password"));
                user.setAdr_id(resultSet.getString("adr_id"));

                //选取地址序号对应的起始地址
                Statement statement = connection.createStatement();
                sqlStatement1.append("SELECT * FROM address WHERE adr_id=");
                sqlStatement1.append('\'');
                sqlStatement1.append(user.getAdr_id());
                sqlStatement1.append('\'');
                String sql = "" + sqlStatement1;
                //System.out.println(sqlStatement1);

                //将起始地址放入info map
                resultSet = statement.executeQuery(sql);
                double lon1, lon2, lat1, lat2;
                //System.out.println(resultSet);
                if (!resultSet.next()) {
                    System.out.println("58-print");
                    return null;
                } else {
                    System.out.println(resultSet.getDouble("adr_id"));
                    System.out.println(resultSet.getDouble("lon1"));
                    lon1 = resultSet.getDouble("lon1");
                    lon2 = resultSet.getDouble("lon2");
                    lat1 = resultSet.getDouble("lat1");
                    lat2 = resultSet.getDouble("lat2");
                    user.info.put("lon1", lon1);
                    user.info.put("lon2", lon2);
                    user.info.put("lat1", lat1);
                    user.info.put("lat2", lat2);
                    System.out.println(user.info);
                }

                //get the address range of the base station.
                double lon_down = Math.min(lon1, lon2);
                double lon_up = Math.max(lon1, lon2);
                double lat_down = Math.min(lat1, lat2);
                double lat_up = Math.max(lat1, lat2);
                lat_down = lat_down - 0.01;
                lat_up = lat_up + 0.01;
                lon_down = lon_down - 0.01;
                lon_up = lon_up + 0.01;

                Statement state = connection.createStatement();

                String bs_state =
                        String.format("SELECT * FROM bs WHERE lat >= %f AND lat <= %f AND lon >= %f " +
                                "AND lon <= %f", lat_down, lat_up, lon_down, lon_up);
                System.out.println(bs_state);

                resultSet = state.executeQuery(bs_state);
                if (!resultSet.next()) {
                    System.out.println("94-print");
                    return null;
                } else {
                    String MCC,LAC,MNC,CID,lon,lat;

                    do {
                        MCC=resultSet.getString("MCC");
                        MNC=resultSet.getString("MNC");
                        LAC=resultSet.getString("LAC");
                        CID=resultSet.getString("CID");
                        lon=resultSet.getString("lon");
                        lat=resultSet.getString("lat");
                        //System.out.println(resultSet.getString("lat")+","+resultSet.getString("lon"));
                        Map<String,String> tmp=new HashMap<>();
                        tmp.put("MCC",MCC);
                        tmp.put("MNC",MNC);
                        tmp.put("LAC",LAC);
                        tmp.put("CID",CID);
                        tmp.put("lon",lon);
                        tmp.put("lat",lat);
                        user.bs.add(tmp);

                    } while (resultSet.next());

                    System.out.println(user.bs);
                }


                return user;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("fuck");
            return null;
        } finally {
            DBManager.closeAll(connection, preparedStatement, resultSet);
        }
    }


}

