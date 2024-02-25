package monitoring.service.dev.controllers.v2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerUiController {

    @GetMapping("/api-docs")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
