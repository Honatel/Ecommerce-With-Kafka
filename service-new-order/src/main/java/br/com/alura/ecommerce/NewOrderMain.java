package  br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(var dispacher = new KafkaDispatcher<Order>()) {
            try(var dispacherEmail = new KafkaDispatcher<String>()){
                for (var i = 0; i < 10; i++)
                {
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    var email = Math.random() + "@email.com";

                    var order =  new Order(orderId, amount, email);
                    dispacher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailCode = "Thank you for your order";
                    dispacherEmail.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                }
            }
        }
    }
}
