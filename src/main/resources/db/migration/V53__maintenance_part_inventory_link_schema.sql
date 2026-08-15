ALTER TABLE maintenance_part_items
ADD COLUMN IF NOT EXISTS spare_part_id UUID,
ADD COLUMN IF NOT EXISTS inventory_deducted BOOLEAN DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_maint_part_items_spare ON maintenance_part_items(organization_id, spare_part_id);
