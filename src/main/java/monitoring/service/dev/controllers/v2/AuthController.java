package monitoring.service.dev.controllers.v2;

import monitoring.service.dev.controllers.v2.impl.ImplAuthController;
import monitoring.service.dev.services.logic.AuthLogicService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/users")
public class AuthController extends ImplAuthController {

    public AuthController(AuthLogicService service) {
        super(service);
    }
}
