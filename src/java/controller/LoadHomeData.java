package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        responseJson.addProperty("message", "Unable To Process Your Request");

        try {
            
            Session session = HibernateUtil.getSessionFactory().openSession();

            String userId = request.getParameter("id");

            User user = (User) session.get(User.class, Integer.parseInt(userId));


            User_Status user_Status = (User_Status) session.get(User_Status.class, 1);


            user.setUser_Status(user_Status);
            session.update(user);


            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.ne("id", user.getId()));
            List<User> otherUserList = criteria1.list();


            JsonArray jsonChatArray = new JsonArray();


            for (User otherUser : otherUserList) {


                Criteria criteria2 = session.createCriteria(Chat.class);
                criteria2.add(
                        Restrictions.or(
                                Restrictions.and(
                                        Restrictions.eq("from_user", user),
                                        Restrictions.eq("to_user", otherUser)
                                ),
                                Restrictions.and(
                                        Restrictions.eq("from_user", otherUser),
                                        Restrictions.eq("to_user", user)
                                )
                        )
                );
                
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);


                JsonObject jsonChatItem = new JsonObject();
                jsonChatItem.addProperty("other_user_id", otherUser.getId());
                jsonChatItem.addProperty("other_user_mobile", otherUser.getMobile());
                jsonChatItem.addProperty("other_user_name", otherUser.getFirst_name() + " " + otherUser.getLast_name());
                jsonChatItem.addProperty("other_user_status", otherUser.getUser_Status().getId()); //1=online , 2 = offline


                String serverPath = request.getServletContext().getRealPath("");
                String otheruserAvatarImagePath = serverPath + File.separator + "AvatarImages" + File.separator + otherUser.getMobile() + ".jpg";
                File otheruserAvatarImageFile = new File(otheruserAvatarImagePath);

                if (otheruserAvatarImageFile.exists()) {
                    jsonChatItem.addProperty("avatar_image_found", true);
                } else {
                    jsonChatItem.addProperty("avatar_image_found", false);
                    jsonChatItem.addProperty("other_user_avatar_letters", otherUser.getFirst_name().charAt(0) + "" + otherUser.getLast_name().charAt(0));
                }


                List<Chat> dbChatList = criteria2.list();
                SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy, MMM dd hh:mm a");

                if (dbChatList.isEmpty()) {
                    //no chat
                    jsonChatItem.addProperty("message", "Let's Start New Conversation");
                    jsonChatItem.addProperty("date_time", dataFormat.format(user.getRegistered_date_time()));
                    jsonChatItem.addProperty("chat_status", 1);
                } else {
                    //found last chat
                    jsonChatItem.addProperty("message", dbChatList.get(0).getMessage());
                    jsonChatItem.addProperty("date_time", dataFormat.format(dbChatList.get(0).getDate_time()));
                    jsonChatItem.addProperty("chat_status", dbChatList.get(0).getChat_status().getId()); //1 seen , 2 = unsen
                }
                
                jsonChatArray.add(jsonChatItem);

            }


            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Success");

            responseJson.add("jsonChatArray",gson.toJsonTree(jsonChatArray));

            session.beginTransaction().commit();
            session.close();

        } catch (Exception e) {
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
