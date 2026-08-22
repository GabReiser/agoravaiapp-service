-- Catalogo de categorias de sistema (fallback estatico do frontend).
-- Ajustar nomes/icones/cores ao catalogo real do frontend quando disponivel.
INSERT INTO categories (id, name, icon, color, system) VALUES
    (gen_random_uuid(), 'Alimentacao',   'restaurant',  '#F59E0B', TRUE),
    (gen_random_uuid(), 'Transporte',    'directions',  '#3B82F6', TRUE),
    (gen_random_uuid(), 'Moradia',       'home',        '#8B5CF6', TRUE),
    (gen_random_uuid(), 'Saude',         'favorite',    '#EF4444', TRUE),
    (gen_random_uuid(), 'Educacao',      'school',      '#10B981', TRUE),
    (gen_random_uuid(), 'Lazer',         'celebration', '#EC4899', TRUE),
    (gen_random_uuid(), 'Compras',       'shopping',    '#6366F1', TRUE),
    (gen_random_uuid(), 'Salario',       'payments',    '#22C55E', TRUE),
    (gen_random_uuid(), 'Outros',        'category',    '#64748B', TRUE);
