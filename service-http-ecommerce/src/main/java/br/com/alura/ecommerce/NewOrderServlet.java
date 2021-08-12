package br.com.alura.ecommerce;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet  {
    private final KafkaDispatcher<Order> dispacher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String>  dispacherEmail = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        dispacher.close();
        dispacherEmail.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));
            var email = req.getParameter("email");

            var order =  new Order(orderId, amount, email);
            dispacher.send("ECOMMERCE_NEW_ORDER", email, order);

            var emailCode = "Thank you for your order";
            dispacherEmail.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

            System.out.println("New order sent successfully.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent successfully.");

        } catch (ExecutionException e) {
            throw  new ServletException(e);
        } catch (InterruptedException e) {
            throw  new ServletException(e);
        }
    }
}
