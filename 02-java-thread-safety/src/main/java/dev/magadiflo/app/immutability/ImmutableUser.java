package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase inmutable que protege su lista interna.
 */
@Slf4j
public final class ImmutableUser {
    private final String name;
    private final List<String> roles;

    public ImmutableUser(String name, List<String> roles) {
        this.name = name;
        this.roles = List.copyOf(roles); // copia defensiva e inmodificable
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles; // seguro, es inmutable
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmutableUser{");
        sb.append("name='").append(name).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        ImmutableUser user = new ImmutableUser("Milagros", externalRoles);
        log.info("Objeto inicial: {}", user);

        externalRoles.add("ADMIN"); // cambiamos la lista original
        log.info("Objeto final: {}", user); // La lista interna NO se ve afectada
    }
}
