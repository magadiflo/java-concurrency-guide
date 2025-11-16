package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableObjectInRecordDemo {

    record UserRecord(String username, Role role) {

    }

    static class Role {
        private String name;

        public Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Role{");
            sb.append("name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Role role = new Role("USER");
        UserRecord record = new UserRecord("Milagros", role);
        log.info("Antes: {}", record);

        role.setName("ADMIN");
        log.info("Después: {}", record);
    }
}
