ALTER TABLE asset_categories
ADD COLUMN IF NOT EXISTS parent_category_id UUID REFERENCES asset_categories(id),
ADD COLUMN IF NOT EXISTS default_depreciation_method VARCHAR(50),
ADD COLUMN IF NOT EXISTS default_useful_life_months INT;

CREATE INDEX IF NOT EXISTS idx_asset_categories_parent ON asset_categories(parent_category_id);
