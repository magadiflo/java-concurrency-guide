package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que *parece* inmutable, pero no lo es.
 */

@Slf4j
public final class UnsafeUser {

    private final String name;
    private final List<String> roles; // referencia directa a lista externa

    public UnsafeUser(String name, List<String> roles) {
        this.name = name;
        this.roles = roles; // peligro: guardamos la referencia original
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles; // se devuelve tal cual, sin protección
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnsafeUser{");
        sb.append("name='").append(name).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        UnsafeUser user = new UnsafeUser("Milagros", externalRoles);
        log.info("Objeto inicial: {}", user);

        // La clase *parece* inmutable… pero como no usamos el List.copyOf(...),
        // podemos modificar la lista desde afuera. Modificando roles....
        externalRoles.add("ADMIN"); // modificamos la lista original

        // La clase "inmutable" no lo era en realidad
        log.info("Objeto final: {}", user);
    }
}
