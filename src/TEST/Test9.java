package TEST;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Test9 extends HttpServlet {

    Test9() {
        System.out.println("ShoeServlet2:构造函数");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            System.out.println("ShoeServlet2:processRequest");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("ShoeServlet2:doGet");
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("ShoeServlet2:doPost");
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        System.out.println("ShoeServlet2:getServletInfo");
        return "Short description";
    }// </editor-fold>
}