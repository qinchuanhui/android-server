package net.hui.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
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
        PreparedStatement preparedStatement1= null;
        ResultSet resultSet = null;
        ResultSet resultSet1=null;

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
                user.setUserName(resultSet.getString("UserName"));
                user.setPassword(resultSet.getString("Password"));
                user.setAdr_id(resultSet.getString("adr_id"));

               /* sqlStatement1.append("SELECT * FROM address WHERE adr_id=?");
                preparedStatement1= connection.prepareStatement(sqlStatement1.toString());
                preparedStatement1.setString(1, user.getAdr_id());
                resultSet1 = preparedStatement1.executeQuery();
                if (resultSet1.next()){
                    user.info.put("lon1",resultSet1.getDouble("lon1"));
                    user.info.put("lon2",resultSet1.getDouble("lon2"));
                    user.info.put("lat1",resultSet1.getDouble("lat1"));
                    user.info.put("lat2",resultSet1.getDouble("lat2"));
                }*/
                return user;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        finally {
            DBManager.closeAll(connection, preparedStatement, resultSet);
        }
    }

    private void getline(User user ){


    }

}

