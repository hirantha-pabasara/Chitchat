package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "UpdateUser", urlPatterns = {"/UpdateUser"})
public class UpdateUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        String mobile = request.getParameter("mobile");
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        Part avatarImage = request.getPart("avatarImage");

        Session session = HibernateUtil.getSessionFactory().openSession();

// Use Criteria or HQL query to get User by mobile
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("mobile", mobile));
        User user = (User) criteria.uniqueResult();

        if (user != null) {
            user.setFirst_name(firstName);
            user.setLast_name(lastName);

            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();

            if (avatarImage != null && avatarImage.getSize() > 0) {
                String serverPath = request.getServletContext().getRealPath("");
                String avatarImagePath = serverPath + File.separator + "AvatarImages" + File.separator + mobile + ".jpg";
                File file = new File(avatarImagePath);
                Files.copy(avatarImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Profile updated successfully");
        } else {
            responseJson.addProperty("message", "User not found");
        }

        session.close();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));

    }

}
