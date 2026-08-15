package com.assetflow.asset.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetBundleItemTest {

    @Test
    void shouldCreateValidAssetBundleItem() {
        UUID orgId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();

        AssetBundleItem item = AssetBundleItem.create(
                orgId,
                parentId,
                childId,
                AssetBundleItem.RelationshipType.ACCESSORY,
                2,
                "Includes extra power adapters"
        );

        assertThat(item.getId()).isNotNull();
        assertThat(item.getParentAssetId()).isEqualTo(parentId);
        assertThat(item.getChildAssetId()).isEqualTo(childId);
        assertThat(item.getRelationshipType()).isEqualTo(AssetBundleItem.RelationshipType.ACCESSORY);
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldRejectSelfBundling() {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();

        assertThatThrownBy(() -> AssetBundleItem.create(
                orgId,
                assetId,
                assetId,
                AssetBundleItem.RelationshipType.COMPONENT,
                1,
                "Invalid link"
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot be bundled with itself");
    }
}
