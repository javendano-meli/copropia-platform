package com.copropia.auth.domain.port.in;

import com.copropia.auth.domain.model.Usuario;

public interface AuthUseCase {
    String login(String email, String password);
    Usuario register(Usuario usuario, String rawPassword);
}
