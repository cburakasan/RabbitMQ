package com.example.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class PublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublisherApplication.class, args);

        publisher();
    }

    public static void publisher() {
        try {

            System.out.println("Publisher");

            ConnectionFactory connectionFactory = new ConnectionFactory();
            //Buraya rabbitmq cloud ortamının bilgilerini insert ediyouz
            connectionFactory.setUri("amqps://szhuwwgj:Zd7cIsBg4mRMOyJUVBKQ1Ny-xxZ0Pj9z@jackal.rmq.cloudamqp.com/szhuwwgj");

            Connection connection = connectionFactory.newConnection();
            //açılan connection içinde channel oluşturulur
            Channel channel = connection.createChannel();

            // bu metod, mesaj doğrulama (message acknowledgement) modunu etkinleştirir.
            // Bu mod sayesinde, gönderilen bir mesajın RabbitMQ tarafından alındığı ve kuyruğa başarıyla eklendiği bilgisi, mesajın göndericisine geri bildirilir.
            channel.confirmSelect();
            /*
        1-Exchange Adı ("my-fanout-exchange"): Bu, oluşturulacak exchange'in adıdır. Bu örnekte exchange adı "my-fanout-exchange" olarak belirlenmiştir.

        2-Exchange Tipi (ExchangeTypes.FANOUT): Exchange'in tipini belirler.
        FANOUT, gönderilen mesajların bağlı olan tüm kuyruklara kopyasının dağıtılmasını sağlar. Diğer yaygın tipler arasında direct, topic, ve headers bulunur.

        3-Durability (true): Bu boolean değer, exchange'in dayanıklı (durable) olup olmadığını belirtir.
        Eğer true ise, RabbitMQ sunucusu yeniden başlatıldığında bile exchange korunur. false ise, sunucu yeniden başlatıldığında exchange silinir.

        4-Auto-delete (false): Bu boolean değer, exchange'in otomatik olarak silinip silinmeyeceğini belirler.
         Eğer true ise, exchange'e bağlı olan son kuyruk silindiğinde exchange de otomatik olarak silinir. false ise, exchange manuel olarak silinene kadar kalır.

        5-Arguments (null): Bu, exchange ile ilişkilendirilebilecek ekstra argümanların bir map'ini sağlar.
         Örneğin, bazı gelişmiş routing davranışlarını ayarlamak için kullanılabilir. null olduğunda, ekstra bir argüman belirtilmemiş demektir.
             */

            channel.exchangeDeclare("my-fanout-exchange", ExchangeTypes.FANOUT, true, false, null);

            String message = "Bu benim ilk rabbitMq publish denemem";

            //RabitMq çalışırken eventin byte olarak gelmesini ister ancak serilize ve deserilize işlemlerini üstlenmez.
            // Bu işlemlerin publisher ve consumier tarafında yapılmasını bekler.
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            channel.basicPublish("my-fanout-exchange", "", true, null, messageBytes);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        System.out.println("Mesaj gonderildi");
    }
}
