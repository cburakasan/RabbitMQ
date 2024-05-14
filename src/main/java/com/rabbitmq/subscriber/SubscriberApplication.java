package com.rabbitmq.subscriber;

import com.rabbitmq.client.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SubscriberApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriberApplication.class, args);
        subscriber();
    }

    private static void subscriber() {
        try {
            System.out.println("Mesajlar Dinleniyor");
            ConnectionFactory connectionFactory = new ConnectionFactory();

            //Buraya rabbitmq cloud ortamının bilgilerini insert ediyouz
            connectionFactory.setUri("amqps://szhuwwgj:Zd7cIsBg4mRMOyJUVBKQ1Ny-xxZ0Pj9z@jackal.rmq.cloudamqp.com/szhuwwgj");

            Connection connection = connectionFactory.newConnection();
            //açılan connection içinde channel oluşturulur
            Channel channel = connection.createChannel();

            /*
            1-queue (String): "my-queue"
                Bu, kuyruk için verilen isimdir.
                Kuyruklar bu isimle tanımlanır ve başka bir yerden bu ismi kullanarak erişilebilir.

            2-durable (boolean): true
                Kuyruğun kalıcı olup olmadığını belirler. true olarak ayarlandığında,
                RabbitMQ yeniden başlatıldığında bu kuyruk kaybolmaz ve kalıcı olarak saklanır.
                Eğer false olsaydı, RabbitMQ yeniden başlatıldığında bu kuyruk silinirdi.

            3-exclusive (boolean): true
                Kuyruğun yalnızca bu bağlantı tarafından kullanılıp kullanılamayacağını belirtir. true olarak ayarlandığında, kuyruk yalnızca bu bağlantı tarafından kullanılır ve bu bağlantı kapandığında kuyruk silinir. false olursa, kuyruk diğer bağlantılar tarafından da kullanılabilir.

            4-autoDelete (boolean): false
                Kuyruğun otomatik olarak silinip silinmeyeceğini belirler.
                true olarak ayarlandığında, bu kuyruk onu kullanan son tüketici (consumer) kapandığında otomatik olarak silinir.
                false olursa, kuyruk elle silinene kadar veya RabbitMQ yeniden başlatılana kadar kalır.

            5-arguments (Map<String, Object>): null
                Kuyruk için ek argümanlar sağlayan bir harita (map) nesnesidir.
                Bu genellikle kuyruk TTL (Time-To-Live), mesaj önceliği, ve benzeri özel özellikler için kullanılır.
                 Burada null olarak ayarlandığı için, ek bir argüman sağlanmamıştır.
             */

            channel.queueDeclare("my-queue", true, true, false, null);


            /*
            1-queue (String): "my-queue"
                Bu, bağlanacak olan kuyruğun adıdır.
                Önceden oluşturulmuş olan kuyruk ismi burada belirtilir.

            2-exchange (String): "my-fanout-exchange"
                Bu, kuyruk ile bağlanacak olan exchange'in adıdır.
                Mesajların hangi exchange üzerinden kuyruğa yönlendirileceğini belirtir.

            3-routingKey (String): ""
                Bu, exchange ile kuyruk arasındaki yönlendirme anahtarıdır (routing key).
                Fanout exchange türünde, routing key genellikle kullanılmaz ve bu nedenle boş bir string olarak bırakılır.
                Direct veya topic exchange türlerinde ise routing key mesajların hangi kuyruklara yönlendirileceğini belirler.

            4-arguments (Map<String, Object>): null
                Bu, ek argümanlar sağlayan bir harita (map) nesnesidir.
                 Çoğu zaman kullanılmaz ve bu nedenle null olarak bırakılır.
                 Ancak bazı ileri düzey routing özellikleri veya politika ayarları yapmak için kullanılabilir.
             */
            channel.queueBind("my-queue", "my-fanout-exchange", "", null);

            /*
            DefaultConsumer sınıfı, RabbitMQ kütüphanesi tarafından sağlanan bir tüketici sınıfıdır. Mesajları almak ve işlemek için kullanılır.
            DefaultConsumer nesnesi, channel adlı RabbitMQ kanalı üzerinde oluşturulur. Bu kanal, RabbitMQ ile iletişim kurmak için kullanılır.
             */
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
                /*
            handleDelivery metodu, DefaultConsumer sınıfında bulunan ve bir mesaj teslim edildiğinde çağrılan bir metoddur.
            Bu metod, dört parametre alır:
            1-consumerTag (String):
                Tüketiciye atanmış benzersiz bir etiket. Bu, hangi tüketicinin mesajı aldığını belirlemek için kullanılır.
            2-envelope (Envelope):
                Mesajın teslim zarfı. Bu, exchange, routing key, mesajın teslim numarası gibi bilgileri içerir.
            3-properties (AMQP.BasicProperties):
                Mesajın özellikleri. Bu, başlıklar (headers), içerik türü (content type), teslim modu (delivery mode) gibi bilgileri içerir.
            4-body (byte[]):
                Mesajın gövdesi. Bu, mesajın içeriğini bayt dizisi (byte array) olarak içerir.
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received: " + message);
                    // Mesajı işleme mantığı buraya eklenebilir, mesaj alındığında yapılması gereken işlemleri belirteceğiniz yerdir.
                    // Örneğin, mesajı bir veritabanına kaydetme, başka bir servise gönderme veya işleme tabi tutma işlemleri burada yapılabilir.
                }
            };

            channel.basicConsume("my-queue", true, defaultConsumer);


        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException();
        }
    }

}
