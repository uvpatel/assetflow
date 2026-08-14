package com.assetflow.shared.persistence;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    static class TestEntity extends BaseEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    void shouldGetAndSetBaseEntityProperties() {
        TestEntity entity = new TestEntity();
        entity.setName("Test");
        entity.setVersion(1L);
        entity.setCreatedBy("user-1");
        entity.setUpdatedBy("user-2");

        assertThat(entity.getName()).isEqualTo("Test");
        assertThat(entity.getVersion()).isEqualTo(1L);
        assertThat(entity.getCreatedBy()).isEqualTo("user-1");
        assertThat(entity.getUpdatedBy()).isEqualTo("user-2");
    }
}
