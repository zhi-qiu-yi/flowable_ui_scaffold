package me.akaada.flowable_ui_scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.flowable.ui.application"})
@SpringBootApplication
public class FlowableDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowableDemoApplication.class, args);
    }
}
