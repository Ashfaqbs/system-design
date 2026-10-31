package  com.example.order_service;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final WebClient webClient;

    public OrderController() {
        this.webClient = WebClient.create("http://localhost:5000"); // Sidecar URL
    }

    @PostMapping
    public Mono<String> placeOrder(@RequestBody Order order) {
        System.out.println("Order received: " + order.getDrink() + ", " + order.getSize() + ", " + order.getExtra());
        
        // Notify the sidecar (Analytics Service)
        return webClient.post()
                .uri("/track")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> "Order placed successfully: " + order.getDrink());
    }
}