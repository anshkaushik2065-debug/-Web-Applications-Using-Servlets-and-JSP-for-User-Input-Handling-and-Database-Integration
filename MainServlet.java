import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class MainServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if (action == null) action = "home";

        out.println("<html><body style='font-family:Arial;'>");
        out.println("<h2>Web Application Demo (Servlet + JDBC + JSP)</h2>");
        out.println("<hr>");

        try {
            if (action.equals("home")) {
                showHome(out);
            } 
            else if (action.equals("loginForm")) {
                showLoginForm(out);
            } 
            else if (action.equals("employee")) {
                showEmployeeForm(out);
            } 
            else if (action.equals("attendance")) {
                showAttendanceForm(out);
            } 
            else {
                out.println("<p>Unknown action.</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        out.println("<html><body style='font-family:Arial;'>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/webappdb", "root", "password");

            if (action.equals("login")) {
                String uname = request.getParameter("username");
                String pass = request.getParameter("password");

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=?");
                ps.setString(1, uname);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next())
                    out.println("<h3>✅ Welcome, " + uname + "!</h3>");
                else
                    out.println("<h3 style='color:red;'>Invalid credentials!</h3>");
            }

            else if (action.equals("searchEmp")) {
                String empid = request.getParameter("empid");
                PreparedStatement ps;
                if (empid != null && !empid.isEmpty()) {
                    ps = con.prepareStatement("SELECT * FROM Employee WHERE EmpID=?");
                    ps.setInt(1, Integer.parseInt(empid));
                } else {
                    ps = con.prepareStatement("SELECT * FROM Employee");
                }
                ResultSet rs = ps.executeQuery();

                out.println("<h3>Employee Records</h3>");
                out.println("<table border='1' cellpadding='5'><tr><th>ID</th><th>Name</th><th>Salary</th></tr>");
                while (rs.next()) {
                    out.println("<tr><td>" + rs.getInt("EmpID") + "</td><td>" +
                                rs.getString("Name") + "</td><td>" +
                                rs.getDouble("Salary") + "</td></tr>");
                }
                out.println("</table>");
            }

            else if (action.equals("markAttendance")) {
                String sid = request.getParameter("studentId");
                String date = request.getParameter("date");
                String status = request.getParameter("status");

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Attendance VALUES (?, ?, ?)");
                ps.setInt(1, Integer.parseInt(sid));
                ps.setString(2, date);
                ps.setString(3, status);
                ps.executeUpdate();

                out.println("<h3>✅ Attendance marked successfully!</h3>");
            }

            con.close();
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }

    private void showHome(PrintWriter out) {
        out.println("<h3>Select a Feature:</h3>");
        out.println("<ul>");
        out.println("<li><a href='?action=loginForm'>User Login</a></li>");
        out.println("<li><a href='?action=employee'>Employee Records</a></li>");
        out.println("<li><a href='?action=attendance'>Student Attendance</a></li>");
        out.println("</ul>");
    }

    private void showLoginForm(PrintWriter out) {
        out.println("<h3>Login</h3>");
        out.println("<form method='post' action='MainServlet'>");
        out.println("<input type='hidden' name='action' value='login'>");
        out.println("Username: <input type='text' name='username'><br><br>");
        out.println("Password: <input type='password' name='password'><br><br>");
        out.println("<input type='submit' value='Login'>");
        out.println("</form>");
    }

    private void showEmployeeForm(PrintWriter out) {
        out.println("<h3>Search Employee</h3>");
        out.println("<form method='post' action='MainServlet'>");
        out.println("<input type='hidden' name='action' value='searchEmp'>");
        out.println("Employee ID (leave blank for all): <input type='text' name='empid'><br><br>");
        out.println("<input type='submit' value='Search'>");
        out.println("</form>");
    }

    private void showAttendanceForm(PrintWriter out) {
        out.println("<h3>Student Attendance</h3>");
        out.println("<form method='post' action='MainServlet'>");
        out.println("<input type='hidden' name='action' value='markAttendance'>");
        out.println("Student ID: <input type='text' name='studentId'><br><br>");
        out.println("Date: <input type='date' name='date'><br><br>");
        out.println("Status: <select name='status'><option>Present</option><option>Absent</option></select><br><br>");
        out.println("<input type='submit' value='Submit Attendance'>");
        out.println("</form>");
    }
}
